import boto3
import csv
import sys

dynamodb = boto3.resource('dynamodb')


def load_account(account_table_name, account_role_table_name):
    table = dynamodb.Table(account_table_name)
    with open(f'{env}/account.csv', mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        for row in csv_reader:
            table.put_item(Item={
                "user_name": row["username"],
                "password": row["password"],
                "firstName": row["first_name"],
                "lastName": row["last_name"],
                "email": row["email"],
                "emailVerified": bool(row["email_verified"]),
                "enabled": bool(row["enabled"]),
                "credentialsNonExpired": bool(row["credentials_non_expired"]),
                "accountNonLocked": bool(row["account_non_locked"]),
                "accountNonExpired": bool(row["account_non_expired"])
            })

    table = dynamodb.Table(account_role_table_name)
    with open(f'{env}/account_role.csv', mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        for row in csv_reader:
            table.put_item(Item={
                "role_name": row["role"],
                "user_name": row["username"]
            })


def load_roles(role_table_name):
    table = dynamodb.Table(role_table_name)

    with open(f'{env}/role.csv', mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        for row in csv_reader:
            table.put_item(Item={"role_name": row["name"], "description": row["description"]})


def load_client_applications(client_application_table_name):
    table = dynamodb.Table(client_application_table_name)
    with open(f'{env}/client_application.csv', mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        for row in csv_reader:
            table.put_item(Item={
                "client_id": row["client_id"],
                "resource_ids": row["resource_ids"],
                "client_secret": row["client_secret"],
                "scopes": set(row["scope"].split(",")),
                "authorized_grant_types": set(row["authorized_grant_types"].split(",")),
                "web_server_redirect_uri": row["web_server_redirect_uri"],
                "authorities": row["authorities"],
                "access_token_validity": int(row["access_token_validity"]),
                "refresh_token_validity": int(row["refresh_token_validity"]),
                "auto_approve": bool(row["autoapprove"]),
                "post_logout_redirect_uris": row["post_logout_redirect_uris"],
                "logout_uris": row["logout_uris"],
            })


if __name__ == '__main__':
    env = sys.argv[1]

    input_role_table_name = sys.argv[2]
    input_account_table_name = sys.argv[3]
    input_account_role_table_name = sys.argv[4]
    input_client_applications_table_name = sys.argv[5]

    load_roles(input_role_table_name)
    load_account(input_account_table_name, input_account_role_table_name)
    load_client_applications(input_client_applications_table_name)
