resource "aws_iam_user" "vauthenticator" {
  name = var.username
  path = var.path


  tags = {
    Name = var.username
  }
}

resource "aws_iam_user_policy_attachment" "vauthenticator_sqs_policy-attach" {
  user = aws_iam_user.vauthenticator.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSQSFullAccess"
}

resource "aws_iam_user_policy_attachment" "vauthenticator_dynamo_policy-attach" {
  user = aws_iam_user.vauthenticator.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
}