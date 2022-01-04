import boto3
import base64
import uuid
import sys

def store_key(table, key):
    key_pair = kms_client.generate_data_key_pair(KeyId=key, KeyPairSpec='RSA_2048')
    table.put_item(Item={
        "master_key_id": key_pair["KeyId"],
        "key_id": str(uuid.uuid4()),
        "private_key_ciphertext_blob": base64.b64encode(key_pair["PrivateKeyCiphertextBlob"]).decode(),
        "public_key": base64.b64encode(key_pair["PublicKey"]).decode(),
        "enabled": True
    })


if __name__ == '__main__':
    master_key = sys.argv[1]
    tableName = sys.argv[2]

    kms_client = boto3.client("kms")
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table(tableName)
    store_key(table, master_key)