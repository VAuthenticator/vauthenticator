resource "aws_sqs_queue" "account-updates" {
  name                      = var.account_updates_queue_name
  message_retention_seconds = var.account_updates_queue_message_retention_seconds_value

  kms_master_key_id                 = "alias/aws/sqs"
  kms_data_key_reuse_period_seconds = 300

  tags = {
    project : var.project
    environment : var.environment
    application : var.application
  }
}

resource "aws_sqs_queue" "signup-account" {
  name                      = var.signup_account_queue_name
  message_retention_seconds = var.signup_account_queue_message_retention_seconds_value

  kms_master_key_id                 = "alias/aws/sqs"
  kms_data_key_reuse_period_seconds = 300

  tags = {
    project : var.project
    environment : var.environment
    application : var.application
  }
}