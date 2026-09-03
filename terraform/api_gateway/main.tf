
data "aws_security_group" "default" {
  vpc_id = "vpc-02d16138329daa219"
  name   = "default"
}

data "aws_lb" "api_alb" {
  name = "api-personas-alb"
}

data "aws_lb_listener" "api_listener" {
  load_balancer_arn = data.aws_lb.api_alb.arn
  port              = 80
}

resource "aws_apigatewayv2_api" "http_api" {
  name          = "api-personas-gateway"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "alb_integration" {
  api_id           = aws_apigatewayv2_api.http_api.id
  integration_type = "HTTP_PROXY"
  integration_uri  = data.aws_lb_listener.api_listener.arn
  integration_method = "ANY"
  connection_type    = "VPC_LINK"
  connection_id      = aws_apigatewayv2_vpc_link.api_vpc_link.id
}

resource "aws_apigatewayv2_vpc_link" "api_vpc_link" {
  name               = "api-vpc-link"
  security_group_ids = [data.aws_security_group.default.id]
  subnet_ids         = ["subnet-063cbe03602b61ad7", "subnet-08bdaa4316e2d8268"]
}

resource "aws_apigatewayv2_authorizer" "cognito_authorizer" {
  api_id           = aws_apigatewayv2_api.http_api.id
  authorizer_type  = "JWT"
  identity_sources = ["$request.header.Authorization"]
  name             = "cognito-authorizer"

  jwt_configuration {
    audience = [var.cognito_client_id]
    issuer   = "https://${var.cognito_user_pool_endpoint}"
  }
}

resource "aws_apigatewayv2_route" "post_persona" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "POST /personas"
  target             = "integrations/${aws_apigatewayv2_integration.alb_integration.id}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.cognito_authorizer.id
}

resource "aws_apigatewayv2_route" "get_persona" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "GET /personas/{id}"
  target             = "integrations/${aws_apigatewayv2_integration.alb_integration.id}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.cognito_authorizer.id
}

resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

output "api_endpoint" {
  value = aws_apigatewayv2_api.http_api.api_endpoint
}
