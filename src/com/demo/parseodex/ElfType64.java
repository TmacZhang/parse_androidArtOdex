package com.demo.parseodex;

import java.util.ArrayList;

public class ElfType64 {
    
    public elf64_rel rel;
    public elf64_rela rela;
    public elf64_sym sym;
    public elf64_hdr hdr;
    public elf64_phdr phdr;
    public elf64_shdr shdr;
    
    public ElfType64() {
        rel = new elf64_rel();
        rela = new elf64_rela();
        sym = new elf64_sym();
        hdr = new elf64_hdr();
        phdr = new elf64_phdr();
        shdr = new elf64_shdr();
    }
    
    public ArrayList<elf64_shdr> shdrList = new ArrayList<elf64_shdr>();//可能会有多个段头
    public ArrayList<elf64_sym> symList = new ArrayList<elf64_sym>();//可能会有多个dynsym
    /**
     *  typedef struct elf64_rel {
          Elf64_Addr r_offset;  // Location at which to apply the action 
          Elf64_Xword r_info;   // index and type of relocation
        } Elf64_Rel;
     */
    public class elf64_rel{
        public long r_offset;
        public long r_info;
    }
    
    /**
     *  typedef struct elf64_rela {
          Elf64_Addr r_offset;  // Location at which to apply the action
          Elf64_Xword r_info;   // index and type of relocation
          Elf64_Sxword r_addend;    // Constant addend used to compute value
        } Elf64_Rela;
     */
    public class elf64_rela{
        public long r_offset;
        public long r_info;
        public long r_addend;
    }
    
    /**
     *  typedef struct elf64_sym {
          Elf64_Word st_name;   // Symbol name, index in string tbl
          unsigned char st_info;    // Type and binding attributes
          unsigned char st_other;   // No defined meaning, 0
          Elf64_Half st_shndx;  // Associated section index
          Elf64_Addr st_value;  // Value of the symbol
          Elf64_Xword st_size;  // Associated symbol size
        } Elf64_Sym;
     */
    public static class elf64_sym {
        public int st_name;// 4
        public byte[] st_info = new byte[1];// 1
        public byte[] st_other = new byte[1];// 1
        public short st_shndx;// 2
        /**地址偏移量**/
        public long st_value;// 8  
        /**总大小**/
        public long st_st_size;// 8

        public static int getSize() {
            return 4 + 1 + 1 + 2 + 8 + 8;//24
        }

        @Override
        public String toString() {
            return "st_name = " + st_name + "  st_info=" + Utils.bytes2HexString(st_info)
                    + "  st_other=" + Utils.bytes2HexString(st_other) + "  st_shndx=" + st_shndx
                    + " st_value=" + st_value + "  st_st_size=" + st_st_size;
        }
    }
    
    /**
     * typedef struct elf64_hdr {
          unsigned char e_ident[16];    // ELF "magic number"
          Elf64_Half e_type;
          Elf64_Half e_machine;
          Elf64_Word e_version;
          Elf64_Addr e_entry;   // Entry point virtual address 
          Elf64_Off e_phoff;    // Program header table file offset 
          Elf64_Off e_shoff;    // Section header table file offset 
          Elf64_Word e_flags;
          Elf64_Half e_ehsize;
          Elf64_Half e_phentsize;
          Elf64_Half e_phnum;
          Elf64_Half e_shentsize;
          Elf64_Half e_shnum;
          Elf64_Half e_shstrndx;
        } Elf64_Ehdr;
        
        size = 40h
        header 头
     */
    public class elf64_hdr{
        public byte[] e_ident = new byte[16];// 16
        public short e_type; // 2
        public short e_machine;//2
        public int e_version;//4
        public long e_entry;//8
        public long e_phoff;//8
        public long e_shoff;//8
        public int e_flags;//4
        public short e_ehsize;//2
        public short e_phentsize;//2
        public short e_phnum;//2
        public short e_shentsize;//2
        public short e_shnum;//2
        public short e_shstrndx;//2
        
        public int getSize() {
            // 64
            return 16 + 2 + 2 + 4 + 8 * 3 + 4 + 2 * 6;
        }
     @Override
    public String toString() {
         return  "magic:"+ Utils.bytes2HexString(e_ident) 
                 +"\ne_type:"+(e_type)
                 +"\ne_machine:"+(e_machine)
                 +"\ne_version:"+(e_version)
                 +"\ne_entry:"+(e_entry)
                 +"\ne_phoff:"+(e_phoff)
                 +"\ne_shoff:"+(e_shoff)
                 +"\ne_flags:"+(e_flags)
                 +"\ne_ehsize:"+(e_ehsize)
                 +"\ne_phentsize:"+(e_phentsize)
                 +"\ne_phnum:"+(e_phnum)
                 +"\ne_shentsize:"+(e_shentsize)
                 +"\ne_shnum:"+(e_shnum)
                 +"\ne_shstrndx:"+(e_shstrndx);
        }
    }
    
    /**
     *  typedef struct elf64_phdr {
          Elf64_Word p_type;
          Elf64_Word p_flags;
          Elf64_Off p_offset;   // Segment file offset 
          Elf64_Addr p_vaddr;   // Segment virtual address 
          Elf64_Addr p_paddr;   // Segment physical address
          Elf64_Xword p_filesz; // Segment size in file 
          Elf64_Xword p_memsz;  // Segment size in memory
          Elf64_Xword p_align;  // Segment alignment, file & memory
        } Elf64_Phdr;
     */
    public class elf64_phdr{
        public int p_type;
        public int p_flags;
        public long p_offset;
        public long p_vaddr;
        public long p_paddr;
        public long p_filesz;
        public long p_memsz;
        public long p_align;
    }
    
    
    /**
     * typedef struct elf64_shdr {
          Elf64_Word sh_name;   // Section name, index in string tbl 
          Elf64_Word sh_type;   // Type of section 
          Elf64_Xword sh_flags; // Miscellaneous section attributes 
          Elf64_Addr sh_addr;   // Section virtual addr at execution 
          Elf64_Off sh_offset;  // Section file offset 
          Elf64_Xword sh_size;  // Size of section in bytes 
          Elf64_Word sh_link;   // Index of another section 
          Elf64_Word sh_info;   // Additional section information 
          Elf64_Xword sh_addralign; // Section alignment 
          Elf64_Xword sh_entsize;   // Entry size if section holds table 
        } Elf64_Shdr;
     */
    public static class elf64_shdr{
        public int sh_name;
        public int sh_type;
        public long sh_flags;
        public long sh_addr;
        public long sh_offset;
        public long sh_size;
        public int sh_link;
        public int sh_info;
        public long sh_addralign;
        public long sh_entsize;

        public static int getSize() {
            //64
            return 4 + 4 + 8 + 8 + 8 + 8 + 4 + 4 + 8 + 8;
        }
        
        @Override
        public String toString() {
            return "\n sh_name = " +sh_name
            +" sh_type ="+sh_type
            +" sh_flags ="+sh_flags
            +" sh_addr ="+sh_addr
            +" sh_offset ="+sh_offset
            +" sh_size ="+sh_size
            +" sh_link ="+sh_link
            +" sh_info ="+sh_info
            +" sh_addralign ="+sh_addralign
            +" sh_entsize ="+sh_entsize;
        }
    }
    

}
