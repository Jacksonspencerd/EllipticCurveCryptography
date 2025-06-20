import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Keygen {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java Keygen <passphrase> <output-file>");
            return;
        }

        String passphrase = args[0];
        String outputFile = args[1];

        // Hash the passphrase with SHA-512
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        byte[] hash = sha512.digest(passphrase.getBytes(StandardCharsets.UTF_8));

        // Convert hash to BigInteger and reduce mod r
        BigInteger r = EdwardsPoint.getCurveOrderR();
        BigInteger s = new BigInteger(1, hash).mod(r); // private scalar

        // Public key: V = s * G
        EdwardsPoint G = EdwardsPoint.generator();
        EdwardsPoint V = G.scalarMultiply(s);

        // Save the public key to a file as (x, y)
        String output = V.x.toString() + "\n" + V.y.toString();
        Files.write(Paths.get(outputFile), output.getBytes(StandardCharsets.UTF_8));

        System.out.println("Public key written to: " + outputFile);
    }
}
