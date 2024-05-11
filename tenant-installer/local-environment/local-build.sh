docker-compose up -d

export DYNAMO_DB_ENDPOINT=http://localhost:4566
export KMS_ENDPOINT=http://localhost:4566
export S3_ENDPOINT=http://s3.localhost.localstack.cloud:4566
export IAM_ENDPOINT=http://localhost:4566

sleep 60s

sh ./setup.sh

sh init.sh