package com.demo.parseodex.dex;

import java.util.ArrayList;
import java.util.List;

public class ProtoIdsItem {
	
	/**
	 * struct proto_id_item
		{
		uint shorty_idx;
		uint return_type_idx;
		uint parameters_off;
		}
	 */
	
	public int shorty_idx;
	public int return_type_idx;
	public int parameters_off;
	
	//������ǹ����ֶΣ�����Ϊ�˴洢����ԭ���еĲ����������Ͳ�������
	public List<String> parametersList = new ArrayList<String>();
	public int parameterCount;
	
	public static int getSize(){
		return 4 + 4 + 4;
	}
	
	@Override
	public String toString(){
		return "shorty_idx:"+shorty_idx+",return_type_idx:"+return_type_idx+",parameters_off:"+parameters_off;
	}

}
