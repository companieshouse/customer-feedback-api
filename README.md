# customer-feedback-api
This API receives customer feedback, saves it to a MongoDB collection and forwards it to a send-email endpoint.

## Requirements

In order to build this service locally you need:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/)
- [Git](https://git-scm.com/downloads)
- [MongoDB](https://www.mongodb.com)

## Installation

To download this repository, run the following from the command line and change into the directory:

```
git clone git@github.com:companieshouse/customer-feedback-api.git

cd customer-feedback-api
```

To run the application standalone set suitable environment variable values and execute the following:

```
./mvnw spring-boot:run
```

#### Configuration

The following are environment variables necessary to run the API:

* _CUSTOMER_FEEDBACK_EMAIL_: The email address to which feedback should be sent ex. feedback@companieshouse.gov.uk
* _KAFKA_API_ENDPOINT_: ex. http://chs-kafka-api:4081/send-email
* _LOGLEVEL_: The log level ex. debug
* _MONGODB_COLLECTION_: The MongoDB collection name ex. customer_feedback
* _MONGODB_DATABASE_: The MongoDB database name ex. customer_feedback
* _MONGODB_URL_: The MongoDB database URL ex. mongodb://localhost:27017
* _SEND_EMAIL_FLAG_: Whether the feedback should be forwarded to the send-email endpoint ex. true
* _SERVER_PORT_: The port on which the service will listen  ex. 18083

#### Docker

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.
2. `cd` into `docker-chs-development`, and run `./bin/chs-dev modules enable customer-feedback-api`
3. Run `./bin/chs-dev development enable customer-feedback-api`
4. Run docker using `chs-dev up` in the `docker-chs-development` directory.



#### Other Environments

The API is deployed via Concourse or by the release team.

## Usage

This API can be accessed via the web via http://chs.local/help/feedback?sourceurl=http://chs.local/
or directly via Postman or cURL

#### Using the REST API directly


#### Endpoints

__GET__ to /customer-feedback/healthcheck

__POST__ to /customer-feedback

##### Customer Feedback

HTTP requests can be sent directly to the API via Postman or cURL

```
curl --location 'api.chs.local:4001/customer-feedback' \
--header 'Accept: */*' \
--header 'Accept-Language: cy-GB,cy;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6' \
--header 'Connection: keep-alive' \
--header 'Content-Type: application/json' \
--header 'Cookie: ch_cookie_consent=eyJ1c2VySGFzQWxsb3dlZENvb2tpZXMiOiJ5ZXMiLCJjb29raWVzQWxsb3dlZCI6WyJwaXdpayIsImdvb2dsZSJdfQ==; _pk_id.24.1175=23958bd8b34e7554.1684421174.; __SID=UWB9NEo1iHMdabr95syWwZ2cQLZpk77qnqdedlCpYIwOKnW53IFj+UI; _pk_ref.24.1175=%5B%22%22%2C%22%22%2C1697451790%2C%22http%3A%2F%2Faccount.chs.local%2F%22%5D; _pk_ses.24.1175=1' \
--header 'Origin: http://chs.local' \
--header 'Referer: http://chs.local/help/feedback?sourceurl=http://chs.local/' \
--header 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36' \
--header 'X-Requested-With: XMLHttpRequest' \
--header 'Authorization: Bearer cPCb5nxwUUmmENJZZleN_-juB7WZBApcpoRQzex0TISpODZM_tqRdvgp3HbsX82Xc7zj-hQGOe6Laab-n6hglg' \
--data-raw '{
    "customer_email": "a.ascii@unicode.org",
    "customer_feedback": "Not sure about the spelling",
    "customer_name": "Arthur ASCII",
    "kind": "feedback",
    "source_url": "http: //chs.local/"
}'
```

The MongoDB entry can be found with ```db.getCollection("customer_feedback").find({})```
and should look like the following
```
{
    "_id" : ObjectId("654e3cd1c97abd60d644592c"),
    "data" : {
        "customer_email" : "a.ascii@unicode.org",
        "customer_feedback" : "Not sure about the spelling",
        "customer_name" : "Arthur ASCII",
        "kind" : "feedback",
        "source_url" : "http://chs.local/"
    },
    "created_at" : ISODate("2023-11-10T14:23:13.350+0000"),
    "email_sent" : true,
    "_class" : "uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDAO"
}
```

In docker-chs-development the customer-feedback-api logging shows the progress of the request

```
{"created":"2023-11-10T14:23:13.346Z","event":"info","namespace":"customer-feedback-api","context":"lV-0jcSzm5KOGiCyExS5Blo0xpW1","data":{"message":"Customer feedback submitted"}}
{"created":"2023-11-10T14:23:13.349Z","event":"debug","namespace":"customer-feedback-api","context":"lV-0jcSzm5KOGiCyExS5Blo0xpW1","data":{"message":"Processing customer feedback"}}
{"created":"2023-11-10T14:23:13.350Z","event":"debug","namespace":"customer-feedback-api","context":"lV-0jcSzm5KOGiCyExS5Blo0xpW1","data":{"message":"Inserting customer feedback record"}}
{"created":"2023-11-10T14:23:13.361Z","event":"debug","namespace":"customer-feedback-api","context":"lV-0jcSzm5KOGiCyExS5Blo0xpW1","data":{"message":"Calling send-email endpoint"}}
{"created":"2023-11-10T14:23:13.374Z","event":"debug","namespace":"customer-feedback-api","context":"lV-0jcSzm5KOGiCyExS5Blo0xpW1","data":{"message":"Response code from endpoint: 200"}}
```

and the  chs-kafka-api logging shows that the customer feedback was received
```
{"context":"cztxcdJJFuyZHRCovgpV","created":"2023-11-10T14:23:13.367380796Z","data":{"app_id":"api_ch_gov_uk.create_feedback","created":"2023-11-10T14:23:13","email_address":"","message":"Marshalling email into binary using Avro","message_id":"5e609b1f-550c-4bfa-80c9-76783326acb0","message_type":"customer-feedback"},"event":"trace","namespace":"chs-kafka-api"}
{"context":"cztxcdJJFuyZHRCovgpV","created":"2023-11-10T14:23:13.373066211Z","data":{"app_id":"api_ch_gov_uk.create_feedback","created":"2023-11-10T14:23:13","email_address":"","message":"EmailSendQueue - email send request enqueued in Kafka","message_id":"5e609b1f-550c-4bfa-80c9-76783326acb0","message_type":"customer-feedback","offset":13,"partition":0,"topic":"email-send"},"event":"trace","namespace":"chs-kafka-api"}
{"context":"cztxcdJJFuyZHRCovgpV","created":"2023-11-10T14:23:13.373223685Z","data":{"duration":6037047,"end":"2023-11-10T14:23:13.373221309Z","method":"POST","path":"/send-email","start":"2023-11-10T14:23:13.367184271Z","status":200},"event":"request","namespace":"chs-kafka-api"}
```
