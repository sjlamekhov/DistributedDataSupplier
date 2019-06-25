package dao;

import org.apache.commons.lang3.RandomUtils;

public class UriGenerator {

    private static final int length = 24;

    public static String generateId() {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < length) {
            stringBuilder.append(Integer.toHexString(RandomUtils.nextInt()));
        }
        return stringBuilder.substring(0, length);
    }

}
