package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import spark.ssl.SslStores;

import java.util.Map;

import static org.junit.Assert.*;

public class SocketConnectorFactoryTest {

    @Test
    public void testCreateSocketConnector_whenServerIsNull_thenThrowException() {

        try {
            SocketConnectorFactory.createSocketConnector(null, "host", 80, false, true);
            fail("SocketConnector creation should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException ex) {
            assertEquals("'server' must not be null", ex.getMessage());
        }
    }


    @Test
    public void testCreateSocketConnector_whenHostIsNull_thenThrowException() {

        Server server = new Server();

        try {
            SocketConnectorFactory.createSocketConnector(server, null, 80, false, true);
            fail("SocketConnector creation should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException ex) {
            assertEquals("'host' must not be null", ex.getMessage());
        }
    }

    @Test
    public void testCreateSocketConnector() {

        final String host = "localhost";
        final int port = 8888;

        Server server = new Server();
        ServerConnector serverConnector = SocketConnectorFactory.createSocketConnector(server, "localhost", 8888, false, true);

        String internalHost = Whitebox.getInternalState(serverConnector, "_host");
        int internalPort = Whitebox.getInternalState(serverConnector, "_port");
        Server internalServerConnector = Whitebox.getInternalState(serverConnector, "_server");

        assertEquals("Server Connector Host should be set to the specified server", host, internalHost);
        assertEquals("Server Connector Port should be set to the specified port", port, internalPort);
        assertEquals("Server Connector Server should be set to the specified server", internalServerConnector, server);
    }

    @Test
    public void testCreateSecureSocketConnector_whenServerIsNull() {

        try {
            SocketConnectorFactory.createSecureSocketConnector(null, "localhost", 80, null, false, true);
            fail("SocketConnector creation should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException ex) {
            assertEquals("'server' must not be null", ex.getMessage());
        }
    }

    @Test
    public void testCreateSecureSocketConnector_whenHostIsNull() {

        Server server = new Server();

        try {
            SocketConnectorFactory.createSecureSocketConnector(server, null, 80, null, false, true);
            fail("SocketConnector creation should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException ex) {
            assertEquals("'host' must not be null", ex.getMessage());
        }
    }

    @Test
    public void testCreateSecureSocketConnector_whenSslStoresIsNull() {

        Server server = new Server();

        try {
            SocketConnectorFactory.createSecureSocketConnector(server, "localhost", 80, null, false,  true);
            fail("SocketConnector creation should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException ex) {
            assertEquals("'sslStores' must not be null", ex.getMessage());
        }
    }


    @Test
    @PrepareForTest({ServerConnector.class})
    public void testCreateSecureSocketConnector() throws  Exception {

        final String host = "localhost";
        final int port = 8888;

        final String keystoreFile = "./target/test-classes/keystoreFile.jks";
        final String keystorePassword = "keystorePassword";
        final String truststoreFile = "./target/test-classes/truststoreFile.jks";
        final String trustStorePassword = "trustStorePassword";

        SslStores sslStores = SslStores.create(keystoreFile, keystorePassword, truststoreFile, trustStorePassword);

        Server server = new Server();

        // This is not required anymore -- peace out SocketConnectorFactory.ENABLE_JETTY_11_COMPATIBILITY = true;

        ServerConnector serverConnector = SocketConnectorFactory.createSecureSocketConnector(server, host, port, sslStores, false, true);

        String internalHost = Whitebox.getInternalState(serverConnector, "_host");
        int internalPort = Whitebox.getInternalState(serverConnector, "_port");

        assertEquals("Server Connector Host should be set to the specified server", host, internalHost);
        assertEquals("Server Connector Port should be set to the specified port", port, internalPort);

        Map<String, ConnectionFactory> factories = Whitebox.getInternalState(serverConnector, "_factories");

        assertTrue("Should return true because factory for SSL should have been set",
                factories.containsKey("ssl") && factories.get("ssl") != null);

        SslConnectionFactory sslConnectionFactory = (SslConnectionFactory) factories.get("ssl");
        SslContextFactory sslContextFactory = sslConnectionFactory.getSslContextFactory();

        assertEquals("Should return the Keystore file specified", "keystoreFile.jks",
                sslContextFactory.getKeyStoreResource().getFileName());

        assertEquals("Should return the Truststore file specified", "truststoreFile.jks",
                sslContextFactory.getTrustStoreResource().getFileName());

    }

}
