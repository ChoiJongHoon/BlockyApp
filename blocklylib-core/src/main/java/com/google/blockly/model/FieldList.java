package com.google.blockly.model;

import android.text.TextUtils;

import com.google.blockly.utils.BlockLoadingException;

import org.json.JSONObject;

/**
 * Created by Eunjoo on 2016-11-16.
 */

public final class FieldList extends Field {
    private String mList;

    public FieldList(String name, String list) {
        super(name, TYPE_LIST);
        mList = list;
    }

    public static FieldList fromJson(JSONObject json) throws BlockLoadingException {
        String name = json.optString("name");
        if (TextUtils.isEmpty(name)) {
            throw new BlockLoadingException("field_list \"name\" attribute must not be empty.");
        }
        return new FieldList(name, json.optString("list", "listitem"));
    }

    @Override
    public FieldList clone() {
        return new FieldList(getName(), mList);
    }

    @Override
    public boolean setFromString(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        setList(text);
        return true;
    }

    /**
     * @return The name of the variable that is set.
     */
    public String getList() {
        return mList;
    }

    /**
     * Sets the variable in this field. All variables are considered global and must be unique.
     * Two variables with the same name will be considered the same variable at generation.
     */
    public void setList(String list) {
        if ((mList == null && list != null)
                || (mList != null && !mList.equalsIgnoreCase(list))) {
            String oldValue = getSerializedValue();
            mList = list;
            String newValue = getSerializedValue();
            fireValueChanged(oldValue, newValue);
        }
    }

    @Override
    public String getSerializedValue() {
        return mList;
    }
}
