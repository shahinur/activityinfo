package org.activityinfo.server.event.sitehistory;

import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.CreateSite;
import org.activityinfo.legacy.shared.command.DeleteSite;
import org.activityinfo.legacy.shared.command.UpdateSite;
import org.activityinfo.server.event.CommandEvent;

public enum ChangeType {
    CREATE,
    UPDATE,
    DELETE,
    UNKNOWN;

    public static ChangeType getType(CommandEvent event) {
        return ChangeType.getType(event.getCommand());
    }

    @SuppressWarnings("rawtypes")
    public static ChangeType getType(Command cmd) {
        if (cmd instanceof CreateSite) {
            return CREATE;
        } else if (cmd instanceof UpdateSite) {
            return UPDATE;
        } else if (cmd instanceof DeleteSite) {
            return DELETE;
        } else {
            return UNKNOWN;
        }
    }

    public boolean isKnown() {
        return (this != UNKNOWN);
    }

    public boolean isNew() {
        return (this == CREATE);
    }

    public boolean isNewOrUpdate() {
        return (this == CREATE || this == UPDATE);
    }

    public boolean isDelete() {
        return (this == DELETE);
    }
}
