function copy_tf_variables() {
  echo $ACCOUNT_ID
  sed 's/ACCOUNT_ID/'$ACCOUNT_ID'/g' ../../local-environment/variables.tfvars | sed 's/VAUTHENTICATOR_BUCKET/'$VAUTHENTICATOR_BUCKET'/g' | sed 's/VAUTHENTICATOR_MANAGEMENT_UI_BUCKET/'$VAUTHENTICATOR_MANAGEMENT_UI_BUCKET'/g' > variables.tfvars

}

function create_symbolic_linkFor() {
  cd $1
  ln -sf ../variable.tf variable.tf
  cp -f ../../local-environment/terraform.tf terraform.tf
  cd ..
}

source .env

TEMPLATES=("welcome.html" "mail-verify-challenge.html" "reset-password.html" "mfa-challenge.html")

cd ../terraform

create_symbolic_linkFor iam
create_symbolic_linkFor resources
create_symbolic_linkFor policy

# IAM
cd iam
copy_tf_variables

terraform init -backend-config="bucket=$TF_STATE_BUCKET" -backend-config="region=$AWS_REGION"
terraform plan -var-file=./variables.tfvars
terraform apply --auto-approve -var-file=variables.tfvars
#
cd ../resources
copy_tf_variables

terraform init -backend-config="bucket=$TF_STATE_BUCKET" -backend-config="region=$AWS_REGION"
terraform plan -var-file=variables.tfvars
terraform apply -var-file=variables.tfvars -auto-approve

cd ../policy
copy_tf_variables

terraform init -backend-config="bucket=$TF_STATE_BUCKET" -backend-config="region=$AWS_REGION"
terraform plan -var-file=variables.tfvars
terraform apply -var-file=variables.tfvars -auto-approve

if [ $IS_PRODUCITON = "False" ]
  then
    END_POINT="--endpoint http://localhost:4566"
fi

cd ../../document/template/mail
for TEMPLATE in ${TEMPLATES[@]}
do
  aws s3 cp $TEMPLATE s3://$VAUTHENTICATOR_BUCKET/mail/templates/$TEMPLATE $END_POINT
done