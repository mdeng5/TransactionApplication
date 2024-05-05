## Bootstrap instructions
With an IDE like Intellij, you can run TransactionServiceApplication. Or, you can move to the target folder and locate the jar file. Then run java -jar CodeScreen_yrnx3ahb-1.0.0.jar.
Once the application is running in the background, you can access the app with localhost:8080 and try the localhost:8080/ping endpoint. 
I used postman to test the PUT endpoints. Here are some example request bodies I used
localhost:8080/load
{
    "userId": "Michael-Id",
    "messageId": "1",
    "transactionAmount": {
        "amount": "100",
        "currency": "USD",
        "debitOrCredit": "CREDIT"
    }
}

localhost:8080/authorization
{
    "userId": "Michael-Id",
    "messageId": "3",
    "transactionAmount": {
        "amount": "100",
        "currency": "USD",
        "debitOrCredit": "DEBIT"
    }
}
## Design considerations
I decided to use a message queue to process the requests and a Map to store the result of each request. I also had a consumer service running in the background (UnprocessedMessageQueueConsumerService) to consume from the message queue and process each transaction.
I chose this option because if we have concurrent transactions, I want to ensure that there is enough money in the account to be taken out.
Ideally, I would have the message queue as a separate entity as well as the consumer be a separate microapp. This would allow it to scale as there is less coupling and we can improve/increase the number of instances of the bottleneck service. 

## Assumptions
I assumed we are not allowed to have over drafts and that there may be multiple transactions happening asynchronously. I implemented retry and timeouts incase the consumer is not able to keep up with the producer.

## Bonus: Deployment considerations
Docker to containerize my Spring Boot App
MySQL database for the user data
MySQL database for the processed messages
    Spring Boot has excellent support for relational databases and since the data has a simple structure, a SQL db would be a great fit.
RabbitMQ or Amazon SQS for message broker in place of my local Message Queue

Jenkins for CI/CD for faster and easier deployment as well speed up development -> production rate
Splunk to monitor logs and help triage issues in production
