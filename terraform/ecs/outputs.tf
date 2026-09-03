output "cluster_name" {
  value = aws_ecs_cluster.api_cluster.name
}

output "service_name" {
  value = aws_ecs_service.api_service.name
}
