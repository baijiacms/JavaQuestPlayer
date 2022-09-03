package com.baijiacms.qsp.jfxviewer;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 禁用右击
 * @author cxy
 */
public class MyEventDispatcher implements EventDispatcher {

    private EventDispatcher originalDispatcher;

    public MyEventDispatcher(EventDispatcher originalDispatcher) {
        this.originalDispatcher = originalDispatcher;
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            if (MouseButton.SECONDARY == mouseEvent.getButton()) {
                mouseEvent.consume();
            }
        }
        return originalDispatcher.dispatchEvent(event, tail);
    }
}
