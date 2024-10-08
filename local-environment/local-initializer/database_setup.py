import bcrypt
import os
import psycopg2
import sys
import uuid
from dotenv import load_dotenv


def str2bool(v):
    return v.lower() in ("True")


load_dotenv(dotenv_path="env")

isProduction = str2bool(os.getenv("IS_PRODUCITON"))


def create_schema():
    with open("./schema.sql", "r") as file:
        cur.execute(file.read())
        conn.commit()


def store_account():
    password = str(uuid.uuid4()) if isProduction else "secret"
    encodedPassword = pass_encoded(password)
    print(f'default user password: {password}')

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
    cur.execute("INSERT INTO Role (name,description) VALUES ('ROLE_USER','Generic user role') ")
    cur.execute("INSERT INTO Role (name,description) VALUES ('VAUTHENTICATOR_ADMIN','VAuthenticator admin role') ")
    conn.commit()


def store_client_applications():
    client_id = str(uuid.uuid4()) if isProduction else "vauthenticator-management-ui"
    print(f'client id: {client_id}')

    client_secret = str(uuid.uuid4()) if isProduction else "secret"
    print(f'client secret: {client_secret}')
    print(f'client_id={client_id}&client_secret={client_secret}')

    scopes = set(
        ["openid", "profile", "email", "admin:reset-password", "admin:change-password", "admin:key-reader",
         "admin:key-editor",
         "admin:email-template-reader", "admin:email-template-writer"])

    if isProduction:
        scopes.add("mfa:always")

    serialized_scopes=','.join(scopes)
    cur.execute(
        f"INSERT INTO CLIENT_APPLICATION (client_app_id, secret,scopes,with_pkce,authorized_grant_types,web_server_redirect_uri,access_token_validity,refresh_token_validity,auto_approve,post_logout_redirect_uri,logout_uri) VALUES ('{client_id}','{pass_encoded(client_secret)}','false','{serialized_scopes}','AUTHORIZATION_CODE,REFRESH_TOKEN','http://local.management.vauthenticator.com:8080/login/oauth2/code/client','180','3600','true','http://local.management.vauthenticator.com:8080/secure/admin/index','http://local.management.vauthenticator.com:8080/logout')"
    )

    scopes.add("mfa:always")
    serialized_scopes=','.join(scopes)
    serialized_client_id=f"mfa-{client_id}"
    cur.execute(
        f"INSERT INTO CLIENT_APPLICATION (client_app_id, secret,scopes,with_pkce,authorized_grant_types,web_server_redirect_uri,access_token_validity,refresh_token_validity,auto_approve,post_logout_redirect_uri,logout_uri) VALUES ('{serialized_client_id}','{pass_encoded(client_secret)}','false','{serialized_scopes}','AUTHORIZATION_CODE,REFRESH_TOKEN','http://local.management.vauthenticator.com:8080/login/oauth2/code/client','180','3600','true','http://local.management.vauthenticator.com:8080/secure/admin/index','http://local.management.vauthenticator.com:8080/logout')"
    )
    conn.commit()


def pass_encoded(password):
    encode = str.encode(password)
    return bcrypt.hashpw(encode, bcrypt.gensalt(12)).decode()


if __name__ == '__main__':
    user_name = sys.argv[1]
    database_host = sys.argv[2]
    if os.getenv("experimental_database_persistence"):
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

        cur.close()
        conn.close()
