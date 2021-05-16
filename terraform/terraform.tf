terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "3.35.0"
    }
  }
}

provider "aws" {
  endpoints {
    dynamodb = "http://localhost:8000"
  }
  region = "us-east-1"
}