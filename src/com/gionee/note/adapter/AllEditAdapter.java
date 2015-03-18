package com.gionee.note.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.gionee.note.ALLEditActivity;
import com.gionee.note.HomeActivity;
import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.ResourceParser;
import com.gionee.note.content.ResourceParser.NoteItemBgResources;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AllEditAdapter extends ArrayAdapter {

	private LayoutInflater listContainer;
	private List<Note> mDataSource;
	private ALLEditActivity context;
	// private ArrayList<HashMap<String, Object>> mChecked ;
	private ArrayList<Note> choicemData;// 放置选中的note的ID
	private boolean isMoveFolder = false;

	public AllEditAdapter(Context context, List<Note> mDataSource) {
		super(context, -1);
		listContainer = LayoutInflater.from(context);

		this.mDataSource = mDataSource;
		this.context = (ALLEditActivity) context;
		choicemData = new ArrayList<Note>();
		// mChecked = new ArrayList<HashMap<String,Object>>();
		// for (int i = 0; i < mDataSource.size(); i++) {
		// HashMap<String, Object> temp=new HashMap<String, Object>();
		// temp.put(mDataSource.get(i).getId(), false);
		// mChecked.add(temp);
		// }

	}


	@Override
	public int getCount() {
		Log.i("AllEditAdapter------getCount: "+mDataSource.size());

		return mDataSource.size();
	}

	@Override
	public Object getItem(int position) {

		return mDataSource.get(position);
	}

	public ArrayList<Note> getChoicemData() {
		return choicemData;
	}

	public void setChoicemData(ArrayList<Note> choicemData) {
		this.choicemData = choicemData;
	}

	@Override
	public long getItemId(int position) {

		Log.i("AllEditAdapter------getItemId, position:" + position);

		return position;
	}

	public void updateData(List<Note> newData) {
		mDataSource = newData;
		// mChecked.clear();
		choicemData.clear();

		// for (int i = 0; i < mDataSource.size(); i++) {
		//
		// HashMap<String, Object> temp=new HashMap<String, Object>();
		// temp.put(mDataSource.get(i).getId(), false);
		// mChecked.add(temp);
		//
		// }
		notifyDataSetChanged();
	}


	// public ArrayList<HashMap<String, Object>> getmChecked() {
	// return mChecked;
	// }

	public void updateView(Boolean isChceck) {

		Log.i( "AllEditAdapter------updateView()" );

		// mChecked.clear();
		choicemData.clear();
		if (isChceck) {
			Log.i( "AllEditAdapter------updateView():" + mDataSource.size());
			for (int i = 0; i < mDataSource.size(); i++) {
				choicemData.add(mDataSource.get(i));
				// if (isChceck) {
				// HashMap<String, Object> temp=new HashMap<String, Object>();
				// temp.put(mDataSource.get(i).getId(), true);
				// mChecked.add(temp);
				// // mChecked.add(true);
				// // choicemData.add(mDataSource.get(i).getId());
				// choicemData.add(mDataSource.get(i));
				// } else {
				// HashMap<String, Object> temp=new HashMap<String, Object>();
				// temp.put(mDataSource.get(i).getId(), false);
				// mChecked.add(temp);
				// // mChecked.add(false);
				// }

			}
		} else {
			choicemData.clear();
		}
		notifyDataSetChanged();
	}

	public void updateViewState(boolean isMoveFolder) {

		this.isMoveFolder = isMoveFolder;
		notifyDataSetChanged();
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = initView(position, convertView);

		return convertView;
	}

	private View initView(int position, View convertView) {
		Log.i( "AllEditAdapter------initView,position:" + position);

		final Note note = mDataSource.get(position);
		String isFolder = note.getIsFolder();
		ListItemView listItemView = null;
		//gionee 20121031 jiating CR00723004 begin
		if (convertView != null	&& ((ListItemView) convertView.getTag()).folder.equals(note.getIsFolder())&& ((ListItemView) convertView.getTag()).presentModel.equals(String.valueOf(HomeActivity.isGridView))) {
			//gionee 20121031 jiating CR00723004 end
			// if (convertView != null){
			listItemView = (ListItemView) convertView.getTag();
		} else {
			listItemView = new ListItemView();
			listItemView.folder = note.getIsFolder();
			//gionee 20121031 jiating CR00723004 begin
			listItemView.presentModel=String.valueOf(HomeActivity.isGridView);
			//gionee 20121031 jiating CR00723004 end
			if (Constants.IS_FOLDER.equals(isFolder)) {
				// is folder
				if (HomeActivity.isGridView) {
				    convertView = listContainer.inflate(com.gionee.note.R.layout.all_edit_gridview_folder_layout_white,	null);

					listItemView.isSelect = (CheckBox) convertView.findViewById(R.id.all_edit_gridview_folder_select);
					listItemView.noteContent = (TextView) convertView.findViewById(R.id.all_edit_gridview_folder_content);
					listItemView.noteData = (TextView) convertView.findViewById(R.id.all_edit_gridview_folder_data);
					listItemView.alllayout = (RelativeLayout) convertView.findViewById(R.id.all_edit_gridview_folder_all_layout);
					listItemView.greyImageView = (ImageView) convertView.findViewById(R.id.grey_imageView);
					listItemView.homeNoteTitleLayout = convertView.findViewById(R.id.all_edit_gridview_folder_title_layout);
					listItemView.homeNoteContentLayout = convertView.findViewById(R.id.all_edit_gridview_folder_content_layout);
					listItemView.noteCount = (TextView) convertView.findViewById(R.id.all_edit_gridview_folder_count);
					convertView.setTag(listItemView);

				} else {

				    convertView = listContainer.inflate(com.gionee.note.R.layout.all_edit_listview_folder_layout_white,	null);

					listItemView.isSelect = (CheckBox) convertView.findViewById(R.id.all_edit_listview_folder_select);
					listItemView.noteContent = (TextView) convertView.findViewById(R.id.all_edit_listview_folder_content);
					listItemView.noteData = (TextView) convertView.findViewById(R.id.all_edit_listview_folder_time);
					listItemView.greyImageView = (ImageView) convertView.findViewById(R.id.grey_imageView);
					listItemView.noteCount = (TextView) convertView.findViewById(R.id.all_edit_listview_folder_count);
					
					// gionee lilg 2013-01-15 add begin
					listItemView.allEditListviewFolderAllLayout = (RelativeLayout) convertView.findViewById(R.id.all_edit_listview_folder_all_layout);
					// gionee lilg 2013-01-15 add end
					
					convertView.setTag(listItemView);
				}
			} else {
				// is note
				if (HomeActivity.isGridView) {
				    convertView = listContainer.inflate(com.gionee.note.R.layout.all_edit_gridview_note_layout_white,null);

					listItemView.isSelect = (CheckBox) convertView.findViewById(R.id.all_edit_gridview_note_seleect);
					listItemView.noteContent = (TextView) convertView.findViewById(R.id.all_edit_gridview_note_content);
					listItemView.noteData = (TextView) convertView.findViewById(R.id.all_edit_gridview_note_data);
					listItemView.alllayout = (RelativeLayout) convertView.findViewById(R.id.all_edit_gridview_note_all_layout);
					listItemView.greyImageView = (ImageView) convertView.findViewById(R.id.grey_imageView);
					listItemView.homeNoteTitleLayout = convertView.findViewById(R.id.all_edit_gridview_note_title_layout);
					listItemView.homeNoteContentLayout = convertView.findViewById(R.id.all_edit_gridview_note_content_layout);
					listItemView.homeNoteAlarmIcon=convertView.findViewById(R.id.all_edit_gridview_alarm_icon);
					convertView.setTag(listItemView);

				} else {
				    convertView = listContainer.inflate(com.gionee.note.R.layout.all_edit_listview_note_layout_white, null);

					listItemView.isSelect = (CheckBox) convertView.findViewById(R.id.all_edit_listview_note_select);
					listItemView.noteContent = (TextView) convertView.findViewById(R.id.all_edit_listview_note_content);
					listItemView.noteData = (TextView) convertView.findViewById(R.id.all_edit_listview_note_time);
					listItemView.greyImageView = (ImageView) convertView.findViewById(R.id.grey_imageView);
					
					// gn lilg 2012-12-27 modify for common controls begin
//					listItemView.folderIcon = (ImageView) convertView.findViewById(R.id.note_icon);
					listItemView.allEditListviewNoteAllLayout = (RelativeLayout) convertView.findViewById(R.id.all_edit_listview_note_all_layout);
					// gn lilg 2012-12-27 modify for common controls end
					
					listItemView.homeNoteAlarmIcon=convertView.findViewById(R.id.all_edit_listview_alarm_icon);
					convertView.setTag(listItemView);
				}
			}
		}

		// jiating begin
		if (isFolder.equals(Constants.NO_FOLDER)) {
			// is note
			String title = note.getTitle();
			String body;
			if (null == title) {
				// gn lilg 2013-02-27 add for delete the media info in the note content begin
//				body = note.getContent();
				body = CommonUtils.noteContentPreDeal(note.getContent());
				// gn lilg 2013-02-27 add for delete the media info in the note content end
			} else {
				body = title;
			}
			String bg = null == note.getBgColor() ? "0" : note.getBgColor();
			if (HomeActivity.isGridView) {
			    listItemView.homeNoteTitleLayout.setBackgroundResource(NoteItemBgResources.getHomeNotetTitleBgNormalResWhite(Integer.parseInt(bg)));
			    listItemView.homeNoteContentLayout.setBackgroundResource(NoteItemBgResources.getHomeNoteContentBgNormalResWhite(Integer.parseInt(bg)));
				if (body.length() > 10) {
					listItemView.noteContent.setText(body.substring(0, 10).replace("\n", " ") + "...");
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
			} else {
			    listItemView.allEditListviewNoteAllLayout.setBackgroundResource(NoteItemBgResources.getHomeListviewNoteBgNormalResourcesWhite(Integer.parseInt(bg)));
				if (body.length() > 8) {
					listItemView.noteContent.setText(body.substring(0, 8).replace("\n", " ") + "...");
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
			}
			
			// gionee lilg 2013-01-23 add for GUI style begin
			//listItemView.noteContent.setTextColor(ResourceParser.getNoteGridContentColor(Integer.parseInt(bg)));
			//listItemView.noteData.setTextColor(ResourceParser.getNoteGridTimeColor(Integer.parseInt(bg)));
			// gionee lilg 2013-01-23 add for GUI style end
			
			// gn pengwei 20121225 modify for CR00753125 begin
			if (note.getAlarmTime() != null) {
				if (Constants.INIT_ALARM_TIME.equals(note.getAlarmTime())) {
					listItemView.homeNoteAlarmIcon.setVisibility(View.GONE);
				} else {
					listItemView.homeNoteAlarmIcon.setVisibility(View.VISIBLE);
				}
			}else{
				listItemView.homeNoteAlarmIcon.setVisibility(View.GONE);
			}
			// gn pengwei 20121225 modify for CR00753125 end
		} else if (isFolder.equals(Constants.IS_FOLDER)) {
			// is folder
			
			String body = note.getTitle();
			if (HomeActivity.isGridView) {
			    listItemView.homeNoteTitleLayout.setBackgroundResource(R.drawable.item_grid_folder_title_selector);
			    listItemView.homeNoteContentLayout.setBackgroundResource(R.drawable.item_grid_folder_content_selector);
				if (body.length() > 3) {
					listItemView.noteContent.setText(body.substring(0, 3).replace("\n", " ") + "...");
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
			} else {
				if (body.length() > 8) {
					listItemView.noteContent.setText(body.substring(0, 8).replace("\n", " ") + "...");
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
				
				// gionee lilg 2013-01-13 add begin
				listItemView.allEditListviewFolderAllLayout.setBackgroundResource(R.drawable.item_list_folder_selector);
				// gionee lilg 2013-01-13 add end
			}
			// listItemView.contentlayout
			// .setBackgroundResource(R.drawable.folder_bg);
			listItemView.noteCount.setText("(" + note.getHaveNoteCount() + ")");
			
			// gionee lilg 2013-01-23 add for GUI style begin
			//listItemView.noteData.setTextColor(context.getResources().getColor(R.color.note_grid_time_yellow));
			//listItemView.noteContent.setTextColor(context.getResources().getColor(R.color.note_grid_content_yellow));
			//listItemView.noteCount.setTextColor(context.getResources().getColor(R.color.note_grid_content_yellow));
			// gionee lilg 2013-01-23 add for GUI style end
		}

		listItemView.noteData.setText(CommonUtils.getNoteData(context, note.getUpdateDate(), note.getUpdateTime()));
		
		// jiating end

		listItemView.isSelect.setTag(position);
		listItemView.isSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    if(!(v instanceof CheckBox)){
			        return;
			    }
				CheckBox cb = (CheckBox) v;
				// HashMap<String, Object> temp=mChecked.get(p);
				// temp.put(note.getId(), cb.isChecked());
				// mChecked.set(p, temp);
				if (cb.isChecked()) {
					choicemData.add(note);
				} else {
					choicemData.remove(note);
				}
				context.setAllViewState();
			}
		});
		// listItemView.greyImageView.setVisibility(View.VISIBLE);

		Log.i( "AllEditAdapter------choicemData.size(): " + choicemData.size());
		boolean isSelect = false;
		setCheckBoxState(note,listItemView,isSelect);
		/*
		if (choicemData.size() > 0) {
			for (int i = 0; i < choicemData.size(); i++) {
				if (note.equals(((Note) choicemData.get(i)))) {
					listItemView.isSelect.setChecked(true);
					isSelect = true;
					break;
				}
				if (!isSelect) {
					listItemView.isSelect.setChecked(false);
				}
			}
		} else {
			listItemView.isSelect.setChecked(false);
		}
		*/
		// listItemView.isSelect.setChecked(Boolean.parseBoolean(mChecked.get(position).get(note.getId()).toString()));
		if (isMoveFolder) {
			if (isFolder.equals(Constants.NO_FOLDER)) {
				listItemView.greyImageView.setVisibility(View.VISIBLE);
				listItemView.greyImageView.setClickable(true);
				listItemView.greyImageView.setFocusable(true);
				listItemView.greyImageView.setFocusableInTouchMode(true);
				listItemView.isSelect.setClickable(false);
				listItemView.isSelect.setFocusable(false);
			} else {
				listItemView.isSelect.setVisibility(View.GONE);
				listItemView.isSelect.setClickable(false);
				listItemView.isSelect.setFocusable(false);
				listItemView.greyImageView.setVisibility(View.GONE);
				listItemView.greyImageView.setClickable(false);
				listItemView.greyImageView.setFocusable(false);
				listItemView.greyImageView.setFocusableInTouchMode(false);
			}
			// convertView.setClickable(false);
		} else {
			listItemView.greyImageView.setVisibility(View.GONE);
			listItemView.isSelect.setVisibility(View.VISIBLE);
			listItemView.isSelect.setClickable(false);
			listItemView.isSelect.setFocusable(false);
			// convertView.setClickable(true);
		}

		return convertView;

	}
	private void setCheckBoxState(final Note note, ListItemView listItemView, boolean isSelect) { 
		if (choicemData.size() > 0) { 
			for (int i = 0; i < choicemData.size(); i++) { 
				if (note.equals(((Note) choicemData.get(i)))) { 
					changeCheckBoxState(listItemView.isSelect,true); 
					isSelect = true; 
					break; 
				} 
			} 
			if (!isSelect) { 
				changeCheckBoxState(listItemView.isSelect,false); 
			} 
		} else { 
			changeCheckBoxState(listItemView.isSelect,false); 
		} 
	} 


	private void changeCheckBoxState(CheckBox cb,boolean state){ 
		boolean currentState=cb.isChecked(); 
		Log.i( "AllEditAdapter------changeCheckBoxState: currentState==" 
		+ currentState+"###newstate=="+state+"##tag=="+cb.getTag()); 
		if(currentState!=state){ 
			cb.setChecked(state); 
		} 
	}	
}
