package com.gionee.note.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.gionee.note.HomeActivity;
import com.gionee.note.domain.ExportItem;
import com.gionee.note.domain.Note;

public class UtilsQueryDatas {
	public static final String isFolder = "yes";
	public static final String isNotFolder = "no";
	public static final String hasParentFolder = "yes";
	public static final String hasNotParentFolder = "no";
	public static final int folderIDInt = -1;
	public static final String enterIntoEditStr = "enterIntoEdit";
	public static final int enterIntoEdit = 1;
	public static synchronized Note queryNoteByID(int id,List<Note> noteArrs){
	try {
		if(noteArrs == null || noteArrs.size() == 0){
			Log.e("UtilsQueryDatas------noteArrs == null || noteArrs.size() == 0!");
			return null;
		}
		Note note = null;
		for(int i = 0;i<noteArrs.size();i++){
			if(id == Integer.valueOf(noteArrs.get(i).getId())){
				note = noteArrs.get(i);
				break;
			}
		}
		return note;
	} catch (Exception e) {
		Log.e("UtilsQueryDatas------queryNoteByID error!" + e);
		return null;
	}
	}
	
	
	
	public static synchronized void deleteNotes(List<Note> noteDelArrs, List<Note> noteArrs) {
		try {
			ArrayList<Note> arrs = new ArrayList<Note>();
			for (int i = 0; i < noteDelArrs.size(); i++) {
				for (int j = 0; j < noteArrs.size(); j++) {
					if (noteArrs.get(j).getId()
							.equals(noteDelArrs.get(i).getId())) {
						arrs.add(noteArrs.get(j));
					}
				}
			}
			for (int k = 0; k < arrs.size(); k++) {
				noteArrs.remove(arrs.get(k));
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("UtilsQueryDatas---deleteNotes---" + e);
		}
	}
	
    public static synchronized void queryNotesIsInFolder(int folderID,
            List<Note> sourceList, List<Note> resultList) {
        if (sourceList == null) {
            return;
        }
        for (int i = 0; i < sourceList.size(); i++) {
            Note note = sourceList.get(i);
            if (folderID == note.getParentId()) {
                resultList.add(note);
            }
        }
    }
	
	public static synchronized void queryNotesInFolder(int folderID,
			List<Note> sourceList, List<Note> resultList) {
		if(sourceList == null){
			return;
		}
			for (int i = 0; i < sourceList.size(); i++) {
				Note note = sourceList.get(i);
				if(folderID == note.getParentId() && !UtilsQueryDatas.isFolder.equals(note.getIsFolder())){
					resultList.add(note);
				}
			}
	}
	
	public static synchronized void queryExportItemIsInFolder(Context context,int folderID,
			List<Note> sourceList, List<ExportItem> resultList) {
		try {
			if(sourceList == null || resultList == null){
				return;
			}
			for (int i = 0; i < sourceList.size(); i++) {
				Note note = sourceList.get(i);
				if(folderID == note.getParentId()){
					if(isFolder.equals(note.getIsFolder()) && note.getHaveNoteCount() > 0){
						ExportItem exportItem = new ExportItem(true, note.getTitle(), note.getHaveNoteCount(), CommonUtils.getNoteData(context, note.getUpdateDate(), note.getUpdateTime()), false, note.getId());
						resultList.add(exportItem);
					}else if(!isFolder.equals(note.getIsFolder())){
						ExportItem exportItem = new ExportItem(false, (note.getTitle() == null || "".equals(note.getTitle()) ? note.getContent() : note.getTitle()), CommonUtils.getNoteData(context, note.getUpdateDate(), note.getUpdateTime()), note.getBgColor(), false, note.getId());
						resultList.add(exportItem);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("UtilsQueryDatas---queryExportItemIsInFolder---" + e);
		}
	}
	
	public static synchronized void deleteNote(Note note,List<Note> noteArrs){
		if(noteArrs == null){
			return;
		}
		
		// Gionee <lilg><2013-04-16> modify for CR00795403 begin
		if(note == null){
			return;
		}
		
		if(noteArrs.contains(note)){
			noteArrs.remove(note);
			if(note.getParentId() != -1){
				Note noteFolder = UtilsQueryDatas.queryNoteByID(note.getParentId(), 
						noteArrs);
				delNoteCountInFolder(noteFolder);
			}
		}
		// Gionee <lilg><2013-04-16> modify for CR00795403 end
		
	}
	
	public static synchronized void addNote(Note note,List<Note> noteArrs){
		if(noteArrs == null){
			return;
		}
		int posInt = UtilsQueryDatas.sortNote(note,noteArrs);
		noteArrs.add(posInt,note);
		if(note.getParentFile() != null && !"no".equals(note.getParentFile())){
			Note noteFolder = UtilsQueryDatas.queryNoteByID(Integer.valueOf(note.getParentFile()), 
					noteArrs);
			addNoteCountInFolder(noteFolder);
		}
	}
	
	public static synchronized void changeNote(Note note,List<Note> noteArrs){
		if(noteArrs == null){
			return;
		}
		noteArrs.remove(note);
		int posInt = UtilsQueryDatas.sortNote(note,noteArrs);
		noteArrs.add(posInt,note);
	}
	
	public static synchronized void moveNote(Note note, int folderID, List<Note> noteArrs) {
	try {
		if(noteArrs == null){
			return;
		}
		String parentID = note.getParentFile();
		Log.v("UtilsQueryDatas---parentID---" + parentID);
		if (folderID == folderIDInt) {
			if (!hasNotParentFolder.equals(parentID)) {
				note.setParentFile(hasNotParentFolder);
				for (int i = 0; i < noteArrs.size(); i++) {
					Note noteTemp = noteArrs.get(i);
						if (parentID.equals(noteTemp.getId())) {
							delNoteCountInFolder(noteTemp);
						}
				}
			}
		} else {
			if (hasNotParentFolder.equals(parentID) || null == parentID) {
				note.setParentFile(folderID + "");
				for (int i = 0; i < noteArrs.size(); i++) {
					Note noteTemp = noteArrs.get(i);
						if ((folderID + "").equals(noteTemp.getId())) {
							addNoteCountInFolder(noteTemp);
						}
				}
			}else if(!(folderID + "").equals(parentID)){
				note.setParentFile(folderID + "");
				for (int i = 0; i < noteArrs.size(); i++) {
					Note noteTemp = noteArrs.get(i);
						if ((folderID + "").equals(noteTemp.getId())) {
							addNoteCountInFolder(noteTemp);
						}
						if(parentID.equals(noteTemp.getId())){
							delNoteCountInFolder(noteTemp);
						}
				}
			}
		}
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("UtilsQueryDatas---moveNote---" + e);
	}
	}
	
	public static synchronized void queryNotesByKey(List<Note> searchNotes,List<Note> allNotes,String keys){
	try {
		if(searchNotes == null || allNotes == null){
			return;
		}
		searchNotes.clear();
		if(keys == null){
			searchNotes.addAll(allNotes);
			return;
		}
		for(int i = 0;i<allNotes.size();i++){
			Note note = allNotes.get(i);
			if(note.getTitle() != null){
				if(note.getTitle().contains(keys)){
					searchNotes.add(note);
					continue;
				}else{//If the title no search
					if(note.getContent() != null){
						
						// Gionee <lilg><2013-03-21> modiry for search without media info begin
						String tmpContent = CommonUtils.noteContentPreDeal(note.getContent());
						if(!TextUtils.isEmpty(tmpContent) && tmpContent.contains(keys)){
							// Gionee <lilg><2013-03-21> modiry for search without media info end
							searchNotes.add(note);
							continue;
						}
						
					}
				}
			}else if(note.getContent() != null){
				
				// Gionee <lilg><2013-03-21> modiry for search without media info begin
				String tmpContent = CommonUtils.noteContentPreDeal(note.getContent());
				if(!TextUtils.isEmpty(tmpContent) && tmpContent.contains(keys)){
					// Gionee <lilg><2013-03-21> modiry for search without media info end
					searchNotes.add(note);
					continue;
				}
				
			}
		}
		} catch (Exception e) {
		// TODO: handle exception
		Log.e("UtilsQueryDatas---queryNotesByKey---" + e);
		}
	}

	
	
	public static  synchronized int sortFolder(Note note,List<Note> allNotes){
		int positionInt = 0;
		try {
			if(allNotes == null){
				return positionInt;
			}
		for(int i = 0;i<allNotes.size();i++){
			Note noteTemp = allNotes.get(i);
			if(!isFolder.equals(noteTemp.getIsFolder())){//if it is not a folder,break
				positionInt = i;
				break;
			}
			if(note.getUpdateDate().compareTo(noteTemp.getUpdateDate()) < 0){
				positionInt = i + 1;
				continue;
			}else if(note.getUpdateDate().compareTo(noteTemp.getUpdateDate()) == 0){
				if(note.getUpdateTime().compareTo(noteTemp.getUpdateTime()) < 0){
					positionInt = i + 1;
					continue;
				}else if(note.getUpdateTime().compareTo(noteTemp.getUpdateTime()) > 0){
					positionInt = i;
					break;
				}else if(note.getUpdateTime().compareTo(noteTemp.getUpdateTime()) == 0){
					positionInt = i + 1;
					break;
				}
			}else if(note.getUpdateDate().compareTo(noteTemp.getUpdateDate()) > 0){
				positionInt = i;
				break;
			}
		}
		Log.v("UtilsQueryDatas---sortFolder---" + positionInt);
		return positionInt;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("UtilsQueryDatas---sortFolder---" + e);
			return positionInt;
		}
	}
	
	public static synchronized int sortNote(Note note,List<Note> allNotes){
		int positionInt = 0;
		try {
		if(allNotes == null){
			return positionInt;
		}
		for(int i = 0;i<allNotes.size();i++){
			Note noteTemp = allNotes.get(i);
			if(isFolder.equals(noteTemp.getIsFolder())){//if it is  a folder,continue
				positionInt = i + 1;
				continue;
			}
			if(note.getUpdateDate().compareTo(noteTemp.getUpdateDate()) < 0){
				positionInt = i + 1;
				continue;
			}else if(note.getUpdateDate().compareTo(noteTemp.getUpdateDate()) == 0){
				if(note.getUpdateTime().compareTo(noteTemp.getUpdateTime()) < 0){
					positionInt = i + 1;
					continue;
				}else if(note.getUpdateTime().compareTo(noteTemp.getUpdateTime()) > 0){
					positionInt = i;
					break;
				}else if(note.getUpdateTime().compareTo(noteTemp.getUpdateTime()) == 0){
					positionInt = i + 1;
					break;
				}
			}else if(note.getUpdateDate().compareTo(noteTemp.getUpdateDate()) > 0){
				positionInt = i;
				break;
			}
		}
		Log.v("UtilsQueryDatas---sortNote---positionInt---" + positionInt);
		return positionInt;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("UtilsQueryDatas---sortNote---" + e);
			Log.d("UtilsQueryDatas---sortNote---e---positionInt---" + positionInt);
			return positionInt;
		}
	}
	
	public static synchronized void updateNoteByWidgetID(int widgetID,List<Note> allNotes,int value){
	try {
		for(int i = 0;i<allNotes.size();i++){
			Note note = allNotes.get(i);
			if(note.getWidgetId() == null){
				continue;
			}
			if(widgetID == Integer.valueOf(note.getWidgetId())){
				note.setWidgetId(value + "");
				break;
			}
		}
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("UtilsQueryData");
	}
	}
	
	public static synchronized void addNoteCountInFolder(Note note){
		if(note == null){
			return;
		}
		int noteCount = note.getHaveNoteCount();
		note.setHaveNoteCount(++noteCount);
	}

	public static synchronized void delNoteCountInFolder(Note note){
		if(note == null){
			return;
		}
		int noteCount = note.getHaveNoteCount();
		note.setHaveNoteCount(--noteCount);
	}
	
}
