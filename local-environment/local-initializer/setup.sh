function prepare_environment_for() {
  cd $1
  ln -sf ../../../iac/terraform/variable.tf  variable.tf
  cp -f ../../local-initializer/terraform.tf terraform.tf
  cp -f ../../local-initializer/variables.tfvars variables.tfvars
  cd ..
}

source .env

mkdir ../local-tenant-iac
cd ../local-tenant-iac

cp -R ../../iac/terraform/iam .
cp -R ../../iac/terraform/resources .
rm resources/s3.tf


prepare_environment_for iam
prepare_environment_for resources

# IAM
cd iam
terraform init
terraform plan -var-file=./variables.tfvars
terraform apply --auto-approve -var-file=variables.tfvars

#
cd ../resources
terraform init
terraform plan -var-file=variables.tfvars
terraform apply -var-file=variables.tfvars -auto-approve
