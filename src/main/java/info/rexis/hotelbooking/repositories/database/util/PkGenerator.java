package info.rexis.hotelbooking.repositories.database.util;

import org.springframework.security.crypto.codec.Hex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PkGenerator {
    private PkGenerator() {
    }

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String generatePk(int id) {
        return df.format(new Date())
                + String.format(" %06d %s", id, randomHex());
    }

    private static String randomHex() {
        byte[] resBuf = new byte[8];
        new Random().nextBytes(resBuf);
        return new String(Hex.encode(resBuf));
    }
}
