package com.cyberbot.bomberman.core.utils;

import java.io.*;

public final class Utils {
    public static byte[] toByteArray(Object o) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out;
            out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T fromByteArray(byte[] buf, Class<T> cls) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            Object o = in.readObject();
            return cls.cast(o);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isSequenceNext(int sequence, int previous) {
        return isSequenceNext(sequence, previous, Integer.MAX_VALUE / 100);
    }

    public static boolean isSequenceNext(int sequence, int previous, int maxDrop) {
        if (Math.abs(sequence - previous) < maxDrop) {
            return sequence >= previous;
        } else {
            return sequence <= previous;
        }
    }
}
