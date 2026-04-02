-- Add token column to user_invitation table
ALTER TABLE user_invitation ADD COLUMN token VARCHAR(36) UNIQUE;

-- Add index for better performance on token lookups
CREATE INDEX idx_user_invitation_token ON user_invitation(token);

