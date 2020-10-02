create table oauth_client_details
(
    client_id                 varchar(256) not null
        constraint oauth_client_details_pkey
            primary key,
    resource_ids              varchar(256),
    client_secret             varchar(256),
    scope                     varchar(256),
    authorized_grant_types    varchar(256),
    web_server_redirect_uri   text,
    authorities               varchar(256),
    access_token_validity     integer,
    refresh_token_validity    integer,
    additional_information    varchar(4096),
    autoapprove               varchar(256),
    post_logout_redirect_uris text,
    logout_uris               text,
    federation                varchar(256)
);



create table account
(
    id  SERIAL primary key,

    account_non_expired boolean NOT NULL,
    account_non_locked boolean NOT NULL,
    credentials_non_expired boolean NOT NULL,
    enabled boolean NOT NULL,

    username varchar(256) UNIQUE not null,
    password varchar(256) not null,

    email varchar(256) UNIQUE not null,
    email_verified boolean not null,

    first_name varchar(256) not null,
    last_name varchar(256) not null
);

create table role
(
    id  SERIAL primary key,

    name varchar(256) UNIQUE not null,
    description varchar(256)
);

create table ACCOUNT_ROLE
(
    ROLE  varchar(256) NOT NULL ,
    USERNAME varchar(256) NOT NULL ,

    PRIMARY KEY (ROLE, USERNAME),

    CONSTRAINT FK_ACCOUNT FOREIGN KEY (USERNAME)
        REFERENCES ACCOUNT(USERNAME),

    CONSTRAINT FK_ROLE FOREIGN KEY (ROLE)
        REFERENCES ROLE(NAME)
);