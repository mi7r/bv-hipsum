# Processing App
A simple application that retrieves random text from an external endpoint:
```https://hipsum.co/api/?type=hipster-centric&paras=<number_of_paragraphs>```, splits text to words,
counts the occurrence of each word, and returns the most frequent one with statistics. 

Application exposes an endpoint ```GET /betvictor/text?p=<number_of_paragraphs>``` which accepts query parameter ```p```.

```p``` is an integer parameter which defines number of paragraphs to retrieve from Hipsum API.

### Example response:

```
{
  "freq_word": "bespoke",
  "avg_paragraph_size": 456,
  "avg_paragraph_processing_time": 194,
  "total_processing_time": 974
}
```
### Parameters description:

- `freq_word` - the most frequent word in the text,
- `avg_paragraph_size` - average size of retrieved paragraphs,
- `avg_paragraph_processing_time` - average processing time of a paragraph in milliseconds,
- `total_processing_time` - total processing time of all paragraphs in milliseconds.

## Processing Notes:
Processing flow will remove all non-alphabetic characters, digits and single characters. Terms like ```8-bit``` and ```single-origin``` will be removed as well.

After successfully processing the same response is than send it to a kafka topic: ```words.processed```.

## Running the Application
Application uses Java 21 and Spring Boot 3.5.3.
Before running App locally, please run ```docker-compose``` file with Kafka configured in `kafka-local-docker-compose.yaml`.

Application is exposed locally on ```http://localhost:8090/``` port can be changed in application.yaml via `server.port`.

Example request: ```http://localhost:8090/text?p=3```

## Running Kafka locally

```sh
# Creating and running Kafka container in the detached mode
docker compose -f kafka-local-docker-compose.yaml up -d
```

```sh
# Stopping and removing Kafka container
docker compose -f kafka-local-docker-compose.yaml down
```