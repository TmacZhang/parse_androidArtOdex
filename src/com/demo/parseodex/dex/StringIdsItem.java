package com.demo.parseodex.dex;

import com.demo.parseodex.Utils;



public class StringIdsItem {

    /**
     * struct string_ids_item { uint string_data_off; }
     */

    public int string_data_off;

    public static int getSize() {
        return 4;
    }

    @Override
    public String toString() {
        return Utils.bytesToHexString(Utils.int2Byte(string_data_off));
    }

}
