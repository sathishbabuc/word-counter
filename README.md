# Assumptions

File extension is not validated. File will be processed irrespective of the content as long as it could convert to string content.

Any characters in the file (other than space, hyphen, alphabets and numbers) will be replaced with space and processed.

Each uploaded file is given an identifier in the response (to support multiple uploads with the same file name). Further status and result retrievel should be via the identifier.

Exposed REST Endpoints are not secured.

In-memory HSQL Database is used to store the file status summary. All details will be lost once application is restarted.


# Configuration:
Tool specific default configurations are defined in the src/main/resources/application.properties with comments.

Tool uses Log4j as the logging framework and it produces .log and .perf files by default as defined in src/main/resources/log4j2.xml.

# Build and run the application locally, 

As a pre-requisite, install and configure Maven if not present already.

1. Checkout the code to a local system
2. Execute below command from the checked out location.
mvn clean install && java -jar target\word-counter-1.0-SNAPSHOT.jar

# Accessing the Application via browser.
Once the application is started, you should be able to access the swagger url using below url and use the different endpoints. Alternatively, you can use postman

http://localhost:9001/swagger-ui.html
