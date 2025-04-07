variable "aws_region" {
  description = "AWS region"
  default     = "eu-west-1"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidr" {
  description = "CIDR block for the public subnet"
  default     = "10.0.1.0/24"
}

variable "private_subnet_cidr" {
  description = "CIDR block for the private subnet"
  default     = "10.0.2.0/24"
}

variable "private_subnet_cidr2" {
  description = "CIDR block for the private subnet"
  default     = "10.0.3.0/24"
}

variable "availability_zone" {
  description = "Availability zone"
  default     = "eu-west-1a"
}

variable "availability_zone2" {
  description = "Availability zone"
  default     = "eu-west-1b"
}

variable "ami_id" {
  description = "AMI ID for EC2 instances"
  default     = "ami-087a0156cb826e921"
}

variable "instance_type" {
  description = "EC2 instance type"
  default     = "t2.micro"
}
