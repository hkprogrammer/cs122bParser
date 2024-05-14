-- noinspection SqlNoDataSourceInspectionForFile

-- noinspection SqlDialectInspectionForFile

CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;

DROP TABLE IF EXISTS sessionCode;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS generes_in_movies;
DROP TABLE IF EXISTS generes;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS creditcards;
DROP TABLE IF EXISTS stars_in_movies;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS stars;


CREATE TABLE IF NOT EXISTS movies(
	id char(10),
    title char(100) NOT NULL,
    year int,
    director char(100) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS stars(
	id char(10),
    name char(100) NOT NULL,
    birthYear int DEFAULT -1,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS stars_in_movies(
	starId char(10) NOT NULL,
    movieId char(10) NOT NULL,
    FOREIGN KEY(starId) REFERENCES stars(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS genres(
	id int AUTO_INCREMENT,
    name char(32) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS genres_in_movies(
	genreId int NOT NULL,
	movieId char(10) NOT NULL,
    FOREIGN KEY(genreId) REFERENCES genres(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS creditcards(
	id char(20),
    firstName char(50) NOT NULL,
    lastName char(50) NOT NULL,
    expiration date NOT NULL,
    PRIMARY KEY(id)
);


CREATE TABLE IF NOT EXISTS customers(
	id int AUTO_INCREMENT,
    firstName char(50) NOT NULL ,
    lastName char(50) NOT NULL,
    ccId char(20) NOT NULL,
    address char(200) NOT NULL,
    email char(50) NOT NULL,
    password char(255) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(ccId) REFERENCES creditcards(id) 
);

CREATE TABLE IF NOT EXISTS sales(
	id int AUTO_INCREMENT,
    customerId int NOT NULL,
    movieId char(10) NOT NULL,
    saleDate date NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(customerId) REFERENCES customers(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS ratings(
	movieId char(10) NOT NULL,
    rating float NOT NULL,
    numVotes int NOT NULL,
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS users(
    username char(255),
    password char(255) NOT NULL,
    customer_id int,
    PRIMARY KEY(username),
    FOREIGN KEY(customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS sessionCode(
	username char(255),
    code char(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS employees(
    email char(50) primary key,
    password char(255) not null,
    fullname char(100)
);