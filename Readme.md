docker login -p pw -u user https://index.docker.io/v1
expose local docker 2375 port

docker run -d -p 8080:8080 --name kafka-producer-ui zalerix/kafka-avro-publish-ui:2.1.1

gradle dockerPushImage -Pvaadin.productionMode