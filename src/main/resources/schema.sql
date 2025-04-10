DROP TABLE IF EXISTS users_friends;
DROP TABLE IF EXISTS films_likes;
DROP TABLE IF EXISTS review_ratings;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS user_feeds;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS ratings;

CREATE TABLE IF NOT EXISTS users (
	id integer PRIMARY KEY AUTO_INCREMENT,
	email varchar(50) NOT NULL,
	login varchar(50) NOT NULL,
	name varchar(50) NOT NULL,
	birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS users_friends (
  id integer PRIMARY KEY AUTO_INCREMENT,
	user_id integer REFERENCES users(id),
	friend_id integer REFERENCES users(id)
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

CREATE TABLE IF NOT EXISTS films_likes (
    id integer PRIMARY KEY AUTO_INCREMENT,
	film_id integer REFERENCES films(id),
	user_id integer REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS genres (
	id integer PRIMARY KEY AUTO_INCREMENT,
	name varchar(50)
);

CREATE TABLE IF NOT EXISTS films_genres (
    id integer PRIMARY KEY AUTO_INCREMENT,
	film_id integer REFERENCES films(id),
	genre_id integer REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id INTEGER REFERENCES users(id),
    film_id INTEGER REFERENCES films(id),
    useful INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS review_ratings (
    review_id BIGINT REFERENCES reviews(review_id),
    user_id INTEGER REFERENCES users(id),
    rating_type INTEGER CHECK (rating_type IN (1, -1)),
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_feeds (
    event_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    event_type ENUM('LIKE', 'REVIEW', 'FRIEND') NOT NULL,
    operation ENUM('ADD', 'REMOVE', 'UPDATE') NOT NULL,
    entity_id INTEGER NOT NULL,
    event_timestamp BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);