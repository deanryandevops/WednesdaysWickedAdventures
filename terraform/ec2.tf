# Public EC2 Instance
resource "aws_instance" "public_instance" {
  ami             = var.ami_id
  instance_type   = var.instance_type
  subnet_id       = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.public_sg.id]
  tags            = { Name = "Public-Instance" }
  key_name        = "WickedWednesdayKP"
  user_data       = file("${path.module}/ec2-user-data.sh")
}

# Private EC2 Instance
resource "aws_instance" "private_instance" {
  ami              = var.ami_id
  instance_type    = var.instance_type
  subnet_id        = aws_subnet.private.id
  vpc_security_group_ids  = [aws_security_group.private_sg.id]
  tags             = { Name = "Private-Instance" }
  key_name        = "WickedWednesdayKP"
  user_data        = file("${path.module}/ec2-user-data.sh")
}

# Jump Box in Public Subnet
resource "aws_instance" "jump_box" {
  ami             = var.ami_id
  instance_type   = var.instance_type
  subnet_id       = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.public_sg.id]
  key_name        = "WickedWednesdayKP"
  tags            = { Name = "Jump-Box" }
}
