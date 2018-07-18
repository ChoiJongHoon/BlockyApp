package com.google.blockly.android.ui.fieldview;

/**
 * Created by Eunjoo on 2016-11-16.
 */

/**
 * Views that allow the user to request changes to the list of variables should implement this
 * interface. The {@link com.google.blockly.android.control.BlocklyController} maintains a
 * {@link ListRequestCallback} for handling UI requests.
 */
public interface ListChangeView {
    /**
     * Sets the callback for user generated variable change requests, such as deleting or renaming a
     * variable. The view takes no action on its own so the callback is expected to handle any
     * requests.
     *
     * @param requestCallback The callback to notify when the user has selected an action.
     */
    public void setListRequestCallback(ListRequestCallback requestCallback);
}
