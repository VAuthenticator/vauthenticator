# local-environment

Here there are all the needed scripts to orchestrate all the process to configure yor AWS account to test locally VAuthenticator and VAuthenticator Management UI.


# local host config

- add on your local hosts file the following configurations

    ```
    127.0.0.1   local.api.vauthenticator.com
    127.0.0.1   local.management.vauthenticator.com
    ```
- make sure that you have a clean installation 
  - docker-compose down  
  - docker-compose rm  
  - docker-compose up
- create an .env file like this:
  ````
  IS_PRODUCITON=False
  DYNAMO_DB_ENDPOINT=http://localhost:4566
  KMS_ENDPOINT=http://localhost:4566
  
  ACCOUNT_ID=000000000000
  AWS_ACCESS_KEY_ID=xxxx
  AWS_SECRET_ACCESS_KEY=xxxx
  AWS_REGION=xxxx
  
  TABLES_SUFFIX=_Local_Staging
  
  VAUTHENTICATOR_BUCKET=vauthenticator-local-dev-document-bucket
  VAUTHENTICATOR_MANAGEMENT_UI_BUCKET=vauthenticator-management-ui-local-dev-document-bucket
  MASTER_KEY=will be available on the aws console or in the terraform resource apply console log 
  ````
- run the setup.sh
  ```
  After that the setup.sh is executed in the AWS console on KMS section you can see the Key ID of your key. 
  It is the master key to insert in the configuration
  file configuration/vauthenticator.yml.
  ```
  
  - configure your app
    - Property name is: `key.master-key: ${MASTER_KEY}`
    - create the IAM key and set up the required environment variables like below
      ```
      AWS_ACCESS_KEY_ID=it is irrelevant
      AWS_SECRET_ACCESS_KEY=it is irrelevant
      AWS_REGION=could be whatever aws region you would like to configure.. in local stack all will be local
      ```
  
- run the init.sh: After that the init.sh is executed you will have configured.
  - default admin client application for M2M:
      - username: admin
      - password: secret 
  - default client application for configure the sso login for the admin ui:
      - username: vauthenticator-management-ui
      - password: secret 
  - default management ui client application 
    - link:  http://local.management.vauthenticator.com:8080/secure/admin/index
    - username: admin@email.com
    - password: secret


- to reset all the environment you can destroy your local compose environment
- please remember that the setup script will override the terraform.tf file in order to override aws endpoint keep in mind it  