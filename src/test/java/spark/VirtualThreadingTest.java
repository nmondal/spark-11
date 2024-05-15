package spark;

import org.eclipse.jetty.util.VirtualThreads;
import org.junit.*;
import spark.util.SparkTestUtil;

import static spark.Service.ignite;

public class VirtualThreadingTest {

    private static final int SOME_PORT = 9999;
    private static Service service;

    @BeforeClass
    public static void setUpClass() throws Exception {
        org.junit.Assume.assumeTrue("Virtual Thread Support is not there, will ignore",
            VirtualThreads.areSupported());
        service = ignite();
        service.port(SOME_PORT);
        service.useVThread(true);
        service.get("/hi", (q, a) -> Thread.currentThread().getClass().getName());
        service.init();
        service.awaitInitialization();
    }

    @Test
    public void testVirtualThreading() throws Exception {
        Assert.assertTrue(service.useVThread());
        SparkTestUtil testUtil = new SparkTestUtil(SOME_PORT);
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Not running in Virtual Thread!", "java.lang.VirtualThread", response.body);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (service != null) {
            service.stop();
        }
    }
}
