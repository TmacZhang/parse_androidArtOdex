package com.demo.parseodex.vdex;

public class VdexDataSection {
    public class VdexHeader {
        public byte[] magic = new byte[4];
        public byte[] version = new byte[4];
        int number_of_dex_files_;
        int dex_size_;
        int verifier_deps_size_;
        int quickening_info_size_;
    }
}
