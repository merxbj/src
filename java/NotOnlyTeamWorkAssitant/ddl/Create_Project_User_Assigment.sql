-- Create Project_User_Assigment table
CREATE TABLE Project_User_Assignment (
    project_id              NUMERIC(19) NOT NULL,
    user_id                 NUMERIC(19) NOT NULL,
    
    CONSTRAINT FK_Project_User_Assigment_User_user_id
    		Foreign Key (user_id) REFERENCES User(user_id),

    CONSTRAINT FK_Project_User_Assigment_Project_project_id
    		Foreign Key (project_id) REFERENCES Project(project_id)
    )