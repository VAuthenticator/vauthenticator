import boto3
import csv
import sys

def clean_table_for_primary_key(table_name, key):
    table = dynamodb.Table(table_name)
    response = table.scan(ProjectionExpression=key)
    items = response.get('Items', [key])
    for item in items:
        table.delete_item(
            Key={
                key: item[key]
            }
        )

def clean_table_for_primary_key_and_sort_ley(table_name, partition_key, sort_key):
    table = dynamodb.Table(table_name)
    response = table.scan(ProjectionExpression=f"{partition_key},{sort_key}")
    items = response.get('Items', [])
    for item in items:
        table.delete_item(
            Key={
                partition_key: item[partition_key],
                sort_key: item[sort_key]
            }
        )


def clean_account(account_table_name, account_role_table_name):
    clean_table_for_primary_key(dynamodb, account_table_name, "user_name")
    clean_table_for_primary_key_and_sort_ley(dynamodb, "%s" % account_role_table_name, "user_name", "role_name")


def clean_roles(role_table_name):
    clean_table_for_primary_key(dynamodb, role_table_name, "role_name")


def clean_client_applications(client_applications_table_name):
    clean_table_for_primary_key(dynamodb, client_applications_table_name, "client_id")


if __name__ == '__main__':
    dynamodb = boto3.resource('dynamodb')
    input_role_table_name = sys.argv[1]
    input_account_table_name = sys.argv[2]
    input_account_role_table_name = sys.argv[3]
    input_client_applications_table_name = sys.argv[4]

    clean_roles(dynamodb, input_role_table_name)
    clean_account(dynamodb, input_account_table_name, input_account_role_table_name)
    clean_client_applications(dynamodb, input_client_applications_table_name)