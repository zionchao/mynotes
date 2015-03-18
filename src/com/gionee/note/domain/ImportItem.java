package com.gionee.note.domain;

public class ImportItem {

	private String fileName;
	private boolean checked;

	public ImportItem() {
		super();
	}

	public ImportItem(String fileName, boolean checked) {
		super();
		this.fileName = fileName;
		this.checked = checked;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
