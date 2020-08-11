# ignite-test
Difference between two documents

1. Task
    
    Provide 2 http endpoints that accepts JSON base64 encoded binary data on both
endpoints
    * <host>/v1/diff/<ID>/left and <host>/v1/diff/<ID>/right
        * The provided data needs to be diff-ed and the results shall be available on a third end
    point
    * <host>/v1/diff/<ID>
        * The results shall provide the following info in JSON format
            * If equal return that
            * If not of equal size just return that
            * If of same size provide insight in where the diffs are, actual diffs are not needed. offsets + length in the data

2. Run project

    All commands should be ran from project folder ignite-test⋅⋅

    2.1. Spring Boot + Ignite
       
      * Option1:
        * mvn spring-boot:run
       
      * Option2:
      
        * mvn clean install
        * java -jar target/ignite-test-1.0.jar
    
    2.2. Docker + Maven
      * mvn clean install dockerfile:build -f pom.xml
      * docker run -d --name ignite-test -p 8080:8080 test/ignite-test:latest

3. Usages

    3.1. Submit document URL. Http Post. Add a header Content-Type=application/json.
        * http://localhost:8080/v1/diff/<ID>/left
        * http://localhost:8080/v1/diff/<ID>/right
        
    3.2. Diff result. Http Get.
        * http://localhost:8080/v1/diff/<ID>

4. Client
    * Use com.client.DiffControllerRestClient.java to post documents and get diff.

