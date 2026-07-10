output "endpoint_security_group_id" {
  description = "Security group attached to the interface endpoints."
  value       = var.enable_vpc_endpoints ? aws_security_group.endpoints[0].id : null
}

output "interface_endpoint_ids" {
  description = "Interface endpoint IDs by service key."
  value       = var.enable_vpc_endpoints ? { for key, endpoint in aws_vpc_endpoint.interface : key => endpoint.id } : {}
}
