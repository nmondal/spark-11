package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spark.Service.ignite;

public class PerfTest {

    private static final int SOME_PORT = 8765;
    private static Service service;

    @BeforeClass
    public static void setUpClass() {
        service = ignite();
        service.port(SOME_PORT);
        service.get("/hello", (q, a) ->  {
            a.status(200);
            return "Hello, World!";
        });
        service.init();
        service.awaitInitialization();
    }

    public static List<Long> percentiles(List<Long> timeTaken){
        Collections.sort(timeTaken);
        int sz = timeTaken.size();
        long min = timeTaken.get(0);
        long max = timeTaken.get(sz-1);
        long q1 =  timeTaken.get( sz/4 );
        long med =  timeTaken.get( sz/2 );
        long q3 =  timeTaken.get( (sz*3) /4 );
        long nPc =  timeTaken.get( (sz*9) /10 );

        return List.of( min, q1, med, q3, nPc, max );
    }

    public static void  testForPerf( int port, int numRequests, int numThreads, long expectedSecondsToComplete) throws InterruptedException {
        List<Long> timeTaken = Collections.synchronizedList( new ArrayList<>() );
        AtomicInteger numErrors = new AtomicInteger(0);

        ExecutorService svc = Executors.newFixedThreadPool( numThreads );


        for ( int i =0; i < numRequests; i++ ) {
            svc.submit(() -> {
                SparkTestUtil testUtil = new SparkTestUtil(port);
                try {
                    SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hello", null);
                    Assert.assertEquals(200, response.status);
                    timeTaken.add(response.timeTaken);
                } catch (Exception e) {
                    numErrors.incrementAndGet();
                }
            });
        }

        svc.shutdown();
        assertTrue( svc.awaitTermination( expectedSecondsToComplete, TimeUnit.SECONDS) );

        List<Long> pcts = percentiles(timeTaken);
        System.out.printf( "(micro-sec) : %s %n", pcts );
        assertEquals( 0, numErrors.get() );

    }

    @Test
    public void testPerf1() throws Exception {
        testForPerf( SOME_PORT, 10000, 10 , 30 );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service.stop();
    }
}
