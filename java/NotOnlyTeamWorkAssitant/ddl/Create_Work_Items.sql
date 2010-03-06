-- Create Work_Items table
CREATE TABLE Work_Items (
    work_item_id            NUMERIC(19) NOT NULL,
    assigned_user_id        NUMERIC(19) NULL,
    state_id                NUMERIC(19) NOT NULL,
    project_id              NUMERIC(19) NOT NULL,
    parent_work_item_id     NUMERIC(19) NULL,
    subject                 VARCHAR(255) NOT NULL,
    working_priority        SMALLINT NOT NULL,
    description             TEXT NULL,
    expected_timestamp      DATETIME NULL,
    last_modified_timestamp DATETIME NOT NULL,

    CONSTRAINT PK_Work_Items
    		Primary Key (work_item_id),
    
    CONSTRAINT FK_Work_Items_Users_assigned_user_id
    		Foreign Key (assigned_user_id) REFERENCES Users(user_id),

    CONSTRAINT FK_Work_Items_States_state_id
    		Foreign Key (state_id) REFERENCES States(state_id),

    CONSTRAINT FK_Work_Items_Projects_assigned_project_id
    		Foreign Key (assigned_user_id) REFERENCES Projects(project_id),

    CONSTRAINT FK_Work_Items_Users_parent_work_item_id
    		Foreign Key (parent_work_item_id) REFERENCES Work_Items(work_item_id)
    )