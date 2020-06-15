package com.cyberbot.bomberman.core.models.actions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * THe base for all actions. Contains an inner enum for more optimal serialization.
 */
public abstract class Action implements Serializable {
    private final Type type;

    protected Action(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        // Smell that? That's Java's lack of byte literals.
        MOVE((byte) 0),
        USE_ITEM((byte) 1);

        private final byte value;
        private static final Map<Byte, Type> map = new HashMap<>();

        static {
            for (Type pageType : Type.values()) {
                map.put(pageType.value, pageType);
            }
        }

        Type(byte value) {
            this.value = value;
        }

        public static Type valueOf(byte pageType) {
            return map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }
}