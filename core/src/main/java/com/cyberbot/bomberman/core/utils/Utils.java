package com.cyberbot.bomberman.core.utils;

import java.io.*;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

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

    public static Object fromByteArray(byte[] buf) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
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

    public static <T> T first(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream()
            .filter(predicate)
            .findFirst()
            .orElseThrow(() ->
                new NoSuchElementException("No element matching predicate in the collection"));
    }

    public static <T> T firstOrNull(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream()
            .filter(predicate)
            .findFirst()
            .orElse(null);
    }
}
