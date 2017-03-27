package com.demo.parseodex;

public class OatDataSection {
    public OatHeader oatHeader;

    public OatDataSection() {
        oatHeader = new OatHeader();
    }

    public class OatHeader {
        public byte[] magic = new byte[4];
        public byte[] version = new byte[4];
        public int adler32_checksum;
        public int instruction_set;
        public int instruction_set_features_bitmap;
        public int dex_file_count;
        public int executable_offset;
        public int interpreter_to_interpreter_bridge_offset;
        public int interpreter_to_compiled_code_bridge_offset;
        public int jni_dlsym_lookup_offset;
        public int quick_generic_jni_trampoline_offset;
        public int quick_imt_conflict_trampoline_offset;
        public int quick_resolution_trampoline_offset;
        public int quick_to_interpreter_bridge_offset;
        public int image_patch_delta;
        public int image_file_location_oat_checksum;
        public int image_file_location_oat_data_begin;
        public int key_value_store_size;
        public byte[] key_value_store;
        // new byte[key_value_store_size],key_value_store_size个
    }

    public static class DexFileInfo {
        public int dex_file_location_size;
        public byte[] dex_file_location_data;
        public int dex_file_checksum;
        public int dex_file_offset;// 相对header初始地址的偏移量,所以总偏移地址就是header的偏移地址+dex_file_offset
        // DEX文件包含的类的本地机器指令信息偏移数组，一共有dex::header->class_defs_size个
        public int[] methods_offsets_pointer;
    }

    public static class OatClass {
        public short status_;
        /**
         * {@link OatClassType}
         */
        public short type_;
        // 只有type == kOatClassSomeCompiled(1)，下面两个才有意义
        public int method_bitmap_size_;
        public int method_bitmap_;
        // 所有compiled方法的code_offset,具体有多少个，只能用(下一个oatclass - 当前)/4
        public int[] code_offsets;
    }

    public static class OatClassType {
        public int kOatClassAllCompiled = 0;
        public int kOatClassSomeCompiled = 1;
        public int kOatClassNoneCompiled = 2;
        public int kOatClassMax = 3;
    }
}
