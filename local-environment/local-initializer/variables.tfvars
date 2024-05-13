# tags variables
common_resource_tags = {}

# iam variables
username = "vauthenticator-local-dev"
path = "/local-stage/"

# dynamodb variables
table_name_suffix  = "_Local_Staging"

# s3 variables
vauthenticator_document_s3_bucket_name = "irrelevant"
vauthenticator_management_ui_document_s3_bucket_name = "irrelevant"

# kms variables
key_name                      = "master_key"
key_administrator_account_ids = ["arn:aws:iam::000000000000:user/local-stage/vauthenticator-local-dev"]
key_user_account_ids = ["arn:aws:iam::000000000000:user/local-stage/vauthenticator-local-dev"]

key_alias               = "vauthenticator-local-dev-key"
key_description         = "vauthenticator-local-dev-key"
deletion_window_in_days = 7
