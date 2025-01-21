#!/usr/bin/env bash

# S3
buckets=$(awslocal s3 ls)
echo $buckets | grep "hmpps-assessments-s3" || exit 1

# SNS
topics=$(awslocal sns list-topics)
echo $topics | grep "hmpps-assessments-topic" || exit 1
