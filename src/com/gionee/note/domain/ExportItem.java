package com.gionee.note.domain;

import java.io.Serializable;

public class ExportItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isFolder;
	private String title;
	private int subNum;
	private String updateDate;
	private String bgColor;
	private boolean checked;
	private String dbId;

	public ExportItem() {
		super();
	}

	public ExportItem(boolean isFolder, String title, int subNum,
			String updateDate, boolean checked, String dbId) {
		super();
		this.isFolder = isFolder;
		this.title = title;
		this.subNum = subNum;
		this.updateDate = updateDate;
		this.checked = checked;
		this.dbId = dbId;
	}

	public ExportItem(boolean isFolder, String title, String updateDate,
			String bgColor, boolean checked, String dbId) {
		super();
		this.isFolder = isFolder;
		this.title = title;
		this.updateDate = updateDate;
		this.bgColor = bgColor;
		this.checked = checked;
		this.dbId = dbId;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSubNum() {
		return subNum;
	}

	public void setSubNum(int subNum) {
		this.subNum = subNum;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}



	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	@Override
	public String toString() {
		return "isFolder: " + isFolder + ", title: " + title + ", subNum: "
				+ subNum + ", updateDate: " + updateDate + ", bgColor: "
				+ bgColor + ", checked: " + checked + ", dbId: " + dbId;
	}

}
