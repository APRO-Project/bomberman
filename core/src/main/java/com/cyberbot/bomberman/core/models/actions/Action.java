package com.cyberbot.bomberman.core.models.actions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Action implements Serializable {
    private final Type type;

    protected Action(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        USE_ITEM(1);

        private final int value;
        private static final Map<Integer, Type> map = new HashMap<>();

        Type(int value) {
            this.value = value;
        }

        static {
            for (Type pageType : Type.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static Type valueOf(int pageType) {
            return map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }
}