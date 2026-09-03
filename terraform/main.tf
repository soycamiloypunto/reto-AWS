provider "aws" {
  region = "us-east-1"
}

module "ecr" {
  source = "./ecr"
}

# Automatización de Docker Build y Push usando null_resource
resource "null_resource" "docker_push" {
  depends_on = [module.ecr]

  triggers = {
    # Siempre se ejecuta cuando cambien los archivos del backend
    always_run = timestamp()
  }

  provisioner "local-exec" {
    command = <<EOF
      cd ../api-personas && \
      ./mvnw clean package -DskipTests && \
      aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${module.ecr.repository_url} && \
      docker build -t api-personas-repo . && \
      docker tag api-personas-repo:latest ${module.ecr.repository_url}:latest && \
      docker push ${module.ecr.repository_url}:latest
    EOF
  }
}

module "rds" {
  source     = "./rds"
}

module "alb" {
  source     = "./alb"
}

module "ssm" {
  source      = "./ssm"
  db_url      = "r2dbc:postgresql://${module.rds.rds_endpoint}/apipersonas_db?sslMode=REQUIRE"
  db_username = "root"
  db_password = "password"
}

module "ecs" {
  source         = "./ecs"
  repository_url = module.ecr.repository_url
  depends_on     = [null_resource.docker_push, module.alb, module.rds, module.ssm]

  db_url_arn      = module.ssm.db_url_arn
  db_username_arn = module.ssm.db_username_arn
  db_password_arn = module.ssm.db_password_arn
}

module "cognito" {
  source = "./cognito"
}

module "cloudwatch" {
  source       = "./cloudwatch"
  cluster_name = module.ecs.cluster_name
  service_name = module.ecs.service_name
}

module "api_gateway" {
  source                     = "./api_gateway"
  depends_on                 = [module.alb]
  cognito_user_pool_endpoint = module.cognito.user_pool_endpoint
  cognito_client_id          = module.cognito.user_pool_client_id
}

output "api_gateway_endpoint" {
  value = module.api_gateway.api_endpoint
}

output "cognito_user_pool_id" {
  value = module.cognito.user_pool_id
}

output "cognito_client_id" {
  value = module.cognito.user_pool_client_id
}
