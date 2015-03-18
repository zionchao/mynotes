package com.gionee.note.domain;

public class GroupInfo {

	private String title;

	/**
	 * 包含的note数量
	 */
	private int subNum;
	private boolean checked;
	private String dbId;

	/**
	 * 用于区分文件夹还是rootNote
	 */
	private boolean isFolder;

	/**
	 * note bg color
	 */
	private String bgColor;
	private String updateDate;
	
	/**
	 * group check box bg
	 * contain three state
	 */
	private int checkedBoxBg;

	// root note
	public GroupInfo(String title, boolean checked,
			boolean isFolder, String dbId, String bgColor, String updateDate) {
		this.title = title;
		this.checked = checked;
		this.isFolder = isFolder;
		this.dbId = dbId;
		this.bgColor = bgColor;
		this.updateDate = updateDate;
	}

	// folder
	public GroupInfo(String title, int subNum, boolean checked,
			boolean isFolder, String dbId, int checkedBoxBg) {
		this.title = title;
		this.subNum = subNum;
		this.checked = checked;
		this.isFolder = isFolder;
		this.dbId = dbId;
		this.checkedBoxBg = checkedBoxBg;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public int getSubNum() {
		return subNum;
	}

	public void setSubNum(int subNum) {
		this.subNum = subNum;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public int getCheckedBoxBg() {
		return checkedBoxBg;
	}

	public void setCheckedBoxBg(int checkedBoxBg) {
		this.checkedBoxBg = checkedBoxBg;
	}

}
