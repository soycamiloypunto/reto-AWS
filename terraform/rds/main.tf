
data "aws_security_group" "default" {
  vpc_id = "vpc-02d16138329daa219"
  name   = "default"
}

# Crear Grupo de Subredes para RDS
resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "rds-subnet-group"
  subnet_ids = ["subnet-063cbe03602b61ad7", "subnet-08bdaa4316e2d8268"]

  tags = {
    Name = "rds-subnet-group"
  }
}

# Aprovisionar RDS PostgreSQL
resource "aws_db_instance" "postgres_db" {
  identifier             = "api-personas-db"
  engine                 = "postgres"
  engine_version         = "15"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  storage_type           = "gp2"
  username               = "root"
  password               = "password" # Esto luego pasara por Secrets Manager
  db_name                = "apipersonas_db"
  publicly_accessible    = true       # Segun requerimiento: "accesible por cualquier persona"
  skip_final_snapshot    = true
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [data.aws_security_group.default.id]
}

output "rds_endpoint" {
  description = "El endpoint de conexion a la base de datos RDS"
  value       = aws_db_instance.postgres_db.endpoint
}
