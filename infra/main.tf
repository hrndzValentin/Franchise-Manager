resource "aws_docdb_subnet_group" "docdb_subnet" {
  name       = "franquicias-subnet-group"
  subnet_ids = ["subnet-12345678", "subnet-87654321"] # dummy

  tags = {
    Name = "franquicias-subnet-group"
  }
}
resource "aws_security_group" "docdb_sg" {
  name        = "franquicias-docdb-sg"
  description = "Allow MongoDB access"

  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # solo para demo
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
resource "aws_docdb_cluster" "cluster" {
  cluster_identifier = "franquicias-cluster"

  master_username = var.db_username
  master_password = var.db_password

  db_subnet_group_name   = aws_docdb_subnet_group.docdb_subnet.name
  vpc_security_group_ids = [aws_security_group.docdb_sg.id]

  skip_final_snapshot = true
}
resource "aws_docdb_cluster_instance" "instance" {
  identifier         = "franquicias-instance-1"
  cluster_identifier = aws_docdb_cluster.cluster.id

  instance_class = "db.t3.medium"
}