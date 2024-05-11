export DYNAMO_DB_ENDPOINT=http://localstack:4566
export KMS_ENDPOINT=http://localstack:4566
export S3_ENDPOINT=http://s3.localstack.localstack.cloud:4566
export IAM_ENDPOINT=http://localstack:4566

sh ./setup.sh

sh init.sh