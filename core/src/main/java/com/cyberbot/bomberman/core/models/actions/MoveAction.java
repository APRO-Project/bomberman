package com.cyberbot.bomberman.core.models.actions;

public class MoveAction extends Action {
    public static final int LEFT = 0x01;
    public static final int RIGHT = 0x02;
    public static final int UP = 0x04;
    public static final int DOWN = 0x08;

    private int direction;

    public MoveAction(int direction) {
        super(Type.MOVE);
        this.direction = direction;
    }

    protected MoveAction(Type type) {
        super(type);
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "MoveAction{" +
            "direction=" + direction +
            '}';
    }
}
