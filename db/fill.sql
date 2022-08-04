LOAD DATA INFILE '/docker-entrypoint-initdb.d/generated_users.csv'
    INTO TABLE user
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n';

LOAD DATA INFILE '/docker-entrypoint-initdb.d/generated_interests.csv'
    INTO TABLE interest
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    (username, description);
