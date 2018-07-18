package com.google.blockly.android.ui.fieldview;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.blockly.android.R;
import com.google.blockly.android.control.NameManager;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldList;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Eunjoo on 2016-11-16.
 */

public class BasicFieldListView extends Spinner implements FieldView, ListChangeView {
    protected Field.Observer mFieldObserver = new Field.Observer() {
        @Override
        public void onValueChanged(Field field, String oldList, String newList) {
            refreshSelection();
        }
    };

    protected FieldList mListField;
    protected BasicFieldListView.ListViewAdapter mAdapter;
    protected ListRequestCallback mCallback;

    private final Handler mMainHandler;

    /**
     * Constructs a new {@link BasicFieldListView}.
     *
     * @param context The application's context.
     */
    public BasicFieldListView(Context context) {
        super(context);
        mMainHandler = new Handler(context.getMainLooper());
    }

    public BasicFieldListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMainHandler = new Handler(context.getMainLooper());
    }

    public BasicFieldListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMainHandler = new Handler(context.getMainLooper());
    }

    @Override
    public void setField(Field field) {
        FieldList listField = (FieldList) field;

        if (mListField == listField) {
            return;
        }

        if (mListField != null) {
            mListField.unregisterObserver(mFieldObserver);
        }
        mListField = listField;
        if (mListField != null) {
            // Update immediately.
            refreshSelection();
            mListField.registerObserver(mFieldObserver);
        } else {
            setSelection(0);
        }
    }

    @Override
    public Field getField() {
        return mListField;
    }

    @Override
    public void setSelection(int position) {
        if (position == getSelectedItemPosition()) {
            return;
        }
        if (mListField == null) {
            return;
        }
        int type = ((ListViewAdapter)getAdapter()).getListAction(position);

        switch (type) {
            case ListViewAdapter.ACTION_SELECT_LIST:
                super.setSelection(position);
                if (mListField != null) {
                    String listName = getAdapter().getItem(position).toString();
                    mListField.setList(listName);
                }
                break;
            case ListViewAdapter.ACTION_RENAME_LIST:
                if (mCallback != null) {
                    mCallback.onListRequest(ListRequestCallback.REQUEST_RENAME,
                            mListField.getList());
                }
                break;
            case ListViewAdapter.ACTION_DELETE_LIST:
                if (mCallback != null) {
                    mCallback.onListRequest(ListRequestCallback.REQUEST_DELETE,
                            mListField.getList());
                }
                break;
        }

    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        mAdapter = (ListViewAdapter) adapter;
        super.setAdapter(adapter);

        if (adapter != null) {
            if (mListField != null) {
                refreshSelection();
            }
            mAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    if (mListField != null) {
                        refreshSelection();
                    }
                }
            });
        }
    }

    @Override
    public void unlinkField() {
        setField(null);
    }

    @Override
    public void setListRequestCallback(ListRequestCallback requestCallback) {
        mCallback = requestCallback;
    }

    /**
     * Updates the selection from the field. This is used when the indices may have changed to
     * ensure the correct index is selected.
     */
    private void refreshSelection() {
        if (mAdapter != null) {
            // Because this may change the available indices, we want to make sure each assignment
            // is in its own event tick.
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListField != null) {
                        setSelection(mAdapter
                                .getOrCreateListIndex(mListField.getList()));
                    }
                }
            });
        }
    }

    /**
     * An implementation of {@link ArrayAdapter} that wraps the
     * {@link NameManager.ListNameManager} to create the variable item views.
     */
    public static class ListViewAdapter extends ArrayAdapter<String> {
        @Retention(RetentionPolicy.SOURCE)
        @IntDef({ACTION_SELECT_LIST, ACTION_RENAME_LIST, ACTION_DELETE_LIST})
        public @interface ListAdapterType{}
        public static final int ACTION_SELECT_LIST = 0;
        public static final int ACTION_RENAME_LIST = 1;
        public static final int ACTION_DELETE_LIST = 2;

        private final NameManager mListNameManager;
        private final String mRenameString;
        private final String mDeleteString;

        /**
         * @param listNameManager The name manager containing the variables.
         * @param context A context for inflating layouts.
         * @param resource The {@link TextView} layout to use when inflating items.
         */
        public ListViewAdapter(Context context, NameManager listNameManager,
                                   @LayoutRes int resource) {
            super(context, resource);
            mListNameManager = listNameManager;
            mRenameString = context.getString(R.string.rename_list);
            mDeleteString = context.getString(R.string.delete_list);
            refreshList();
            listNameManager.registerObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    refreshList();
                }
            });
        }

        /**
         * Retrieves the index for the given variable name, creating a new variable if it is not found.
         *
         * @param listName The name of the variable to retrieve.
         * @return The index of the variable.
         */
        public int getOrCreateListIndex(String listName) {
            int count = mListNameManager.size();
            for (int i = 0; i < count; i++) {
                if (listName.equalsIgnoreCase(getItem(i))) {
                    return i;
                }
            }

            // No match found.  Create it.
            mListNameManager.addName(listName);

            // Reindex, finding the new index along the way.
            count = mListNameManager.size();
            clear();
            int insertionIndex = -1;
            for (int i = 0; i < count; i++) {
                add(mListNameManager.get(i));
                if (listName.equals(getItem(i))) {
                    insertionIndex = i;
                }
            }
            if (insertionIndex == -1) {
                throw new IllegalStateException("List not found after add.");
            }

            notifyDataSetChanged();
            return insertionIndex;
        }

        @Override
        public int getCount() {
            int count = super.getCount();
            return count == 0 ? 0 : count + 2;
        }

        @Override
        public String getItem(int index) {
            int count = super.getCount();
            if (index >= count + 2 || index < 0) {
                throw new IndexOutOfBoundsException("There is no item at index " + index
                        + ". Count is " + count);
            }
            if (index < count) {
                return super.getItem(index);
            }
            if (index == count) {
                return mRenameString;
            } else {
                return mDeleteString;
            }
        }

        public @ListAdapterType
        int getListAction(int index) {
            int count = super.getCount();
            if (index >= count + 2 || index < 0) {
                throw new IndexOutOfBoundsException("There is no item at index " + index + ". Count is "
                        + count);
            }
            if (index < count) {
                return ACTION_SELECT_LIST;
            } else if (index == count) {
                return ACTION_RENAME_LIST;
            } else {
                return ACTION_DELETE_LIST;
            }

        }

        /**
         * Updates the ArrayAdapter internal list with the latest list from the NameManager.
         */
        private void refreshList() {
            clear();
            for (int i = 0; i < mListNameManager.size(); i++) {
                add(mListNameManager.get(i));
            }
            notifyDataSetChanged();
        }
    }
}