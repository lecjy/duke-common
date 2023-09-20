package com.duke.common.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[1][0-9]{10}$");
    private static final Pattern PHONE_WITH_AREA_CODE_PATTERN = Pattern.compile("^[0][1-9]{2,3}-?[0-9]{5,10}$");
    private static final Pattern PHONE_WITHOUT_AREA_CODE_PATTERN = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");
    private static final String ZERO = "0";

    public static boolean isMobile(String str) {
        boolean b = false;
        Matcher m = MOBILE_PATTERN.matcher(str);
        b = m.matches();
        return b;
    }

    public static boolean isPhone(String str) {
        Matcher m = null;
        boolean b = false;
        if (str.startsWith("0")) {
            m = PHONE_WITH_AREA_CODE_PATTERN.matcher(str);
            b = m.matches();
        } else {
            m = PHONE_WITHOUT_AREA_CODE_PATTERN.matcher(str);
            b = m.matches();
        }
        return b;
    }

}
