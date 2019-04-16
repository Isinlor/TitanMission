import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Some useful common functions.
 */
public class Utils {

    /**
     * Outputs easy to read string representing double.
     */
    public static String round(double n) {
        if(n == Double.NEGATIVE_INFINITY || n == Double.POSITIVE_INFINITY) {
            return "Infinity";
        } else if(Double.isNaN(n)) {
            return "NaN";
        }
        BigDecimal bd = new BigDecimal(n);
        bd = bd.round(new MathContext(3));
        return bd.toString();
    }

}
