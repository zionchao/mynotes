package com.gionee.note.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
public final class ListItemView {

	public TextView noteContent;
	public TextView noteData;
	public CheckBox isSelect;
	public View homeNoteTitleLayout;
	public View homeNoteContentLayout;
	public View  alllayout;
	public ImageView  greyImageView;
	public TextView noteCount;
	public String folder;
	//gionee 20121031 jiating CR00723004 begin
	public String presentModel;
	//gionee 20121031 jiating CR00723004 end
	public View homeNoteAlarmIcon;
	//gionee 20121225 jiating modify for theme begin
	public View homeListFolderLayout;
	//gionee 20121225 jiating modify for theme end
	
	// gn lilg 2012-12-27 add for common controls begin
	public RelativeLayout allEditListviewNoteAllLayout;
	// gn lilg 2012-12-27 add for common controls end
	
	// gn lilg 2013-01-15 add begin
	public RelativeLayout allEditListviewFolderAllLayout;
	// gn lilg 2013-01-15 add end
	
}