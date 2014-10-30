package org.activityinfo.server.digest;

import org.activityinfo.server.database.hibernate.entity.User;

import java.io.IOException;
import java.util.Date;

public interface DigestModelBuilder {

    public abstract DigestModel createModel(User user, Date date, int days) throws IOException;

}