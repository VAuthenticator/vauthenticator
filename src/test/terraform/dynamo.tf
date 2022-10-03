resource "aws_dynamodb_table" "client_application_table_staging" {
  name = "TESTING_VAuthenticator_ClientApplication"
  billing_mode = "PAY_PER_REQUEST"
  hash_key = "client_id"

  attribute {
    name = "client_id"
    type = "S"
  }

  tags = {
    Environment = "testing"
  }
}

resource "aws_dynamodb_table" "account_table_staging" {
  name = "TESTING_VAuthenticator_Account"
  billing_mode = "PAY_PER_REQUEST"
  hash_key = "user_name"

  attribute {
    name = "user_name"
    type = "S"
  }

  tags = {
    Environment = "testing"
  }
}

resource "aws_dynamodb_table" "role_table_staging" {
  name = "TESTING_VAuthenticator_Role"
  billing_mode = "PAY_PER_REQUEST"
  hash_key = "role_name"

  attribute {
    name = "role_name"
    type = "S"
  }

  tags = {
    Environment = "testing"
  }
}

resource "aws_dynamodb_table" "account_role_table_staging" {
  name = "TESTING_VAuthenticator_Account_Role"
  billing_mode = "PAY_PER_REQUEST"
  hash_key = "user_name"
  range_key = "role_name"

  attribute {
    name = "role_name"
    type = "S"
  }

  attribute {
    name = "user_name"
    type = "S"
  }

  tags = {
    Environment = "testing"
  }
}

resource "aws_dynamodb_table" "keys_table_staging" {
  name         = "TESTING_VAuthenticator_Keys"
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
    Environment = "testing"
  }
}


resource "aws_dynamodb_table" "mail_verification_ticket_table" {
  name         = "TESTING_Authenticator_mail_verification_ticket"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "mail_verification_ticket"

  attribute {
    name = "mail_verification_ticket"
    type = "S"
  }


  tags = {
    Environment = "testing"
  }
}