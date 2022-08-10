terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.25.0"
    }
  }
}

provider "aws" {
  region = "eu-central-1"
  endpoints {
    dynamodb = "http://localhost:8000"
  }
}