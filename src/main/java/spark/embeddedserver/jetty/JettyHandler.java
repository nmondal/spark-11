package spark.embeddedserver.jetty;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.ee10.servlet.ServletContextRequest;
import org.eclipse.jetty.ee10.servlet.SessionHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

/**
 * Simple Jetty Handler
 *
 * @author Per Wendel
 */
public class JettyHandler extends SessionHandler {
    private final Filter filter;

    public JettyHandler(Filter filter) {
        this.filter = filter;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        if (request instanceof ServletContextRequest servletContextRequest) {
            final HttpServletResponse httpServletResponse = servletContextRequest.getHttpServletResponse();
            final HttpServletRequest httpServletRequest = servletContextRequest.getServletApiRequest();
            final HttpRequestWrapper wrapper = new HttpRequestWrapper(httpServletRequest);

            filter.doFilter(wrapper, httpServletResponse, null);
            callback.succeeded();
            return true;
        }

        return false;
    }
}
