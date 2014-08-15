package org.activityinfo.ui.store.remote.server;

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.core.ext.ServletContainerLauncher;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import java.io.Closeable;
import java.io.File;
import java.net.BindException;

public class JerseyLauncher extends ServletContainerLauncher {


    @Override
    public ServletContainer start(TreeLogger treeLogger, int port, File appRootDir) throws BindException, Exception {

        TreeLogger logger = treeLogger.branch(TreeLogger.Type.INFO, "Starting Jersey Test Servlet");

        Closeable server;

        try {
            server = TestApplication.start(port);
        } catch (Exception e) {
            logger = treeLogger.branch(TreeLogger.Type.INFO, "Error starting server" ,e);
            throw new UnableToCompleteException();
        }

        return new JerseyServletContainer(logger, server, port);
    }
}
