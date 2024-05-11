terraform {

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.55.0"
    }
  }
}

provider "aws" {

  endpoints {
    iam = "$IAM_ENDPOINT"
    s3 = "$S3_ENDPOINT"
    kms = "$KMS_ENDPOINT"
    dynamodb = "$DYNAMO_DB_ENDPOINT"
  }
}

