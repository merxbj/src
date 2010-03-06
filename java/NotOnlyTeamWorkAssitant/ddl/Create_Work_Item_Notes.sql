-- Create Work_Items_Notes table
CREATE TABLE Work_Item_Notes (
    note_id                 NUMERIC(19) NOT NULL,
    work_item_id            NUMERIC(19) NOT NULL,
    author_user_id          NUMERIC(19) NOT NULL,
    note                    TEXT NULL,
    
    CONSTRAINT PK_Work_Item_Notes
    		Primary Key (note_id, work_item_id),
    
    CONSTRAINT FK_Work_Item_Notes_Work_Items_work_item_id
    		Foreign Key (work_item_id) REFERENCES Work_Items(work_item_id),
            
    CONSTRAINT FK_Work_Item_Notes_Users_author_user_id
    		Foreign Key (author_user_id) REFERENCES Users(user_id)
    )