import java.math.BigInteger;
import java.security.SecureRandom;

public class EdwardsPoint {

    // Field prime: p = 2^256 - 189
    private static final BigInteger p = BigInteger.valueOf(2).pow(256).subtract(BigInteger.valueOf(189));
    private static final BigInteger d = BigInteger.valueOf(15343); // curve constant
    private static final EdwardsPoint O = new EdwardsPoint(BigInteger.ZERO, BigInteger.ONE); // identity

    public final BigInteger x;
    public final BigInteger y;

    public EdwardsPoint(BigInteger x, BigInteger y) {
        this.x = x.mod(p);
        this.y = y.mod(p);
    }

    public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
        if (v.signum() == 0) return BigInteger.ZERO;

        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
            r = p.subtract(r); // correct LSB
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }

    public static EdwardsPoint generator() {
        BigInteger x0 = new BigInteger("71960290988967339173789813630180596073015995841422083565797893352357436157838");
        BigInteger y0 = getModulus().subtract(BigInteger.valueOf(4));
        return new EdwardsPoint(x0, y0);
    }



    public static EdwardsPoint identity() {
        return O;
    }

    public EdwardsPoint negate() {
        return new EdwardsPoint(p.subtract(x), y);
    }

    public EdwardsPoint add(EdwardsPoint Q) {
        BigInteger x1 = this.x;
        BigInteger y1 = this.y;
        BigInteger x2 = Q.x;
        BigInteger y2 = Q.y;

        BigInteger xNum = x1.multiply(y2).add(y1.multiply(x2)).mod(p);
        BigInteger xDen = BigInteger.ONE.add(d.multiply(x1).multiply(x2).multiply(y1).multiply(y2)).mod(p);

        BigInteger yNum = y1.multiply(y2).subtract(x1.multiply(x2)).mod(p);
        BigInteger yDen = BigInteger.ONE.subtract(d.multiply(x1).multiply(x2).multiply(y1).multiply(y2)).mod(p);

        BigInteger x3 = xNum.multiply(xDen.modInverse(p)).mod(p);
        BigInteger y3 = yNum.multiply(yDen.modInverse(p)).mod(p);

        return new EdwardsPoint(x3, y3);
    }

    public EdwardsPoint scalarMultiply(BigInteger k) {
        if (k.signum() == 0) return O;
        if (k.signum() < 0) return this.negate().scalarMultiply(k.negate());

        EdwardsPoint result = O;
        EdwardsPoint addend = this;

        for (int i = k.bitLength() - 1; i >= 0; i--) {
            result = result.add(result);
            if (k.testBit(i)) {
                result = result.add(addend);
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof EdwardsPoint)) return false;
        EdwardsPoint other = (EdwardsPoint) obj;
        return this.x.equals(other.x) && this.y.equals(other.y);
    }

    public String toString() {
        return "(" + x.toString() + ", " + y.toString() + ")";
    }

    // Accessors for field modulus and curve parameters
    public static BigInteger getModulus() {
        return p;
    }

    public static BigInteger getCurveOrderR() {
        return BigInteger.valueOf(2).pow(254).subtract(new BigInteger("87175310462106073678594642380840586067"));
    }

    public static BigInteger getD() {
        return d;
    }
}
