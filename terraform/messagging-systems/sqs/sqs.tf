resource "aws_sqs_queue" "account-updates" {
  name                      = var.account_updates_name
  message_retention_seconds = var.message_retention_seconds_value

  tags = {
    project : var.project
    environment : var.environment
    application : var.application
  }
}