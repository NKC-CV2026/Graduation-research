output "cluster_arn" {
  description = "ECS cluster ARN."
  value       = aws_ecs_cluster.this.arn
}

output "cluster_name" {
  description = "ECS cluster name."
  value       = aws_ecs_cluster.this.name
}

output "task_definition_arn" {
  description = "Migration task definition ARN."
  value       = aws_ecs_task_definition.this.arn
}

output "task_family" {
  description = "Migration task definition family."
  value       = aws_ecs_task_definition.this.family
}

output "container_name" {
  description = "Migration container name."
  value       = local.container_name
}

output "execution_role_arn" {
  description = "ECS task execution role ARN."
  value       = aws_iam_role.execution.arn
}

output "task_role_arn" {
  description = "ECS task role ARN."
  value       = aws_iam_role.task.arn
}
