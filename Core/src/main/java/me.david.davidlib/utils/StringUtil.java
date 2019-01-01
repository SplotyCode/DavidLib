package me.david.davidlib.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class StringUtil {

    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    public static String humanReadableBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = ("kMGTPE").charAt(exp-1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    public static boolean isNoWhiteSpace(char ch){
        return ch != Character.MIN_VALUE && ch != ' ' && ch != '\n' && ch != '\r' && ch != '\t';
    }

    public static boolean isNoSpecialSpace(char ch){
        return ch != '\n' && ch != '\r' && ch != '\t' && ch != Character.MIN_VALUE;
    }

    public static boolean isWhiteSpace(char ch){
        return !isNoWhiteSpace(ch);
    }

    public static String fromException(Throwable throwable){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    public static boolean isFloat(String str){
        try {
            Float.valueOf(str);
        } catch (NumberFormatException ex){
            return false;
        }
        return true;
    }

    public static float toFloat(String str){
        return Float.valueOf(str);
    }

    public static boolean isDouble(String str){
        try {
            Double.valueOf(str);
        } catch (NumberFormatException ex){
            return false;
        }
        return true;
    }

    public static double toDouble(String str){
        return Double.parseDouble(str);
    }

    public static boolean isLong(String str){
        try {
            Long.valueOf(str);
        } catch (NumberFormatException ex){
            return false;
        }
        return true;
    }

    public static boolean isInteger(String str){
        try {
            Integer.valueOf(str);
        } catch (NumberFormatException ex){
            return false;
        }
        return true;
    }

    public static long toLong(String str){
        return Long.parseLong(str);
    }

    public interface Joiner<T> {
        String join(T obj);
    }

    public static final Joiner<String> STRING_JOINER = str -> str;

    public static String join(Iterable<String> iterable, String seperator){
        return join(iterable, STRING_JOINER, seperator);
    }

    public static String join(String[] array, String seperator){
        return join(array, STRING_JOINER, seperator);
    }

    public static <T> String join(Iterable<T> iterable, Joiner<T> joiner, String seperator){
        StringBuilder builder = new StringBuilder();
        for (T element : iterable)
            builder.append(joiner.join(element)).append(seperator);
        String result = builder.toString();
        if(result.endsWith(seperator)) return result.substring(0, result.length()-seperator.length());
        return result;
    }

    public static <T> String join(T[] array, Joiner<T> joiner, String seperator){
        StringBuilder builder = new StringBuilder();
        for (T element : array)
            builder.append(joiner.join(element)).append(seperator);
        String result = builder.toString();
        if(result.endsWith(seperator)) return result.substring(0, result.length()-seperator.length());
        return result;
    }

    public static boolean charsEqualIgnoreCase(char a, char b) {
        return a == b || Character.toLowerCase(a) == Character.toLowerCase(b);
    }

    public static boolean endsWithChar(String str, char suffix) {
        return str != null && str.length() != 0 && str.charAt(str.length() - 1) == suffix;
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        int stringLength = str.length();
        int prefixLength = prefix.length();
        return stringLength >= prefixLength && str.regionMatches(true, 0, prefix, 0, prefixLength);
    }

    public static boolean endsWithIgnoreCase(String text, String suffix) {
        int l1 = text.length();
        int l2 = suffix.length();
        if (l1 < l2) return false;

        for (int i = l1 - 1; i >= l1 - l2; i--) {
            if (!charsEqualIgnoreCase(text.charAt(i), suffix.charAt(i + l2 - l1))) {
                return false;
            }
        }

        return true;
    }

    public static int lastIndexOf(String str, char c, int start, int end) {
        start = Math.max(start, 0);
        for (int i = Math.min(end, str.length()) - 1; i >= start; i--) {
            if (str.charAt(i) == c) return i;
        }
        return -1;
    }

}
