# Bash client Swagger codegen
[![Build Status](https://api.travis-ci.org/bkryza/bash-swagger-codegen.svg?branch=master)](https://travis-ci.org/bkryza/bash-swagger-codegen)

## Overview
This is a Bash client script generator for REST services from their Swagger™ specification. The generated script provides a wrapper layer over [cURL](https://curl.haxx.se).

This generator uses [swagger-codegen](github.com/swagger-api/swagger-codegen).

For more information about Swagger™ check out [OpenAPI-Spec](https://github.com/OAI/OpenAPI-Specification).

## Features
- Fully automatic generation of a working Bash script to access any Swagger REST service
- Generation of Bash completion script
- All valid cURL options can be used directly
- Preview of cURL commands to execute each operation using `--dry-run` option

## Usage

### Generating Bash client for REST service

Get the sources:
```shell
$ git clone https://github.com/bkryza/bash-swagger-codegen
```

Build the codegen:
```shell
$ mvn assembly:assembly -DdescriptorId=jar-with-dependencies
```

Define custom codegen properties in a Json file, e.g.:
```shell
{
  "processMarkdown": true,
  "curlOptions": "-sS --tlsv1.2",
  "scriptName": "petstore-cli",
  "generateBashCompletion": true
}
```

Generate the client:
```shell
$ java -cp target/bash-swagger-codegen-1.0.0.jar io.swagger.codegen.SwaggerCodegen generate -l bash -i http://petstore.swagger.io/v2/swagger.json -o output -c resources/example-config.json

$ chmod +x output/petstore-cli
```

Enjoy:
```shell
$ output/petstore-cli --help

Swagger Petstore command line client (API version 1.0.0)

Usage

$ petstore-cli [-h|--help] [-V|--version] [--about] [<curl-options>]
           --host <url> [--dry-run] <operation> [-h|--help] [<headers>]
           [<parameters>]

  - <curl-options> - any valid cURL options can be passed before <operation>
  - <headers> - HTTP headers can be passed in the form HEADER:VALUE
  - <parameters> - REST operation parameters can be passed in the following
                   forms:
                     * KEY=VALUE - path or query parameters
                     * KEY:=VALUE - body parameters which will be added to body
                                    Json as '{ \"KEY\": \"VALUE\" }'
                     * KEY+=VALUE - body parameters which will be added to body
                                    Json as '{ \"KEY\": VALUE }'

Authentication methods

    - Api-key (add 'api_key:ACCESS_KEY' after <operation>)
    - OAuth (url: http://petstore.swagger.io/oauth/dialog)
        Scopes:
          * write:pets - modify pets in your account
          * read:pets - read your pets

Operations

[pet]
  addPet             Add a new pet to the store
  deletePet          Deletes a pet
  findPetsByStatus   Finds Pets by status
  findPetsByTags     Finds Pets by tags
  getPetById         Find pet by ID
  updatePet          Update an existing pet
  updatePetWithForm  Updates a pet in the store with form data
  uploadFile         uploads an image

[store]
  deleteOrder   Delete purchase order by ID
  getInventory  Returns pet inventories by status
  getOrderById  Find purchase order by ID
  placeOrder    Place an order for a pet

[user]
  createUser                 Create user
  createUsersWithArrayInput  Creates list of users with given input array
  createUsersWithListInput   Creates list of users with given input array
  deleteUser                 Delete user
  getUserByName              Get user by user name
  loginUser                  Logs user into the system
  logoutUser                 Logs out current logged in user session
  updateUser                 Updated user

Options
  -h,--help                             Print this help
  -V,--version                          Print API version
  --about                               Print the information about service
  --host <url>                          Specify the host URL (e.g.
                                        'https://petstore.swagger.io')
  --dry-run                             Print out the cURL command without
                                        executing it
  -ac,--accept <mime-type>              Set the accept header in the request
  -ct,--content-type <mime-type>        Set the content-type header in
                                        the request
```

Client generator takes several specific configuration options:
* *processMarkdown* - [boolean] if set to `true`, all text (descriptions) in the Swagger specification will be treated as Markdown and converted to terminal formatting commands,
* *curlOptions* - [string] a list of default cURL options that will be added to each command
* *scriptName* - [string] the name of the target script, necessary when building Bash completion script
* *generateBashCompletion* - [boolean] if set to `true` the Bash completion script will be generated

These options can be specified in a Json file used when running the codegen using option `-c` (see [example](resources/example-config.json)).

### Using the generated Bash script

```shell
# Print the list of operations available on the service
$ output/petstore-cli --help

# Print the service description
$ output/petstore-cli --about

# Print detailed information about specific operation
$ output/petstore-cli addPet --help

# Call REST API operation
$ echo '{"id":891,"name":"lucky","status":"available"}' | output/petstore-cli --host http://petstore.swagger.io --content-type json addPet

{"id":891,"name":"lucky","photoUrls":[],"tags":[],"status":"available"}

# The above is equivalent to
$ output/petstore-cli --host http://petstore.swagger.io --content-type json --accept xml addPet id+=891 name:=lucky status:=available

<xml version="1.0" encoding="UTF-8" standalone="yes"?><Pet><id>891</id><name>lucky</name><photoUrls/><status>available</status><tags/></Pet>


# Preview the cURL command without actually executing it
# The above is equivalent to
$ output/petstore-cli --host http://petstore.swagger.io --content-type json --dry-run addPet id+=891 name:=lucky status:=available

curl -sS --tlsv1.2 -H 'Content-type: application/json' -X POST -d '{"name": "lucky", "status": "available", "id": 891}' "http://petstore.swagger.io/v2/pet"
```

## Installing generated Bash completion
The generated bash-completion script can be either directly loaded to the current Bash session using:

```shell
source output/petstore-cli.bash-completion
```

Alternatively, the script can be copied to the `/etc/bash-completion.d` (or on OSX with Homebrew to `/usr/local/etc/bash-completion.d`):

```shell
sudo cp /output/petstore-cli.bash-completion /etc/bash-completion.d/petstore-cli
```

### OS X
On OSX you might need to install bash-completion using Homebrew:
```shell
brew install bash-completion
```
and add the following to the `~/.bashrc`:

```shell
if [ -f $(brew --prefix)/etc/bash_completion ]; then
  . $(brew --prefix)/etc/bash_completion
fi
``` 

## TODO
- [ ] Add checking if all required parameters are provided
- [x] Add option to specify default cURL options in codegen which will be passed to each command
- [x] Add shell completion generation
- [ ] Add enum values for parameters shell completion
- [x] Add boolean values for parameters shell completion
- [ ] Wrap handling of errors returned by the service, using comments defined in the Swagger specification
- [x] Add abbreviated form support for standard headers (Accept, Content-type, X-Auth-Token, ...)
- [ ] Add proper checking for Bash version and cURL availability
- [ ] Improve `--help` and `--about` formatting
- [x] Add Zsh completion generation 
- [ ] Add support to bash 4.0-4.2 (currently must be >= 4.3)
- [x] Add environment variables for authentication and hostname
- [ ] Add manpage generation

## LICENSE
Copyright 2016 Bartosz Kryza <bkryza@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](./LICENSE)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.