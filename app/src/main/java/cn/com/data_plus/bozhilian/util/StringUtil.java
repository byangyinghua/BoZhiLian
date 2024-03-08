package cn.com.data_plus.bozhilian.util;

public class StringUtil {
    public static String[] splitByHyphen(String text) {
        String s1, s2;
        if (text.startsWith("-")) {
            text = text.substring(1);
            int firstIndex = text.indexOf("-");
            s1 = "-" + text.substring(0, firstIndex);
            s2 = text.substring(firstIndex + 1);
        } else {
            int firstIndex = text.indexOf("-");
            s1 = text.substring(0, firstIndex);
            s2 = text.substring(firstIndex + 1);
        }
        return new String[]{s1, s2};
    }
}
