module "vauthenticator_s3_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "3.7.0"

  bucket                  = var.vauthenticator_document_s3_bucket_name
  acl                     = "private"
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  force_destroy = true

  tags = merge(tomap({ "Name" = var.vauthenticator_document_s3_bucket_name }), var.common_resource_tags)
}
module "vauthenticator_management_ui_s3_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "3.7.0"

  bucket                  = var.vauthenticator_management_ui_document_s3_bucket_name
  acl                     = "private"
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  force_destroy = true

  tags = merge(tomap({ "Name" = var.vauthenticator_management_ui_document_s3_bucket_name }), var.common_resource_tags)
}
