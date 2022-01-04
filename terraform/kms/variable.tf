variable "key_administrator_account_ids" {
  type = set(string)
}

variable "key_user_account_ids" {
  type = set(string)
}

variable "key_allow_to_attach_persistent_resources_account_ids" {
  type = set(string)
}

variable "key_alias" {}

variable "key_description" {}
variable "deletion_window_in_days" {
  default = 7
}

variable "application" {}
variable "environment" {}


