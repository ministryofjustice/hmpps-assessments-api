#! /usr/bin/env bash

awslocal s3 mb s3://hmpps-assessments-s3
awslocal sns create-topic --name hmpps-assessments-topic
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-west-2:000000000000:hmpps-assessments-topic \
    --protocol http \
    --notification-endpoint http://arns-api:8080/sns
