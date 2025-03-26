output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "public_subnet_id" {
  description = "Public subnet ID"
  value       = aws_subnet.public.id
}

output "private_subnet_id" {
  description = "Private subnet ID"
  value       = aws_subnet.private.id
}

output "public_ec2_instance_id" {
  description = "Public EC2 Instance ID"
  value       = aws_instance.public_instance.id
}

output "instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.public_instance.public_ip
}

output "private_ec2_instance_id" {
  description = "Private EC2 Instance ID"
  value       = aws_instance.private_instance.id
}

output "private_instance_ip" {
  description = "Private IP address of the EC2 instance"
  value       = aws_instance.public_instance.private_ip
}

output "jump_box_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.public_instance.public_ip
}

output "jump_box_instance_id" {
  description = "Jump Box Instance ID"
  value       = aws_instance.jump_box.id
}
