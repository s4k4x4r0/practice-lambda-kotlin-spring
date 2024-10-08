#!/bin/bash

tfenv use

# Check if the environment variable is set
if [ -z "$TFSTATE_BUCKET" ]; then
  echo "Error: Environment variable TFSTATE_BUCKET is not set."
  exit 1
fi

# Run terraform init with the backend configuration
terraform init -migrate-state \
  -backend-config="bucket=${TFSTATE_BUCKET}" \
  -backend-config="key=practice-lambda-kotlin-spring.tfstate"