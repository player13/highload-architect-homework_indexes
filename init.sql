CREATE TABLE user
(
    username           VARCHAR(256)            NOT NULL,
    encrypted_password VARCHAR(256)            NOT NULL,
    first_name         VARCHAR(256)            NOT NULL,
    last_name          VARCHAR(256)            NOT NULL,
    sex                ENUM ('MALE', 'FEMALE') NOT NULL,
    age                SMALLINT                NOT NULL,
    city               VARCHAR(256)            NOT NULL,

    PRIMARY KEY (username),
    INDEX user_last_name_first_name_idx (last_name, first_name)
);

CREATE TABLE interest
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(256) NOT NULL,
    description VARCHAR(256) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (username) REFERENCES user (username)
);
