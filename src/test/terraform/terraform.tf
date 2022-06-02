terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "3.35.0"
    }
  }
}

provider "aws" {
  region = "eu-central-1"
  endpoints {
    dynamodb = "http://localhost:8000"
  }
}