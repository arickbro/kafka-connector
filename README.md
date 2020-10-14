# Kafka Connect Quickstart
This is an example project to play around with [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect) 
and to deploy a Kafka Connect source and sink connector from a Maven project. This quickstart example uses the following 
versions:
- Confluent Platform 6.0.0 
- Kafka 2.6
- Java 11

## Builds
- 

## Getting Started
### Build and Startup the Environment
To use the custom sink and source connectors we have to build them first with Maven.

```
mvn clean package
```

Now we can build and start the Docker containers. 

```
docker-compose up --build
```

This will start the following Docker containers:
- `zookeeper` => Apache Zookeeper (`confluentinc/cp-zookeeper`)
- `broker` => Apache Kafka (`confluentinc/cp-kafka`)
- `schema-registry`=> Schema Registry (`confluentinc/cp-schema-registry`)
- `connect`=> Kafka Connect. This services uses a [custom Docker image](Dockerfile) which is based on `confluentinc/cp-kafka-connect-base`.
- `kafdrop`=> Kafdrop – Kafka Web UI  (`obsidiandynamics/kafdrop`)
- `connect-ui` => Kafka Connect UI from Lenses.io (`landoop/kafka-connect-ui`)

When all containers are started you can access different services like 
- **Kafka Connect Rest API** => http://localhost:8083/
- **Kafdrop** => http://localhost:8082/
- **Schema Registry** => http://localhost:8081/
- **kafka-connect-ui** from Lenses.io  => http://localhost:8000/


By default, Apache Avro convertor will be used when nothing else is set for value or key convertor in the connector settings. 
If you want to change the default settings just adapt the [docker-compose.yml](docker-compose.yml ) file for the Kafka Connect service.

```
environment:
  CONNECT_KEY_CONVERTER: io.confluent.connect.avro.AvroConverter
  CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL:  http://schema-registry:8081

  CONNECT_VALUE_CONVERTER: io.confluent.connect.avro.AvroConverter
  CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL: http://schema-registry:8081
```


### Let’s Deploy Some Connectors
First we have to check if Kafka Connect is available.
```
curl http://localhost:8083/
```

When Kafka Connect is up and running you should see a response like this.

```json
{
  "version": "6.0.0-ccs",
  "commit": "17b744c31e00868b",
  "kafka_cluster_id": "nj5o6tIHQIawd0muagtQmQ"
}
```


In the [config](config) directory are the configuration files for the custom source and sink connector. 
- [LogSinkConnector](src/main/java/ch/yax/connect/quickstart/sink)
- [RandomSourceConnector](src/main/java/ch/yax/connect/quickstart/source)


This will install the `RandomSourceConnector` [(random-source.json)](config/random-source.json) 
which publishes random data in the Kafka topic `random-data`.

```
curl -X POST http://localhost:8083/connectors  \
    -H "Content-Type: application/json" \
    --data @config/random-source.json
```


With the following command we install the `LogSinkConnector` [(log-sink.json)](config/log-sink.json) 
which will log the data from the Kafka topic `random-data` to the console.

```
curl -X POST http://localhost:8083/connectors \
    -H "Content-Type: application/json" \
    --data @config/log-sink.json
```

> A detail description of the Kafka Connect Rest API can be found here, https://docs.confluent.io/current/connect/references/restapi.html


### How to Install Other Connectors

If you want a special Connect plugin installed you have three options:

1. Download the JAR file and copy it in the [mount](mount) directory. This directory will be 
automatically mounted as Docker volume to the Kafka Connect plugin path `/etc/kafka-connect/jars` 
(`CONNECT_PLUGIN_PATH`).

```
services:
  
  connect:
   
    environment:
      CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components,/etc/kafka-connect/jars"
    
    volumes:
      - ./mount:/etc/kafka-connect/jars

```


2. Modify the [Dockerfile](Dockerfile) and install a plugin with the `confluent-hub` CLI.

```
FROM confluentinc/cp-kafka-connect-base:6.0.0

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.4.0
```
> 

3. Build a connector plugin with Maven and add it to Kafka Connect plugin path.
```
FROM confluentinc/cp-kafka-connect-base:6.0.0

COPY target/*.jat /usr/share/java
```

## References

- [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect)
- [Confluent Platform Kafka Connect](https://docs.confluent.io/current/connect/index.html)
- [Docker images for Kafka](https://github.com/confluentinc/kafka-images)
