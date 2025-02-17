# Public EC2 Instance
resource "aws_instance" "public_instance" {
  ami           = var.ami_id
  instance_type = var.instance_type
  subnet_id     = aws_subnet.public.id
  security_groups = [aws_security_group.public_sg.id]
  tags = { Name = "Public-Instance" }
}

# Private EC2 Instance
resource "aws_instance" "private_instance" {
  ami           = var.ami_id
  instance_type = var.instance_type
  subnet_id     = aws_subnet.private.id
  security_groups = [aws_security_group.private_sg.id]
  tags = { Name = "Private-Instance" }
}

# Jump Box in Public Subnet
resource "aws_instance" "jump_box" {
  ami           = var.ami_id
  instance_type = var.instance_type
  subnet_id     = aws_subnet.public.id
  security_groups = [aws_security_group.public_sg.id]
  tags = { Name = "Jump-Box" }
}
