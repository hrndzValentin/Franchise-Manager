output "docdb_endpoint" {
  value = aws_docdb_cluster.cluster.endpoint
}

output "connection_string" {
  value = "mongodb://${var.db_username}:${var.db_password}@${aws_docdb_cluster.cluster.endpoint}:27017/${var.db_name}?ssl=true&replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false"
}