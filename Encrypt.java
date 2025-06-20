import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class Encrypt {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java Encrypt <public-key-file> <message-file> <output-file>");
            return;
        }

        String pubKeyFile = args[0];
        String messageFile = args[1];
        String outputFile = args[2];

        // Load public key (V = (x, y))
        String[] lines = Files.readAllLines(Paths.get(pubKeyFile)).toArray(new String[0]);
        BigInteger vx = new BigInteger(lines[0]);
        BigInteger vy = new BigInteger(lines[1]);
        EdwardsPoint V = new EdwardsPoint(vx, vy);

        // Load message
        byte[] message = Files.readAllBytes(Paths.get(messageFile));

        // Generate ephemeral scalar u
        SecureRandom rand = new SecureRandom();
        BigInteger r = EdwardsPoint.getCurveOrderR();
        BigInteger u;
        do {
            u = new BigInteger(512, rand).mod(r);
        } while (u.signum() == 0);

        // Compute W = u * V, Z = u * G
        EdwardsPoint G = EdwardsPoint.generator();
        EdwardsPoint W = V.scalarMultiply(u);
        EdwardsPoint Z = G.scalarMultiply(u);

        // Derive AES key from W.x
        byte[] Wx = W.x.toByteArray();
        byte[] encrypted = AESGCM.encrypt(Wx, message);

        String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);

        // Write output
        StringBuilder output = new StringBuilder();
        output.append(Z.x.toString()).append("\n");
        output.append(Z.y.toString()).append("\n");
        output.append(encryptedBase64).append("\n");

        Files.write(Paths.get(outputFile), output.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("Encrypted message written to: " + outputFile);
    }
}
