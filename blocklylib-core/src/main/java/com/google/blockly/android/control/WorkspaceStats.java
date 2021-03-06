/*
 *  Copyright 2015 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.blockly.android.control;

import android.support.v4.util.SimpleArrayMap;

import com.google.blockly.model.Block;
import com.google.blockly.model.Connection;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldList;
import com.google.blockly.model.FieldVariable;
import com.google.blockly.model.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks information about the Workspace that we want fast access to.
 */
public class WorkspaceStats {

    // Maps from variable/procedure names to the blocks/fields where they are referenced.
    private final SimpleArrayMap<String, List<FieldVariable>> mVariableReferences =
            new SimpleArrayMap<>();
    private final SimpleArrayMap<String, List<FieldList>> mListReferences =
            new SimpleArrayMap<>();
    private final NameManager mVariableNameManager;
    private final NameManager mListNameManager;
    private final ProcedureManager mProcedureManager;
    private final ConnectionManager mConnectionManager;

    private final Field.Observer mVariableObserver = new Field.Observer() {
        @Override
        public void onValueChanged(Field field, String oldVar, String newVar) {
            FieldVariable varField = (FieldVariable) field;
            List<FieldVariable> list = mVariableReferences.get(oldVar);
            if (list != null) {
                list.remove(varField);
            }

            if (newVar == null) {
                return;
            }
            list = mVariableReferences.get(newVar);
            if (list == null) {
                list = new ArrayList<>();
                mVariableReferences.put(newVar, list);
                mVariableNameManager.addName(newVar);
            }
            list.add(varField);
        }
    };
    private final Field.Observer mListObserver = new Field.Observer() {
        @Override
        public void onValueChanged(Field field, String oldList, String newList) {
            FieldList listField = (FieldList) field;
            List<FieldList> list = mListReferences.get(oldList);
            if (list != null) {
                list.remove(listField);
            }

            if (newList == null) {
                return;
            }
            list = mListReferences.get(newList);
            if (list == null) {
                list = new ArrayList<>();
                mListReferences.put(newList, list);
                mListNameManager.addName(newList);
            }
            list.add(listField);
        }
    };

    private final List<Connection> mTempConnecitons = new ArrayList<>();

    public WorkspaceStats(NameManager variableManager, NameManager listManager, ProcedureManager procedureManager,
                          ConnectionManager connectionManager) {
        mVariableNameManager = variableManager;
        mListNameManager = listManager;
        mProcedureManager = procedureManager;
        mConnectionManager = connectionManager;
    }

    public NameManager getVariableNameManager() {
        return mVariableNameManager;
    }

    public SimpleArrayMap<String, List<FieldVariable>> getVariableReferences() {
        return mVariableReferences;
    }

    public SimpleArrayMap<String, List<FieldList>> getListReferences() {
        return mListReferences;
    }

    /**
     * Walks through a block and records all Connections, variable references, procedure
     * definitions and procedure calls.
     *
     * @param block The block to inspect.
     * @param recursive Whether to recursively collect stats for all descendants of the current
     * block.
     */
    public void collectStats(Block block, boolean recursive) {
        for (int i = 0; i < block.getInputs().size(); i++) {
            Input in = block.getInputs().get(i);
            addConnection(in.getConnection(), recursive);

            // Variables and references to them.
            for (int j = 0; j < in.getFields().size(); j++) {
                Field field = in.getFields().get(j);
                if (field.getType() == Field.TYPE_VARIABLE) {
                    FieldVariable var = (FieldVariable) field;
                    var.registerObserver(mVariableObserver);
                    if (mVariableReferences.containsKey(var.getVariable())) {
                        mVariableReferences.get(var.getVariable()).add(var);
                    } else {
                        List<FieldVariable> references = new ArrayList<>();
                        references.add(var);
                        mVariableReferences.put(var.getVariable(), references);
                    }
                    mVariableNameManager.addName(var.getVariable());
                }
                if (field.getType() == Field.TYPE_LIST) {
                    FieldList list = (FieldList) field;
                    list.registerObserver(mListObserver);
                    if(mListReferences.containsKey(list.getList())) {
                        mListReferences.get(list.getList()).add(list);
                    } else {
                        List<FieldList> references = new ArrayList<>();
                        references.add(list);
                        mListReferences.put(list.getList(), references);
                    }
                    mListNameManager.addName(list.getList());
                }
            }
        }

        addConnection(block.getNextConnection(), recursive);
        // Don't recurse on outputs or previous connections--that's effectively walking back up the
        // tree.
        addConnection(block.getPreviousConnection(), false);
        addConnection(block.getOutputConnection(), false);

        // Procedures
        if (mProcedureManager.isDefinition(block)) {
            mProcedureManager.addDefinition(block);
        }
        // TODO (fenichel): Procedure calls will only work when mutations work.
        // The mutation will change the name of the block.  I believe that means name field,
        // not type.
        if (mProcedureManager.isReference(block)) {
            mProcedureManager.addReference(block);
        }
    }

    /**
     * Clear state about variables, procedures, and connections.
     * These changes will be reflected in the externally owned connection and procedure manager.
     */
    public void clear() {
        mVariableReferences.clear();
        mListReferences.clear();
        mProcedureManager.clear();
        mVariableNameManager.clearUsedNames();
        mListNameManager.clearUsedNames();
        mConnectionManager.clear();
    }

    /**
     * Remove all the stats associated with this block and its descendents. This will remove all
     * connections from the ConnectionManager and dereference any variables in the tree.
     *
     * @param block The starting block to cleanup stats for.
     */
    public void cleanupStats(Block block) {
        block.getAllConnections(mTempConnecitons);
        for (int i = 0; i < mTempConnecitons.size(); i++) {
            mConnectionManager.removeConnection(mTempConnecitons.get(i));
        }
        mTempConnecitons.clear();
        List<Input> inputs = block.getInputs();
        for (int i = 0; i < inputs.size(); i++) {
            Input input = inputs.get(i);
            List<Field> fields = input.getFields();
            for (int j = 0; j < fields.size(); j++) {
                Field field = fields.get(j);
                if (field instanceof FieldVariable) {
                    mVariableReferences.get(((FieldVariable)field).getVariable()).remove(field);
                }
                if (field instanceof FieldList) {
                    mListReferences.get(((FieldList)field).getList()).remove(field);
                }
            }
            if (input.getConnection() != null && input.getConnection().getTargetBlock() != null) {
                cleanupStats(input.getConnection().getTargetBlock());
            }
        }
        if (block.getNextBlock() != null) {
            cleanupStats(block.getNextBlock());
        }
    }

    private void addConnection(Connection conn, boolean recursive) {
        if (conn != null) {
            mConnectionManager.addConnection(conn);
            if (recursive) {
                Block recursiveTarget = conn.getTargetBlock();
                if (recursiveTarget != null) {
                    collectStats(recursiveTarget, true);
                }
            }
        }
    }
}
