package com.google.blockly.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.blockly.android.R;
import com.google.blockly.android.control.BlocklyController;

/**
 * Created by Eunjoo on 2016-11-16.
 */

public class DeleteListDialog extends DialogFragment {
    private String mList;
    private int mCount;
    private BlocklyController mController;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceBundle) {
        AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        bob.setTitle(R.string.delete_list_title);
        bob.setMessage(String.format(getResources().getString(R.string.delete_list_confirm),
                mList, mCount));
        bob.setPositiveButton(R.string.delete_list_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mController.deleteVariable(mList);
                    }
                });
        return bob.create();
    }

    public void setController(BlocklyController controller) {
        mController = controller;
    }

    public void setList(String list, int count) {
        mList = list;
        mCount = count;
    }
}
