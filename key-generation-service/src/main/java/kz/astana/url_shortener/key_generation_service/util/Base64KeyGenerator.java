package kz.astana.url_shortener.key_generation_service.util;

public class Base64KeyGenerator {

    private static final char[] BASE64_URL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
            .toCharArray();

    public static String toBase64Key6(long value) {
        char[] out = new char[6];

        for (int i = 5; i >= 0; i--) {
            out[i] = BASE64_URL[(int) (value & 0b111111)];
            value >>>= 6;
        }

        return new String(out);
    }
}
