export $(cat env)

MASTER_KEY=$(grep target_key_id ../local-tenant-iac/resources/terraform.tfstate | awk -F ":"  '{print $2}'| sed "s/\"//g" | sed 's/ //g')
export MASTER_KEY=$MASTER_KEY
echo "MASTER_KEY=$MASTER_KEY"

pip3 install -r requirements.txt

echo "MASTER_KEY: $MASTER_KEY"
echo "TABLES_SUFFIX: $TABLES_SUFFIX"

echo "KMS_ENDPOINT: $KMS_ENDPOINT"
echo "DYNAMO_DB_ENDPOINT: $DYNAMO_DB_ENDPOINT"

python3 key_setup.py $MASTER_KEY $TABLES_SUFFIX
python3 setup.py admin@email.com $TABLES_SUFFIX