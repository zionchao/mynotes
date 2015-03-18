package com.gionee.note.domain;

import java.io.Serializable;

import com.gionee.note.content.Constants;

public class MediaInfo  implements Serializable{
	
    private static final long serialVersionUID = 1L;
    
	private String id;
	private String noteId;
	private String mediaType; //Constants.MEDIA_PHOTO  is photo,Constants.MEDIA_VOIDCE is voice
	private String mediaFileName;
	private int isDelete=Constants.MEDIA_UNDELETE;  //Constants.MEDIA_UNDELETE is unDelete , Constants.MEDIA_DELETED is deleted
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNoteId() {
		return noteId;
	}
	public int getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getMediaFileName() {
		return mediaFileName;
	}
	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof MediaInfo && this.id .equals(((MediaInfo)o).getId())){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.id == null ? 0:Integer.parseInt(this.id);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "id="+id+", noteId="+noteId+",mediaType= "+mediaType+",mediaFileName="+mediaFileName;
	}
	
}
