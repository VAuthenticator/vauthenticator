source .env

pip3 install -r requirements.txt

echo "MASTER_KEY: $MASTER_KEY"
echo "TABLES_SUFFIX: $TABLES_SUFFIX"

echo "KMS_ENDPOINT: $KMS_ENDPOINT"
echo "DYNAMO_DB_ENDPOINT: $DYNAMO_DB_ENDPOINT"

python3 key_setup.py $MASTER_KEY $TABLES_SUFFIX
python3 setup.py admin@email.com $TABLES_SUFFIX