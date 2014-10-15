package org.activityinfo.legacy.shared.util;
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

/**
 * @author yuriyz on 10/15/2014.
 */
public class Commands {

    private Commands() {
    }

    public static boolean hasMutatingCommand(Command command) {
        if (command instanceof MutatingCommand) {
            return true;
        } else if (command instanceof BatchCommand) {
            BatchCommand batchCommand = (BatchCommand) command;
            for (Command innerCommand : batchCommand.getCommands()) {
                if (hasMutatingCommand(innerCommand)) {
                    return true;
                }
            }
        }
        return false;
    }
}
