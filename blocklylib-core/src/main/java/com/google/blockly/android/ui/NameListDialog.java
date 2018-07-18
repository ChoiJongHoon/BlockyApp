package com.google.blockly.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.blockly.android.R;

/**
 * Created by Eunjoo on 2016-11-16.
 */

public class NameListDialog extends DialogFragment {
    private String mList;
    private DialogInterface.OnClickListener mListener;
    private EditText mNameEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceBundle) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View nameView = inflater.inflate(R.layout.name_list_view, null);
        mNameEditText = (EditText) nameView.findViewById(R.id.name);
        mNameEditText.setText(mList);

        AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        bob.setTitle(R.string.name_list_title);
        bob.setView(nameView);
        bob.setPositiveButton(R.string.name_list_positive, mListener);
        bob.setNegativeButton(R.string.name_list_negative, mListener);
        return bob.create();
    }

    public void setList(String list, final NameListDialog.Callback clickListener) {
        if (clickListener == null) {
            throw new IllegalArgumentException("Must have a listener to perform an action.");
        }
        mList = list;
        mListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        clickListener.onNameConfirmed(mList,
                                mNameEditText.getText().toString());
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        clickListener.onNameCanceled(mList);
                        break;
                }
            }
        };
    }

    public abstract static class Callback {
        public abstract void onNameConfirmed(String originalName, String newName);
        public void onNameCanceled(String originalName) {
            // Do nothing by default
        }
    }
}
