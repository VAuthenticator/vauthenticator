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
    iam = "http://localhost:4566"
    s3 = "http://s3.localhost.localstack.cloud:4566"
    kms = "http://localhost:4566"
    dynamodb = "http://localhost:4566"
  }
}

