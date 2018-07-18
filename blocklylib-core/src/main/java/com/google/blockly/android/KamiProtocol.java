package com.google.blockly.android;

import java.util.ArrayList;

/**
 * Created by CJH on 2016-12-30.
 */

public class KamiProtocol {

    byte[][] b = null;
    int count = 0;

    String abc = null;

    public KamiProtocol(String abc) {

        this.abc = abc;
    }

    public byte[][] getData() {

            String[] line1 = abc.split("\n");
            ArrayList<String[]> funcArray = new ArrayList<String[]>();
            for (int i = 0; i < line1.length; i++) {
                funcArray.add(line1[i].split("/"));
            }

            b = new byte[funcArray.size()][];

            for (int i = 0; i < funcArray.size(); i++) {

                switch (funcArray.get(i)[3]) {
                    case "FRONT": {
                        byte[] a = new byte[]{(byte) 255, 0x55, 0x04, 0x00, 0x02, 0x5A, (byte) (Integer.parseInt(funcArray.get(i)[4]))};
                        b[count++] = a;
                        break;
                    }
                    case "BACK": {
                        byte[] a = new byte[]{(byte) 255, 0x55, 0x04, 0x00, 0x02, 0x5D, (byte) (Integer.parseInt(funcArray.get(i)[4]))};
                        b[count++] = a;
                        break;
                    }
                    case "LEFT": {
                        byte[] a = new byte[]{(byte) 255, 0x55, 0x04, 0x00, 0x02, 0x5B, (byte) (Integer.parseInt(funcArray.get(i)[4]))};
                        b[count++] = a;
                        break;
                    }
                    case "RIGHT": {
                        byte[] a = new byte[]{(byte) 255, 0x55, 0x04, 0x00, 0x02, 0x5C, (byte) (Integer.parseInt(funcArray.get(i)[4]))};
                        b[count++] = a;
                        break;
                    }
                }
        }
        return b;
    }
}
