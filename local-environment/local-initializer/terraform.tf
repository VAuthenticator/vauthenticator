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
    dynamodb = "http://localhost:4566"
    kms = "http://localhost:4566"
  }
}