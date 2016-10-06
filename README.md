# bash-swagger-codegen library

## Overview
This is a Bash client generator for REST services from their Swagger specification.

This generator uses [swagger-codegen](github.com/swagger-api/swagger-codegen).

## What's Swagger?
The goal of Swagger™ is to define a standard, language-agnostic interface to REST APIs which allows both humans and computers to discover and understand the capabilities of the service without access to source code, documentation, or through network traffic inspection. When properly defined via Swagger, a consumer can understand and interact with the remote service with a minimal amount of implementation logic. Similar to what interfaces have done for lower-level programming, Swagger removes the guesswork in calling the service.


Check out [OpenAPI-Spec](https://github.com/OAI/OpenAPI-Specification) for additional information about the Swagger project, including additional libraries with support for other languages and more. 

## Usage

### Generating Bash client for REST service

Get the sources:
```shell
$ git clone https://github.com/bkryza/bash-swagger-codegen
```

Build the generator jar:
```shell
$ mvn assembly:assembly -DdescriptorId=jar-with-dependencies
```

Generate the client:
```shell
$ java -cp target/bash-swagger-codegen-1.0.0-jar-with-dependencies.jar io.swagger.codegen.SwaggerCodegen generate -l bash -i http://petstore.swagger.io/v2/swagger.json -o output

$ chmod +x output/client.sh
```


### Using the Bash script

```shell

# Print the list of operations available on the service
$ ./client.sh --help

# Print the service description
$ ./client.sh --about

# Print detailed information about specific operation
$ ./client.sh addPet --help

# Call REST API operation
$ ./client.sh --host http://petstore.swagger.io addPet 



## TODO
[] Add checking if all required parameters are provided
[] Add option to specify default cURL options in codegen which will be passed to each command



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