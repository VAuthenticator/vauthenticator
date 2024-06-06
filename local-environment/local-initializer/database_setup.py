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

        cur.close()
        conn.close()
