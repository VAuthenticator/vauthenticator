# Local Tenant Installer

VAuthenticator needs of a lot of infrastructure to run: dynamo, kms, redis and an email server. It can be a barrier to adopt VAuthenticator.
That's way we provide one docker image with all the needed to install a new tenant with an admin account for the management ui together with a  
client app for admin M2M purpose.

- default admin client application for M2M:
    - username: admin
    - password: secret
- default client application for configure the sso login for the admin ui:
    - username: vauthenticator-management-ui
    - password: secret
- default admin user
    - link: http://local.management.vauthenticator.com:8080/secure/admin/index
    - username: admin@email.com
    - password: secret

In order to have all the needed infrastructure you can avail on the [docker-compose.yml](..%2Fdocker-compose.yml)`, while
it is possible to instantiate a container to install a new tenant usable for local development using the command: 

```shell
docker run --pull=always --add-host=host.docker.internal:host-gateway -it mrflick72/vauthenticator-local-tenant-installer:latest
``` 

if you need a specific branch docker image you can use the following command
```shell
docker run --pull=always --add-host=host.docker.internal:host-gateway -it mrflick72/vauthenticator-local-tenant-installer:$(git rev-parse --abbrev-ref HEAD)
``` 


# local host config

add on your local hosts file the following configurations

    ```
    127.0.0.1   local.api.vauthenticator.com
    127.0.0.1   local.management.vauthenticator.com
    ```
# local application configuration

in order to configure your app to run from your preferred ide you can start your app with the option --spring.config.additional-location=[application.yml](..%2Fapplication.yml) 
and key.master-key=`MASTER_KEY`

p.s. `MASTER_KEY` is shown in the std out of the mrflick72/vauthenticator-local-tenant-installer:tag execution like below:

```shell
....

MASTER_KEY: c3415e13-7d3b-4807-88a1-5e7009c94668
TABLES_SUFFIX: _Local_Staging
KMS_ENDPOINT: http://host.docker.internal:4566
DYNAMO_DB_ENDPOINT: http://host.docker.internal:4566
kms_endpoint http://host.docker.internal:4566
False
default user password: secret
client id: admin
client secret: secret
client_id=admin&client_secret=secret
client id: vauthenticator-management-ui
client secret: secret
client_id=vauthenticator-management-ui&client_secret=secret
Local User IAM VAuthenticator AccessKeyId: LKIAQAAAAAAABPAIE4RX
Local User IAM VAuthenticator SecretAccessKey: U2lSO7CfwtxQJdAyoP15ccz9y4PEFtTm/bK+8SKw

```

### ui and mail template local environment
In order to make simple the ui assets build for local development take in consideration to enable the following spring configuration properties:

```yaml
  document:
    engine: file-system
    fs-base-path: dist
```

in order to be sure to have the asset files in the correct path execute this script:

```shell
cd ..
rm -rf dist

mkdir -p dist/static-asset/content/asset/
mkdir -p dist/mail/templates

cd src/main/frontend
npm install
npm run-script build

cd dist/asset

cp * ../../../../../dist/static-asset/content/asset/

cd ../../../../../communication/default/mail 
cp *  ../../../dist/mail/templates
```

## Installation in a NON AWS Environment 

Postgres and plain java key management is an available option

In order to activate aws native service usage like KMS, DynamoDB and so on please use the spring profile '''aws''' default otherwise