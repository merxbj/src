-- Create Users table
CREATE TABLE User (
    user_id                 NUMERIC(19) NOT NULL,
    login                   VARCHAR(255) NOT NULL,
    password                VARCHAR(255) NOT NULL,
    first_nam               VARCHAR(255) NULL,
    last_name               VARCHAR(255) NULL,
    
    CONSTRAINT PK_User
        Primary Key (user_id)
    )