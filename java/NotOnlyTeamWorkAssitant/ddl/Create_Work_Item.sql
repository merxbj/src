-- Create Work_Items table
CREATE TABLE Work_Item (
    work_item_id            NUMERIC(19) NOT NULL,
    assigned_user_id        NUMERIC(19) NULL,
    project_id              NUMERIC(19) NOT NULL,
    parent_work_item_id     NUMERIC(19) NULL,
    subject                 VARCHAR(255) NOT NULL,
    status_id               SMALLINT NOT NULL,
    working_priority        SMALLINT NOT NULL,
    description             TEXT NULL,
    expected_timestamp      DATETIME NULL,
    last_modified_timestamp DATETIME NOT NULL,

    CONSTRAINT PK_Work_Item
    		Primary Key (work_item_id),
    
    CONSTRAINT FK_Work_Item_Users_assigned_user_id
    		Foreign Key (assigned_user_id) REFERENCES Users(user_id),

    CONSTRAINT FK_Work_Item_Project_assigned_project_id
    		Foreign Key (assigned_user_id) REFERENCES Projects(project_id),

    CONSTRAINT FK_Work_Item_User_parent_work_item_id
    		Foreign Key (parent_work_item_id) REFERENCES Work_Item(work_item_id)
    )