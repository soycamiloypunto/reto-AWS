
data "aws_security_group" "default" {
  vpc_id = "vpc-02d16138329daa219"
  name   = "default"
}

resource "aws_lb" "api_alb" {
  name               = "api-personas-alb"
  internal           = true
  load_balancer_type = "application"
  security_groups    = [data.aws_security_group.default.id]
  subnets            = ["subnet-063cbe03602b61ad7", "subnet-08bdaa4316e2d8268"]

  enable_deletion_protection = false
}

resource "aws_lb_target_group" "api_tg" {
  name        = "api-personas-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = "vpc-02d16138329daa219"
  target_type = "ip"

  health_check {
    path                = "/personas/1" # Endpoint de prueba para el health check
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
    matcher             = "200-499"     # Acepta el 404 Not Found (porque la BD esta vacia)
  }
}

resource "aws_lb_listener" "api_listener" {
  load_balancer_arn = aws_lb.api_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.api_tg.arn
  }
}

output "alb_dns_name" {
  value = aws_lb.api_alb.dns_name
}
output "target_group_arn" {
  value = aws_lb_target_group.api_tg.arn
}
