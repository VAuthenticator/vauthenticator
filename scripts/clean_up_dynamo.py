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


def clean_account(account_table_name):
    clean_table_for_primary_key(dynamodb, account_table_name, "user_name")


def clean_roles(role_table_name):
    clean_table_for_primary_key(dynamodb, role_table_name, "role_name")


def clean_client_applications(client_applications_table_name):
    clean_table_for_primary_key(dynamodb, client_applications_table_name, "client_id")


if __name__ == '__main__':
    dynamodb = boto3.resource('dynamodb')
    input_role_table_name = sys.argv[1]
    input_account_table_name = sys.argv[2]
    input_client_applications_table_name = sys.argv[3]

    clean_roles(dynamodb, input_role_table_name)
    clean_account(dynamodb, input_account_table_name)
    clean_client_applications(dynamodb, input_client_applications_table_name)
