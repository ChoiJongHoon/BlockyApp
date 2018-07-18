package com.google.blockly.android.codegen;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Eunjoo on 2016-11-18.
 */

public class RobotCompiler implements Compiler {
    protected final Context mContext;

    public RobotCompiler(Context context) {
        mContext = context;
    }

    public void setCommand(String[] command) {
//        if(command[2].equals("SPEED")) {
//            /*
//            참고:
//            command[3] = FRONT:30
//            result[0] = FRONT
//            result[1] = 30
//            */
//            //String[] result = command[3].split("/");
//
//            if(command[3].contains("FRONT")) goFront(Integer.parseInt(command[4]));
//            else if(command[3].contains("BACK")) goBack(Integer.parseInt(command[4]));
//            else if(command[3].contains("LEFT")) goLeft(Integer.parseInt(command[4]));
//            else if(command[3].contains("RIGHT")) goRight(Integer.parseInt(command[4]));
//
//        }
//        else if(command[2].equals("SET")) {
//            setColour();
//        }
    }

//    private void goFront(int speed) {
//        Toast.makeText(mContext, Integer.toString(speed), Toast.LENGTH_SHORT).show();
//    }
//
//    private void goBack(int speed) {
//        Toast.makeText(mContext, Integer.toString(speed), Toast.LENGTH_SHORT).show();
//    }
//
//    private void goLeft(int speed) {
//        Toast.makeText(mContext, Integer.toString(speed), Toast.LENGTH_SHORT).show();
//    }
//
//    private void goRight(int speed) {
//        Toast.makeText(mContext, Integer.toString(speed), Toast.LENGTH_SHORT).show();
//    }
//
//    private void setColour() {
//
//    }
}
