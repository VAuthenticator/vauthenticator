terraform {
  backend "s3" {
    key    = "terraform-state/policy/state.tfstate"
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.55.0"
    }
  }
}

provider "aws" {}