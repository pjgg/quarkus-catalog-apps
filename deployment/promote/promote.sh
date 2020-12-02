#!/bin/bash
# usage:
# ./promote.sh v1.0.2 https://api.ocp46-catalog.dynamic.quarkus:6443 user password

RELEASE=$1
OC_SERVER=$2
OC_USER=$3
OC_PWD=$4

# Login
oc login -u=$OC_USER -p=$OC_PWD -s=$OC_SERVER --insecure-skip-tls-verify=true

# Promote
cd /deployment/helmfiles/dev
export CATALOG_API_TAG=$RELEASE
export CATALOG_STORAGE_TAG=$RELEASE
export CATALOG_ENRICHER_TAG=$RELEASE
helmfile -f helmfile.yaml sync