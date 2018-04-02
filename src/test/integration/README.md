# HR API Integration Tests

This directory contains files that run integration tests against the HR API.

First, create a configuration.json file from configuration\_example.json.

Use this command to run the tests:
`python integration_tests.py -i /path/to/configuration.json`

### Docker

Use these commands to build and run the tests in a container. All you need installed is Docker.

```shell
$ docker build -t hr-api-integration-tests .
# Run the integration tests in Unix
$ docker run -v "$PWD"/configuration.json:/usr/src/app/configuration.json:ro hr-api-integration-tests
# Run the integration tests in Windows
$ docker run -v c:\path\to\configuration.json:/c:\usr\src\app\configuration.json:ro hr-api-integration-tests
```

Python Version: 3.6.2
