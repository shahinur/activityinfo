package org.activityinfo.store.cloudsql;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.*;
import java.io.IOException;


/**
 * Ensures that any open connections are closed when the request completes.
 */
@Singleton
public class ConnectionCleanupFilter implements javax.servlet.Filter {

    private final ConnectionProvider connectionProvider;

    @Inject
    public ConnectionCleanupFilter(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        try {
            filterChain.doFilter(servletRequest, servletResponse);

            // Cleanup normally
            connectionProvider.cleanupAfterRequestFinishes(false);

        } catch (Exception e) {

            // Cleanup after an error
            connectionProvider.cleanupAfterRequestFinishes(true);
        }
    }

    @Override
    public void destroy() {
    }
}
