CREATE TABLE ROLE
(
    name        VARCHAR2(64)  not null PRIMARY KEY,
    description VARCHAR2(255) not null DEFAULT ''
);


CREATE TABLE ACCOUNT
(
    account_non_expired     boolean      not null default false,
    account_non_locked      boolean      not null default false,
    credentials_non_expired boolean      not null default false,
    enabled                 boolean      not null default false,

    username                VARCHAR2(255) not null primary key,
    password                VARCHAR2(255) not null,

    email                   VARCHAR2(255) not null unique,
    email_verified          boolean      not null default false,

    first_name              VARCHAR2(255) not null default '',
    last_name               VARCHAR2(255) not null default '',

    birth_date              date,
    phone                   VARCHAR2(30)           default '',
    locale                  VARCHAR2(10)           default 'en',
    mandatory_action        VARCHAR2(100) not null default 'NO_ACTION'
);

CREATE TABLE ACCOUNT_ROLE
(
    account_username VARCHAR2(255) not null,
    role_name        VARCHAR2(64)  not null,


    FOREIGN KEY (account_username) REFERENCES ACCOUNT(username) on delete cascade,
    FOREIGN KEY (role_name) REFERENCES ROLE (name) on delete cascade
);
