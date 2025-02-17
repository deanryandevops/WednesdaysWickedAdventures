# DocumentDB Cluster
resource "aws_docdb_cluster" "mongodb" {
  cluster_identifier      = "documentdb-cluster"
  engine                 = "docdb"
  master_username        = var.db_username
  master_password        = var.db_password
  #backup_retention_period = 7
  #preferred_backup_window = "07:00-09:00"
  storage_encrypted      = true
  vpc_security_group_ids = [aws_security_group.private_sg.id]
  db_subnet_group_name   = aws_docdb_subnet_group.mongodb_subnet_group.name
  skip_final_snapshot    = true

  tags = { Name = "DocumentDB Cluster" }
}

# DocumentDB Subnet Group
resource "aws_docdb_subnet_group" "mongodb_subnet_group" {
  name       = "documentdb-subnet-group"
  subnet_ids = [aws_subnet.private.id, aws_subnet.private2.id]

  tags = { Name = "DocumentDB Subnet Group" }
}

# DocumentDB Cluster Instances
resource "aws_docdb_cluster_instance" "mongodb_instance" {
  count               = 2  # Creates 2 instances in different subnets
  identifier          = "documentdb-instance-${count.index}"
  cluster_identifier = aws_docdb_cluster.mongodb.id
  instance_class     = "db.t3.medium"
  #publicly_accessible = false

  tags = { Name = "DocumentDB Instance ${count.index}" }
}

output "documentdb_endpoint" {
  value = aws_docdb_cluster.mongodb.endpoint
}
