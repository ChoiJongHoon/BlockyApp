package com.google.blockly.android.ui.fieldview;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Eunjoo on 2016-11-16.
 */

public abstract class ListRequestCallback {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({REQUEST_DELETE, REQUEST_RENAME, REQUEST_CREATE})
    public @interface LisdtRequestType {
    }

    public static final int REQUEST_DELETE = 1;
    public static final int REQUEST_RENAME = 2;
    public static final int REQUEST_CREATE = 3;

    public abstract void onListRequest(int request, String list);
}
