package com.gionee.note;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.app.AmigoAlertDialog.Builder;
import android.app.Dialog;
import amigo.app.AmigoProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import amigo.provider.AmigoSettings;
import amigo.widget.AmigoListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.note.adapter.AllEditAdapter;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.Notes;
import com.gionee.note.content.StatisticalValue;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.MediaInfo;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.FileUtils;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.Statistics;
import com.gionee.note.utils.UtilsQueryDatas;
import com.gionee.note.utils.WidgetUtils;
import amigo.widget.AmigoButton;


//Gionee <pengwei><2013-11-2> modify for CR00935356 begin
public class ALLEditActivity extends AmigoActivity implements OnClickListener {

	// Gionee <lilg><2013-05-21> modify for super theme begin
	// gn lilg 12-12-15 modify for common controls begin
	//	private ImageButton btn_delete, btn_move, btn_send;
	/*private ImageView btn_delete, btn_send, btn_move;
	private LinearLayout layout_btn_delete, layout_btn_send, layout_btn_move, bottom_view;
	private TextView mTextDelete, mTextSend, mTextMove;*/
	// gn lilg 12-12-15 modify for common controls end
	// Gionee <lilg><2013-05-21> modify for super theme end
	
	// Gionee <lilg><2013-05-21> add for super theme begin
	private MenuItem mDelete;
	private MenuItem mShare;
	private MenuItem mMove;
	// Gionee <lilg><2013-05-21> add for super theme end

	private AllEditAdapter adapter;

	private DBOperations dbo;

	private GridView gridView;
	private AmigoListView listView;

	private boolean isHaveFolder, isSelectFolder;
	private boolean isMoveFolder;
	private boolean isInFolder;
	private int folderId;
	private AmigoProgressDialog mProgressDialog;
	private static int MAX_PROGRESS;
	private static int BATCH_DELETE = 1;
	private static int BATCH_MOVE_INFOLDER = 2;
	private static int BATCH_MOVE_FROM_FOLDER = 3;

	private List<Note> noteList;
	private List<Note> mTempList  = new ArrayList<Note>();
	// gn lilg 20120-12-14 modify for common controls begin
	private AmigoActionBar actionBar;
	private MenuItem allSelectItem;
	private View mCustomView;

    private AmigoButton mSelectedAllButton;

	// gn lilg 20120-12-14 modify for common controls end

	// Gionee <lilg><2013-04-16> add for CR00795403 begin
	private static boolean mIsBatchDeleteCurrent;
	private static boolean mIsBatchMoveCurrent;
	// Gionee <lilg><2013-04-16> add for CR00795403 end
	
	private boolean hasTask = false;
	//gionee 20121026  jiating CR00718021 begin
	private ContentObserver allEditActivitymGusestModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
		@Override
		public void onChange(boolean selfChange) {
			Log.d("ALLEditActivity------Guest mode changed, selfChange: " + selfChange);
			if (NoteApplication.GN_GUEST_MODE && !hasTask) {
				finish();
			}
		}
	};
	//gionee 20121026  jiating CR00718021 end
	
	private TextView selectCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("ALLEditActivity------onCreate begin!");

		CommonUtils.setTheme(this);
		setContentView(R.layout.edit_all_folders_notes_white);

		super.onCreate(savedInstanceState);
		//gionee 20121026  jiating CR00718021 begin
		getContentResolver().registerContentObserver(CommonUtils.getUri(), false, allEditActivitymGusestModeObserver);
		//gionee 20121026  jiating CR00718021 end

		// gn lilg 20120-12-14 modify for common controls begin
		actionBar = getAmigoActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		mCustomView = getLayoutInflater().inflate(R.layout.actionbar_custom_view, null);
		selectCount = (TextView) mCustomView.findViewById(R.id.all_edit_selected_count_tv);
		selectCount.setText(getResources().getString(R.string.select_count, 0));
//		actionBar.setTitle(getResources().getString(R.string.select_count, 0));
		
		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gn_com_title_bar));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
		
		// Gionee <lilg><2013-05-23> modify for super theme begin
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);
		
        mSelectedAllButton = (AmigoButton) mCustomView.findViewById(R.id.selected_all);
        mSelectedAllButton.setText(R.string.all_secect);
        mSelectedAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
            public void onClick(View v) {
				Log.i("ALLEditActivity------mSelectedAllButton onClick");
				//Gionee <pengwei><20131013> modify for CR00916677 begin
				//if(noteList.size() == 0){
       			//	mSelectedAllButton.setText(R.string.all_secect);
				//	return;
				//}
				//Gionee <pengwei><20131013> modify for CR00916677 end
//				getCheckedItemCount();
//				if(checkedNum > 0){
//
//					if(checkedNum != allItemNum){
//						if(!checked){
//							return;
//						}
//					}
//				}
				
				int checkedSize = adapter.getChoicemData().size();
				int maxCount = adapter.getCount();
				Log.i("ALLEditActivity------mSelectedAllButton checked changed, is checkedSize: " + checkedSize);
				Log.d("ALLEditActivity------mSelectedAllButton checked changed, is maxCount: " + maxCount);
				boolean checked = false;
				if(checkedSize < maxCount){
					checked = true;
                    mSelectedAllButton.setText(R.string.all_no_select);
				}else{
                    mSelectedAllButton.setText(R.string.all_secect);
                }
				//if(checkedSize > 0){
				//	if(checkedSize != maxCount){
				//		if(!checked){
				//			return;
				//		}
				//	}
				//}
				
				actionAllSelect2(checked);
			}
		});
		Log.d("ALLEditActivity------mSelectedAllButton checked changed, is1: ");
		// Gionee <lilg><2013-05-23> modify for super theme end
		// gn lilg 20120-12-14 modify for common controls end

		isMoveFolder = false;
		isInFolder = getIntent().getBooleanExtra(Constants.IS_IN_FOLDER, false);
		folderId = getIntent().getIntExtra(DBOpenHelper.ID, -1);
		Log.d("ALLEditActivity------isInFolder: " + isInFolder);
		Log.d("ALLEditActivity------folderId: " + folderId);


		Statistics.setReportCaughtExceptions(true);
		initViews();
		noteList = new ArrayList<Note>();
		adapter = new AllEditAdapter(this, noteList);
		Log.d("ALLEditActivity------mSelectedAllButton checked changed, is3: " + noteList.size());
		Log.i("ALLEditActivity------onCreate end!");
	}

	//Gionee <pengwei><2013-11-12> modify for CR00950700 begin
	private void notifyAdapter(){
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	//Gionee <pengwei><2013-11-12> modify for CR00950700 end
	
	@Override
	protected void onResume() {
		Log.d("ALLEditActivity------mSelectedAllButton checked changed, is2: ");
		Log.i("ALLEditActivity------onResume begin!");
		super.onResume();
		//Gionee <pengwei><2013-11-12> modify for CR00950700 begin
		notifyAdapter();
		//Gionee <pengwei><2013-11-12> modify for CR00950700 end
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).registerVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		getDBAllData();
		setAllViewState();
		setShowViewMode();
		Statistics.onResume(this);
		Log.i("ALLEditActivity------onResume end!");
	}

	@Override
	protected void onPause() {
		Log.i("ALLEditActivity------onPause begin!");
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).unregisterVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		Log.i("ALLEditActivity------onPause end!");
		super.onPause();
	}
	
	/*
	 * init the view
	 */
	private void initViews() {
		Log.i("ALLEditActivity------initViews begin!");

		gridView = (GridView) findViewById(R.id.all_edit_gridview);
		listView = (AmigoListView) findViewById(R.id.all_edit_listview);

		// Gionee <lilg><2013-05-21> modify for super theme begin
		// gn lilg 12-12-15 modify for common controls begin
		/*btn_delete = (ImageView) findViewById(R.id.btn_delete);
		btn_send = (ImageView) findViewById(R.id.btn_send);
		btn_move = (ImageView) findViewById(R.id.btn_move);

		bottom_view = (LinearLayout) findViewById(R.id.bottum_view);
		layout_btn_delete = (LinearLayout) findViewById(R.id.layout_btn_delete);
		layout_btn_send = (LinearLayout) findViewById(R.id.layout_btn_send);
		layout_btn_move = (LinearLayout) findViewById(R.id.layout_btn_move);

		mTextDelete = (TextView) findViewById(R.id.text_delete);
		mTextSend = (TextView) findViewById(R.id.text_send);
		mTextMove = (TextView) findViewById(R.id.text_move);*/
		// gn lilg 12-12-15 modify for common controls end

		// gn lilg 12-12-15 modify for common controls begin
		/*layout_btn_delete.setOnClickListener(this);
		layout_btn_send.setOnClickListener(this);
		layout_btn_move.setOnClickListener(this);*/
		// gn lilg 12-12-15 modify for common controls end
		// Gionee <lilg><2013-05-21> modify for super theme end
		
		Log.d("ALLEditActivity------isInFolder: " + isInFolder);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Log.i("ALLEditActivity------listView on item click, position: " + position + ", id: " + id);
				clickGridOrListView(position);
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Log.i("ALLEditActivity------gridView on item click, position: " + position + ", id: " + id);
				clickGridOrListView(position);
			}
		});

		Log.i("ALLEditActivity------initViews end!");
	}

	private void clickGridOrListView(int position) {
		Log.i("ALLEditActivity------clickGridOrListView begin!");
		Log.d("ALLEditActivity------isMoveFolder: " + isMoveFolder);
		Note folder = noteList.get(position);
		if (isMoveFolder) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			dialogShow();
			new ApiAsynTask(BATCH_MOVE_INFOLDER, adapter.getChoicemData(), folder).executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());

		} else {

			if (adapter.getChoicemData().contains(folder)) {
				adapter.getChoicemData().remove(folder);
			} else {
				adapter.getChoicemData().add(folder);
			}
			setAllViewState();
			adapter.updateViewState(isMoveFolder);

			// gn lilg 20120-12-14 modify for common controls begin
			// update the state of the all select button when the item clicked 
			List<Note> noteList = adapter.getChoicemData();
			notifyAdapter();
			if(allSelectItem != null){
				if (noteList.size() < adapter.getCount()) {
					allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);
				} else {
					allSelectItem.setIcon(R.drawable.gn_btn_check_on_light);
				}
			}
			// Gionee <lilg><2013-05-23> modify for CR00809680 begin
			if (noteList.size() < adapter.getCount()) {
            	mSelectedAllButton.setText(R.string.all_secect);

			} else {
            	mSelectedAllButton.setText(R.string.all_no_select);
			}
			// Gionee <lilg><2013-05-23> modify for CR00809680 end
			// gn lilg 20120-12-14 modify for common controls end
		}
		Log.i("ALLEditActivity------clickGridOrListView end!");
	}

	private void setShowViewMode() {
		Log.i("ALLEditActivity------setShowViewMode begin!");
		if (HomeActivity.isGridView) {
			gridView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			gridView.setAdapter(adapter);
		} else {
			gridView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			listView.setAdapter(adapter);
		}
		Log.d("ALLEditActivity------setShowViewMode end!");
	}

	private OnMenuItemClickListener mFilterOptionsMenuItemClickListener = new OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {

			switch (item.getItemId()) {
			case R.id.all_select_mode:
				//gn pengwei 20121126 add for statistics begin
				if(isInFolder){
					Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_FOLDER_OPERATIONS);
				}else{
					Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_OPERATIONS);
				}
				//gn pengwei 20121126 add for statistics begin
				//				if (item.getTitle() == getResources().getString(
				//						R.string.all_no_select)) {
				//GN pengwei 2012-11-23 modify  for find bugs start
				if (item.getTitle().equals(getResources().getString(
						R.string.all_no_select))) {
					//GN pengwei 2012-11-23 modify  for find bugs end
					adapter.updateView(false);
				} else {
					adapter.updateView(true);
				}
				setAllViewState();

				break;
			}
			return true;
		}
	};

	
	public void setAllViewState() {
		try {
			Log.i("ALLEditActivity------setAllViewState begin!");

			for (Note note : noteList) {
				if (Constants.IS_FOLDER.equals(note.getIsFolder())) {
					isHaveFolder = true;
					break;
				}
			}
			isSelectFolder = false;
			List choiceList = adapter.getChoicemData();
			if (isMoveFolder) {
				Log.i("ALLEditActivity------setAllViewState 1!");
				// Gionee <lilg><2013-05-21> modify for super theme begin
				// gn lilg 20120-12-14 modify for common controls begin
				/* bottom_view.setVisibility(View.GONE); */
				// gn lilg 20120-12-14 modify for common controls end

				if (mDelete != null && mShare != null && mMove != null) {
					// mDelete.setVisible(false);
					// mShare.setVisible(false);
					// mMove.setVisible(false);
					this.setOptionsMenuHideMode(true);
				}
				// Gionee <lilg><2013-05-21> modify for super theme end

				// gn lilg 20120-12-14 modify for common controls begin
				selectCount.setText(getResources().getString(
						R.string.move_to_folder));
				if (allSelectItem != null) {
					allSelectItem.setVisible(false);
				}
				// Gionee <lilg><2013-05-23> modify for CR00809680 begin
				mSelectedAllButton.setVisibility(View.INVISIBLE);
				// Gionee <lilg><2013-05-23> modify for CR00809680 end
				// gn lilg 20120-12-14 modify for common controls end
			} else {
				Log.i("ALLEditActivity------setAllViewState 2!");
				// Gionee <lilg><2013-05-21> modify for super theme begin
				// gn lilg 20120-12-14 modify for common controls begin
				/* bottom_view.setVisibility(View.VISIBLE); */
				// gn lilg 20120-12-14 modify for common controls end

				if (mDelete != null && mShare != null && mMove != null) {
					// mDelete.setVisible(true);
					// mShare.setVisible(true);
					// mMove.setVisible(true);
					this.setOptionsMenuHideMode(false);
				}
				// Gionee <lilg><2013-05-21> modify for super theme end

				// gn lilg 20120-12-14 modify for common controls begin
				selectCount
						.setText(getResources().getString(
								R.string.select_count,
								adapter.getChoicemData().size()));
				if (allSelectItem != null) {
					allSelectItem.setVisible(true);
				}
				// Gionee <lilg><2013-05-23> modify for CR00809680 begin
				mSelectedAllButton.setVisibility(View.VISIBLE);
				// Gionee <lilg><2013-05-23> modify for CR00809680 end
				// gn lilg 20120-12-14 modify for common controls end
			}

			for (int i = 0; i < choiceList.size(); i++) {
				Note note = (Note) choiceList.get(i);
				if (Constants.IS_FOLDER.equals(note.getIsFolder())) {
					isSelectFolder = true;
					break;
				}
			}

			if (mDelete != null && mShare != null && mMove != null) {
				if (choiceList.size() > 0) {
					Log.i("ALLEditActivity------setAllViewState 3!");

					// Gionee <lilg><2013-05-21> add for super theme begin
					setItemEnabled(DELETE_ENABLED | SHARE_ENABLED);
					// Gionee <lilg><2013-05-21> add for super theme end

					// Gionee <lilg><2013-05-21> modify for super theme begin
					/*
					 * layout_btn_delete.setClickable(true);
					 * btn_delete.setImageResource(R.drawable.gn_com_delete_bg);
					 * mTextDelete.setTextColor(getResources().getColor(R.color.
					 * all_edit_bottom_btn_text_color));
					 * 
					 * layout_btn_send.setClickable(true);
					 * btn_send.setImageResource(R.drawable.gn_com_share_bg);
					 * mTextSend.setTextColor(getResources().getColor(R.color.
					 * all_edit_bottom_btn_text_color));
					 */
					// Gionee <lilg><2013-05-21> modify for super theme end

					if (isInFolder) {
						// Gionee <lilg><2013-05-21> add for super theme begin
						setItemEnabled(MOVE_ENABLED);
						// Gionee <lilg><2013-05-21> add for super theme end

						// Gionee <lilg><2013-05-21> modify for super theme
						// begin
						/*
						 * layout_btn_move.setClickable(true);
						 * btn_move.setImageResource
						 * (R.drawable.gn_com_collection_bg);
						 * mTextMove.setTextColor
						 * (getResources().getColor(R.color
						 * .all_edit_bottom_btn_text_color));
						 */
						// Gionee <lilg><2013-05-21> modify for super theme end
					} else {
						if (isHaveFolder) {
							if (!isSelectFolder) {
								// Gionee <lilg><2013-05-21> add for super theme
								// begin
								setItemEnabled(MOVE_ENABLED);
								// Gionee <lilg><2013-05-21> add for super theme
								// end

								// Gionee <lilg><2013-05-21> modify for super
								// theme begin
								/*
								 * layout_btn_move.setClickable(true);
								 * btn_move.setImageResource
								 * (R.drawable.gn_com_collection_bg);
								 * mTextMove.setTextColor
								 * (getResources().getColor
								 * (R.color.all_edit_bottom_btn_text_color));
								 */
								// Gionee <lilg><2013-05-21> modify for super
								// theme end
							} else {
								Log.v("AllEditActivity---MOVE_DISABLE");
								// Gionee <lilg><2013-05-21> add for super theme
								// begin
								setItemDisabled(MOVE_DISABLE);
								// Gionee <lilg><2013-05-21> add for super theme
								// end

								// Gionee <lilg><2013-05-21> modify for super
								// theme begin
								/*
								 * layout_btn_move.setClickable(false);
								 * btn_move.
								 * setImageResource(R.drawable.gn_com_collection_dis
								 * );
								 * mTextMove.setTextColor(getResources().getColor
								 * (
								 * R.color.all_edit_bottom_btn_text_color_dis));
								 */
								// Gionee <lilg><2013-05-21> modify for super
								// theme end
							}
						} else {
							// Gionee <lilg><2013-05-21> add for super theme
							// begin
							setItemDisabled(MOVE_DISABLE);
							// Gionee <lilg><2013-05-21> add for super theme end

							// Gionee <lilg><2013-05-21> modify for super theme
							// begin
							/*
							 * layout_btn_move.setClickable(false);
							 * btn_move.setImageResource
							 * (R.drawable.gn_com_collection_dis);
							 * mTextMove.setTextColor
							 * (getResources().getColor(R.color
							 * .all_edit_bottom_btn_text_color_dis));
							 */
							// Gionee <lilg><2013-05-21> modify for super theme
							// end
						}
					}
				} else {
					Log.i("ALLEditActivity------setAllViewState 4!");

					// Gionee <lilg><2013-05-21> add for super theme begin
					setItemDisabled(DELETE_DISABLED & SHARE_DISABLED
							& MOVE_DISABLE);
					// Gionee <lilg><2013-05-21> add for super theme end

					// Gionee <lilg><2013-05-21> modify for super theme begin
					/*
					 * layout_btn_delete.setClickable(false);
					 * layout_btn_send.setClickable(false);
					 * layout_btn_move.setClickable(false);
					 * 
					 * btn_delete.setImageResource(R.drawable.gn_com_delete_dis);
					 * btn_send.setImageResource(R.drawable.gn_com_share_dis);
					 * btn_move
					 * .setImageResource(R.drawable.gn_com_collection_dis);
					 * 
					 * mTextDelete.setTextColor(getResources().getColor(R.color.
					 * all_edit_bottom_btn_text_color_dis));
					 * mTextSend.setTextColor(getResources().getColor(R.color.
					 * all_edit_bottom_btn_text_color_dis));
					 * mTextMove.setTextColor(getResources().getColor(R.color.
					 * all_edit_bottom_btn_text_color_dis));
					 */
					// Gionee <lilg><2013-05-21> modify for super theme end
				}
			}
			Log.d("ALLEditActivity------setAllViewState end!");
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("ALLEditActivity---setAllViewState---e == " + e);
		}
	}

	private void getDBAllData() {
		// gn lilg 2012-12-08 modify for optimization begin
		dbo = DBOperations.getInstances(ALLEditActivity.this);
		// gn lilg 2012-12-08 modify for optimization end
		noteList.clear();
		//Gionee <pengwei><2013-12-3> modify for CR00965838 begin
		mTempList.clear();
		//Gionee <pengwei><2013-12-3> modify for CR00965838 end
		notifyAdapter();
		new QueryDatasTask().executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());
//		if (isInFolder) {
//			UtilsQueryDatas.queryNotesIsInFolder(folderId, HomeActivity.mNoteDatas, noteList);
//
//		} else {
//			UtilsQueryDatas.queryNotesIsInFolder(folderId,HomeActivity.mNoteDatas, noteList);
//		}
//		if (noteList.size() < 1) {
//			Log.e("ALLEditActivity------noteList.size < 1!");
//			HomeActivity.setInFolder(false);
//			finish();
//		}
//		if (adapter.getChoicemData().size() > 0) {
//			Log.d("ALLEditActivity------adapter.getChoicemData(): " + adapter.getChoicemData().size());
//
//			int choiceCount = adapter.getChoicemData().size();
//			Log.d("ALLEditActivity------choiceCount: " + choiceCount);
//
//			ArrayList<Note> haveRemoveData = new ArrayList<Note>();
//			for (int i = 0; i < choiceCount; i++) {
//				if (!noteList.contains(((Note) adapter.getChoicemData().get(i)))) {
//					Note note = (Note) adapter.getChoicemData().get(i);
//					haveRemoveData.add(note);
//				}
//			}
//
//			for (Note note : haveRemoveData) {
//				adapter.getChoicemData().remove(note);
//			}
//			Log.d("ALLEditActivity------adapter.getChoicemData(): " + adapter.getChoicemData().size());
//			if (adapter.getChoicemData().size() < 1) {
//				if (isMoveFolder) {
//					isMoveFolder = !isMoveFolder;
//				}
//			}
//			adapter.updateViewState(isMoveFolder);
//
//		} else {
//			Log.d("ALLEditActivity------adapter.getChoicemData(): " + adapter.getChoicemData().size());
//			adapter.updateData(noteList);
//		}
	}

	private void setItemEnabled(int itemEnabled){
		mItemIsEnabled = mItemIsEnabled | itemEnabled;
		onCreateOptionsMenu(mMenu);
	}
	
	private void setItemDisabled(int itemDisabled){
		mItemIsEnabled = mItemIsEnabled & itemDisabled;
		onCreateOptionsMenu(mMenu);
	}
	
	@Override
	public void onClick(View v) {
		Log.i("ALLEditActivity------onClick!");

		// Gionee <lilg><2013-05-21> modify for super theme begin
		
		/*// gn lilg 12-12-15 modify for common controls begin
		//		if (R.id.btn_delete == v.getId()) {
		if (R.id.layout_btn_delete == v.getId()) {
			
			// Gionee <lilg><2013-04-16> add for CR00795403 begin
			Log.d("ALLEditActivity------mIsBatchDeleteCurrent: " + mIsBatchDeleteCurrent + ", mIsBatchMoveCurrent: " + mIsBatchMoveCurrent);
			if(mIsBatchDeleteCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_delete), Toast.LENGTH_SHORT).show();
				return;
			}else if(mIsBatchMoveCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_move), Toast.LENGTH_SHORT).show();
				return;
			}
			// Gionee <lilg><2013-04-16> add for CR00795403 end
			
			// gn lilg 12-12-15 modify for common controls end
			showDialog(Constants.DIALOG_DELETE_NOTEPAD_LIST);
			// gn lilg 12-12-15 modify for common controls begin
			//		} else if (R.id.btn_move == v.getId()) {
		} else if (R.id.layout_btn_move == v.getId()) {
			// gn lilg 12-12-15 modify for common controls end
			Log.i("ALLEditActivity------click move button!");
			
			// Gionee <lilg><2013-04-16> add for CR00795403 begin
			Log.d("ALLEditActivity------mIsBatchDeleteCurrent: " + mIsBatchDeleteCurrent + ", mIsBatchMoveCurrent: " + mIsBatchMoveCurrent);
			if(mIsBatchDeleteCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_delete), Toast.LENGTH_SHORT).show();
				return;
			}else if(mIsBatchMoveCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_move), Toast.LENGTH_SHORT).show();
				return;
			}
			// Gionee <lilg><2013-04-16> add for CR00795403 end
			
			if (isInFolder) {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				dialogShow();
				new ApiAsynTask(BATCH_MOVE_FROM_FOLDER,
						adapter.getChoicemData(), null).execute();

			} else {
				Log.d("ALLEditActivity------isInFolder: " + isInFolder);
				isMoveFolder = true;
				adapter.updateViewState(true);
				setAllViewState();

			}
			// gn lilg 12-12-15 modify for common controls begin
			//		} else if (R.id.btn_send == v.getId()) {
		} else if (R.id.layout_btn_send == v.getId()) {	
			// gn lilg 12-12-15 modify for common controls end
			//gn pengwei 20121126 add for statistics begin
			StatisticalValue statics = StatisticalValue.getInstance();
			if(isInFolder){
				int num = statics.getMainAppFolderOperationsShare();
				statics.setMainAppFolderOperationsShare(++num);
				Statistics.addNum(statics.getKeyMainAppFolderOperationsShare(), 
						num);
			}else{
				int num = statics.getMainAppOperationShare();
				statics.setMainAppOperationShare(++num);
				Statistics.addNum(statics.getKeyMainAppOperationShare(), 
						num);
			}
			//gn pengwei 20121126 add for statistics end
			// gn lilg 2012-07-09 add for share note start.
			List<Note> list = adapter.getChoicemData();
			
			// Gionee <lilg><2013-04-18> modify for CR00795054 begin
			int noteCount = CommonUtils.getNoteCount(list);
			if(noteCount <= Constants.SHARE_NOTE_MAX_COUNT){
				// permit share note
				shareNote(list);
			}else{
				// do not permit share note
				CommonUtils.showToast(ALLEditActivity.this, getString(R.string.share_note_count_toast));
			}
			// Gionee <lilg><2013-04-18> modify for CR00795054 end
			
			// if(isInFolder){
			// Intent intent = new Intent();
			// // intent.putExtra(DBOpenHelper.NOTE_TITLE, note.getTitle());
			// intent.putExtra(Constants.IS_IN_FOLDER, false);
			// intent.setAction(Constants.START_FOLDER_ACTIVITY_ACTION);
			//
			// sendBroadcast(intent);
			// }else{
			Log.i("ALLEditActivity------isShare!");

			// HomeActivity.setInFolder(false);
			finish();
			// }

			// gn lilg 2012-07-09 add for share note end.
		}*/
		// Gionee <lilg><2013-05-21> modify for super theme end
		
	}

	/**
	 * share note gn lilg 2012-07-09
	 * 
	 * @param list
	 */
	private void shareNote(List<Note> list) {
		if (list == null || list.size() <= 0) {
			Log.i("ALLEditActivity------id list is null or the size <= 0!");
			return;
		}

		List<Note> noteListToShare = new ArrayList<Note>();
		List<Note> tempList = null;
		Note note = null;
		// int pos = -1;
		int noteId = -1;
		for (int i = 0; i < list.size(); i++) {
			// try {
			// pos = Integer.parseInt(list.get(i).toString());
			// } catch (Exception e) {
			// Log.e(LOG_TAG, e.getMessage());
			// continue;
			// }
			note = (Note) list.get(i);

			if (note == null) {
				Log.i("ALLEditActivity------note == null!");
				continue;
			}
			if (CommonUtils.STR_YES.equals(note.getIsFolder())) {
				try {
					noteId = Integer.parseInt(note.getId());
				} catch (Exception e) {
					Log.e("ALLEditActivity------e.getMessage(): "+e.getMessage());
					continue;
				}
				if (noteId == -1) {
					continue;
				}
				tempList = new ArrayList<Note>();
//				tempList = dbo.queryFromFolder(ALLEditActivity.this, noteId);
				UtilsQueryDatas.queryNotesIsInFolder(noteId,HomeActivity.mTempNoteList, tempList);
				if (tempList.size() < 1) {
					// gn lilg 2013-03-02 modify for CR00774631 begin 
//					CommonUtils.showToast(ALLEditActivity.this,getString(R.string.share_no_note_infolder_toast) );
					// gn lilg 2013-03-02 modify for CR00774631 end
					// Toast.makeText(ALLEditActivity.this,
					// getString(R.string.share_no_note_infolder_toast),
					// Toast.LENGTH_SHORT).show();

				}
				for (Note n : tempList) {
					noteListToShare.add(n);
				}
			} else if (CommonUtils.STR_NO.equals(note.getIsFolder())) {
				if("".equals(note.getContent())){
					// gn lilg 2013-03-02 modify for CR00774631 begin 
//					CommonUtils.showToast(ALLEditActivity.this,getString(R.string.share_no_note_infolder_toast) );
					// gn lilg 2013-03-02 modify for CR00774631 end
				}else{
					noteListToShare.add(note);
				}
			} else {
				Log.d("ALLEditActivity------note.getIsFolder: " + note.getIsFolder());
			}
		}
		// gn lilg 2013-03-02 modify for CR00774631 begin 
//		CommonUtils.shareNote(this, CommonUtils.DEFAULT_MIMETYPE, noteListToShare);
		int result = CommonUtils.shareNote(this, CommonUtils.DEFAULT_MIMETYPE, noteListToShare);
		if(result == -1){
			Toast.makeText(this, getResources().getString(R.string.share_note_empty_toast), Toast.LENGTH_SHORT).show();
		}
		// gn lilg 2013-03-02 modify for CR00774631 end
		
		// HomeActivity.setInFolder(false);
		// finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (isMoveFolder) {
					isMoveFolder = false;
					adapter.updateViewState(isMoveFolder);
					setAllViewState();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		// gn lilg 2012-12-28 modify for common controls begin
		//		Builder builder = new AmigoAlertDialog.Builder(ALLEditActivity.this);
		// Gionee <wangpan><2014-04-16> modify for CR01032136 begin
        //Builder builder = new AmigoAlertDialog.Builder(ALLEditActivity.this, CommonUtils.getFullScreenTheme());
		Builder builder = new AmigoAlertDialog.Builder(ALLEditActivity.this, CommonUtils.getTheme());
        //Gionee <wangpan><2014-04-16> modify for CR01032136 end
		// gn lilg 2012-12-28 modify for common controls end
		switch (id) {
		case Constants.DIALOG_DELETE_NOTEPAD_LIST:
			builder.setTitle(R.string.delete_note_dialog_title);
			builder.setMessage(R.string.delete_note_dialog_body);

			// gionee lilg 2013-01-16 modify for new demands begin
			//			builder.setIcon(android.R.drawable.ic_dialog_alert);
			// gionee lilg 2013-01-16 modify for new demands end

			builder.setPositiveButton(R.string.delete_note_dialog_sure,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//gn pengwei 20121126 add for statistics begin
					if(isInFolder){
						Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_FOLDER_OPERATIONS_DEL);
					}else{
						Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_OPERATION_DEL);
					}
					//gn pengwei 20121126 add for statistics end
					// jiating
					if (mProgressDialog != null) {
						mProgressDialog.dismiss();
					}
					dialogShow();

					new ApiAsynTask(BATCH_DELETE, adapter
							.getChoicemData(), null).executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());

				}
			});
			builder.setNegativeButton(R.string.delete_note_dialog_cancle,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					setAllViewState();
					hasTask=false;
				}
			});
			builder.setCancelable(true);
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// setButtonState(isHaveFolder, isSelectFolder);
					hasTask=false;
					setAllViewState();

				}
			});
			dialog = builder.create();
			return dialog;
		default:
			return null;
		}

	}

	private void dialogShow() {
		Log.i("ALLEditActivity------dialogShow!");

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		mProgressDialog = new AmigoProgressDialog(ALLEditActivity.this,CommonUtils.getTheme());
		mProgressDialog.setMessage(getResources().getString(R.string.all_delete_dialog_message));
		mProgressDialog.setProgressStyle(AmigoProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.show();
	}

	class ApiAsynTask extends AsyncTask<Void, Void, Integer> {

		private int clickButton;
		private Note folder;
		private ArrayList<Note> choiceList;

		public ApiAsynTask(int clickButton, ArrayList<Note> list, Note folder) {
			super();
			Log.d("ALLEditActivity------ApiAsynTask List: " + list.size());

			this.clickButton = clickButton;
			// this.choiceList=choiceList;
			
			// Gionee <lilg><2013-03-19> add for CR00785662 begin
			/*choiceList = new ArrayList<Note>();
			try {
				this.choiceList = deepcopy(list);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} */
			
//			this.choiceList = deepCopy(list);
			choiceList = list;
			// Gionee <lilg><2013-03-19> add for CR00785662 end

			this.folder = folder;
		}

		protected Integer doInBackground(Void... params) {
			//gn pengwei 20121126 add for statistics begin
			if(isInFolder){
				Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_FOLDER_OPERATIONS_MOVE);
			}else{
				Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_OPERATION_MOVE);
			}
			//gn pengwei 20121126 add for statistics end
			hasTask=true;
			if (BATCH_DELETE == clickButton) {
				Log.i("ALLEditActivity------doInBackground shanchu!");

				// Gionee <lilg><2013-04-16> add for CR00795403 begin
				mIsBatchDeleteCurrent = true;
				// Gionee <lilg><2013-04-16> add for CR00795403 end
				
				ArrayList<Note> list = new ArrayList<Note>();
				list = adapter.getChoicemData();

				String widgetId = "";
				String widgetType = "";
				for (int i = 0; i < choiceList.size(); i++) {
					Log.i("ALLEditActivity------shanchu: " + i + ", list.size(): " + MAX_PROGRESS + ", choiceList.size(): " + choiceList.size());

					Log.i("ALLEditActivity------shanchu, adapter.getChoicemData(): "	+ adapter.getChoicemData().size());

					Note note = (Note) choiceList.get(i);
					if (list.contains(note)) {
						int id = Integer.parseInt(note.getId());

						if (Constants.IS_FOLDER.equals(note.getIsFolder())) {

							List<Note> notes = dbo.queryFromFolder(ALLEditActivity.this, Integer.parseInt(note.getId()));
							Map<Integer, Set<Integer>> widgetIdMap = new HashMap<Integer, Set<Integer>>();

							for (Note tempNote : notes) {
								Integer widgetIdTemp = Integer.parseInt(tempNote.getWidgetId());
								Integer widgetTypeTemp = Integer.parseInt(tempNote.getWidgetType());
								Set<Integer> widgetIdSet = widgetIdMap.get(widgetTypeTemp);
								if(widgetIdSet == null){
									widgetIdSet = new HashSet<Integer>();
									widgetIdMap.put(widgetTypeTemp, widgetIdSet);
								}
								widgetIdSet.add(widgetIdTemp);
								
								// gn lilg 2013-03-04 add for delete media files in the sdcard begin
								List<MediaInfo> mediaInfoList = dbo.queryMeidas(ALLEditActivity.this, tempNote.getId());
								if(mediaInfoList != null && mediaInfoList.size() > 0){
									for(MediaInfo mediaInfo : mediaInfoList){
										if(mediaInfo != null && mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media))){
											Log.d("ALLEditActivity------path contains 备份/便签多媒体: " + mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media)));
//											FileUtils.deleteFile(mediaInfo.getMediaFileName());
											String deleteFile = FileUtils.getPathByPathType(mediaInfo.getMediaFileName().substring(0, 1)) + FileUtils.getSubPathAndFileName(mediaInfo.getMediaFileName());
											Log.d("NoteActivity------delete file: " + deleteFile);
											FileUtils.deleteFile(deleteFile);
										}
									}
								}
								// gn lilg 2013-03-04 add for delete media files in the sdcard end
							}
							
							dbo.deleteNote(ALLEditActivity.this, note);
							notes.add(note);
							UtilsQueryDatas.deleteNotes(notes,HomeActivity.mTempNoteList);
							WidgetUtils.updateWidget(ALLEditActivity.this, widgetIdMap);
						} else {
							// if (isInFolder && isHaveCount) {
							// Note folderNote = dbo.queryOneNote(
							// ALLEditActivity.this,
							// Integer.parseInt(note.getParentFile()));
							// folderNote.setHaveNoteCount(folderNote
							// .getHaveNoteCount() - choiceList.size());
							// dbo.update(ALLEditActivity.this, folderNote);
							// isHaveCount = false;
							// }

							widgetId = note.getWidgetId();
							widgetType = note.getWidgetType();

							// gn lilg 2013-03-04 add for delete media files in the sdcard begin
							List<MediaInfo> mediaInfoList = dbo.queryMeidas(ALLEditActivity.this, note.getId());
							if(mediaInfoList != null && mediaInfoList.size() > 0){
								for(MediaInfo mediaInfo : mediaInfoList){
									if(mediaInfo != null && mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media))){
										Log.d("ALLEditActivity------path contains 备份/便签多媒体: " + mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media)));
//										FileUtils.deleteFile(mediaInfo.getMediaFileName());
										String deleteFile = FileUtils.getPathByPathType(mediaInfo.getMediaFileName().substring(0, 1)) + FileUtils.getSubPathAndFileName(mediaInfo.getMediaFileName());
										Log.d("NoteActivity------delete file: " + deleteFile);
										FileUtils.deleteFile(deleteFile);
									}
								}
							}
							// gn lilg 2013-03-04 add for delete media files in the sdcard end
							
							dbo.deleteNote(ALLEditActivity.this, note);
							// gn lilg 2012-07-06 add start for update
							// widget
							UtilsQueryDatas.deleteNote(note,HomeActivity.mTempNoteList);
							updateWidget(widgetId, widgetType);
							// gn lilg 2012-07-06 add end for update

						}
						publishProgress();
					}
				}
				//				if (isInFolder) {
				//					Note folderNote = dbo.queryOneNote(ALLEditActivity.this,
				//							folderId);
				//					folderNote.setHaveNoteCount(dbo.queryNoteByParentId(
				//							ALLEditActivity.this, folderId).size());
				//					dbo.update(ALLEditActivity.this, folderNote);
				//
				//				}

			} else if (BATCH_MOVE_INFOLDER == clickButton) {
				Log.i("ALLEditActivity------move in");

				// Gionee <lilg><2013-04-16> add for CR00795403 begin
				mIsBatchMoveCurrent = true;
				// Gionee <lilg><2013-04-16> add for CR00795403 end
				
				// 移入文件夹

				if (Constants.IS_FOLDER.equals(folder.getIsFolder())) {
					ArrayList<Note> list = adapter.getChoicemData();

					for (int i = 0; i < choiceList.size(); i++) {

						// int
						// id=Integer.parseInt(list.get(i).toString());
						Note note = (Note) choiceList.get(i);
						if (list.contains(note)) {
							dbo.moveOneNote(ALLEditActivity.this, note, folder);
							UtilsQueryDatas.moveNote(note,Integer.valueOf(folder.getId()),HomeActivity.mTempNoteList);
							//							note.setParentFile(folder.getId());
							//							dbo.updateNote(ALLEditActivity.this, note);
							publishProgress();
						}
					}

					//					folder.setHaveNoteCount(dbo.queryNoteByParentId(
					//							ALLEditActivity.this,
					//							Integer.parseInt(folder.getId())).size());
					//					dbo.updateNote(ALLEditActivity.this, folder);
				}
			} else if (BATCH_MOVE_FROM_FOLDER == clickButton) {
				// 移出文件夹
				Log.i("ALLEditActivity------move out");

				// Gionee <lilg><2013-04-16> add for CR00795403 begin
				mIsBatchMoveCurrent = true;
				// Gionee <lilg><2013-04-16> add for CR00795403 end
				
				ArrayList<Note> list = adapter.getChoicemData();

				for (int i = 0; i < choiceList.size(); i++) {
					// int
					// id=Integer.parseInt(list.get(i).toString());
					Note note = (Note) choiceList.get(i);
					if (list.contains(note)) {
						dbo.moveOneNote(ALLEditActivity.this, note, null);
						UtilsQueryDatas.moveNote(note,UtilsQueryDatas.folderIDInt,HomeActivity.mTempNoteList);
						//						note.setParentFile("no");
						//						dbo.updateNote(ALLEditActivity.this, note);
						publishProgress();
					}
				}
				//				Note folderNote = dbo.queryOneNote(ALLEditActivity.this,
				//						folderId);
				//				folderNote.setHaveNoteCount(dbo.queryNoteByParentId(
				//						ALLEditActivity.this, folderId).size());
				//				dbo.updateNote(ALLEditActivity.this, folderNote);
			}

			return 1;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			Log.i("ALLEditActivity------onProgressUpdate!");

			mProgressDialog.setMessage(getResources().getString(
					R.string.all_delete_dialog_message));
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i("ALLEditActivity------onPostExecute!");

			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			isMoveFolder = false;
			this.choiceList.clear();
			this.choiceList = null;

			// TODO what is mean: if(Log.LOGV)
//			if(Log.LOGV){
				Log.i("ALLEditActivity------adapter.getChoicemData().size()"
						+ adapter.getChoicemData().size());
				hasTask=false;
				// getDBAllData();
				// if (noteList.size() > 0) {
				// // updateDisplay();
				// adapter.updateData(noteList);
				// setAllViewState();
				// isSelectFolder = false;
				// // setButtonState(isHaveFolder, isSelectFolder);
				// } else {
				// HomeActivity.setInFolder(false);
				finish();

				// }
//			}

			// Gionee <lilg><2013-04-16> add for CR00795403 begin
			if(adapter != null){
				adapter.notifyDataSetChanged();
			}
			mIsBatchDeleteCurrent = false;
			mIsBatchMoveCurrent = false;
			// Gionee <lilg><2013-04-16> add for CR00795403 end
		}
	}

	
	
	// Gionee <lilg><2013-03-19> add for CR00785662 begin
	private ArrayList<Note> deepCopy(ArrayList<Note> src) {
		ArrayList<Note> resultList = new ArrayList<Note>();
		for(Note note : src){
			try {
				resultList.add((Note)note.clone());
			} catch (CloneNotSupportedException e) {
				Log.e("ALLEditActivity------clone note exception!", e);
			}
		}
		return resultList;
	}
	// Gionee <lilg><2013-03-19> add for CR00785662 end

	@Override
	protected void onDestroy() {
		Log.i("ALLEditActivity------onDestroy() start!");

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		super.onDestroy();
		if(allEditActivitymGusestModeObserver!=null){
			getContentResolver().unregisterContentObserver(allEditActivitymGusestModeObserver);
			allEditActivitymGusestModeObserver=null;
		}

	}

	/**
	 * update widget gn lilg 2012-07-11
	 * 
	 * @param note
	 */
	private void updateWidget(String wId, String wType) {

		if ("".equals(wId) || "".equals(wType)) {
			Log.i("ALLEditActivity------widgetId is \"\" or widgetType is \"\"!");

			return;
		}

		int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		int widgetType = Notes.TYPE_WIDGET_INVALIDE;

		try {
			widgetId = Integer.parseInt(wId);
		} catch (Exception e) {
			Log.e("ALLEditActivity------e.getMessage(): "+e.getMessage());
		}

		try {
			widgetType = Integer.parseInt(wType);
		} catch (Exception e) {
			Log.e("ALLEditActivity------e.getMessage(): "+e.getMessage());
		}

		if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID
				|| widgetType == Notes.TYPE_WIDGET_INVALIDE) {
			Log.i("ALLEditActivity------widgetId is: " + widgetId + ", widgetType is: " + widgetType);
			return;
		}

		WidgetUtils.updateWidget(ALLEditActivity.this, widgetId, widgetType);
	}

	private int mItemIsEnabled = 000; 
	private Menu mMenu = null;
	public static final int DELETE_ENABLED = 001;
	public static final int DELETE_DISABLED = 110;
	public static final int SHARE_ENABLED = 010;
	public static final int SHARE_DISABLED = 101;
	public static final int MOVE_ENABLED = 100;
	public static final int MOVE_DISABLE = 011;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("ALLEditActivity------onCreateOptionsMenu!");
		if(mMenu == null){
			mMenu = menu;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.all_edit_actionbar_menu, menu);

		// Gionee <lilg><2013-05-21> modify for super theme begin
//		allSelectItem = menu.findItem(R.id.action_all_select);
//		allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);
		
			mDelete = menu.findItem(R.id.action_delete);
			mShare = menu.findItem(R.id.action_share);
			mMove = menu.findItem(R.id.action_move);
		}
		mDelete.setEnabled(((mItemIsEnabled & 001) == 001));
		mShare.setEnabled(((mItemIsEnabled & 010) == 010));
		mMove.setEnabled(((mItemIsEnabled & 100) == 100));
		setItemIcon();
		// Gionee <lilg><2013-05-21> modify for super theme end
		
		return super.onCreateOptionsMenu(menu);
	}

	private void setItemIcon(){
		if(mDelete.isEnabled()){
			mDelete.setIcon(R.drawable.gn_com_delete_bg);
		}else{
			mDelete.setIcon(R.drawable.gn_com_delete_dis);
		}
		
		if(mShare.isEnabled()){
			mShare.setIcon(R.drawable.gn_com_share_bg);
		}else{
			mShare.setIcon(R.drawable.gn_com_share_dis);
		}
		
		if(mMove.isEnabled()){
			mMove.setIcon(R.drawable.gn_com_collection_bg);
		}else{
			mMove.setIcon(R.drawable.gn_com_collection_dis);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("ALLEditActivity------onOptionsItemSelected!");

		switch (item.getItemId()) {
		case android.R.id.home:
			Log.d("ALLEditActivity------click back button!");
			finish();
			break;
		case R.id.action_all_select:
			Log.d("ALLEditActivity------click all select button!");
			actionAllSelect();
			break;
		case R.id.action_delete:

			Log.d("ALLEditActivity------mIsBatchDeleteCurrent: " + mIsBatchDeleteCurrent + ", mIsBatchMoveCurrent: " + mIsBatchMoveCurrent);
			if(mIsBatchDeleteCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_delete), Toast.LENGTH_SHORT).show();
			}else if(mIsBatchMoveCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_move), Toast.LENGTH_SHORT).show();
			}
			
			showDialog(Constants.DIALOG_DELETE_NOTEPAD_LIST);
			
			break;
		case R.id.action_share:
			
			if(isInFolder){
				Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_FOLDER_OPERATIONS_SHARE);
			}else{
				Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_OPERATION_SHARE);
			}
			List<Note> list = adapter.getChoicemData();
			
			int noteCount = CommonUtils.getNoteCount(list);
			if(noteCount <= Constants.SHARE_NOTE_MAX_COUNT){
				// permit share note
				shareNote(list);
			}else{
				// do not permit share note
				CommonUtils.showToast(ALLEditActivity.this, getString(R.string.share_note_count_toast));
			}
			
			Log.d("ALLEditActivity------isShare!");

			finish();
			
			break;
		case R.id.action_move:
			
			Log.i("ALLEditActivity------click move button!");
			
			Log.d("ALLEditActivity------mIsBatchDeleteCurrent: " + mIsBatchDeleteCurrent + ", mIsBatchMoveCurrent: " + mIsBatchMoveCurrent);
			if(mIsBatchDeleteCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_delete), Toast.LENGTH_SHORT).show();
			}else if(mIsBatchMoveCurrent){
				Toast.makeText(ALLEditActivity.this, getResources().getString(R.string.message_batch_move), Toast.LENGTH_SHORT).show();
			}
			
			if (isInFolder) {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				dialogShow();
				new ApiAsynTask(BATCH_MOVE_FROM_FOLDER,
						adapter.getChoicemData(), null).executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());

			} else {
				Log.d("ALLEditActivity------isInFolder: " + isInFolder);
				isMoveFolder = true;
				adapter.updateViewState(true);
				setAllViewState();

			}
			
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void actionAllSelect(){
		Log.i("ALLEditActivity------actionAllSelect begin!");
		//gn pengwei 20121126 add for statistics begin
		if(isInFolder){
			Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_FOLDER_OPERATIONS);

		}else{
			Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_OPERATIONS);
		}
		//gn pengwei 20121126 add for statistics end

		List<Note> noteList = adapter.getChoicemData();
		notifyAdapter();
		Log.d("ALLEditActivity------all size: " + adapter.getCount());
		Log.d("ALLEditActivity------select size: " + noteList.size());

		if (noteList.size() < adapter.getCount()) {
			adapter.updateView(true);
			// Gionee <lilg><2013-05-23> modify for CR00809680 begin
//			allSelectItem.setIcon(R.drawable.gn_btn_check_on_light);
//			mSelectedAllButton.setChecked(true);
			// Gionee <lilg><2013-05-23> modify for CR00809680 end
		} else {
			adapter.updateView(false);
			// Gionee <lilg><2013-05-23> modify for CR00809680 begin
//			allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);
//			mSelectedAllButton.setChecked(false);
			// Gionee <lilg><2013-05-23> modify for CR00809680 end
		}
		setAllViewState();

		Log.i("ALLEditActivity------actionAllSelect end!");
	}
	
	// Gionee <lilg><2013-05-23> modify for CR00809680 begin
	private void actionAllSelect2(boolean checked){
		Log.i("ALLEditActivity------actionAllSelect begin!");
		//gn pengwei 20121126 add for statistics begin
		if(isInFolder){
			Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_FOLDER_OPERATIONS);
		}else{
			Statistics.onEvent(ALLEditActivity.this, Statistics.MAIN_APP_OPERATIONS);
		}
		//gn pengwei 20121126 add for statistics end

		if(checked){
			adapter.updateView(true);
		}else{
			adapter.updateView(false);
		}
		
		setAllViewState();

		Log.i("ALLEditActivity------actionAllSelect end!");
	}
	// Gionee <lilg><2013-05-23> modify for CR00809680 end


	//gionee 20121220 jiating modify for CR00747076 config begin
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.i("AllEditActivity....onConfigurationChanged");
	}

	//gionee 20121220 jiating modify for CR00747076 config end

	class QueryDatasTask extends AsyncTask<Void, Void, Integer>{
		
		@Override
		protected Integer doInBackground(Void... params) {
		    UtilsQueryDatas.queryNotesIsInFolder(folderId, HomeActivity.mTempNoteList, mTempList);
		    return null;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			noteList.clear();
			notifyAdapter();
			noteList.addAll(mTempList);
			if (noteList.size() < 1) {
				Log.e("ALLEditActivity------noteList.size < 1!");
				HomeActivity.setInFolder(false);
				finish();
			}
			if (adapter.getChoicemData().size() > 0) {
				Log.d("ALLEditActivity------adapter.getChoicemData(): "
						+ adapter.getChoicemData().size());

				int choiceCount = adapter.getChoicemData().size();
				Log.d("ALLEditActivity------choiceCount: " + choiceCount);

				ArrayList<Note> haveRemoveData = new ArrayList<Note>();
				for (int i = 0; i < choiceCount; i++) {
					if (!noteList.contains(((Note) adapter.getChoicemData()
							.get(i)))) {
						Note note = (Note) adapter.getChoicemData().get(i);
						haveRemoveData.add(note);
						notifyAdapter();
					}
				}

				for (Note note : haveRemoveData) {
					adapter.getChoicemData().remove(note);
					notifyAdapter();
				}
				Log.d("ALLEditActivity------adapter.getChoicemData(): "
						+ adapter.getChoicemData().size());
				if (adapter.getChoicemData().size() < 1) {
					if (isMoveFolder) {
						isMoveFolder = !isMoveFolder;
					}
				}
				adapter.updateViewState(isMoveFolder);

			} else {
				Log.d("ALLEditActivity------adapter.getChoicemData(): "
						+ adapter.getChoicemData().size());
				adapter.updateData(noteList);
			}
		}
		
	}
	
}
//Gionee <pengwei><2013-11-2> modify for CR00935356 end
