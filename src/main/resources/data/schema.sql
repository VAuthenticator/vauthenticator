CREATE TABLE ROLE
(
    name        varchar(64)  not null PRIMARY KEY,
    description varchar(255) not null DEFAULT ''
);


CREATE TABLE ACCOUNT
(
    account_non_expired     boolean      not null default false,
    account_non_locked      boolean      not null default false,
    credentials_non_expired boolean      not null default false,
    enabled                 boolean      not null default false,

    username                varchar(255) not null primary key,
    password                varchar(255) not null,

    email                   varchar(255) not null unique,
    email_verified          boolean      not null default false,

    first_name              varchar(255) not null default '',
    last_name               varchar(255) not null default '',

    birth_date              date,
    phone                   varchar(30)           default '',
    locale                  varchar(10)           default 'en',
    mandatory_action        varchar(100) not null default 'NO_ACTION'
);

CREATE TABLE ACCOUNT_ROLE
(
    account_username varchar(255) not null,
    role_name        varchar(64)  not null,


    FOREIGN KEY (account_username) REFERENCES ACCOUNT(username) on delete cascade,
    FOREIGN KEY (role_name) REFERENCES ROLE (name) on delete cascade
);
