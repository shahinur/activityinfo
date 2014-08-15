package org.activityinfo.ui.store.remote.server;

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import java.io.Closeable;
import java.io.IOException;

public class JerseyServletContainer extends ServletContainer {
    private TreeLogger logger;
    private Closeable server;
    private int port;

    public JerseyServletContainer(TreeLogger treeLogger, Closeable server, int port) {
        this.logger = treeLogger;
        this.server = server;
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void refresh() throws UnableToCompleteException {
        logger.log(TreeLogger.Type.INFO, "Refresh requested (not implemented).");
    }

    @Override
    public void stop() throws UnableToCompleteException {
        try {
            logger.log(TreeLogger.Type.INFO, "Stopping...");
            server.close();
            logger.log(TreeLogger.Type.INFO, "Stopped.");

        } catch (IOException e) {
            logger.log(TreeLogger.Type.ERROR, "Exception thrown while stopping", e);
        }
    }
}
