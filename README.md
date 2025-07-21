# Processing App
 Retrieve data from external endpoint ```https://hipsum.co/the-api``` , process data and returns statistics. 

Application is exposed locally on ```http://localhost:8090/text```

Example request: ```http://localhost:8090/text?p=3```

### Running Kafka locally

```sh
# Creating and running Kafka container in the detached mode
docker compose -f kafka-local-docker-compose.yaml up -d
```

```sh
# Stopping and removing Kafka container
docker compose -f kafka-local-docker-compose.yaml down
```


[//]: # (todo: add more details)