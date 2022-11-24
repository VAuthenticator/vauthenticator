#!/bin/bash

aws s3api create-bucket --bucket document-bucket --endpoint http://localhost:4566 --region us-east-1

aws s3 cp  index.html s3://document-bucket/mail/templates/welcome.html --endpoint http://localhost:4566 --region us-east-1