**Important: Don't forget to update the [Candidate README](#candidate-readme) section**

Real-time Transaction Challenge
===============================
## Overview
Welcome to Current's take-home technical assessment for backend engineers! We appreciate you taking the time to complete this, and we're excited to see what you come up with.

You are tasked with building a simple bank ledger system that utilizes the [event sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) pattern to maintain a transaction history. The system should allow users to perform basic banking operations such as depositing funds, withdrawing funds, and checking balances. The ledger should maintain a complete and immutable record of all transactions, enabling auditability and reconstruction of account balances at any point in time.

## Details
The [included service.yml](service.yml) is the OpenAPI 3.0 schema to a service we would like you to create and host.

The service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.


Implement the event sourcing pattern to record all banking transactions as immutable events. Each event should capture relevant information such as transaction type, amount, timestamp, and account identifier.
Define the structure of events and ensure they can be easily serialized and persisted to a data store of your choice. We do not expect you to use a persistent store (you can you in-memory object), but you can if you want. We should be able to bootstrap your project locally to test.

## Expectations
We are looking for attention in the following areas:
1) Do you accept all requests supported by the schema, in the format described?

2) Do your responses conform to the prescribed schema?

3) Does the authorizations endpoint work as documented in the schema?

4) Do you have unit and integrations test on the functionality?

Here’s a breakdown of the key criteria we’ll be considering when grading your submission:

**Adherence to Design Patterns:** We’ll evaluate whether your implementation follows established design patterns such as following the event sourcing model.

**Correctness**: We’ll assess whether your implementation effectively implements the desired pattern and meets the specified requirements.

**Testing:** We’ll assess the comprehensiveness and effectiveness of your test suite, including unit tests, integration tests, and possibly end-to-end tests. Your tests should cover critical functionalities, edge cases, and potential failure scenarios to ensure the stability of the system.

**Documentation and Clarity:** We’ll assess the clarity of your documentation, including comments within the code, README files, architectural diagrams, and explanations of design decisions. Your documentation should provide sufficient context for reviewers to understand the problem, solution, and implementation details.

# Candidate README
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
## License

At CodeScreen, we strongly value the integrity and privacy of our assessments. As a result, this repository is under exclusive copyright, which means you **do not** have permission to share your solution to this test publicly (i.e., inside a public GitHub/GitLab repo, on Reddit, etc.). <br>

## Submitting your solution

Please push your changes to the `main branch` of this repository. You can push one or more commits. <br>

Once you are finished with the task, please click the `Submit Solution` link on <a href="https://app.codescreen.com/candidate/8716c250-3f8a-46b3-88cc-bbd0a3289e0e" target="_blank">this screen</a>.