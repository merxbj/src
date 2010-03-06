-- Create Project_User_Assigment table
CREATE TABLE Project_User_Assigment (
    project_id              NUMERIC(19) NOT NULL,
    user_id                 NUMERIC(19) NOT NULL,
    
    CONSTRAINT FK_Project_User_Assigment_Users_user_id
    		Foreign Key (user_id) REFERENCES Users(user_id),

    CONSTRAINT FK_Project_User_Assigment_Projects_project_id
    		Foreign Key (project_id) REFERENCES Projects(project_id)
    )