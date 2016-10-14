#!/usr/bin/env bats


export PETSTORE_CLI="output/petstore-cli"

export PETSTORE_HOST="http://petstore.swagger.io"


@test "addPet without host" {
    unset PETSTORE_HOST
    run bash $PETSTORE_CLI -ac xml -ct json \
        addPet id+=123321 name:=lucky status:=available
    [[ "$output" =~ "Error: No hostname provided!!!" ]]
}


@test "addPet without content type" {
    run bash $PETSTORE_CLI  -ac xml --host $PETSTORE_HOST \
        addPet id+=123321 name:=lucky status:=available
    [[ "$output" =~ "Error: Request's content-type not specified!" ]]
}


@test "addPet from parameters" {
    run bash $PETSTORE_CLI -ct json -ac xml \
        addPet id+=123321 name:=lucky status:=available
    [[ "$output" =~ "<id>123321</id>" ]]
}


@test "addPet from pipe" {
    run bash \
      -c "echo '{\"id\": 37567, \"name\": \"lucky\", \"status\": \"available\"}' | \
            bash $PETSTORE_CLI -ct json -ac xml addPet"
    [[ "$output" =~ "<id>37567</id>" ]]
}
