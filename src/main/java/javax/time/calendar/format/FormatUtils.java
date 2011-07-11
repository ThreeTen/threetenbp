package javax.time.calendar.format;

public class FormatUtils {
    public static boolean regionMatches(boolean ignoreCase, CharSequence a,
            int aOffset, CharSequence b, int bOffset, int len) {

        if ((bOffset < 0) || (aOffset < 0)) {
            return false;
        }
        if ((aOffset > (long) a.length() - len)
                || (bOffset > (long) b.length() - len)) {
            return false;
        }

        while (len-- > 0) {
            char c1 = a.charAt(aOffset++);
            char c2 = b.charAt(bOffset++);
            if (c1 == c2) {
                continue;
            }
            if (ignoreCase) {
                // If characters don't match but case may be ignored,
                // try converting both characters to uppercase.
                // If the results match, then the comparison scan should
                // continue.
                char u1 = Character.toUpperCase(c1);
                char u2 = Character.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }
                // Unfortunately, conversion to uppercase does not work properly
                // for the Georgian alphabet, which has strange rules about case
                // conversion. So we need to make one last check before
                // exiting.
                if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }
}
