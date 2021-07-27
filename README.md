# PHP Route Extractor
A small application to extract @Route annotations from PHP Symfony code and generate a Swagger / OpenAPI specification after that

## Basic Usage

```shell
java -jar <JAR File> -s /path/to/mole-web/src -o ./output
```

## Extended Usage:

```shell
java -jar <JAR File> -s /path/to/mole-web/src -o ./output -j ../console.symfony.json
```