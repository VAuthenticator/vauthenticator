function prepare_environment_for() {
  cd $1
  ln -sf ../../../iac/terraform/variable.tf  variable.tf
  cp -f ../../local-initializer/terraform.tf terraform.tf
  cp -f ../../local-initializer/variables.tfvars variables.tfvars
  cd ..
}

export $(cat env)

mkdir ../local-tenant-iac
cd ../local-tenant-iac

cp -R ../../iac/terraform/iam .
cp -R ../../iac/terraform/resources .
cp -R ../../iac/terraform/policy .

# remove s3 stuff since that for local file system is intended to be used
rm resources/s3.tf
rm policy/s3.tf

prepare_environment_for iam
prepare_environment_for resources
prepare_environment_for policy

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

MASTER_KEY=$(grep target_key_id terraform.tfstate | awk -F ":"  '{print $2}'| sed "s/\"//g" | sed 's/ //g')
export MASTER_KEY=$MASTER_KEY
echo "MASTER_KEY=$MASTER_KEY"


#
cd ../policy
terraform init
terraform plan -var-file=variables.tfvars
terraform apply -var-file=variables.tfvars -auto-approve
