package com.gionee.note.domain;

public class ChildInfo {

	private String bgColor;
	private String title;
	private String updateDate;
	private boolean checked;
	private String dbId;

	public ChildInfo(String bgColor, String title, String updateDate,
			boolean checked, String dbId) {
		this.bgColor = bgColor;
		this.title = title;
		this.updateDate = updateDate;
		this.checked = checked;
		this.dbId = dbId;
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

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
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

}
