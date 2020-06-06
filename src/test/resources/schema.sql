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

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('client_id', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','A_FEDERATION');

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('federated_client_id1', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','ANOTHER_FEDERATION');
INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('federated_client_id2', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','ANOTHER_FEDERATION');

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('A_CLIENT_APPLICATION_ID', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri123', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri123', 'http://an_uri123','ANOTHER_FEDERATION');