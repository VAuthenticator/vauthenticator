resource "aws_dynamodb_table" "client_application_table" {
  name         = var.client_application_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "client_id"


  attribute {
    name = "client_id"
    type = "S"
  }

  tags = {
    Name        = var.client_application_table_name
    Environment = var.environment
  }
}

resource "aws_dynamodb_table" "account_table" {
  name         = var.account_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "user_name"

  attribute {
    name = "user_name"
    type = "S"
  }


  tags = {
    Name        = var.account_table_name
    Environment = var.environment
  }
}

resource "aws_dynamodb_table" "role_table" {
  name         = var.role_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "role_name"

  attribute {
    name = "role_name"
    type = "S"
  }


  tags = {
    Name        = var.role_table_name
    Environment = var.environment
  }
}

resource "aws_dynamodb_table" "account_role_table" {
  name         = var.account_role_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "user_name"
  range_key    = "role_name"

  attribute {
    name = "role_name"
    type = "S"
  }

  attribute {
    name = "user_name"
    type = "S"
  }

  tags = {
    Name        = var.account_role_table_name
    Environment = var.environment
  }
}

resource "aws_dynamodb_table" "keys_table" {
  name         = var.keys_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "master_key_id"
  range_key    = "key_id"

  attribute {
    name = "master_key_id"
    type = "S"
  }

  attribute {
    name = "key_id"
    type = "S"
  }

  tags = {
    Name        = var.keys_table_name
    Environment = var.environment
  }
}


resource "aws_dynamodb_table" "mail_verification_ticket_table" {
  name         = var.mail_verification_ticket_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "mail_verification_ticket"

  attribute {
    name = "mail_verification_ticket"
    type = "S"
  }


  tags = {
    Name        = var.mail_verification_ticket_table_name
    Environment = var.environment
  }
}