import base64
import boto3
import os
import sys
import uuid
import psycopg2
from dotenv import load_dotenv

load_dotenv(dotenv_path="env")


def dynamodbClient():
    dynamodb_endpoint = os.getenv('DYNAMO_DB_ENDPOINT')
    if dynamodb_endpoint is None:
        client = boto3.resource('dynamodb')
    else:
        client = boto3.resource('dynamodb', endpoint_url=dynamodb_endpoint)
    return client


def kmsClient():
    kms_endpoint = os.getenv('KMS_ENDPOINT')
    print(f"kms_endpoint {kms_endpoint}")
    if kms_endpoint is None:
        client = boto3.client("kms")
    else:
        client = boto3.client('kms', endpoint_url=kms_endpoint)
    return client


dynamodb = dynamodbClient()
kms_client = kmsClient()


def store_key(key_table_name, master_key):
    key_pair = kms_client.generate_data_key_pair(KeyId=master_key, KeyPairSpec='RSA_2048')

    master_key_id=key_pair["KeyId"].split("/")[1]
    key_id=str(uuid.uuid4())
    encrypted_private_key=base64.b64encode(key_pair["PrivateKeyCiphertextBlob"]).decode()
    public_key=base64.b64encode(key_pair["PublicKey"]).decode()

    if os.getenv("experimental_database_persistence"):
        cur.execute(
            f"INSERT INTO KEYS (master_key_id, key_id, key_purpose, key_type, encrypted_private_key, public_key, enabled, key_expiration_date_timestamp)  VALUES ('{master_key_id}', '{key_id}', 'SIGNATURE', 'ASYMMETRIC', '{encrypted_private_key}','{public_key}', true, 0)")
        conn.commit()
    else:
        table = dynamodb.Table(key_table_name)
        table.put_item(Item={
            "master_key_id": master_key_id,
            "key_id": key_id,
            "encrypted_private_key": encrypted_private_key,
            "public_key": public_key,
            "key_purpose": "SIGNATURE",
            "key_type": "ASYMMETRIC",
            "enabled": True
        })


if __name__ == '__main__':
    input_master_key = sys.argv[1]
    input_key_table_name = f'VAuthenticator_Signature_Keys{sys.argv[2]}'

    if os.getenv("experimental_database_persistence"):
        database_host=sys.argv[3]
        conn = psycopg2.connect(database="postgres",
                                host=database_host,
                                user="postgres",
                                password="postgres",
                                port="5432")
        cur = conn.cursor()

        store_key(input_key_table_name, input_master_key)

        cur.close()
        conn.close()
    else:
        store_key(input_key_table_name, input_master_key)

