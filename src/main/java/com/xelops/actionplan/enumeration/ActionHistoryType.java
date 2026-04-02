package com.xelops.actionplan.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ActionHistoryType {

    ACTION_CREATED("%s created the action", "%s created the action", ActionHistoryChangeType.NOT_DIFF_NEW_DATA, true),
    TITLE_CHANGE("%s changed the title from '%s' to '%s'", "%s changed the title to %s", ActionHistoryChangeType.DIFF, true),
    DESCRIPTION_CHANGE("%s changed the description from '%s' to '%s'", "%s changed the description to '%s'", ActionHistoryChangeType.DIFF, false),
    DUE_DATE_CHANGE("%s changed the due date from '%s' to '%s'", "%s changed the due date to '%s'", ActionHistoryChangeType.DIFF, true),
    PRIORITY_CHANGE("%s changed the priority from '%s' to '%s'", "%s changed the priority to '%s'", ActionHistoryChangeType.DIFF, true),
    COLUMN_CHANGE("%s moved the action from '%s' to '%s'", "%s moved the action to '%s'", ActionHistoryChangeType.DIFF, true),
    ASSIGNEE_CHANGE("%s changed the assignee from '%s' to '%s'", "%s assigned the action to '%s'", ActionHistoryChangeType.DIFF, true),
    MEMBER_ADD("%s added %s to the action", "%s joined the action", ActionHistoryChangeType.NOT_DIFF_NEW_DATA, true),
    MEMBER_REMOVE("%s removed %s from the action", "%s left the action", ActionHistoryChangeType.NOT_DIFF_OLD_DATA, true),
    ESTIMATION_CHANGE("%s changed the estimation from '%s' to '%s'", "%s changed the estimation to '%s'", ActionHistoryChangeType.DIFF, true);

    private final String details;
    private final String alternativeDetails; // Used when the user doing the action is normally a change of values but got only the new value
    private final ActionHistoryChangeType changeType;
    private final boolean visible;

    public ActionHistoryChangeType getChangeType(boolean alternate) {
        return alternate && !alternativeDetails.isEmpty() ? ActionHistoryChangeType.NOT_DIFF_NEW_DATA: changeType;
    }

    public String getDetails(boolean alternate) {
        return alternate && !alternativeDetails.isEmpty() ? alternativeDetails : details;
    }
}
