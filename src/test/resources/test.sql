DROP TABLE IF EXISTS User;

CREATE TABLE User (
    UserId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
    UserName VARCHAR(30) NOT NULL,
    EmailAddress VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX idx_ue on User(UserName,EmailAddress);

INSERT INTO User (UserName,EmailAddress) VALUES ('user1','user1@mail.ru');
INSERT INTO User (UserName,EmailAddress) VALUES ('user2','user2@mail.ru');
INSERT INTO User (UserName,EmailAddress) VALUES ('user3','user3@mail.ru');

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (
    AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
    UserName VARCHAR(30),
    Balance DECIMAL(19,4),
    CurrencyCode VARCHAR(30)
);

CREATE UNIQUE INDEX idx_acc on Account(UserName,CurrencyCode);

INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('user1',100.0000,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('user2',200.0000,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('user3',500.0000,'EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('user4',500.0000,'EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('user5',500.0000,'GBP');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('user6',500.0000,'GBP');
