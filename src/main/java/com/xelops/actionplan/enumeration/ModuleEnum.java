package com.xelops.actionplan.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ModuleEnum {

    ORGANIZATION("Organization"),
    USER("User"),
    WORKSPACE("Workspace"),
    ACTION("Action"),
    BOARD("Board"),
    BOARD_COLUMN("Board Column"),
    CUSTOM_FIELD("Custom Field"),
    USER_INVITATION("User Invitation"),
    USER_WORKSPACE("User Workspace"),
    USER_BOARD("User Board"),
    NOTIFICATION("Notification"),
    COMMENT("Comment"),
    ATTACHMENT("Attachment");

    private final String name;

}
