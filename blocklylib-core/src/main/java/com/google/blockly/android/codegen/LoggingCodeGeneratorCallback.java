/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
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
package com.google.blockly.android.codegen;

import android.content.Context;
import android.widget.Toast;

import com.google.blockly.android.MyGlobals;

import java.util.ArrayList;

import static com.google.blockly.android.AbstractBlocklyActivity.haha;

/**
 * Simple generator callback that logs the generated code to device Log and a Toast.
 */
public class LoggingCodeGeneratorCallback implements CodeGenerationRequest.CodeGeneratorCallback {
    protected final String mTag;
    protected final Context mContext;


    private Compiler compiler;

    public LoggingCodeGeneratorCallback(Context context, String loggingTag) {
        mTag = loggingTag;
        mContext = context;
    }

    @Override
    public void onFinishCodeGeneration(String generatedCode) {
        // Sample callback.

        if (generatedCode.isEmpty()) {
            Toast.makeText(mContext,
                    "Something went wrong with code generation.", Toast.LENGTH_LONG).show();
        } else {

            //Toast.makeText(mContext, generatedCode, Toast.LENGTH_SHORT).show();

            //라인단위 구분
            String[] lineArray = generatedCode.split("\n");


            //명령단위 구분(인자포함)
//            ArrayList<String[]> funcArray = new ArrayList<String[]>();
//            for(int i = 0; i< lineArray.length; i++) {
//                funcArray.add(lineArray[i].split("/"));
//
//                //동기화 비동기화 구분
//                if(funcArray.get(i)[0].equals("SYN")) { //synchronize(동시발생) *예시 for
//                    if(funcArray.get(i)[1].equals("ROBOT")) {
//                        compiler = new RobotCompiler(mContext);
//                        compiler.setCommand(funcArray.get(i));
//                    }
//                }
//
//                if(funcArray.get(i)[0].equals("ASYN")) { //asynchronize(기다림)
//
//                }
//
//            }
        }
        haha = generatedCode;

//        MyGlobals.getInstance().setData(generatedCode);
//        haha = MyGlobals.getInstance().getData();
//        Toast.makeText(mContext,MyGlobals.getInstance().getData()  , Toast.LENGTH_LONG).show();
    }
}
