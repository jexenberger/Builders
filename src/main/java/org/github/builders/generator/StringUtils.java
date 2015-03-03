package org.github.builders.generator;

/**
 * Created by julian3 on 15/03/03.
 */
public class StringUtils {


    public static String join(String delimiter, Object ... args) {
        String delim = (delimiter == null) ? "" : delimiter;
        StringBuilder stringBuilder = new StringBuilder();
        if (args == null) {
            return "";
        }
        for (Object arg : args) {
            if (arg != null && !arg.toString().trim().equals("")) {
                stringBuilder.append(arg).append(delim);
            }
        }
        if (!delim.equals("") && stringBuilder.toString().endsWith(delim)) {
          return stringBuilder.substring(0,stringBuilder.lastIndexOf(delim));
        } else {
            return stringBuilder.toString();
        }
    }

    public static String concat(Object... args) {
        return join(null, args);
    }

    public static String wrap(String left, Object value, String right) {
        return join(null, left, value, right);
    }

    public static String getBlank(String val) {
        if (val == null) {
            return "";
        }
        return val.trim();

    }

    public static String decapitalize(String val) {
       return concat(val.substring(0,1).toLowerCase(),val.substring(1));
    }





}
