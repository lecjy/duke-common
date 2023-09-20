package com.duke.common.base.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("com.duke.operation not supported");
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    public static String join(Object[] array, String separator) {
        return array == null ? null : join(array, separator, 0, array.length);
    }

    private static StringBuilder newStringBuilder(int noOfItems) {
        return new StringBuilder(noOfItems * 16);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        } else {
            if (separator == null) {
                separator = "";
            }
            int noOfItems = endIndex - startIndex;
            if (noOfItems <= 0) {
                return "";
            } else {
                StringBuilder buf = newStringBuilder(noOfItems);
                for (int i = startIndex; i < endIndex; ++i) {
                    if (i > startIndex) {
                        buf.append(separator);
                    }
                    if (array[i] != null) {
                        buf.append(array[i]);
                    }
                }
                return buf.toString();
            }
        }
    }

    public static String trimToEmpty(String string) {
        return string == null ? "" : string.trim();
    }

    public static String removeFirstCharacter(String string) {
        return isEmpty(string) ? "" : string.substring(1, string.length());
    }

    public static String removeLastCharacter(String string) {
        return isEmpty(string) ? "" : string.substring(0, string.length() - 1);
    }

    public static String removeFirstAndLastCharacter(String string) {
        string = removeFirstCharacter(string);
        return removeLastCharacter(string);
    }

    public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str != null && searchStr != null) {
            int len = searchStr.length();
            int max = str.length() - len;

            for (int i = 0; i <= max; ++i) {
                if (regionMatches(str, true, i, searchStr, 0, len)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;
            int srcLen = cs.length() - thisStart;
            int otherLen = substring.length() - start;
            if (thisStart >= 0 && start >= 0 && length >= 0) {
                if (srcLen >= length && otherLen >= length) {
                    while (tmpLen-- > 0) {
                        char c1 = cs.charAt(index1++);
                        char c2 = substring.charAt(index2++);
                        if (c1 != c2) {
                            if (!ignoreCase) {
                                return false;
                            }
                            if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                                return false;
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public static boolean isEmptyAfterTrim(String str) {
        return StringUtils.isEmpty(StringUtils.trimToEmpty(str));
    }

    public static void isEmptyAfterTrim(String str, String message) {
        if (isEmptyAfterTrim(str)) {
            throw new RuntimeException(message);
        }
    }

    public static boolean isNotEmptyAfterTrim(String str) {
        return !isEmptyAfterTrim(str);
    }

    public static boolean exist(String str, String substr, String sepatator) {
        if (str != null && str.trim().length() != 0) {
            if (substr != null && substr.trim().length() != 0) {
                String[] strArr = str.split(sepatator);
                int size = strArr.length;
                for (int i = 0; i < size; ++i) {
                    if (strArr[i].equals(substr)) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes(StandardCharsets.UTF_8);
                } catch (Exception var7) {
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; ++j) {
                    int k = b[j];
                    if (k < 0) {
                        k += 256;
                    }
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    public static String castStringBySeparator(String source, String separator) {
        source = source.replace(separator, "'" + separator + "'");
        source = "'" + source + "'";
        return source;
    }

    public static String cutString(String str, int num, String charsetName) {
        try {
            int length = str.getBytes(charsetName).length;
            if (length > num) {
                str = str.substring(0, str.length() - 1);
                str = cutString(str, num, charsetName);
            }
            return str;
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static boolean isBlank(CharSequence str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for (int i = 0; i < length; ++i) {
                if (!(Character.isWhitespace(str.charAt(i)) ||
                        Character.isSpaceChar(str.charAt(i)) ||
                        str.charAt(i) == 65279 ||
                        str.charAt(i) == 8234)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
}
