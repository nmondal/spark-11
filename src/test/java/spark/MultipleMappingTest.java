package spark;

import org.junit.*;
import spark.util.SparkTestUtil;

import java.util.*;

import static spark.Service.ignite;

public class MultipleMappingTest {

    private static final int SOME_PORT = 8888;
    private static Service service;

    static final class ParamCollector{
        final List<String> requestBodies = Collections.synchronizedList( new ArrayList<>() );
        final List<Response> responses = Collections.synchronizedList( new ArrayList<>() );

        Route route( Object ret ){
            return (req,resp) ->{
                requestBodies.add(req.body());
                responses.add(resp);
                return ret;
            };
        }

        Filter filter( ){
            return (req,resp) ->{
                requestBodies.add(req.body());
                responses.add(resp);
            };
        }
    }

    private static final ParamCollector paramCollector = new ParamCollector();

    @BeforeClass
    public static void setUpClass() throws Exception {
        service = ignite();
        service.port(SOME_PORT);
        service.before( "/x", paramCollector.filter() );
        service.before( "/x", paramCollector.filter() );
        service.before( "/x", paramCollector.filter() );
        service.before( "/x", paramCollector.filter() );
        service.post( "/x", paramCollector.route( "42" ) );

        service.init();
        service.awaitInitialization();
    }

    @Test
    public void testUniqueRequestResponseInSameChain() throws Exception {
        SparkTestUtil testUtil = new SparkTestUtil(SOME_PORT);
        SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", "/x", "foo-bar" );
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("42", response.body);
        Set<Response> responseSet = new HashSet<>( paramCollector.responses );
        Assert.assertEquals(1, responseSet.size() );
        paramCollector.requestBodies.forEach( body -> Assert.assertEquals("foo-bar", body) );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (service != null) {
            service.stop();
        }
    }
}
