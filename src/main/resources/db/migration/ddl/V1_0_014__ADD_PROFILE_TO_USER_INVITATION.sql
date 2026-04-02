-- Add profile column to user_invitation table
ALTER TABLE user_invitation
    ADD COLUMN profile VARCHAR(50) NOT NULL;
