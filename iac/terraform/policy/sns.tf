data "aws_iam_policy_document" "vauthenticator_sns_send_sms_policy" {
  statement {
    principals {
      type        = "AWS"
      identifiers = [data.aws_iam_user.vauthenticator.arn]
    }

    actions = [
      "sns:Publish"
    ]

    not_resources = ["arn:aws:sns:*:*:*"]
  }
}