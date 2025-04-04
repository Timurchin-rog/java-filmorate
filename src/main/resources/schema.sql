DROP TABLE IF EXISTS users_friends;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films_likes;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS ratings;

CREATE TABLE IF NOT EXISTS users_friends (
    id integer PRIMARY KEY AUTO_INCREMENT,
	user_id integer,
	friend_id integer
);

CREATE TABLE IF NOT EXISTS users (
	id integer PRIMARY KEY AUTO_INCREMENT,
	email varchar(50) NOT NULL,
	login varchar(50) NOT NULL,
	name varchar(50) NOT NULL,
	birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS films_likes (
    id integer PRIMARY KEY AUTO_INCREMENT,
	film_id integer,
	user_id integer
);

CREATE TABLE IF NOT EXISTS genres (
	id integer PRIMARY KEY AUTO_INCREMENT,
	name varchar(50)
);

CREATE TABLE IF NOT EXISTS films_genres (
    id integer PRIMARY KEY AUTO_INCREMENT,
	film_id integer,
	genre_id integer
);

CREATE TABLE IF NOT EXISTS ratings (
	id integer PRIMARY KEY AUTO_INCREMENT,
	name varchar(50)
);

CREATE TABLE IF NOT EXISTS films (
	id integer PRIMARY KEY AUTO_INCREMENT,
	name varchar(50) NOT NULL,
	description varchar(200) NOT NULL,
	release_date date NOT NULL,
	duration integer NOT NULL,
	count_likes integer,
	mpa integer REFERENCES ratings(id)
);