#!/usr/bin/env bash

PETSTORE_HOST=http://petstore.swagger.io

result=$(bash output/client.sh -ct json -ac xml --host $PETSTORE_HOST \
         addPet \
            id+=123321 \
            name:=lucky \
            status:=available)

if [[ ! $result =~ "<id>123321</id>"  ]]; then
    echo "addPet failed!"
    echo "Output: $result"
    exit 1
fi

