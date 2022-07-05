CREATE TABLE city
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(256) NOT NULL,
    PRIMARY KEY (id) -- , -- disabled due to restrictions of first version
    -- UNIQUE (name)
);

CREATE TABLE user
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(256) NOT NULL,
    password   VARCHAR(256) NOT NULL,
    first_name VARCHAR(256) NOT NULL,
    last_name  VARCHAR(256) NOT NULL,
    age        SMALLINT     NOT NULL,
    city_id    BIGINT       NOT NULL,

    PRIMARY KEY (id) -- , -- disabled due to restrictions of first version
    -- FOREIGN KEY (city_id) REFERENCES city (id),
    -- UNIQUE (username)
);

CREATE TABLE interest
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    description VARCHAR(256) NOT NULL,
    PRIMARY KEY (id) -- , -- disabled due to restrictions of first version
    -- FOREIGN KEY (user_id) REFERENCES user (id)
);
