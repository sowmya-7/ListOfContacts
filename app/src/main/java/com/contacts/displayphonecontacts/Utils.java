package com.contacts.displayphonecontacts;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern PHONE_PATTERN
            = Pattern.compile(
            "(\\+[0-9]+[\\- \\.]*)?"
                    + "(1?[ ]*\\([0-9]+\\)[\\- \\.]*)?"
                    + "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])");

    public static final String[] getTelTypetoStr = {
            "NONE",
            "HOME",
            "MOBILE",
            "WORK",
            "FAX_WORK",
            "FAX_HOME",
            "PAGER",
            "OTHER",
            "CALLBACK",
            "CAR",
            "COMPANY_MAIN",
            "ISDN",
            "MAIN",
            "OTHER_FAX",
            "RADIO",
            "TELEX",
            "TDD",
            "WORK_MOBILE",
            "WORK_PAGER",
            "ASSISTANT",
            "MMS",
            "EMAIL",
            "CUSTOM"
    };

    public static boolean isValidPhoneNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }

        Matcher match = PHONE_PATTERN.matcher(number);
        return match.matches();
    }
}
