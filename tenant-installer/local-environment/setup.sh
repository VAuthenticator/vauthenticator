function copy_tf_variables() {
  echo $ACCOUNT_ID
  sed 's@ACCOUNT_ID@'$ACCOUNT_ID'@g' ../../local-environment/variables.tfvars | sed 's@VAUTHENTICATOR_BUCKET@'$VAUTHENTICATOR_BUCKET'@g' | sed 's@VAUTHENTICATOR_MANAGEMENT_UI_BUCKET@'$VAUTHENTICATOR_MANAGEMENT_UI_BUCKET'@g' > variables.tfvars
}

function create_symbolic_linkFor() {
  cd $1
  ln -sf ../variable.tf variable.tf
  cp -f ../../local-environment/terraform.tf terraform.tf
  cd ..
}

export $(cat env)

sed -i 's@DYNAMO_DB_ENDPOINT@'$DYNAMO_DB_ENDPOINT'@g' terraform.tf
sed -i 's@KMS_ENDPOINT@'$KMS_ENDPOINT'@g' terraform.tf
sed -i 's@S3_ENDPOINT@'$S3_ENDPOINT'@g' terraform.tf
sed -i 's@EC2_DB_ENDPOINT@'$EC2_DB_ENDPOINT'@g' terraform.tf
sed -i 's@IAM_ENDPOINT@'$IAM_ENDPOINT'@g' terraform.tf

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

MASTER_KEY=$(grep target_key_id terraform.tfstate | awk -F ":"  '{print $2}'| sed "s/\"//g" | sed 's/ //g')
export MASTER_KEY=$MASTER_KEY
echo "MASTER_KEY=$MASTER_KEY"

cd ../policy
copy_tf_variables

terraform init -backend-config="bucket=$TF_STATE_BUCKET" -backend-config="region=$AWS_REGION"
terraform plan -var-file=variables.tfvars
terraform apply -var-file=variables.tfvars -auto-approve

if [ $IS_PRODUCITON = "False" ]
  then
    END_POINT="--endpoint http://localhost:4566"
fi

cd ../../communication/default/mail
for TEMPLATE in ${TEMPLATES[@]}
do
  aws s3 cp $TEMPLATE s3://$VAUTHENTICATOR_BUCKET/mail/templates/$TEMPLATE $END_POINT
done