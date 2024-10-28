import bcrypt
import boto3
import os
import sys
import uuid
import psycopg2
import base64
from dotenv import load_dotenv


def str2bool(v):
    return v.lower() in ("True")


load_dotenv(dotenv_path="env")

isProduction = str2bool(os.getenv("IS_PRODUCITON"))
print(isProduction)


def dynamodbClient():
    dynamodb_endpoint = os.getenv('DYNAMO_DB_ENDPOINT')
    if dynamodb_endpoint is None:
        client = boto3.resource('dynamodb')
    else:
        client = boto3.resource('dynamodb', endpoint_url=dynamodb_endpoint)
    return client


def kmsClient():
    kms_endpoint = os.getenv('KMS_ENDPOINT')
    if kms_endpoint is None:
        client = boto3.client("kms")
    else:
        client = boto3.client('kms', endpoint_url=kms_endpoint)
    return client


dynamodb = dynamodbClient()
kms_client = kmsClient()


def create_schema():
    with open("./schema.sql", "r") as file:
        cur.execute(file.read())
        conn.commit()


def store_account():
    password = str(uuid.uuid4()) if isProduction else "secret"
    print(f'default user password: {password}')
    encodedPassword = pass_encoded(password)
    table = dynamodb.Table(f"VAuthenticator_Account{table_suffix}")
    table.put_item(Item={
        "user_name": user_name,
        "password": encodedPassword,
        "phone": "",
        "birthDate": "",
        "locale": "en",
        "firstName": "Admin",
        "lastName": "",
        "email": user_name,
        "emailVerified": True,
        "enabled": True,
        "credentialsNonExpired": True,
        "accountNonLocked": True,
        "accountNonExpired": True,
        "mandatory_action": "NO_ACTION",
        "authorities": set(["ROLE_USER", "VAUTHENTICATOR_ADMIN"])
    })
    cur.execute(f"""
    INSERT INTO Account (
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    enabled,
    username,
    password,
    email,
    email_verified,
    first_name,
    last_name,
    birth_date,
    phone,
    locale,
    mandatory_action
    ) VALUES (True,True,True,True,
    '{user_name}','{encodedPassword}','{user_name}',True,'Admin','',null,'','en','NO_ACTION')
    """)
    cur.execute(f"INSERT INTO ACCOUNT_ROLE (account_username, role_name) VALUES ('{user_name}','ROLE_USER')")
    cur.execute(f"INSERT INTO ACCOUNT_ROLE (account_username, role_name) VALUES ('{user_name}','VAUTHENTICATOR_ADMIN')")
    conn.commit()


def store_roles():
    table = dynamodb.Table(f"VAuthenticator_Role{table_suffix}")
    table.put_item(Item={"role_name": "ROLE_USER", "description": "Generic user role"})
    table.put_item(Item={"role_name": "VAUTHENTICATOR_ADMIN", "description": "VAuthenticator admin role"})

    cur.execute("INSERT INTO Role (name,description) VALUES ('ROLE_USER','Generic user role') ")
    cur.execute("INSERT INTO Role (name,description) VALUES ('VAUTHENTICATOR_ADMIN','VAuthenticator admin role') ")
    conn.commit()


def store_sso_client_applications():
    client_id = str(uuid.uuid4()) if isProduction else "vauthenticator-management-ui"
    print(f'client id: {client_id}')

    client_secret = str(uuid.uuid4()) if isProduction else "secret"
    print(f'client secret: {client_secret}')
    print(f'client_id={client_id}&client_secret={client_secret}')

    table = dynamodb.Table(f"VAuthenticator_ClientApplication{table_suffix}")
    scopes = set(
        ["openid", "profile", "email", "admin:reset-password", "admin:change-password", "admin:key-reader",
         "admin:key-editor",
         "admin:email-template-reader", "admin:email-template-writer"])

    if isProduction:
        scopes.add("mfa:always")

    table.put_item(Item={
        "client_id": client_id,
        "client_secret": pass_encoded(client_secret),
        "with_pkce": False,
        "scopes": scopes,
        "authorized_grant_types": set(["AUTHORIZATION_CODE", "REFRESH_TOKEN"]),
        "web_server_redirect_uri": "http://local.management.vauthenticator.com:8080/login/oauth2/code/client",
        "access_token_validity": 180,
        "refresh_token_validity": 3600,
        "auto_approve": True,
        "post_logout_redirect_uris": "http://local.management.vauthenticator.com:8080/secure/admin/index",
        "logout_uris": "http://local.management.vauthenticator.com:8080/logout",
    })
    serialized_scopes = ','.join(scopes)
    cur.execute(
        f"INSERT INTO CLIENT_APPLICATION (client_app_id, secret,scopes,with_pkce,authorized_grant_types,web_server_redirect_uri,access_token_validity,refresh_token_validity,auto_approve,post_logout_redirect_uri,logout_uri) VALUES ('{client_id}','{pass_encoded(client_secret)}', '{serialized_scopes}',false,'AUTHORIZATION_CODE,REFRESH_TOKEN','http://local.management.vauthenticator.com:8080/login/oauth2/code/client','180','3600','true','http://local.management.vauthenticator.com:8080/secure/admin/index','http://local.management.vauthenticator.com:8080/logout')"
    )

    serialized_client_id = f"mfa-{client_id}"

    scopes.add("mfa:always")
    table.put_item(Item={
        "client_id": f"mfa-{client_id}",
        "client_secret": pass_encoded(client_secret),
        "with_pkce": False,
        "scopes": scopes,
        "authorized_grant_types": set(["AUTHORIZATION_CODE", "REFRESH_TOKEN"]),
        "web_server_redirect_uri": "http://local.management.vauthenticator.com:8080/login/oauth2/code/client",
        "access_token_validity": 180,
        "refresh_token_validity": 3600,
        "auto_approve": True,
        "post_logout_redirect_uris": "http://local.management.vauthenticator.com:8080/secure/admin/index",
        "logout_uris": "http://local.management.vauthenticator.com:8080/logout",
    })
    serialized_scopes = ','.join(scopes)
    cur.execute(
        f"INSERT INTO CLIENT_APPLICATION (client_app_id, secret,scopes,with_pkce,authorized_grant_types,web_server_redirect_uri,access_token_validity,refresh_token_validity,auto_approve,post_logout_redirect_uri,logout_uri) VALUES ('{serialized_client_id}','{pass_encoded(client_secret)}','{serialized_scopes}',false,'AUTHORIZATION_CODE,REFRESH_TOKEN','http://local.management.vauthenticator.com:8080/login/oauth2/code/client','180','3600','true','http://local.management.vauthenticator.com:8080/secure/admin/index','http://local.management.vauthenticator.com:8080/logout')"
    )
    conn.commit()


def store_client_applications():
    client_id = str(uuid.uuid4()) if isProduction else "admin"
    print(f'client id: {client_id}')

    client_secret = str(uuid.uuid4()) if isProduction else "secret"
    print(f'client secret: {client_secret}')
    print(f'client_id={client_id}&client_secret={client_secret}')

    table = dynamodb.Table(f"VAuthenticator_ClientApplication{table_suffix}")
    scopes = set(
        ["openid", "profile", "email", "admin:signup", "admin:welcome", "admin:email-verify", "admin:reset-password",
         "admin:change-password", "admin:key-reader", "admin:key-editor", "admin:client-app-reader",
         "admin:client-app-writer", "admin:client-app-eraser", "admin:email-template-reader",
         "admin:email-template-writer", "mfa:always"])
    table.put_item(Item={
        "client_id": client_id,
        "client_secret": pass_encoded(client_secret),
        "with_pkce": False,
        "scopes": scopes,
        "authorized_grant_types": set(["CLIENT_CREDENTIALS"]),
        "web_server_redirect_uri": "",
        "access_token_validity": 180,
        "refresh_token_validity": 3600,
        "auto_approve": True,
        "post_logout_redirect_uris": "",
        "logout_uris": "",
    })
    serialized_scopes = ','.join(scopes)
    cur.execute(
        f"INSERT INTO CLIENT_APPLICATION (client_app_id, secret,scopes,with_pkce,authorized_grant_types,web_server_redirect_uri,access_token_validity,refresh_token_validity,auto_approve,post_logout_redirect_uri,logout_uri) VALUES ('{client_id}','{pass_encoded(client_secret)}','{serialized_scopes}',false,'CLIENT_CREDENTIALS','http://local.management.vauthenticator.com:8080/login/oauth2/code/client','180','3600','true','http://local.management.vauthenticator.com:8080/secure/admin/index','http://local.management.vauthenticator.com:8080/logout')"
    )
    conn.commit()


def store_key():
    key_table_name=f'VAuthenticator_Signature_Keys{table_suffix}'
    key_pair = kms_client.generate_data_key_pair(KeyId=input_master_key, KeyPairSpec='RSA_2048')

    master_key_id = key_pair["KeyId"].split("/")[1]
    key_id = str(uuid.uuid4())
    encrypted_private_key = base64.b64encode(key_pair["PrivateKeyCiphertextBlob"]).decode()
    public_key = base64.b64encode(key_pair["PublicKey"]).decode()

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

    cur.execute(
        f"INSERT INTO KEYS (master_key_id, key_id, key_purpose, key_type, encrypted_private_key, public_key, enabled, key_expiration_date_timestamp)  VALUES ('{master_key_id}', '{key_id}', 'SIGNATURE', 'ASYMMETRIC', '{encrypted_private_key}','{public_key}', true, 0)")
    conn.commit()


def pass_encoded(password):
    encode = str.encode(password)
    return bcrypt.hashpw(encode, bcrypt.gensalt(12)).decode()


if __name__ == '__main__':
    user_name = sys.argv[1]
    table_suffix = sys.argv[2]
    input_master_key = sys.argv[3]
    database_host=sys.argv[4]

    conn = psycopg2.connect(database="postgres",
                            host=database_host,
                            user="postgres",
                            password="postgres",
                            port="5432")
    cur = conn.cursor()
    create_schema()

    store_roles()
    store_account()
    store_client_applications()
    store_sso_client_applications()
    store_key()

    cur.close()
    conn.close()
