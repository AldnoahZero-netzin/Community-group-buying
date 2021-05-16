package com.xupt.enums;

/**
 * @Auther: yhn
 * @Date: 2021/3/6 16:35
 */
public enum Code {
    SUCCESS(200),
    NOTLOGGEDIN(1001),
    PARAMETEREMPTY(1002),
    PARAMETERERROR(1003),
    UPLOADPICTUREERROR(1004),
    STOCKNOTENOUGH(1005);

    private int code;

    Code(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}
