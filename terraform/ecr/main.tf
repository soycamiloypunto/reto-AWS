
resource "aws_ecr_repository" "api_personas_repo" {
  name                 = "api-personas-repo"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

output "repository_url" {
  value = aws_ecr_repository.api_personas_repo.repository_url
}
