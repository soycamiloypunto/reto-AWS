output "db_url_arn" {
  value = aws_ssm_parameter.db_url.arn
}

output "db_username_arn" {
  value = aws_ssm_parameter.db_username.arn
}

output "db_password_arn" {
  value = aws_ssm_parameter.db_password.arn
}
