
data "aws_security_group" "default" {
  vpc_id = "vpc-02d16138329daa219"
  name   = "default"
}

resource "aws_ecs_cluster" "api_cluster" {
  name = "api-personas-cluster"
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "api-personas-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  inline_policy {
    name = "ssm-secrets-policy"
    policy = jsonencode({
      Version = "2012-10-17"
      Statement = [
        {
          Action = [
            "ssm:GetParameters",
            "ssm:GetParameter"
          ]
          Effect   = "Allow"
          Resource = "*"
        }
      ]
    })
  }
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_cloudwatch_log_group" "api_logs" {
  name              = "/ecs/api-personas"
  retention_in_days = 7
}

resource "aws_ecs_task_definition" "api_task" {
  family                   = "api-personas-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "ARM64"
  }

  container_definitions = jsonencode([
    {
      name      = "api-personas-container"
      image     = "${var.repository_url}:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]
      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "prod"
        }
      ]
      secrets = [
        {
          name      = "SPRING_R2DBC_URL"
          valueFrom = var.db_url_arn
        },
        {
          name      = "SPRING_R2DBC_USERNAME"
          valueFrom = var.db_username_arn
        },
        {
          name      = "SPRING_R2DBC_PASSWORD"
          valueFrom = var.db_password_arn
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.api_logs.name
          "awslogs-region"        = "us-east-1"
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "api_service" {
  name            = "api-personas-service"
  cluster         = aws_ecs_cluster.api_cluster.id
  task_definition = aws_ecs_task_definition.api_task.arn
  launch_type     = "FARGATE"
  desired_count   = 2

  network_configuration {
    subnets          = ["subnet-063cbe03602b61ad7", "subnet-08bdaa4316e2d8268"]
    security_groups  = [data.aws_security_group.default.id]
    assign_public_ip = true
  }

  load_balancer {
    # Al no tener root module, omitiremos asociar el LB aquí por ahora o sugerir la importación del estado
    # target_group_arn = "arn:aws:elasticloadbalancing:..."
    # Mejor usar el Data source del target group
    target_group_arn = data.aws_lb_target_group.tg.arn
    container_name   = "api-personas-container"
    container_port   = 8080
  }
}

data "aws_lb_target_group" "tg" {
  name = "api-personas-tg"
}
