import boto3
import sys

dynamodb = boto3.resource('dynamodb')

def clean_table_for_primary_key(table, key):
    response = table.scan(ProjectionExpression=key)
    items = response.get('Items', [key])
    for item in items:
        table.delete_item(
            Key={
                key: item[key]
            }
        )

def clean_table_for_primary_key_and_sort_key(table, partition_key, sort_key):
    response = table.scan(ProjectionExpression=f"{partition_key}, {sort_key}")
    items = response.get('Items', [])
    for item in items:
        table.delete_item(
            Key={
                partition_key: item[partition_key],
                sort_key: item[sort_key]
            }
        )


def clean_account(account_table_name, account_role_table_name):
    clean_table_for_primary_key(dynamodb.Table(account_table_name), "user_name")
    clean_table_for_primary_key_and_sort_key(dynamodb.Table(account_role_table_name), "user_name", "role_name")


def clean_roles(role_table_name):
    clean_table_for_primary_key(dynamodb.Table(role_table_name), "role_name")


def clean_client_applications(client_applications_table_name):
    clean_table_for_primary_key(dynamodb.Table(client_applications_table_name), "client_id")


def clean_key(key_table_name):
    clean_table_for_primary_key_and_sort_key(dynamodb.Table(key_table_name), "master_key_id", "key_id")


if __name__ == '__main__':
    input_role_table_name = sys.argv[1]
    input_account_table_name = sys.argv[2]
    input_account_role_table_name = sys.argv[3]
    input_client_applications_table_name = sys.argv[4]
    input_key_table_name = sys.argv[5]

    clean_roles(input_role_table_name)
    clean_account(input_account_table_name, input_account_role_table_name)
    clean_client_applications(input_client_applications_table_name)
    clean_key(input_key_table_name)