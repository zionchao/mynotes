package com.gionee.note.adapter;

import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.TextView;


import com.gionee.note.HomeActivity;
import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.ResourceParser;
import com.gionee.note.content.ResourceParser.NoteItemBgResources;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;

public class ViewAdapter extends ArrayAdapter implements OnClickListener {

	private LayoutInflater listContainer;
	private List<Note> mDataSource;
	private Context context;
	//	private boolean isHaveFolder = true;
	private int movePisition = -1;

	public ViewAdapter(Context context, List<Note> mDataSource) {
		super(context, -1);
		listContainer = LayoutInflater.from(context);

		this.mDataSource = mDataSource;
		this.context = context;

	}

	public void updateView(List<Note> mDataSource, int movePosition) {
		// this.mDataSource.clear();
		//movePisition表示在拖动过程中是否拖动到文件夹上
		this.movePisition = movePosition;
		this.mDataSource = mDataSource;
		notifyDataSetChanged();
	}

	public void updateView(int movePition) {
		this.movePisition = movePition;
		notifyDataSetChanged();
	}


	public List<Note> getmDataSource() {
		return mDataSource;
	}

	//	public boolean isHaveFolder() {
	//		return isHaveFolder;
	//	}
	//
	//	public void setHaveFolder(boolean isHaveFolder) {
	//		this.isHaveFolder = isHaveFolder;
	//	}

	@Override
	public int getCount() {
		Log.i("ViewAdapter------getCount_size: " + mDataSource.size());

		return mDataSource.size();
	}

	@Override
	public Object getItem(int position) {
		Log.i("ViewAdapter------getItem,position:" + position);

		return mDataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		Log.i("ViewAdapter------getItemId,position:" + position);

		return position;
	}

	//	private void nextNoteIsFolder(int position) {
	//		if (position + 1 < mDataSource.size()
	//				&& Constants.IS_FOLDER.equals(mDataSource.get(position + 1)
	//						.getIsFolder())) {
	//		
	//			isHaveFolder = true;
	//		} else {
	//		
	//			isHaveFolder = false;
	//
	//		}
	//
	//	}
	//Gionee <pengwei><20130626> modify for CR00829888 begin
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	try {
		Log.i("ViewAdapter------getView__position: " + position);
		if(mDataSource.size() == 0){
			return convertView;
		}
		Note note = mDataSource.get(position);
		String isFolder = note.getIsFolder();
		//		Log.i("jiating", "DragGridViewReal.isMove="+DragGridViewReal.isMove);
		//		if (DragGridViewReal.isMove) {
		//		
		//			if (HomeActivity.isGridView) {
		//				nextNoteIsFolder(position);
		//			} else {
		//				if (isHaveFolder) {
		//					nextNoteIsFolder(position);
		//				}
		//			}
		//		}

		ListItemView listItemView = null;
		//gionee 20121031 jiating CR00723004 begin
		if (convertView != null
				&& ((ListItemView) convertView.getTag()).folder.equals(note
						.getIsFolder())
						&& ((ListItemView) convertView.getTag()).presentModel
						.equals(String.valueOf(HomeActivity.isGridView))) {
			//gionee 20121031 jiating CR00723004 end
			Log.i("ViewAdapter------convertView != null");

			listItemView = (ListItemView) convertView.getTag();


		} else {

			Log.i("ViewAdapter------convertView == null");

			listItemView = new ListItemView();
			listItemView.folder = note.getIsFolder();
			//gionee 20121031 jiating CR00723004 begin
			listItemView.presentModel=String.valueOf(HomeActivity.isGridView);
			//gionee 20121031 jiating CR00723004 end
			if (Constants.IS_FOLDER.equals(isFolder)) {
				if (HomeActivity.isGridView) {
				    convertView = listContainer.inflate(
				            R.layout.home_gridview_folder_item_layout_white, null);

					listItemView.noteContent = (TextView) convertView
					.findViewById(R.id.home_folder_griditem_title);
					listItemView.noteData = (TextView) convertView
					.findViewById(R.id.home_folder_griditem_date);
					listItemView.alllayout = convertView
					.findViewById(R.id.home_folder_griditem_layout);
					listItemView.homeNoteTitleLayout = convertView
					.findViewById(R.id.home_folder_griditem_title_layout);
					//gionee 20121225 jiating modify for theme begin
//					listItemView.=convertView.findViewById(R.id.home_folder__griditem__layout);
					//gionee 20121225 jiating modify for theme end
					listItemView.homeNoteContentLayout = convertView
					.findViewById(R.id.home_folder__griditem_content_layout);
					listItemView.noteCount = (TextView) convertView
					.findViewById(R.id.home_foler_griditem_count);
					convertView.setTag(listItemView);

				} else {
					Log.i("ViewAdapter------getView__listCiew");
					convertView = listContainer.inflate(
                            R.layout.home_listview_folder_item_layout_white, null);

					listItemView.noteContent = (TextView) convertView
					.findViewById(R.id.home_folder_listview_content);
					listItemView.noteData = (TextView) convertView
					.findViewById(R.id.home_folder_listview_time);

					//					listItemView.listDiver = convertView
					//							.findViewById(R.id.list_diver);
					listItemView.noteCount = (TextView) convertView
					.findViewById(R.id.home_folder_listview_count);
					//gionee 20121225 jiating modify for theme begin
//					listItemView.folderIcon = (ImageView) convertView
//					.findViewById(R.id.folder_icon);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 begin
                    listItemView.homeListFolderLayout=convertView.findViewById(R.id.home_folder_listview_content_layout);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 end
					
					//gionee 20121225 jiating modify for theme begin
					
					convertView.setTag(listItemView);

				}
			} else {

				if (HomeActivity.isGridView) {
				    convertView = listContainer.inflate(
                            R.layout.home_gridview_note_item_layout_white,
                            null);

					listItemView.noteContent = (TextView) convertView
					.findViewById(R.id.home_note_griditem_content);
					listItemView.noteData = (TextView) convertView
					.findViewById(R.id.home_note_griditem_date);
					listItemView.alllayout = convertView
					.findViewById(R.id.home_note_griditem_layout);
					listItemView.homeNoteTitleLayout = convertView
					.findViewById(R.id.home_note_griditem_title_layout);
					listItemView.homeNoteContentLayout = convertView
					.findViewById(R.id.home_note_griditem_content_layout);
					listItemView.homeNoteAlarmIcon=convertView.findViewById(R.id.home_note_gridview_alarm_icon);
					convertView.setTag(listItemView);

				} else {

					Log.i("ViewAdapter------getView__listCiew");
					convertView = listContainer.inflate(
                            R.layout.home_listview_note_item_layout_white,
                            null);

					listItemView.noteContent = (TextView) convertView
					.findViewById(R.id.home_note_listview_content);
					listItemView.noteData = (TextView) convertView
					.findViewById(R.id.home_note_listview_time);
					//					listItemView.listDiver = convertView
					//	
					//gionee 20121225 jiating modify for theme begin.findViewById(R.id.list_diver);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 begin
					listItemView.homeListFolderLayout=convertView.findViewById(R.id.home_note_listview_content_layout);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 end
//					listItemView.folderIcon = (ImageView) convertView
//					.findViewById(R.id.note_icon);
					//gionee 20121225 jiating modify for theme end
					listItemView.homeNoteAlarmIcon=convertView.findViewById(R.id.home_note_listview_alarm_icon);

					convertView.setTag(listItemView);

				}

			}
		}
		
		if (isFolder.equals(Constants.NO_FOLDER)) {

			// listItemView.noteCount.setVisibility(View.GONE);
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
                listItemView.homeNoteTitleLayout
                .setBackgroundResource(NoteItemBgResources
                        .getHomeNotetTitleBgNormalResWhite(Integer
                                .parseInt(bg)));
                listItemView.homeNoteContentLayout
                .setBackgroundResource(NoteItemBgResources
                        .getHomeNoteContentBgNormalResWhite(Integer
                                .parseInt(bg)));

				if (body.length() > 15) {
					listItemView.noteContent.setText(body.substring(0, 15)
							.replace("\n", " ") + "...");
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
				//listItemView.noteContent.setTextColor(ResourceParser.getNoteGridContentColor(Integer
				//		.parseInt(bg)));
				//listItemView.noteData.setTextColor(ResourceParser.getNoteGridTimeColor(Integer
				//		.parseInt(bg)));
				
			} else {
				//				listItemView.listDiver.setVisibility(View.VISIBLE);
			    listItemView.homeListFolderLayout.setBackgroundResource(NoteItemBgResources
                        .getHomeListviewNoteBgNormalResourcesWhite(Integer
                        .parseInt(bg)));

				// if(position==mDataSource.size()-1){
				// listItemView.listDiver.setVisibility(View.GONE);
				// }
				// GIONEE wanghaiyan 2015-03-18 modify CR01455390 for begin
				if (body.length() > 20) {
					listItemView.noteContent.setText(body.substring(0, 20)
							.replace("\n", " ") + "...");
			     // GIONEE wanghaiyan 2015-03-18 modify CR01455390 for end
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
			    
				//listItemView.noteContent.setTextColor(ResourceParser.getNotelistContentColor(Integer
				//		.parseInt(bg)));
				//listItemView.noteData.setTextColor(ResourceParser.getNotelistTimeColor(Integer
				//		.parseInt(bg)));
				// listItemView.noteContent.setText(body);
			}
			
			// gn lilg 2012-12-04 modiry for NullPointerException begin
			/*if(!note.getAlarmTime().equals(Constants.INIT_ALARM_TIME)){
				listItemView.homeNoteAlarmIcon.setVisibility(View.VISIBLE);
			}else{
				listItemView.homeNoteAlarmIcon.setVisibility(View.GONE);
			}*/
			if(note.getAlarmTime() == null || note.getAlarmTime().equals(Constants.INIT_ALARM_TIME)){
				listItemView.homeNoteAlarmIcon.setVisibility(View.GONE);
			}else{
				listItemView.homeNoteAlarmIcon.setVisibility(View.VISIBLE);
			}
			// gn lilg 2012-12-04 modiry for NullPointerException end

		} else {
		    // the note is a folder
			String body = note.getTitle();
			if (HomeActivity.isGridView) {
				if (movePisition == position) {

					Log.i("ViewAdapter------movPotion: " + movePisition);

                    listItemView.homeNoteTitleLayout
                    .setBackgroundResource(R.drawable.folder_title_bg_white);
                    listItemView.homeNoteContentLayout
                    .setBackgroundResource(R.drawable.home_folder_content_selecton_white);

					// movePisition = -1;
				} else {
				    listItemView.homeNoteTitleLayout.setBackgroundResource(R.drawable.item_grid_folder_title_selector);
                    listItemView.homeNoteContentLayout.setBackgroundResource(R.drawable.item_grid_folder_content_selector);
                    

				}
				// Gionee <lilg><2013-04-05> modiry for CR00793613 begin
				// Gionee <wanghaiyan><2013-04-05> modiry for CR01452047 begin
				if (body.length() > 6) {
					listItemView.noteContent.setText(body.substring(0, 3)
							.replace("\n", " ") + "...");
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
				// Gionee <wanghaiyan><2013-04-05> modiry for CR01452047 end
				// Gionee <lilg><2013-04-05> modiry for CR00793613 end
				
				// gionee lilg 2013-01-13 modify begin
//				listItemView.noteContent.setTextColor(R.color.note_grid_content_yellow);
//				listItemView.noteCount.setTextColor(R.color.note_grid_content_yellow);
//				listItemView.noteData.setTextColor(R.color.note_grid_time_yellow);
				//listItemView.noteContent.setTextColor(context.getResources().getColor(R.color.note_grid_content_yellow));
				//listItemView.noteCount.setTextColor(context.getResources().getColor(R.color.note_grid_content_yellow));
				//listItemView.noteData.setTextColor(context.getResources().getColor(R.color.note_grid_time_yellow));
				// gionee lilg 2013-01-13 modify end
				
			} else {
				// if(position==mDataSource.size()-1){
				// listItemView.listDiver.setVisibility(View.GONE);
				// }
				//				listItemView.listDiver.setVisibility(View.VISIBLE);
				if (movePisition == position) {
				    listItemView.homeListFolderLayout.setBackgroundResource(R.drawable.home_list_folder_selected);

					// movePisition = -1;
				} else {
				    listItemView.homeListFolderLayout.setBackgroundResource(R.drawable.item_list_folder_selector);
				}
				if (body.length() > 10) {
					listItemView.noteContent.setText(body.substring(0, 10)
							.replace("\n", " ") + "...");
				} else {
					listItemView.noteContent.setText(body.replace("\n", " "));
				}
				
				// gionee lilg 2013-01-13 modify begin
//				listItemView.noteContent.setTextColor(R.color.note_list_content_yellow);
//				listItemView.noteCount.setTextColor(R.color.note_list_content_yellow);
//				listItemView.noteData.setTextColor(R.color.note_list_time_yellow);
				//listItemView.noteContent.setTextColor(context.getResources().getColor(R.color.note_list_content_yellow));
				//listItemView.noteCount.setTextColor(context.getResources().getColor(R.color.note_list_content_yellow));
				//listItemView.noteData.setTextColor(context.getResources().getColor(R.color.note_list_time_yellow));
				// gionee lilg 2013-01-13 modify end
			}

			listItemView.noteCount.setText("(" + note.getHaveNoteCount() + ")");

		}

		listItemView.noteData.setText(CommonUtils.getNoteData(context, 
				note.getUpdateDate(), note.getUpdateTime()));

		return convertView;
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("ViewAdapter---getView---" + e);
		return convertView;
	}
	}
	//Gionee <pengwei><20130626> modify for CR00829888 end
	@Override
	public void onClick(View v) {

	}

}
