#!/bin/bash

# Check if the environment variable is set
if [ -z "$TFSTATE_BUCKET" ]; then
  echo "Error: Environment variable TF_VAR_tfstate_bucket is not set."
  exit 1
fi

# Run terraform init with the backend configuration
terraform init -migrate-state \
  -backend-config="bucket=${TFSTATE_BUCKET}" \
  -backend-config="key=practice-lambda-kotlin-spring.tfstate"