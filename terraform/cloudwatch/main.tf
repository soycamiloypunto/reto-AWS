resource "aws_cloudwatch_metric_alarm" "ecs_cpu_alarm" {
  alarm_name          = "api-personas-high-cpu"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "2"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = "60"
  statistic           = "Average"
  threshold           = "80"
  alarm_description   = "Alarma cuando el CPU del API excede el 80%"

  dimensions = {
    ClusterName = var.cluster_name
    ServiceName = var.service_name
  }
}
