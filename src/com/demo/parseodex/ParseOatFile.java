package com.demo.parseodex;

import java.io.File;

import com.demo.parseodex.ElfType32.elf32_shdr;
import com.demo.parseodex.ElfType64.elf64_shdr;
import com.demo.parseodex.ElfType64.elf64_sym;
import com.demo.parseodex.OatDataSection.DexFileInfo;


public class ParseOatFile {
    public static ElfType32 type_32 = new ElfType32();
    public static ElfType64 type_64 = new ElfType64();
    private static boolean is64 =false;
    private static final int SHT_DYNSYM  = 11;

    public static void main(String[] args) throws Exception {
        byte[] fileByteArys = Utils.readFile("odex/oat.dex");
        if (fileByteArys == null) {
            System.out.println("read file byte failed...");
            return;
        }

        parseOat(fileByteArys);
    }

    private static void parseOat(byte[] fileByteArys) throws Exception {
        // 读取头部内容
        System.out.println("+++++++++++++++++++Elf Header+++++++++++++++++");
        parseHeader(fileByteArys, 0);
        System.out.println("+++++++++++++++++++Elf Section片段+++++++++++++++++");
        
        long s_header_offset =0 ;
        if(is64){
            s_header_offset = type_64.hdr.e_shoff;
            parseSectionHeaderList64(fileByteArys, (int)s_header_offset);
            System.out.println("+++++++++++++++++++Elf dynsym片段+++++++++++++++++");
            elf64_shdr sectionDynSym = null;
            for(elf64_shdr shdr :type_64.shdrList){
                if(shdr.sh_type == SHT_DYNSYM){
                    sectionDynSym = shdr;
                    break;
                }
            }
            if (sectionDynSym == null) {
                throw new Exception("shdrDynSym must be not null");
            }
            int dynSymSize = (int) (sectionDynSym.sh_size/elf64_sym.getSize()) ;
            parseDynSymList64(fileByteArys, (int)sectionDynSym.sh_offset ,dynSymSize);
            System.out.println("+++++++++++++++++++oat data片段+++++++++++++++++");
            //!!!!!这里暂时取第2个，不知道怎么读取dynstr字符串，通过字符串找到dynSym中的oat data段
            elf64_sym oatDataSym = type_64.symList.get(1);
            long oatDataSymOffset = oatDataSym.st_value;
            long oatDataSymSize = oatDataSym.st_st_size;
            parseOatData(fileByteArys, oatDataSymOffset,oatDataSymSize);
        }else{
            s_header_offset = Utils.byte2Int(type_32.hdr.e_shoff);
            System.out.println("-----------------32------------");
        }
    }


    private static void parseOatData(byte[] fileByteArys,
            long oatDataSymOffset, long oatDataSymSize) {
        OatDataSection dataSection = new OatDataSection();
        int offset = (int) oatDataSymOffset;
        dataSection.oatHeader.magic = Utils.copyBytes(fileByteArys, offset, 4);
        dataSection.oatHeader.version = Utils.copyBytes(fileByteArys,
                offset + 4, 4);
        dataSection.oatHeader.adler32_checksum = Utils.byte2Int(Utils
                .copyBytes(fileByteArys, offset + 8, 4));
        dataSection.oatHeader.instruction_set = Utils.byte2Int(Utils.copyBytes(
                fileByteArys, offset + 12, 4));
        dataSection.oatHeader.instruction_set_features_bitmap = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 16, 4));
        dataSection.oatHeader.dex_file_count = Utils.byte2Int(Utils.copyBytes(
                fileByteArys, offset + 20, 4));
        dataSection.oatHeader.executable_offset = Utils.byte2Int(Utils
                .copyBytes(fileByteArys, offset + 24, 4));
        dataSection.oatHeader.interpreter_to_interpreter_bridge_offset = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 28, 4));
        dataSection.oatHeader.interpreter_to_compiled_code_bridge_offset = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 32, 4));
        dataSection.oatHeader.jni_dlsym_lookup_offset = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 36, 4));
        dataSection.oatHeader.quick_generic_jni_trampoline_offset = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 40, 4));
        dataSection.oatHeader.quick_imt_conflict_trampoline_offset = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 44, 4));
        dataSection.oatHeader.quick_resolution_trampoline_offset = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 48, 4));
        dataSection.oatHeader.quick_to_interpreter_bridge_offset = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 52, 4));
        dataSection.oatHeader.image_patch_delta = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 56, 4));
        dataSection.oatHeader.image_file_location_oat_checksum = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 60, 4));
        dataSection.oatHeader.image_file_location_oat_data_begin = Utils
                .byte2Int(Utils.copyBytes(fileByteArys, offset + 64, 4));
        dataSection.oatHeader.key_value_store_size = Utils.byte2Int(Utils
                .copyBytes(fileByteArys, offset + 68, 4));
        int key_value_store_size = dataSection.oatHeader.key_value_store_size;
        dataSection.oatHeader.key_value_store = new byte[key_value_store_size];
        dataSection.oatHeader.key_value_store = Utils.copyBytes(fileByteArys,
                offset + 72, key_value_store_size);
        System.out.println("dex_file_count == " +dataSection.oatHeader.dex_file_count);
        System.out.println("+++++++++++++++++++oat DexFileInfo片段+++++++++++++++++");
        DexFileInfo dexFileInfo = new DexFileInfo();
        int dexFileOffset = offset + 72 + key_value_store_size;
        dexFileInfo.dex_file_location_size = Utils.byte2Int(Utils.copyBytes(
                fileByteArys, dexFileOffset, 4));
        dexFileInfo.dex_file_location_data = new byte[dexFileInfo.dex_file_location_size];
        dexFileInfo.dex_file_location_data = Utils.copyBytes(fileByteArys,
                dexFileOffset + 4, dexFileInfo.dex_file_location_size);
        dexFileInfo.dex_file_checksum = Utils.byte2Int(Utils.copyBytes(
                fileByteArys, dexFileOffset + 4
                        + dexFileInfo.dex_file_location_size, 4));
        dexFileInfo.dex_file_offset = Utils.byte2Int(Utils.copyBytes(
                fileByteArys, dexFileOffset + 4
                        + dexFileInfo.dex_file_location_size + 4, 4));
        System.out.println("oatdata的dex_file_offset相对偏移地址 = "
                + Integer.toHexString(dexFileInfo.dex_file_offset));
        System.out.println("oatdata的dex_file_offset绝对偏移地址 = "
                + Integer.toHexString(dexFileInfo.dex_file_offset
                        + offset));
        System.out.println("++++++++++++++++++++解析dex  header++++++++++++++");
        //解析Dex 只需要长度就可以取出来啦，因为已经知道偏移量了。
        int dexOffset= dexFileInfo.dex_file_offset+ offset;
        HeaderType headerType = praseDexHeader(fileByteArys, dexOffset);
        System.out.println("拷贝dex++++++++++++++++++++++++++++++++++++++");
        File dexFile = new File("real.dex");
        byte [] dexBytes= new byte[headerType.file_size];
        System.arraycopy(fileByteArys, dexOffset, dexBytes, 0, headerType.file_size);
        System.out.println("dexFile = " +dexFile.getAbsolutePath());
        Utils.saveFile(dexFile.getAbsolutePath(), dexBytes);
    }

    public static HeaderType praseDexHeader(byte[] byteSrc, int offset) {
        HeaderType headerType = new HeaderType();
        // 解析魔数
        byte[] magic = Utils.copyBytes(byteSrc, offset, 8);
        headerType.magic = magic;

        // 解析checksum
        byte[] checksumByte = Utils.copyBytes(byteSrc,offset+ 8, 4);
        headerType.checksum = Utils.byte2Int(checksumByte);

        // 解析siganature
        byte[] siganature = Utils.copyBytes(byteSrc, offset+12, 20);
        headerType.siganature = siganature;

        // 解析file_size
        byte[] fileSizeByte = Utils.copyBytes(byteSrc, offset+32, 4);
        headerType.file_size = Utils.byte2Int(fileSizeByte);

        // 解析header_size
        byte[] headerSizeByte = Utils.copyBytes(byteSrc, offset+36, 4);
        headerType.header_size = Utils.byte2Int(headerSizeByte);

        // 解析endian_tag
        byte[] endianTagByte = Utils.copyBytes(byteSrc, offset+40, 4);
        headerType.endian_tag = Utils.byte2Int(endianTagByte);

        // 解析link_size
        byte[] linkSizeByte = Utils.copyBytes(byteSrc, offset+44, 4);
        headerType.link_size = Utils.byte2Int(linkSizeByte);

        // 解析link_off
        byte[] linkOffByte = Utils.copyBytes(byteSrc, offset+48, 4);
        headerType.link_off = Utils.byte2Int(linkOffByte);

        // 解析map_off
        byte[] mapOffByte = Utils.copyBytes(byteSrc, offset+52, 4);
        headerType.map_off = Utils.byte2Int(mapOffByte);

        // 解析string_ids_size
        byte[] stringIdsSizeByte = Utils.copyBytes(byteSrc,offset+ 56, 4);
        headerType.string_ids_size = Utils.byte2Int(stringIdsSizeByte);

        // 解析string_ids_off
        byte[] stringIdsOffByte = Utils.copyBytes(byteSrc, offset+60, 4);
        headerType.string_ids_off = Utils.byte2Int(stringIdsOffByte);

        // 解析type_ids_size
        byte[] typeIdsSizeByte = Utils.copyBytes(byteSrc, offset+64, 4);
        headerType.type_ids_size = Utils.byte2Int(typeIdsSizeByte);

        // 解析type_ids_off
        byte[] typeIdsOffByte = Utils.copyBytes(byteSrc, offset+68, 4);
        headerType.type_ids_off = Utils.byte2Int(typeIdsOffByte);

        // 解析proto_ids_size
        byte[] protoIdsSizeByte = Utils.copyBytes(byteSrc,offset+ 72, 4);
        headerType.proto_ids_size = Utils.byte2Int(protoIdsSizeByte);

        // 解析proto_ids_off
        byte[] protoIdsOffByte = Utils.copyBytes(byteSrc, offset+76, 4);
        headerType.proto_ids_off = Utils.byte2Int(protoIdsOffByte);

        // 解析field_ids_size
        byte[] fieldIdsSizeByte = Utils.copyBytes(byteSrc, offset+80, 4);
        headerType.field_ids_size = Utils.byte2Int(fieldIdsSizeByte);

        // 解析field_ids_off
        byte[] fieldIdsOffByte = Utils.copyBytes(byteSrc, offset+84, 4);
        headerType.field_ids_off = Utils.byte2Int(fieldIdsOffByte);

        // 解析method_ids_size
        byte[] methodIdsSizeByte = Utils.copyBytes(byteSrc, offset+88, 4);
        headerType.method_ids_size = Utils.byte2Int(methodIdsSizeByte);

        // 解析method_ids_off
        byte[] methodIdsOffByte = Utils.copyBytes(byteSrc, offset+92, 4);
        headerType.method_ids_off = Utils.byte2Int(methodIdsOffByte);

        // 解析class_defs_size
        byte[] classDefsSizeByte = Utils.copyBytes(byteSrc, offset+96, 4);
        headerType.class_defs_size = Utils.byte2Int(classDefsSizeByte);

        // 解析class_defs_off
        byte[] classDefsOffByte = Utils.copyBytes(byteSrc, offset+100, 4);
        headerType.class_defs_off = Utils.byte2Int(classDefsOffByte);

        // 解析data_size
        byte[] dataSizeByte = Utils.copyBytes(byteSrc, offset+104, 4);
        headerType.data_size = Utils.byte2Int(dataSizeByte);

        // 解析data_off
        byte[] dataOffByte = Utils.copyBytes(byteSrc, offset+108, 4);
        headerType.data_off = Utils.byte2Int(dataOffByte);

        System.out.println("header:" + headerType);
        return headerType;

    }

    private static void parseDynSymList64(byte[] fileByteArys,
            int s_header_offset, int size) {
        System.out.println("s_header_offset = " +s_header_offset+ "  size = " +size);
        int symSize = elf64_sym.getSize();
        byte[] res = new byte[symSize];
        for(int i =0 ;i < size ;i++){
            System.arraycopy(fileByteArys, s_header_offset + i *symSize, res, 0, symSize);
            type_64.symList.add(parseDynSym64(res));
        }
      
        
    }

    private static elf64_sym parseDynSym64(byte[] res) {
        elf64_sym sym = new elf64_sym();
        sym.st_name = Utils.byte2Int(Utils.copyBytes(res, 0, 4));
        sym.st_info = Utils.copyBytes(res, 4, 1);
        sym.st_other = Utils.copyBytes(res, 5, 1);
        sym.st_shndx = Utils.byte2Short(Utils.copyBytes(res, 6, 2));
        sym.st_value = Utils.byte2Long(Utils.copyBytes(res, 8, 8));
        sym.st_st_size = Utils.byte2Long(Utils.copyBytes(res, 16, 8));
        System.out.println("sym  = " +sym );
        return sym;
    }

    private static void parseHeader(byte[] header, int offset) {
        if (header == null) {
            System.out.println("header is null");
            return;
        }
        /**
         * char    4 file_identification[4]  //.ELF
         * enum    1 ei_class_2  //02 64位， 01 32位
         * enum    1 ei_data_e  //ELFDATAONE  =0,ELFDATA2LSB =1(littleEndian  android默认),ELFDATA2MSB =2
         * enum    1 ei_version_e
         * enum    1 ei_osabi_e
         * uchar   1 ei_abiversion;
         * uchar   6 ei_pad[6];
         * uchar   1 ei_nident_SIZE;
         */
        byte[]magic = Utils.copyBytes(header, 0, 16);
        int ei_class_2 = magic[4] & 0xFF;//byte是单字节
        System.out.println("ei_class_2 = " +ei_class_2);
        if(ei_class_2 == 1){
            type_32.hdr.e_ident = Utils.copyBytes(header, 0, 16);// 魔数
            type_32.hdr.e_type = Utils.copyBytes(header, 16, 2);
            type_32.hdr.e_machine = Utils.copyBytes(header, 18, 2);
            type_32.hdr.e_version = Utils.copyBytes(header, 20, 4);
            type_32.hdr.e_entry = Utils.copyBytes(header, 24, 4);
            type_32.hdr.e_phoff = Utils.copyBytes(header, 28, 4);
            type_32.hdr.e_shoff = Utils.copyBytes(header, 32, 4);
            type_32.hdr.e_flags = Utils.copyBytes(header, 36, 4);
            type_32.hdr.e_ehsize = Utils.copyBytes(header, 40, 2);
            type_32.hdr.e_phentsize = Utils.copyBytes(header, 42, 2);
            type_32.hdr.e_phnum = Utils.copyBytes(header, 44, 2);
            type_32.hdr.e_shentsize = Utils.copyBytes(header, 46, 2);
            type_32.hdr.e_shnum = Utils.copyBytes(header, 48, 2);
            type_32.hdr.e_shstrndx = Utils.copyBytes(header, 50, 2);
            System.out.println("header 32 位 :"+type_32.hdr.toString());
        }else{
            type_64.hdr.e_ident = Utils.copyBytes(header, 0, 16);// 魔数
            type_64.hdr.e_type = Utils.byte2Short(Utils.copyBytes(header, 16, 2));
            type_64.hdr.e_machine = Utils.byte2Short(Utils.copyBytes(header, 18, 2));
            type_64.hdr.e_version =  Utils.byte2Short(Utils.copyBytes(header, 20, 4));
            type_64.hdr.e_entry = Utils.byte2Long(Utils.copyBytes(header, 24, 8));
            type_64.hdr.e_phoff = Utils.byte2Long(Utils.copyBytes(header, 32, 8));
            type_64.hdr.e_shoff =Utils.byte2Long( Utils.copyBytes(header, 40, 8));
            type_64.hdr.e_flags = Utils.byte2Int(Utils.copyBytes(header, 48, 4));
            type_64.hdr.e_ehsize = Utils.byte2Short(Utils.copyBytes(header, 52, 2));
            type_64.hdr.e_phentsize =  Utils.byte2Short(Utils.copyBytes(header, 54, 2));
            type_64.hdr.e_phnum =  Utils.byte2Short(Utils.copyBytes(header, 56, 2));
            type_64.hdr.e_shentsize = Utils.byte2Short(Utils.copyBytes(header, 58, 2));
            type_64.hdr.e_shnum = Utils.byte2Short(Utils.copyBytes(header, 60, 2));
            type_64.hdr.e_shstrndx = Utils.byte2Short(Utils.copyBytes(header, 62, 2));
            System.out.println("header 64 位 :"+type_64.hdr.toString());
            is64 = true;
        }
        /**
         * public byte[] e_ident = new byte[16]; public short e_type; public
         * short e_machine; public int e_version; public int e_entry; public int
         * e_phoff; public int e_shoff; public int e_flags; public short
         * e_ehsize; public short e_phentsize; public short e_phnum; public
         * short e_shentsize; public short e_shnum; public short e_shstrndx;
         */

    }
    
    
    

    /**
     * 解析段头信息内容
     */
    public static void parseSectionHeaderList32(byte[] header, int offset) {
        int header_size = 40;// 40个字节
        int header_count = Utils.byte2Short(type_32.hdr.e_shnum);// 头部的个数
        byte[] des = new byte[header_size];
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0,
                    header_size);
            type_32.shdrList.add(parseSectionHeader32(des));
        }
    }

    private static elf32_shdr parseSectionHeader32(byte[] header) {
        ElfType32.elf32_shdr shdr = new ElfType32.elf32_shdr();
        /**
         * public byte[] sh_name = new byte[4]; public byte[] sh_type = new
         * byte[4]; public byte[] sh_flags = new byte[4]; public byte[] sh_addr
         * = new byte[4]; public byte[] sh_offset = new byte[4]; public byte[]
         * sh_size = new byte[4]; public byte[] sh_link = new byte[4]; public
         * byte[] sh_info = new byte[4]; public byte[] sh_addralign = new
         * byte[4]; public byte[] sh_entsize = new byte[4];
         */
        shdr.sh_name = Utils.copyBytes(header, 0, 4);
        shdr.sh_type = Utils.copyBytes(header, 4, 4);
        shdr.sh_flags = Utils.copyBytes(header, 8, 4);
        shdr.sh_addr = Utils.copyBytes(header, 12, 4);
        shdr.sh_offset = Utils.copyBytes(header, 16, 4);
        shdr.sh_size = Utils.copyBytes(header, 20, 4);
        shdr.sh_link = Utils.copyBytes(header, 24, 4);
        shdr.sh_info = Utils.copyBytes(header, 28, 4);
        shdr.sh_addralign = Utils.copyBytes(header, 32, 4);
        shdr.sh_entsize = Utils.copyBytes(header, 36, 4);
        return shdr;
    }
    
    
    /**
     * 解析段头信息内容
     */
    public static void parseSectionHeaderList64(byte[] header, int offset) {
        int header_size = 64;
        int header_count = type_64.hdr.e_shnum;// 头部的个数
        byte[] des = new byte[header_size];
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0,
                    header_size);
            type_64.shdrList.add(parseSectionHeader64(des));
            System.out.println("i * header_size + offset = " +(i * header_size + offset));
        }
        for (elf64_shdr shdr : type_64.shdrList) {
           System.out.println("Sectopn header = " +shdr);
        }
    }

    private static elf64_shdr parseSectionHeader64(byte[] header) {
        ElfType64.elf64_shdr shdr = new ElfType64.elf64_shdr();
        /**
         * public byte[] sh_name = new byte[4]; public byte[] sh_type = new
         * byte[4]; public byte[] sh_flags = new byte[4]; public byte[] sh_addr
         * = new byte[4]; public byte[] sh_offset = new byte[4]; public byte[]
         * sh_size = new byte[4]; public byte[] sh_link = new byte[4]; public
         * byte[] sh_info = new byte[4]; public byte[] sh_addralign = new
         * byte[4]; public byte[] sh_entsize = new byte[4];
         */
        shdr.sh_name = Utils.byte2Int(Utils.copyBytes(header, 0, 4));
        shdr.sh_type = Utils.byte2Int(Utils.copyBytes(header, 4, 4));
        shdr.sh_flags = Utils.byte2Long(Utils.copyBytes(header, 8, 8));
        shdr.sh_addr = Utils.byte2Long(Utils.copyBytes(header, 16, 8));
        shdr.sh_offset = Utils.byte2Long(Utils.copyBytes(header, 24, 8));
        shdr.sh_size = Utils.byte2Long(Utils.copyBytes(header, 32, 8));
        shdr.sh_link = Utils.byte2Int(Utils.copyBytes(header, 40, 4));
        shdr.sh_info = Utils.byte2Int(Utils.copyBytes(header, 44, 4));
        shdr.sh_addralign = Utils.byte2Long(Utils.copyBytes(header, 48, 8));
        shdr.sh_entsize = Utils.byte2Long(Utils.copyBytes(header, 56, 8));
        return shdr;
    }
}
