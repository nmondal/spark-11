package spark;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A Test class to check the performance implication
 * A Jetty12 vs Spark11 Comparator
 */
public class CompareApp {

    static final String RESPONSE = "Hello World" ;

    public static final class Spark11{
        public static void run(){
            Spark.port(8080);
            Spark.get( "/", (req,resp) -> RESPONSE);
        }
    }

    public static final class Jetty12{

        public static void run() throws Exception {
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setName("server");
            // Create a Server instance.
            Server server = new Server(threadPool);
            // Create a ServerConnector to accept connections from clients.
            Connector connector = new ServerConnector(server);
            ((ServerConnector)connector).setPort(8080);
            // Add the Connector to the Server
            server.addConnector(connector);

            // Set a simple Handler to handle requests/responses.
            server.setHandler(new Handler.Abstract()
            {
                @Override
                public boolean handle(org.eclipse.jetty.server.Request request, org.eclipse.jetty.server.Response response, Callback callback) throws Exception {
                    final ByteBuffer resp = ByteBuffer.wrap( RESPONSE.getBytes(StandardCharsets.UTF_8));
                    response.write(true, resp,  Callback.NOOP );
                    // Succeed the callback to signal that the
                    // request/response processing is complete.
                    callback.succeeded();
                    return true;
                }
            });

            server.start();
        }
    }

    public static void main(String[] args) throws Exception {
        Spark11.run();
        //Jetty12.run();
    }
}
