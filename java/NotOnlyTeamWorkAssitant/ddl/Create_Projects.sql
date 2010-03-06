-- Create Projects table
CREATE TABLE Projects (
    project_id              NUMERIC(19) NOT NULL,
    name                    VARCHAR(255) NOT NULL,
    
    CONSTRAINT PK_Projects
        Primary Key (project_id)
    )