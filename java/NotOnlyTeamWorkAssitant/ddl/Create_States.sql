-- Create States table
CREATE TABLE States (
    state_id                NUMERIC(19) NOT NULL,
    label                   VARCHAR(255) NOT NULL,
    
    CONSTRAINT PK_States
        Primary Key (state_id)
    )
