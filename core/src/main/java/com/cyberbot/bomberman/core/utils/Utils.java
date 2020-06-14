package com.cyberbot.bomberman.core.utils;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public final class Utils {
    public static String[] MALE_NAMES = {"Bob", "James", "John", "Robert", "Michael",
        "William", "David", "Richard", "Joseph", "Charles", "Thomas", "Bryan"};
    public static String[] FEMALE_NAMES = {"Alice", "Mary", "Patricia", "Jennifer",
        "Elizabeth", "Linda", "Barbara", "Susan", "Margaret", "Jessica", "Sarah", "Karen"};

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

    public static Object fromByteArray(byte[] buf, int offset, int length) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String generateLobbyId(int length) {
        return RandomStringUtils.randomNumeric(length);
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

    // From: https://stackoverflow.com/a/2347356/4061413
    public static InetSocketAddress parseServerString(String s, int defaultPort) throws URISyntaxException {
        URI uri = new URI("my://" + s);
        String host = uri.getHost();
        int port = uri.getPort();

        if (uri.getHost() == null) {
            throw new URISyntaxException(uri.toString(), "URI must have the host part");
        }

        if (port == -1) {
            port = defaultPort;
        }

        return new InetSocketAddress(host, port);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String hashPassword(String password) {
        return Hashing.sha256()
            .hashString(password, StandardCharsets.UTF_8)
            .toString();
    }

    public static String generatePlayerNick() {
        String suffix = RandomStringUtils.randomNumeric(RandomUtils.nextInt(2, 4));
        if (RandomUtils.nextBoolean()) {
            return MALE_NAMES[RandomUtils.nextInt(0, MALE_NAMES.length)] + suffix;
        } else {
            return FEMALE_NAMES[RandomUtils.nextInt(0, FEMALE_NAMES.length)] + suffix;
        }
    }
}
