terraform {
  backend "s3" {
    bucket = var.tf_state_bucket
    key    = var.tf_state_key
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "3.64.2"
    }
  }
}

provider "aws" {}