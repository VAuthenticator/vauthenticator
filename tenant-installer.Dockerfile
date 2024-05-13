FROM amazonlinux:2023

RUN yum install -y yum-utils && \
    yum-config-manager --add-repo https://rpm.releases.hashicorp.com/AmazonLinux/hashicorp.repo && \
    yum -y install terraform && \
    yum update && yum install -y python3-pip && \
    yum install -y unzip

RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
     unzip awscliv2.zip && \
     ./aws/install --bin-dir /usr/local/bin --install-dir /usr/local/aws-cli --update

RUN aws --profile default configure set aws_access_key_id "xxx" && \
    aws --profile default configure set aws_secret_access_key "xxx" && \
    aws --profile default configure set region "eu-central-1"

ADD iac iac

WORKDIR local-environment/local-initializer

ADD local-environment/local-initializer .

ENTRYPOINT sh ./build.sh