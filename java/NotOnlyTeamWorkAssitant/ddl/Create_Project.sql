-- Create Projects table
CREATE TABLE Project (
    project_id              NUMERIC(19) NOT NULL,
    name                    VARCHAR(255) NOT NULL,
    
    CONSTRAINT PK_Project
        Primary Key (project_id)
    )