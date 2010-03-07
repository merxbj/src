-- Create Work_Items_Notes table
CREATE TABLE Work_Item_Note (
    note_id                 NUMERIC(19) NOT NULL,
    work_item_id            NUMERIC(19) NOT NULL,
    author_user_id          NUMERIC(19) NOT NULL,
    note                    TEXT NULL,
    
    CONSTRAINT PK_Work_Item_Note
    		Primary Key (note_id, work_item_id),
    
    CONSTRAINT FK_Work_Item_Note_Work_Item_work_item_id
    		Foreign Key (work_item_id) REFERENCES Work_Item(work_item_id),
            
    CONSTRAINT FK_Work_Item_Note_User_author_user_id
    		Foreign Key (author_user_id) REFERENCES User(user_id)
    )