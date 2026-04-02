ALTER TABLE user_board
    ADD CONSTRAINT userId_boardId_unique UNIQUE (user_id,board_id);

ALTER TABLE user_workspace
  ADD CONSTRAINT userId_workspaceId_unique UNIQUE (user_id,workspace_id);