package personal.lyh.voter.other;

public class MyValidator {
    //若使用链式调用，在参数少的时候反而冗余，而大多数时候要验证的参数没那么多就没必要

    public static boolean verifyString(String aimString) {
        return aimString != null && !aimString.equals("");
    }

    public static boolean verifyString(String aimString, int minLength) {
        return aimString != null && !aimString.equals("") && aimString.length() >= minLength;
    }

    public static boolean verifyString(String aimString, int minLength, int maxLength) {
        return aimString != null && !aimString.equals("") && aimString.length() >= minLength && aimString.length() <= maxLength;
    }

}
