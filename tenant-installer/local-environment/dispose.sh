function copy_tf_variables() {
  echo $ACCOUNT_ID
  sed 's/ACCOUNT_ID/'$ACCOUNT_ID'/g' ../../local-environment/variables.tfvars | sed 's/VAUTHENTICATOR_BUCKET/'$VAUTHENTICATOR_BUCKET'/g' | sed 's/VAUTHENTICATOR_MANAGEMENT_UI_BUCKET/'$VAUTHENTICATOR_MANAGEMENT_UI_BUCKET'/g' > variables.tfvars
}

function create_symbolic_linkFor() {
  cd $1
  ln -sf ../variable.tf variable.tf
  cd ..
}

source env

cd ../terraform

create_symbolic_linkFor iam
create_symbolic_linkFor policy
create_symbolic_linkFor resources

cd policy
copy_tf_variables

terraform init -backend-config="bucket=$TF_STATE_BUCKET" -backend-config="region=$AWS_REGION"
terraform destroy --auto-approve -var-file=variables.tfvars

cd ../resources
copy_tf_variables

terraform init -backend-config="bucket=$TF_STATE_BUCKET" -backend-config="region=$AWS_REGION"
terraform destroy --auto-approve -var-file=variables.tfvars


# IAM
cd ../iam
copy_tf_variables

terraform init -backend-config="bucket=$TF_STATE_BUCKET" -backend-config="region=$AWS_REGION"
terraform destroy --auto-approve -var-file=variables.tfvars

