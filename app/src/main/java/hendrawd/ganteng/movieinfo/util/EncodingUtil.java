package hendrawd.ganteng.movieinfo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Utility class for JavaScript compatible UTF-8 encoding and decoding.
 *
 * @author John Topley
 * @see http://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent-that-produces-identical-output
 */
public class EncodingUtil {
    /**
     * Decodes the passed UTF-8 String using an algorithm that's compatible with
     * JavaScript's <code>decodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The UTF-8 encoded String to be decoded
     * @return the decoded String
     */
    private static final String UTF8 = "UTF-8";

    public static String decodeURIComponent(String s) {
        if (s == null) {
            return null;
        }

        String result;

        try {
            result = URLDecoder.decode(s, UTF8);
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        } catch (Exception e) {
            result = s;
        }
        return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The String to be encoded
     * @return the encoded String
     */
    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, UTF8)
                    .replaceAll("\\+", "%20")
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        } catch (Exception e) {
            result = s;
        }

        return result;
    }

    /**
     * Private constructor to prevent this class from being instantiated.
     */
    private EncodingUtil() {
        super();
    }
}
