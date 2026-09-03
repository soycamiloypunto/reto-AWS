variable "repository_url" {
  description = "URL del repositorio ECR"
  type        = string
}

variable "db_url_arn" {
  description = "SSM Parameter ARN for the R2DBC URL"
  type        = string
}

variable "db_username_arn" {
  description = "SSM Parameter ARN for Database username"
  type        = string
}

variable "db_password_arn" {
  description = "SSM Parameter ARN for Database password"
  type        = string
}
