#!/usr/bin/env bash


export PETSTORE_CLI=output/petstore-cli

msg="addPet without host"

result=$( bash $PETSTORE_CLI  -ac xml -ct json addPet id+=123321 name:=lucky status:=available )
if [[ ! $result =~  "Error: No hostname provided!!!" ]]; then
    echo "${msg}: FAILED"
    echo "Output: $result"
    exit 1
else
    echo "${msg}: PASSED"
fi

export PETSTORE_HOST=http://petstore.swagger.io

msg="addPet without content type"

result=$( bash $PETSTORE_CLI  -ac xml addPet id+=123321 name:=lucky status:=available )
if [[ ! $result =~  "Error: Request's content-type not specified!" ]]; then
    echo "${msg}: FAILED"
    echo "Output: $result"
    exit 1
else
    echo "${msg}: PASSED"
fi


msg="Test addPet from parameters"

result=$( bash $PETSTORE_CLI -ct json -ac xml addPet id+=123321 name:=lucky status:=available )
if [[ ! $result =~  "<id>123321</id>" ]]; then
    echo "${msg}: FAILED"
    echo "Output: $result"
    exit 1
else
    echo "${msg}: PASSED"
fi


msg="Test addPet from pipe"

result=$( echo "{\"id\": 37567, \"name\": \"lucky\", \"status\": \"available\"}" | bash $PETSTORE_CLI -ct json -ac xml addPet )
if [[ ! $result =~  "<id>37567</id>" ]]; then
    echo "${msg}: FAILED"
    echo "Output: $result"
    exit 1
else
    echo "${msg}: PASSED"
fi



echo "All tests passed."