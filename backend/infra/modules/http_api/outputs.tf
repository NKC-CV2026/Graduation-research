output "api_endpoint" {
  description = "Base URL of the HTTP API."
  value       = aws_apigatewayv2_api.this.api_endpoint
}

output "api_id" {
  description = "HTTP API ID."
  value       = aws_apigatewayv2_api.this.id
}

output "execution_arn" {
  description = "Execution ARN of the HTTP API."
  value       = aws_apigatewayv2_api.this.execution_arn
}
