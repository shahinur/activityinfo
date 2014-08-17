package org.activityinfo.ui.style.tools.base;

import com.google.common.base.Strings;
import com.google.gwt.core.ext.TreeLogger;

class CliTreeLogger extends TreeLogger {

    private String indent = "";
    private Type logLevel;

    CliTreeLogger(Type logLevel) {
        this.logLevel = logLevel;
    }

    CliTreeLogger(Type logLevel, String indent) {
        this.logLevel = logLevel;
        this.indent = indent;
    }

    @Override
    public TreeLogger branch(Type type, String s, Throwable throwable, HelpInfo helpInfo) {
        log(type, s, throwable, helpInfo);
        return new CliTreeLogger(logLevel, indent + "  ");
    }

    @Override
    public boolean isLoggable(Type type) {
        return !type.isLowerPriorityThan(logLevel);
    }

    @Override
    public void log(Type type, String s, Throwable throwable, HelpInfo helpInfo) {
        System.out.println(indent + "[" + Strings.padEnd(type.name(), 6, ' ') + "] " + s);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
