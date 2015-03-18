package com.gionee.note.domain;

import java.io.Serializable;
import java.util.List;

import com.gionee.note.content.Constants;

public class Note implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	private String content;
	private String updateDate;
	private String updateTime;
	private String alarmTime=Constants.INIT_ALARM_TIME;
	private String bgColor;
	private String isFolder;
	private String parentFile;
	private String noteFontSize;
	private String noteListMode;
	private int haveNoteCount;
	private String widgetId;
	private String widgetType;

	private String title;

	private String mediaFolderName=Constants.MEDIA_FOLDER_NAME;
	private List<MediaInfo>  mediaInfos;
	private int noteMediaType ; //Constants.NOTE_NO_MEDIA has not  meida, Constants.NOTE_HAVE_PHOTO  has photo, Constants.NOTE_HAVE_VOICE has voice, Constants.NOTE_HAVE_PHOTO_VOICE has voice and photo
	private String addressName = "";
	private String addressDetail = "";
	public static final int NOTE_NO_EXIST = -2;
	public int getNoteMediaType() {
		return noteMediaType;
	}

	public void setNoteMediaType(int noteMediaType) {
		this.noteMediaType = noteMediaType;
	}

	public Note(){

	}
	
	// Gionee <lilg><2013-03-19> add for CR00785662 begin
	public Note(String id, String isFolder, String widgetId, String widgetType) {
		super();
		this.id = id;
		this.isFolder = isFolder;
		this.widgetId = widgetId;
		this.widgetType = widgetType;
	}
	// Gionee <lilg><2013-03-19> add for CR00785662 end

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNoteId(){
		if(id == null || "".equals(id)){
			return NOTE_NO_EXIST;
		}
		return Integer.valueOf(id);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(String isFolder) {
		this.isFolder = isFolder;
	}

	public String getParentFile() {
		return parentFile;
	}

	public void setParentFile(String parentFile) {
		this.parentFile = parentFile;
	}
	
	public int getParentId(){
		if(parentFile == null){
			return -1;
		}
		return "no".equals(parentFile) ? -1:Integer.valueOf(parentFile);
	}

	public String getNoteFontSize() {
		return noteFontSize;
	}

	public void setNoteFontSize(String noteFontSize) {
		this.noteFontSize = noteFontSize;
	}

	public String getNoteListMode() {
		return noteListMode;
	}

	public void setNoteListMode(String noteListMode) {
		this.noteListMode = noteListMode;
	}

	public String getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	public String getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}

	public int getHaveNoteCount() {
		return haveNoteCount;
	}

	public void setHaveNoteCount(int haveNoteCount) {
		this.haveNoteCount = haveNoteCount;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Note && this.id .equals(((Note)o).getId())){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.id == null ? 0:Integer.parseInt(this.id);
	}

	public String getMediaFolderName() {
		return mediaFolderName;
	}
	public void setMediaFolderName(String mediaFolderName) {
		this.mediaFolderName = mediaFolderName;
	}
	public List<MediaInfo> getMediaInfos() {
		return mediaInfos;
	}
	public void setMediaInfos(List<MediaInfo> mediaInfos) {
		this.mediaInfos = mediaInfos;
	}

	@Override
	public String toString() {
		return "id: " + getId() + ", content:" + getContent() + ", cdate:" + getUpdateDate()
		+ ", ctime:" + getUpdateTime() + ", atime:" + getAlarmTime()
		+ ", bgcolor:" + getBgColor() + ", isfolder:" + getIsFolder()
		+ ",parentfile:" + getParentFile() + ", noteFontSize:"
		+ getNoteFontSize() + ", noteListMode: " + getNoteListMode() + ", widgetId: " + getWidgetId() + ", widgetType: " + getWidgetType()
		+ ", haveNoteCount: " + getHaveNoteCount() + ", title: " + getTitle()+",mediaFolderName="+mediaFolderName+",noteMediaType="+noteMediaType;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	
	// Gionee <lilg><2013-03-19> add for CR00785662 begin
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Note(id, isFolder, widgetId, widgetType);
	}
	// Gionee <lilg><2013-03-19> add for CR00785662 end
}
