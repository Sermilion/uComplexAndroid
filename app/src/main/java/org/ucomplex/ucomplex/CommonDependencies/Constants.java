package org.ucomplex.ucomplex.CommonDependencies;

import org.ucomplex.ucomplex.R;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 10/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class Constants {

    public static final String PREFIX = "org.ucomplex.ucomplex.";
    public static final String EXTRA_KEY_USER = PREFIX +"user";
    public static final String EXTRA_KEYT_USER_TYPE = "user_type";
    public static final String EVENTS_REFRESH_BROADCAST = PREFIX +"EVENTS_REFRESH";
    public static final String UCOMPLEX_PROFILE = "ucomplex_profile";
    public static final String IMAGE_FORMAT_JPG = ".jpg";

    public static final int USER_TYPE_TEACHER = 3;
    public static final int USER_TYPE_STUDENT = 4;

    public static final String AUTH_STRING = "AUTH_STRING";

    public static final int[] colorsUserSelect = new int[]{
            R.drawable.select_account_1,
            R.drawable.select_account_2,
            R.drawable.select_account_3,
            R.drawable.select_account_4,
            R.drawable.select_account_5
    };

    public static final String STRING_EMPTY = "";
    public static final String IMAGE_FORMAT = ".jpg";

    public static final String AUTH_DELIMETER = ":";

    public static final String UC_ACTION_DOWNLOAD_COMPLETE = PREFIX+"download_complete";
    public static final String UC_ACTION_DOWNLOAD_CLICKED = PREFIX+"download_clicked";


}
