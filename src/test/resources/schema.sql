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
    id  SERIAL primary key,
    ROLE  varchar(256) NOT NULL ,
    USERNAME varchar(256) NOT NULL ,

    CONSTRAINT FK_ACCOUNT FOREIGN KEY (USERNAME)
        REFERENCES ACCOUNT(USERNAME),

    CONSTRAINT FK_ROLE FOREIGN KEY (ROLE)
        REFERENCES ROLE(NAME)
);

INSERT INTO role (name, description) VALUES ('a_role','A_ROLE');

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('client_id', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','A_FEDERATION');

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('federated_client_id1', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','ANOTHER_FEDERATION');
INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('federated_client_id2', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','ANOTHER_FEDERATION');

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('A_CLIENT_APPLICATION_ID', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri123','ANOTHER_FEDERATION');

