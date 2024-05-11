FROM amazonlinux:2023

RUN yum install -y yum-utils && \
    yum-config-manager --add-repo https://rpm.releases.hashicorp.com/AmazonLinux/hashicorp.repo && \
    yum -y install terraform

ADD . .

WORKDIR local-environment

ENTRYPOINT sh ./remote-build.sh