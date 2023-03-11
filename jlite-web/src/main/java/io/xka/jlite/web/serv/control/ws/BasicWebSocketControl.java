package io.xka.jlite.web.serv.control.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import java.util.function.Consumer;

public class BasicWebSocketControl implements WebSocketListener {

    private WSControl wsControl;

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.println("ws close");
    }

    @Override
    public void onWebSocketConnect(Session session) {
        String path = session
                .getUpgradeRequest()
                .getRequestURI()
                .getPath();
        Consumer<WSControl> wsControlConsumer = WSControlFactory.get(path);
        wsControl = new WSControl(session);
        wsControlConsumer.accept(wsControl);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        wsControl.binaryQueue.put(payload);
    }

    @Override
    public void onWebSocketText(String message) {
        wsControl.textQueue.put(message);
    }
}
