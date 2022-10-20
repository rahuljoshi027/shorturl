# shorturl
Short URL app

Technologies used :-
Spring boot, kafka streams as well as processor apis, java 17, rocks db, gradle, docker,
open api, swagger, intellij IDE.

Project contains 2 microservices.
1) ShortUrl : to create short url
-- Has 1 api /v1/url/makeshort . On a high level, takes input a long url and converts to short url.

2) RedirectUrl : to redirect created short url
-- Has 1 api /v1/{key}. Takes short url and redirect to original long url.

How to start the app ?
First way :- 
-- After cloning the repository , just use docker-compose up.
This should build and start both services.
-- Once services are up, swagger UI can be viewed on urls as follows :-
http://<docker host ip>:8080/swagger-ui/index.html --> ShortUrl services
http://<docker host ip>:8081/swagger-ui/index.html --> RedirectUrl services
-- Short Url can be created using ShortUrl service's /v1/url/makeshort api.
-- To visit original long url , simply paste the short url on browser.

Second way :- 
-- Build both services using gradle build -x test command.
-- Start provided zookeeper using zookeeper-start.cmd. (replace kafka_home with suitable path)
-- Once zookeeper is up, start kafka server using 0-kafka-server-start.cmd. (replace kafka_home with suitable path)
-- Start ShortUrl service using java -jar shorturl-0.0.1-SNAPSHOT.jar from /build/libs folder.
-- Start RedirectUrl server using java -jar redirectshorturl-0.0.1-SNAPSHOT.jar from /build/libs folder.
-- Once they are up , swagger can be accessed on localhost.
