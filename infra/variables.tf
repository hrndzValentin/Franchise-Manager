variable "aws_region" {
  default = "us-east-1"
}

variable "db_username" {
  default = "admin"
}

variable "db_password" {
  description = "Password for DocumentDB"
  sensitive   = true
}

variable "db_name" {
  default = "franquiciasdb"
}