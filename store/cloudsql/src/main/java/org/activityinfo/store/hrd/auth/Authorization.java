package org.activityinfo.store.hrd.auth;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public abstract class Authorization {

    public abstract boolean isOwner();

    public abstract boolean canEdit();

    public abstract boolean canView();


    public void assertCanEdit() {
        if(!canEdit()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }
    }

    public void assertCanView() {
        if (!canView()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }
    }

    public void assertIsOwner() {
        if (!isOwner()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }
    }
}
