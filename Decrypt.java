import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

public class Decrypt {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java Decrypt <passphrase> <encrypted-file> <output-file>");
            return;
        }

        String passphrase = args[0];
        String encryptedFile = args[1];
        String outputFile = args[2];

        // Read file lines
        String[] lines = Files.readAllLines(Paths.get(encryptedFile)).toArray(new String[0]);
        BigInteger zx = new BigInteger(lines[0].trim());
        BigInteger zy = new BigInteger(lines[1].trim());
        EdwardsPoint Z = new EdwardsPoint(zx, zy);

        // Regenerate private scalar s from passphrase
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        byte[] hash = sha512.digest(passphrase.getBytes(StandardCharsets.UTF_8));
        BigInteger r = EdwardsPoint.getCurveOrderR();
        BigInteger s = new BigInteger(1, hash).mod(r);

        // Compute W = s * Z
        EdwardsPoint W = Z.scalarMultiply(s);
        byte[] Wx = W.x.toByteArray();

        // Decode base64
        byte[] encrypted = Base64.getDecoder().decode(lines[2].trim());

        Wx = W.x.toByteArray();
        byte[] message;

        try {
            message = AESGCM.decrypt(Wx, encrypted);
        } catch (Exception e) {
            System.out.println("Decryption failed: " + e.getMessage());
            return;
        }

        // Write decrypted message to output file
        Files.write(Paths.get(outputFile), message);
        System.out.println("Decrypted message written to: " + outputFile);
    }
}
