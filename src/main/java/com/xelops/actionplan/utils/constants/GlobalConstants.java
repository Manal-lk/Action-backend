package com.xelops.actionplan.utils.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalConstants {

    public static final String HEADER_BEARER = "Bearer ";

    /**
     * codes messages
     */
    public static final String ERROR_WS_TECHNICAL = "error.ws.technical";
    public static final String ERROR_WS_NOT_FOUND = "error.ws.NotFound";
    public static final String ERROR_WS_NOT_FOUND_BY_FIELD = "error.ws.notFoundByField";
    public static final String ERROR_WS_NOT_AFFECTED = "error.ws.notAffectedTo";
    public static final String ERROR_WS_MISSING_REQUIRED_FIELD = "error.ws.MissingRequiredField";
    public static final String ERROR_WS_MISSING_ORGANIZATION = "error.ws.OrganizationNotFound";

    public static final String NOTIFICATION_QUEUE = "/queue/notification";
    public static final String NOTIFICATION_ERROR_QUEUE = "/queue/error";

    public static final String USER_NOT_IN_WORKSPACE_ERROR = "error.ws.userNotInWorkspace";
    public static final String BOARD_DUPLICATE_NAME_ERROR = "error.ws.boardDuplicateName";
    public static final String BOARD_MAX_COLUMNS_ERROR = "error.ws.boardMaxColumns";
    public static final String ERROR_WS_COLUMN_NOT_FOUND = "error.ws.columnNotFound";
    public static final String ERROR_WS_COLUMN_NOT_EMPTY = "error.ws.columnNotEmpty";
    public static final String BOARD_NOT_FOUND_ERROR = "error.actionplan.board.not_found";
    public static final String ERROR_WS_ACTION_NOT_FOUND = "error.ws.actionNotFound";
    public static final String ERROR_WS_COMMENT_NOT_FOUND = "error.ws.commentNotFound";
    public static final String USER_DOES_NOT_HAVE_ACCESS_TO_BOARD_ERROR = "error.actionplan.board.does_not_have_access_to_board";
    public static final String ACTION_NOT_FOUND_ERROR = "error.actionplan.action.not_found";

    public static final String ACTION_NOT_FOUND_IN_BOARD_ERROR = "error.actionplan.action.not_found_in_board";
    public static final String COLUMN_NOT_FOUND_IN_BOARD_ERROR = "error.actionplan.board_column.not_found_in_board";
    public static final String ACTION_TITLE_ALREADY_EXISTS_IN_BOARD_COLUMN_ERROR = "error.actionplan.action.title_already_exists_in_board_column";
    public static final String ACTION_DUE_DATE_IN_THE_PAST_ERROR = "error.actionplan.action.due_date_in_past";

    public static final String ACTION_TITLE_EXCEEDS_MAX_LENGTH_ERROR = "error.actionplan.action.title_exceeds_max_length";
    public static final String ACTION_TITLE_REQUIRED_ERROR = "error.actionplan.action.title_required";
    public static final String BOARD_COLUMN_NOT_FOUND_ERROR = "error.actionplan.board_column.not_found";
    public static final String ACTION_ASSIGNEES_LIST_SHOULD_NOT_BE_EMPTY = "error.actionplan.action.assignees_list_should_not_be_empty";
    public static final String USER_INVITATION_WORKSPACE_OR_BOARD_REQUIRED = "error.actionplan.userInvitation.workspaceOrBoardRequired";
    public static final String BOARD_COLUMNS_NOT_FOUND_IN_BOARD_ERROR = "error.actionplan.board_columns.not_found_in_board";
    public static final String ACTION_ESTIMATION_NEGATIVE_ERROR = "error.actionplan.action.estimation_negative";
    public static final String ACTION_ESTIMATION_EXCEEDS_MAX_VALUE_ERROR = "error.actionplan.action.estimation_exceeds_max_value";
    public static final String USER_CANNOT_EDIT_COMMENT_ERROR = "error.ws.userCannotEditComment";
    public static final String USER_CANNOT_DELETE_COMMENT_ERROR = "error.ws.userCannotDeleteComment";

    public static final String STORAGE_FAILED_TO_CREATE_DIRECTORY_ERROR = "error.actionplan.storage.failed_to_create_directory";
    public static final String STORAGE_FAILED_TO_STORE_FILE_ERROR = "error.actionplan.storage.failed_to_store_file";
    public static final String STORAGE_FILE_NOT_FOUND_ERROR = "error.actionplan.storage.file_not_found";
    public static final String STORAGE_FAILED_TO_READ_FILE_ERROR = "error.actionplan.storage.failed_to_read_file";
    public static final String STORAGE_FAILED_TO_DELETE_FILE_ERROR = "error.actionplan.storage.failed_to_delete_file";

    public static final String ATTACHMENT_FAILED_TO_READ_FILE_DATA_ERROR = "error.actionplan.attachment.failed_to_read_file_data";
    public static final String ATTACHMENT_NOT_FOUND_ERROR = "error.actionplan.attachment.not_found";


    public static final String NOTIFICATION_BOARD_NAME_KEY = "boardName";
    public static final String NOTIFICATION_WORKSPACE_NAME_KEY = "workspaceName";
    public static final String NOTIFICATION_APP_LINK_KEY = "appLink";

    public static final String NOTIFICATION_BOARD_REDIRECTION_PATH = "/boards/";
    public static final String NOTIFICATION_WORKSPACE_REDIRECTION_PATH = "/board-management?workspaceId=";
    /**
     * openApi
     */

    public static final String BEARER_FORMAT = "JWT";
    public static final String SCHEME = "bearer";
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * scope
     */

    public static final String SCOPE_PREFIX = "SCOPE_";

    /**
     * roles
     */

    public static final String ROLE_PREFIX = "ROLE_";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
