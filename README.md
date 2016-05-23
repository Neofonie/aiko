Neofonie Aiko
============

[![Build Status](https://snap-ci.com/Neofonie/aiko/branch/master/build_image)](https://snap-ci.com/Neofonie/aiko/branch/master)
[![codecov.io](https://codecov.io/github/Neofonie/aiko/coverage.svg?branch=master)](https://codecov.io/github/Neofonie/aiko?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/dd27be975eaf4b70843b915ed0a91639)](https://www.codacy.com/app/muecke-jo/aiko)
[![Download](https://api.bintray.com/packages/neofonie/maven/aiko/images/download.svg) ](https://bintray.com/neofonie/maven/aiko/_latestVersion)

## Test your REST interface ##
First of all: code and tests are separated from each other, so the person writing tests does not need to know how to code.

### So where are the tests? ###
You can find them in java/src/test/resources looking like:

    groups:
      - name: user tests
        domain: http://localhost:8080
        tests:
        - name: create new user
          request:
            method: POST
            uri: /user
            body: '@testdata.json'
            headers:
              Accept: 'application/json'
              Content-Type: 'application/json'
          response:
            headers:
              Content-Type: 'application/json; charset=utf-8'
            status: 201
            body: '{
                     "id": 1,
                     "name": "Leanne Graham",
                     "username": "Bret",
                     "email": "Sincere@april.biz"
                   }'

# Examples

## Testing a binary file

You can upload a binary by referencing the filename in the `body` tag. When
no leading slash is found, the file is referenced from the current directory.

      - name: upload image file
        domain:  http://localhost:8080
        tests:         
        - name: upload file
          request:
            method: PUT
            uri: /resource/test.jpg
            headers:
              Content-Length: 63058
              Content-Type: image/jpeg
            body: '@test.jpg'
          response:
            status: 201
            headers:
              Etag: a12bae32eb58b25d6cbdb5588d582741
              Location: /resource/test.jpg

You test if the response matches a binary by specifying a file in the `body`
tag.

      - name: download file
        request:
          method: GET
          uri: /resource/test.jpg
        response:
          status: 200
          headers:
            Content-Length: 63058
            Content-Type: image/jpeg
            Etag: a12bae32eb58b25d6cbdb5588d582741        
          body: '@test.jpg'

The test will only be valid when both binary streams are identical.

## Testing an empty body

If you want to test if the body is really empty, you can specify:

    body: ''

## Run example tests against jsonplaceholder

    $  java -jar target/aiko-x.y-SNAPSHOT.jar -f src/test/resources/jsonplaceholder-tests.yml


### What else do I need to know? ###
* After the first error tests in containing group exit with message and next group is tested.
* The order of json elements in each response is unconsidered.
* The request-part constructs your request (multiple - for example - headers are possible).
* The response-part is compared to the real response and differences are shown.
* The request or response body can be referenced in a file (put '@' before path).
* There is a configurable retry mechanism rechecking per testcase.
* There already are a few test configurations inside java/src/test/resources to show what is possible.

Have fun! :)
