package com.gionee.note.utils;

import java.util.Comparator;

import com.gionee.note.domain.ImportItem;

public class ImportCheckedFileDescComparator implements Comparator<ImportItem> {

	/**
	 * 按文件名逆序
	 */
	public int compare(ImportItem obj1, ImportItem obj2) {
		
		if(obj1.getFileName().compareTo(obj2.getFileName()) > 0){
			return -1;
		}else if(obj1.getFileName().compareTo(obj2.getFileName()) < 0){
			return 1;
		}else{
			return 0;
		}
		
	};
	
}
