#! /usr/bin/env bash

awslocal s3 mb s3://hmpps-assessments-s3
awslocal sns create-topic --name hmpps-assessments-topic
