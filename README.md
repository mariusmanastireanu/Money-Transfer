
# Money Transfer REST API

## Tools and libraries used:
- Java 8 as programming language
- Maven for build system
- Jetty for the embedded server
- Jersey for REST
- JUnit for testing

## How to install and run the application

Go to project location and run from command line
```bash
mvn clean install
cd target
java -jar moneytransfer-1.0-SNAPSHOT-jar-with-dependencies.jar
```
## API

#### View all accounts

Returns a JSON object containing a list of Account objects.

 - URL ```/accounts```
 -  Method ```GET```
 -  Success Response  - **Code:** 200
```JSON
[
    {
        "id": "e6b068c4-788c-42e1-81db-35cf9815823e",
        "balance": 1523.51
    },
    {
        "id": "922ab185-a4a6-4c4a-a987-734f352c9ca1",
        "balance": 3259.64
    }
]

```

#### View Account information

Returns a JSON object containing an Account object.

 - URL ```/accounts/{id}```
 -  Method ```GET```
 - URL Parameters: ```id```
 - Example request: ```/accounts/e6b068c4-788c-42e1-81db-35cf9815823e```
 -  Success Response  - **Code:** 200
```JSON
{
    "id": "e6b068c4-788c-42e1-81db-35cf9815823e",
    "balance": 1523.51
}
```
 -  Not found Response - **Code:** 404
#### Add new Account

Adds a new account in the data store and returns a JSON data containing the newly created account.
The account will have balance 0.

 - URL ```/accounts/```
 -  Method ```POST```
 -  Success Response  - **Code:** 200
```JSON
{
    "id": "b2cac407-c17d-41c2-9042-35208905dbb3",
    "balance": 0
}
```
#### Update an existing Account/Add a new Account

Updates or adds a new account in the data store (based on a JSON sent in the Body) and returns a JSON object containing the account.

 - URL ```/accounts/```
 -  Method ```PUT```
 - Body Parameters: ```JSON``` object representing account.
**Example:**
```JSON
{
    "id": "b2cac407-c17d-41c2-9042-35208905dbb3",
    "balance": 555
}
```
 -  Success Response  - **Code:** 200
```JSON
{
    "id": "b2cac407-c17d-41c2-9042-35208905dbb3",
    "balance": 555
}
```
#### Delete an existing Account

Returns a JSON object containing the deleted Account.

 - URL ```/accounts/{id}```
 -  Method ```DELETE```
 - URL Parameters: ```id```
 - Example request: ```/accounts/b2cac407-c17d-41c2-9042-35208905dbb3```
 -  Success Response  - **Code:** 200
```JSON
{
    "id": "b2cac407-c17d-41c2-9042-35208905dbb3",
    "balance": 555
}
```
 -  Not found Response - **Code:** 404

#### Deposit money

Accepts a JSON object as parameter representing a Transfer object and returns a JSON object containing the updated Account.

 - URL ```/deposit/```
 -  Method ```PUT```
 - Body Parameters: ```JSON``` object representing the deposit.
**Example:**
```JSON
{
    "accountId": "e6b068c4-788c-42e1-81db-35cf9815823e",
    "amount": 100
}
```
 -  Success Response  - **Code:** 200
```JSON
{
    "id": "e6b068c4-788c-42e1-81db-35cf9815823e",
    "balance": 1623.51
}
```
 -  Bad request Response - **Code:** 400
In case input data is invalid.

#### Withdraw money

Accepts a JSON object as parameter representing a Transfer object and returns a JSON object containing the updated Account.

 - URL ```/withdraw/```
 -  Method ```PUT```
 - Body Parameters: ```JSON``` object representing the withdrawal.
**Example:**
```JSON
{
    "accountId": "e6b068c4-788c-42e1-81db-35cf9815823e",
    "amount": 100
}
```
 -  Success Response  - **Code:** 200
```JSON
{
    "id": "e6b068c4-788c-42e1-81db-35cf9815823e",
    "balance": 1523.51
}
```
 -  Bad request Response - **Code:** 400
In case input data is invalid or there are insufficient funds for this operation.

#### Transfer money between two accounts

Accepts a JSON object as parameter representing a Transfer object and returns a JSON object containing a list of the updated Accounts.

 - URL ```/transfer/```
 -  Method ```PUT```
 - Body Parameters: ```JSON``` object representing the transfer.
**Example:**
```JSON
{
    "accountId": "e6b068c4-788c-42e1-81db-35cf9815823e",
    "destinationId": "b2cac407-c17d-41c2-9042-35208905dbb3",
    "amount": 1000
}
```
 -  Success Response  - **Code:** 200
```JSON
[
    {
        "id": "e6b068c4-788c-42e1-81db-35cf9815823e",
        "balance": 623.51
    },
    {
        "id": "b2cac407-c17d-41c2-9042-35208905dbb3",
        "balance": 1000
    }
]
```
 -  Bad request Response - **Code:** 400
In case input data is invalid or there are insufficient funds for this operation.


## Tests
To run unit tests simply run the following command
```bash
mvn test
```
The tests cover the exposed REST API calls with their expected input and output, as well as the interaction with the datastore layer, the model and the transfer service implementation itself taking into account the possibility of a multi-threaded environment.

