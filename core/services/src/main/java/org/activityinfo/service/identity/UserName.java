package org.activityinfo.service.identity;

import javax.annotation.Nonnull;

public class UserName {
    private final String email;
    private final String name;

    public UserName(@Nonnull String email, @Nonnull String name) {
        this.email = email;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserName userName = (UserName) o;

        if (!email.equals(userName.email)) return false;
        if (!name.equals(userName.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
