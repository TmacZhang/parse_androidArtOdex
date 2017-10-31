package com.demo.parseodex;

import java.io.File;

import com.demo.parseodex.dex.HeaderType;

public class ParseVdex {

    public static void main(String[] args) {
        byte[] fileByteArys = Utils.readFile("odex/Dialer.vdex");
        if (fileByteArys == null) {
            System.out.println("read file byte failed...");
            return;
        }
        parseVdex(fileByteArys);
    }

    private static void parseVdex(byte[] fileByteArys) {
        System.out.println("parse vdex header");
        parseHeader(fileByteArys, 0);
    }

    private static void parseHeader(byte[] header, int offset) {
        byte[] magic = Utils.copyBytes(header, 0, 4);
        System.out.println("vdex magic : " + Utils.bytes2HexString(magic));
        byte[] version = Utils.copyBytes(header, 4, 4);
        System.out.println("vdex version : " + Utils.bytes2HexString(version));
        int number_of_dex_files_ = Utils
                .byte2int(Utils.copyBytes(header, 8, 4));
        System.out.println("vdex number_of_dex_files_ = : "
                + number_of_dex_files_);
        int dex_size_ = Utils.byte2int(Utils.copyBytes(header, 12, 4));
        System.out.println("vdex dex_size_ = : " + dex_size_);
        int verifier_deps_size_ = Utils
                .byte2int(Utils.copyBytes(header, 16, 4));
        System.out.println("vdex verifier_deps_size_ = : "
                + verifier_deps_size_);
        int quickening_info_size_ = Utils.byte2int(Utils.copyBytes(header, 20,
                4));
        System.out.println("vdex quickening_info_size_ = : "
                + quickening_info_size_);
        int VdexChecksum = 4 * number_of_dex_files_;
        int dexOffset = 4 + 4 + 4 + 4 + 4 + 4 + VdexChecksum;//
        System.out.println("dexoffset = " + dexOffset);
        HeaderType headerType = praseDexHeader(header, dexOffset);
        System.out.println("拷贝dex++++++++++++++++++++++++++++++++++++++");
        File dexFile = new File("real.dex");
        byte[] dexBytes = new byte[headerType.file_size];
        System.arraycopy(header, dexOffset, dexBytes, 0, headerType.file_size);
        System.out.println("dexFile end :"
                + Integer.toHexString(dexOffset + headerType.file_size));
        System.out.println("dexFile = " + dexFile.getAbsolutePath());
        // 从Oat中取出来的dex文件和原始dex文件并不是完全一样的，因为在dex2oat过程中，已经将一些指令进行了替换
        // 比如将 invoke-virtual ==>invoke-virtual-quick 指令
        Utils.saveFile(dexFile.getAbsolutePath(), dexBytes);
    }

    public static HeaderType praseDexHeader(byte[] byteSrc, int offset) {
        HeaderType headerType = new HeaderType();
        // 解析魔数
        byte[] magic = Utils.copyBytes(byteSrc, offset, 8);
        headerType.magic = magic;

        // 解析checksum
        byte[] checksumByte = Utils.copyBytes(byteSrc, offset + 8, 4);
        headerType.checksum = Utils.byte2Int(checksumByte);

        // 解析siganature
        byte[] siganature = Utils.copyBytes(byteSrc, offset + 12, 20);
        headerType.siganature = siganature;

        // 解析file_size
        byte[] fileSizeByte = Utils.copyBytes(byteSrc, offset + 32, 4);
        headerType.file_size = Utils.byte2Int(fileSizeByte);

        // 解析header_size
        byte[] headerSizeByte = Utils.copyBytes(byteSrc, offset + 36, 4);
        headerType.header_size = Utils.byte2Int(headerSizeByte);

        // 解析endian_tag
        byte[] endianTagByte = Utils.copyBytes(byteSrc, offset + 40, 4);
        headerType.endian_tag = Utils.byte2Int(endianTagByte);

        // 解析link_size
        byte[] linkSizeByte = Utils.copyBytes(byteSrc, offset + 44, 4);
        headerType.link_size = Utils.byte2Int(linkSizeByte);

        // 解析link_off
        byte[] linkOffByte = Utils.copyBytes(byteSrc, offset + 48, 4);
        headerType.link_off = Utils.byte2Int(linkOffByte);

        // 解析map_off
        byte[] mapOffByte = Utils.copyBytes(byteSrc, offset + 52, 4);
        headerType.map_off = Utils.byte2Int(mapOffByte);

        // 解析string_ids_size
        byte[] stringIdsSizeByte = Utils.copyBytes(byteSrc, offset + 56, 4);
        headerType.string_ids_size = Utils.byte2Int(stringIdsSizeByte);

        // 解析string_ids_off
        byte[] stringIdsOffByte = Utils.copyBytes(byteSrc, offset + 60, 4);
        headerType.string_ids_off = Utils.byte2Int(stringIdsOffByte);

        // 解析type_ids_size
        byte[] typeIdsSizeByte = Utils.copyBytes(byteSrc, offset + 64, 4);
        headerType.type_ids_size = Utils.byte2Int(typeIdsSizeByte);

        // 解析type_ids_off
        byte[] typeIdsOffByte = Utils.copyBytes(byteSrc, offset + 68, 4);
        headerType.type_ids_off = Utils.byte2Int(typeIdsOffByte);

        // 解析proto_ids_size
        byte[] protoIdsSizeByte = Utils.copyBytes(byteSrc, offset + 72, 4);
        headerType.proto_ids_size = Utils.byte2Int(protoIdsSizeByte);

        // 解析proto_ids_off
        byte[] protoIdsOffByte = Utils.copyBytes(byteSrc, offset + 76, 4);
        headerType.proto_ids_off = Utils.byte2Int(protoIdsOffByte);

        // 解析field_ids_size
        byte[] fieldIdsSizeByte = Utils.copyBytes(byteSrc, offset + 80, 4);
        headerType.field_ids_size = Utils.byte2Int(fieldIdsSizeByte);

        // 解析field_ids_off
        byte[] fieldIdsOffByte = Utils.copyBytes(byteSrc, offset + 84, 4);
        headerType.field_ids_off = Utils.byte2Int(fieldIdsOffByte);

        // 解析method_ids_size
        byte[] methodIdsSizeByte = Utils.copyBytes(byteSrc, offset + 88, 4);
        headerType.method_ids_size = Utils.byte2Int(methodIdsSizeByte);

        // 解析method_ids_off
        byte[] methodIdsOffByte = Utils.copyBytes(byteSrc, offset + 92, 4);
        headerType.method_ids_off = Utils.byte2Int(methodIdsOffByte);

        // 解析class_defs_size
        byte[] classDefsSizeByte = Utils.copyBytes(byteSrc, offset + 96, 4);
        headerType.class_defs_size = Utils.byte2Int(classDefsSizeByte);

        // 解析class_defs_off
        byte[] classDefsOffByte = Utils.copyBytes(byteSrc, offset + 100, 4);
        headerType.class_defs_off = Utils.byte2Int(classDefsOffByte);

        // 解析data_size
        byte[] dataSizeByte = Utils.copyBytes(byteSrc, offset + 104, 4);
        headerType.data_size = Utils.byte2Int(dataSizeByte);

        // 解析data_off
        byte[] dataOffByte = Utils.copyBytes(byteSrc, offset + 108, 4);
        headerType.data_off = Utils.byte2Int(dataOffByte);

        System.out.println("header:" + headerType);
        return headerType;
    }

}
