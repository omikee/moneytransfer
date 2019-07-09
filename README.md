# Money transfer Rest API

A Java RESTful API for money transfers between users accounts


### How to build
```sh
./gradlew jar
```

### How to run
```sh
./gradlew run
```

### How to test
```sh
./gradlew test
```

Application starts a jetty server on localhost. An H2 in memory database initialized with some sample user and account data To view

- http://localhost:8080/user/user1
- http://localhost:8080/user/user2
- http://localhost:8080/account/1
- http://localhost:8080/account/2

### Available Services

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /user/{userName} | get user by user name | 
| GET | /user/all | get all users | 
| POST | /user/create | create a new user | 
| PUT | /user/{userId} | update user | 
| DELETE | /user/{userId} | remove user | 
| GET | /account/{accountId} | get account by accountId | 
| GET | /account/all | get all accounts | 
| GET | /account/{accountId}/balance | get account balance by accountId | 
| POST | /account/create | create a new account
| DELETE | /account/{accountId} | remove account by accountId | 
| PUT | /account/{accountId}/withdraw/{amount} | withdraw money from account | 
| PUT | /account/{accountId}/deposit/{amount} | deposit money to account | 
| POST | /transaction | perform transaction between 2 user accounts | 

### Http Status
- 200 OK: The request has succeeded
- 400 Bad Request: The request could not be understood by the server 
- 404 Not Found: The requested resource cannot be found
- 406 Not Acceptable: The request could not be performed 
- 500 Internal Server Error: The server encountered an unexpected condition 

### Sample JSON for User and Account
##### User : 
```sh
{  
  "userName":"test1",
  "emailAddress":"test1@mail.ru"
} 
```
##### User Account: : 

```sh
{  
   "userName":"test1",
   "balance":10.0000,
   "currencyCode":"GBP"
} 
```

#### User Transaction:
```sh
{  
   "currencyCode":"EUR",
   "amount":100000.0000,
   "fromAccountId":1,
   "toAccountId":2
}
```
