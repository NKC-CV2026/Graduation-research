output "endpoint_security_group_id" {
  description = "Security group attached to the interface endpoints."
  value       = aws_security_group.endpoints.id
}

output "interface_endpoint_ids" {
  description = "Interface endpoint IDs by service key."
  value       = { for key, endpoint in aws_vpc_endpoint.interface : key => endpoint.id }
}
