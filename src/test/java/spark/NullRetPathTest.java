package spark;

import org.junit.*;
import spark.util.SparkTestUtil;

import static spark.Service.ignite;

public class NullRetPathTest {

    private static final int SOME_PORT = 8888;
    private static Service service;

    @BeforeClass
    public static void setUpClass() {
        service = ignite();
        service.port(SOME_PORT);
        service.get("/null", (q, a) ->  {
            a.status(200);
            return null;
        });
        service.init();
        service.awaitInitialization();
    }

    @Test
    public void testNullReturningPath() throws Exception {
        SparkTestUtil testUtil = new SparkTestUtil(SOME_PORT);
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/does_not_exist", null);
        Assert.assertEquals(404, response.status);
        response = testUtil.doMethod("GET", "/null", null);
        Assert.assertEquals(200, response.status);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service.stop();
    }
}
