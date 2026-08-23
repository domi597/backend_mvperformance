package at.htlkaindorf.backend_mwperformence.util;

public final class PhoneUtils {

    private static final String DEFAULT_COUNTRY_CODE = "43";

    private PhoneUtils() {
    }
    public static String normalize(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return null;

        boolean hasPlus = trimmed.startsWith("+");
        String digits = trimmed.replaceAll("[^0-9]", "");

        String national;
        if (hasPlus) {
            String stripped = stripLeadingZeros(digits);
            national = stripped.startsWith(DEFAULT_COUNTRY_CODE)
                    ? stripped.substring(DEFAULT_COUNTRY_CODE.length())
                    : stripped;
        } else if (digits.startsWith("00")) {
            String stripped = stripLeadingZeros(digits.substring(2));
            national = stripped.startsWith(DEFAULT_COUNTRY_CODE)
                    ? stripped.substring(DEFAULT_COUNTRY_CODE.length())
                    : stripped;
        } else if (digits.startsWith("0")) {
            national = digits.substring(1);
        } else {
            national = digits;
        }

        return "0" + groupNational(national);
    }

    private static String groupNational(String national) {
        if (national.length() <= 3) {
            return national;
        }
        return national.substring(0, 3) + " " + national.substring(3);
    }

    private static String stripLeadingZeros(String digits) {
        return digits.replaceFirst("^0+(?=.)", "");
    }
}
