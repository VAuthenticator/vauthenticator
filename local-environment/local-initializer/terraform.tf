terraform {

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.55.0"
    }
  }
}

provider "aws" {
  skip_credentials_validation = true
  skip_metadata_api_check = true
  endpoints {
    dynamodb = "http://localhost:4566"
    kms = "http://localhost:4566"
    iam = "http://localhost:4566"
  }
}