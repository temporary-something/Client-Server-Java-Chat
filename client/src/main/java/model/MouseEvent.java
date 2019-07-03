package model;

import model.enums.EventType;

public class MouseEvent implements Event {
    private static final long serialVersionUID = -2264824570373866709L;

    private EventType eventType;
    private double posX;
    private double posY;

    public static MouseEvent newInstance(EventType eventType, double posX, double posY) {
        return new MouseEvent(eventType, posX, posY);
    }

    private MouseEvent(EventType eventType, double posX, double posY) {
        this.eventType = eventType;
        this.posX = posX;
        this.posY = posY;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "MouseEvent{" +
                "posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
