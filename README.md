# Vertx GraphQL Example

This is an example application to exercise graphql-java-async as well as graphql schema apigen.


## System Architecture 

```text
                    .---------.       .-----------.
  POST /graphql --> | GraphQL |       | Customer  |
                    | Service | ----> | Service   |
                    '---------'   |   '-----------'
                                  |   .-----------.   
                                  |   | Vehicle   |
                                  |-> | Service   |
                                  |   '-----------'
                                  |   .-----------.
                                  |   | Rental    |
                                  '-> | Service   |
                                      '-----------'
```


## Before you start

```graphql-java-async``` is not out yet. In order to build this project you need to:

 1. ```graphql-java``` - Checkout and build Dmitry's [async branch](https://github.com/dminkovsky/graphql-java/tree/async)
 1. ```graphql-apigen``` - Checkout and build the [async branch](https://github.com/bmsantos/graphql-apigen/tree/async) of my fork of [Distelli/graphql-apigen](https://github.com/bmsantos/graphql-apigen/tree/async)


## Build:

After building the async branches of both graphql-java and graphql-apigen do:

```sh
mvn clean package
```


## Execute:

```sh
./docker/run.sh
```


## Test

The graphql-service exposes a POST endpoint. You can use CURL but it is recommended to use [Graphiql App](https://github.com/skevy/graphiql-app).

Sample queries to use on a POST to http://localhost:8080/graphql.


### Querying for a single rental entry:
```graphql
{
  rental(id: 1) {
    id
    customer {
      id
      name
      address
      city
      state
      country
      contact {
        phone
        type
      }
    }
    vehicle {
      id
      brand
      model
      type
      year
      mileage
      extras
    }
  }
}
```


### Querying for all active rentals:
```graphql
{
  rentals {
    id
    customer {
      id
      name
      address
      city
      state
      country
      contact {
        phone
        type
      }
    }
    vehicle {
      id
      brand
      model
      type
      year
      mileage
      extras
    }
  }
}
```


### Example using CURL:
```bash
curl -k -X POST -d '{ "operationName": null, "query": "{ rentals { customer { name } vehicle { brand model } } }", "variables": "{}" }' http://localhost:8080/graphql
```
