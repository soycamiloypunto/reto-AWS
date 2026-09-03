resource "aws_ssm_parameter" "db_url" {
  name        = "/api-personas/prod/db/url"
  description = "URL de la base de datos RDS"
  type        = "SecureString"
  value       = var.db_url
}

resource "aws_ssm_parameter" "db_username" {
  name        = "/api-personas/prod/db/username"
  description = "Usuario de la base de datos"
  type        = "SecureString"
  value       = var.db_username
}

resource "aws_ssm_parameter" "db_password" {
  name        = "/api-personas/prod/db/password"
  description = "Contraseña de la base de datos"
  type        = "SecureString"
  value       = var.db_password
}
