# bash-swagger-codegen library

## Overview
This is a Bash client generator for REST services from their Swagger™ specification.

This generator uses [swagger-codegen](github.com/swagger-api/swagger-codegen).

For more information about Swagger™ check out [OpenAPI-Spec](https://github.com/OAI/OpenAPI-Specification).

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
$ java -cp target/bash-swagger-codegen-1.0.0.jar io.swagger.codegen.SwaggerCodegen generate -l bash -i http://petstore.swagger.io/v2/swagger.json -o output

$ chmod +x output/client.sh
```

Client generator takes several specific configuration options:
* *processMarkdown* - [boolean] if set to `true`, all text (descriptions) in the Swagger specification will be treated as Markdown and converted to terminal formatting commands,
* *curlOptions* - [string] a list of default cURL options that will be added to each command

These options can be specified in a Json file used when running the codegen, for example:
```json
{
  "processMarkdown": true,
  "curlOptions": "-ksS --tlsv1.2"
}

```
### Using the generated Bash script

```shell

# Print the list of operations available on the service
$ output/client.sh --help

# Print the service description
$ output/client.sh --about

# Print detailed information about specific operation
$ output/client.sh addPet --help

# Call REST API operation
$ echo '{"id":891,"name":"lucky","status":"available"}' | output/client.sh --host http://petstore.swagger.io addPet content-type:application/json

# The above is equivalent to
$ output/client.sh --host http://petstore.swagger.io addPet content-type:application/json id+=891 name:=lucky status:=available
```


## TODO
* [] Add checking if all required parameters are provided
* [] Add option to specify default cURL options in codegen which will be passed to each command
* [] Add shell completion generation
* [] Wrap handling of errors returned by the service, using comments defined in the Swagger specification
* [] Add abbreviate support for standard headers (Accept, Content-type, X-Auth-Token, ...)



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