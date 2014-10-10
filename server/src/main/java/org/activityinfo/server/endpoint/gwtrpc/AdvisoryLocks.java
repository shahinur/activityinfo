package org.activityinfo.server.endpoint.gwtrpc;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.legacy.shared.command.BatchCommand;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.MutatingCommand;
import org.hibernate.Query;
import org.hibernate.ejb.HibernateEntityManager;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yuriyz on 10/10/2014.
 */
public class AdvisoryLocks {

    private static final Logger LOGGER = Logger.getLogger(AdvisoryLocks.class.getName());

    private static final String ADVISORY_LOCK_NAME = "activityinfo.remote_execution_context";
    private static final int ADVISORY_GET_LOCK_TIMEOUT = 10;

    private final HibernateEntityManager entityManager;

    public AdvisoryLocks(HibernateEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void releaseAdvisoryLock() {
        String sql = String.format("SELECT RELEASE_LOCK('%s')", ADVISORY_LOCK_NAME);
        try {
            Query query = entityManager.getSession().createSQLQuery(sql);
            Object result = query.uniqueResult();
            if (result instanceof BigInteger && ((BigInteger) result).intValue() == 1) {
                return; // released successfully
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception during releasing advisory lock, sql:" + sql, e);
        }
        LOGGER.log(Level.SEVERE, "Failed to releasing advisory lock, sql:" + sql);
    }

    public boolean hasAdvisoryLock() {
        String sql = String.format("SELECT IS_USED_LOCK('%s')", ADVISORY_LOCK_NAME);
        try {
            Query query = entityManager.getSession().createSQLQuery(sql);
            Object result = query.uniqueResult();
            if (result instanceof String) {
                return !((String)result).isEmpty();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception during checking advisory lock, sql:" + sql, e);
        }
        return false;
    }

    /**
     *
     * @param command command to execute
     * @return whether lock was obtained
     */
    public boolean takeAdvisoryLock(Command command) {
        if (hasMutatingCommand(command) && !hasAdvisoryLock()) {
            String sql = String.format("SELECT GET_LOCK('%s', %s)", ADVISORY_LOCK_NAME, ADVISORY_GET_LOCK_TIMEOUT);
            try {
                Query query = entityManager.getSession().createSQLQuery(sql);
                Object result = query.uniqueResult();
                if (result instanceof BigInteger && ((BigInteger) result).intValue() == 1) {
                    return true;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception during getting advisory lock, sql:" + sql, e);
            }
            // todo failover ?
            //throw new IllegalStateException("Failed to obtain advisory lock.");
        }
        return false;
    }

    public static boolean hasMutatingCommand(Command command) {
        if (command instanceof MutatingCommand) {
            return true;
        } else if (command instanceof BatchCommand) {
            BatchCommand batchCommand = (BatchCommand) command;
            for (Command innerCommand : batchCommand.getCommands()) {
                if (innerCommand instanceof MutatingCommand) {
                    return true;
                }
            }
        }
        return false;
    }
}
