DROP TABLE IF EXISTS film_genre, film_like, film, genre, rating, friends, users;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY  NOT NULL ,
    email VARCHAR(250)  NOT NULL ,
    login VARCHAR(250)  NOT NULL ,
    name VARCHAR(250)  NOT NULL ,
    birthday DATE ,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS friends (
    friend_id BIGINT GENERATED BY DEFAULT AS IDENTITY  NOT NULL ,
    initiator BIGINT  NOT NULL ,
    candidate BIGINT  NOT NULL ,
    is_confirmed BOOLEAN  DEFAULT false NOT NULL ,
    CONSTRAINT pk_friend PRIMARY KEY (friend_id),
    CONSTRAINT fk_friend_initiator FOREIGN KEY (initiator) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_candidate FOREIGN KEY(candidate) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rating (
    rating_id BIGINT GENERATED BY DEFAULT AS IDENTITY  NOT NULL ,
    rating_value VARCHAR(250)  NOT NULL ,
    CONSTRAINT pk_rating PRIMARY KEY (rating_id)
);

CREATE TABLE IF NOT EXISTS film (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY  NOT NULL ,
    name VARCHAR(250)  NOT NULL ,
    description VARCHAR(200)  NULL ,
    release_date DATE  NOT NULL ,
    duration BIGINT  NOT NULL ,
    rate INTEGER ,
    rating_id BIGINT ,
    CONSTRAINT pk_film PRIMARY KEY (film_id),
    CONSTRAINT fk_film_rating_id FOREIGN KEY(rating_id) REFERENCES rating (rating_id)
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY  NOT NULL ,
    name VARCHAR(250)  NOT NULL ,
    CONSTRAINT pk_genre PRIMARY KEY (genre_id),
    CONSTRAINT uk_genre_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT  NOT NULL ,
    genre_id BIGINT  NOT NULL ,
    CONSTRAINT pk_film_genre PRIMARY KEY (film_id,genre_id),
    CONSTRAINT fk_film_genre_film_id FOREIGN KEY(film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    CONSTRAINT fk_film_genre_genre_id FOREIGN KEY(genre_id) REFERENCES genre (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_like (
    film_id BIGINT  NOT NULL ,
    user_id BIGINT  NOT NULL ,
    CONSTRAINT pk_film_like PRIMARY KEY (film_id,user_id),
    CONSTRAINT fk_film_like_film_id FOREIGN KEY(film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    CONSTRAINT fk_film_like_user_id FOREIGN KEY(user_id) REFERENCES users (user_id) ON DELETE CASCADE
);