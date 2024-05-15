package spark.embeddedserver.jetty.websocket;

import java.lang.reflect.InvocationTargetException;

import static java.util.Objects.requireNonNull;

public class WebSocketHandlerClassWrapper implements WebSocketHandlerWrapper {
    
    private final Class<?> handlerClass;

    public WebSocketHandlerClassWrapper(Class<?> handlerClass) {
        requireNonNull(handlerClass, "WebSocket handler class cannot be null");
        WebSocketHandlerWrapper.validateHandlerClass(handlerClass);
        this.handlerClass = handlerClass;
    }
    @Override
    public Object getHandler() {
        try {
            return handlerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException("Could not instantiate websocket handler", ex);
        }
    }

}
