package spark.embeddedserver.jetty.websocket;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketOpen;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@WebSocket
public class WebSocketTestClient {
    private final CountDownLatch closeLatch;

    public WebSocketTestClient() {
        closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        closeLatch.countDown();
    }

    @OnWebSocketOpen
    public void onConnect(Session session) {
        session.sendText("Hi Spark!", Callback.NOOP);
        session.close(StatusCode.NORMAL, "Bye!", Callback.NOOP);
    }
}
