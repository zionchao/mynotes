package com.gionee.note;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.app.AmigoProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import amigo.widget.AmigoButton;
import amigo.widget.AmigoExpandableListView;
import amigo.widget.AmigoExpandableListView.OnChildClickListener;
import amigo.widget.AmigoExpandableListView.OnGroupClickListener;
import amigo.widget.AmigoExpandableListView.OnGroupCollapseListener;
import amigo.widget.AmigoExpandableListView.OnGroupExpandListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import amigo.widget.AmigoListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.note.adapter.ExportItemsAdapter;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.StatisticalName;
import com.gionee.note.content.StatisticalValue;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.ExportItem;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.DateUtils;
import com.gionee.note.utils.ImportExportUtils;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.Statistics;
import com.gionee.note.utils.UtilsQueryDatas;
import amigo.widget.AmigoButton;


public class ExportItemSelectActivity extends AmigoActivity implements
OnClickListener, OnGroupClickListener, OnChildClickListener,
OnGroupExpandListener, OnGroupCollapseListener {

	// gn lilg 2012-12-18 add for common controls begin
	//	private Button btnCancel;
	//	private Button btnExport;
	//	private Button btnAllSelect;

	private LinearLayout layout_btn_cancel;
	private LinearLayout layout_btn_export;
	private LinearLayout layout_btn_allSelect;

	private ImageView btnCancel;
	private ImageView btnExport;
	private ImageView btnAllSelect;

	private TextView textExport;
	private TextView textAllSelect;

	private AmigoButton button_export;
	// gn lilg 2012-12-18 add for common controls end
	
	// gn lilg 2012-12-08 modify for optimization begin
	private DBOperations dbo = DBOperations.getInstances(ExportItemSelectActivity.this);
	// gn lilg 2012-12-08 modify for optimization end

	// gn lilg 2012-12-25 annotate begin
	//	private AmigoExpandableListView elvItems;
	//	private ExpandableListAdapter expandableListAdapter;
	private AmigoListView lvItems;
	private BaseAdapter listAdapter;
	private List<ExportItem> dataList;
	// gn lilg 2012-12-25 annotate end

	private AmigoProgressDialog mProgressDialog;
	private SharedPreferences sharedPreferences;
	//	private Handler handler;

	private Integer typeSelect;
	private static int MAX_PROGRESS;
	private String message = "";
	private int checkedNum = 0;
	private int allItemNum = 0;

	//	private List<Group> groupList;

	// gn lilg 20120919 add for CR00693893 start
	private AmigoProgressDialog mProDialog;
	private AmigoAlertDialog comfirmDialog;
	// gn lilg 20120919 add for CR00693893 end

	// gn lilg 2012-12-18 add for common controls begin
	private AmigoActionBar actionBar;
	private MenuItem allSelectItem;
	private TextView mDataExistInfo;
	private View mCustomView;
    private AmigoButton mSelectedAllButton;
	private boolean checked = false;

	// gn lilg 2012-12-18 add for common controls end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("ExportItemSelectActivity------onCreate() start!");

		CommonUtils.setTheme(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.export_item_select_layout_white);
		Statistics.setReportCaughtExceptions(true);
		initData();

		initResources();

		Log.d("ExportItemSelectActivity------onCreate() end!");
	}

	private void initData(){
		Log.i("ExportItemSelectActivity------init data start!");

		// get export type
		Intent intent = getIntent();
		typeSelect = intent.getExtras().getInt(ImportExportActivity.EXPORT_TYPE);
		Log.d("ExportItemSelectActivity------type select: " + typeSelect);

		initDataList();
		//		initGroupList();

		Log.d("ExportItemSelectActivity------init data end!");

	}

	/**
	 * 初始化列表的数据
	 */
	private void initDataList(){
		Log.i("ExportItemSelectActivity------init data list start!");
		dataList = new ArrayList<ExportItem>();

		// init folder list
//		List<Note> folderList = dbo.queryAllFolders(this);
//		if(folderList.size() == 0){
//			Log.i("ExportItemSelectActivity------folder list size is 0!");
//		}
//		for (Note folder : folderList) {
//			if(folder.getHaveNoteCount() > 0){
//				ExportItem exportItem = new ExportItem(true, folder.getTitle(), folder.getHaveNoteCount(), CommonUtils.getNoteData(ExportItemSelectActivity.this, folder.getUpdateDate(), folder.getUpdateTime()), false, folder.getId());
//				dataList.add(exportItem);
//			}
//		}
//
//		// init note list
//		List<Note> noteList = dbo.queryAllRootNotes(this);
//		if(noteList.size() == 0){
//			Log.i("ExportItemSelectActivity------root note list size is 0!");
//		}
//		for(Note note : noteList){
//			ExportItem exportItem = new ExportItem(false, (note.getTitle() == null || "".equals(note.getTitle()) ? note.getContent() : note.getTitle()), CommonUtils.getNoteData(ExportItemSelectActivity.this, note.getUpdateDate(), note.getUpdateTime()), note.getBgColor(), false, note.getId());
//			dataList.add(exportItem);
//		}
		UtilsQueryDatas.queryExportItemIsInFolder(this,UtilsQueryDatas.folderIDInt,HomeActivity.mTempNoteList, dataList);
		if(dataList.size() == 0){
			Log.i("ExportItemSelectActivity------dataList size is 0!");
		}
		Log.d("ExportItemSelectActivity------init data list end!");
	}

	/**
	 * 初始化可展开列表的数据
	 */
	/*private void initGroupList(){
		Log.i("ExportItemSelectActivity------init group list start!");

		groupList = new ArrayList<Group>();

		List<Note> folderList = dbo.queryAllFolders(this);

		if(folderList.size() == 0){
			Log.i("ExportItemSelectActivity------folder list size is 0!");
		}
		for (Note folder : folderList) {

			GroupInfo groupInfo = new GroupInfo(folder.getTitle(),
					folder.getHaveNoteCount(), false, true, folder.getId(),
					Constants.GROUP_ITEM_NO_CHECKED);

			List<ChildInfo> childList = new ArrayList<ChildInfo>();

			List<Note> noteList = dbo.queryNoteByParentId(this,
					Integer.parseInt(folder.getId()));

			for(Note note : noteList){
				//				ChildInfo child = new ChildInfo(note.getBgColor(), (note.getTitle() == null || "".equals(note.getTitle()) ? note.getContent() : note.getTitle()), note.getUpdateDate(), false, note.getId());
				ChildInfo child = new ChildInfo(note.getBgColor(), (note.getTitle() == null || "".equals(note.getTitle()) ? note.getContent() : note.getTitle()), CommonUtils.getNoteData(ExportItemSelectActivity.this, note.getUpdateDate(), note.getUpdateTime()), false, note.getId());
				childList.add(child);
			}

			if (childList.size() <= 0) {
				continue;
			}
			Group group = new Group(groupInfo, childList);
			groupList.add(group);

		}

		List<Note> noteList = dbo.queryAllRootNotes(this);
		if(noteList.size() == 0){
			Log.i("ExportItemSelectActivity------root note list size is 0!");
		}

		for(Note note : noteList){
			//			GroupInfo groupInfo = new GroupInfo((note.getTitle() == null || "".equals(note.getTitle()) ? note.getContent() : note.getTitle()), false, false, note.getId(), note.getBgColor(), note.getUpdateDate());
			GroupInfo groupInfo = new GroupInfo((note.getTitle() == null || "".equals(note.getTitle()) ? note.getContent() : note.getTitle()), false, false, note.getId(), note.getBgColor(), CommonUtils.getNoteData(ExportItemSelectActivity.this, note.getUpdateDate(), note.getUpdateTime()));
			Group group = new Group(groupInfo, new ArrayList<ChildInfo>());  
			groupList.add(group);
		}
		Log.i("ExportItemSelectActivity------init group list end!");

	}*/

	private TextView selectCount;
	private void initResources(){
		Log.i("ExportItemSelectActivity------init resources start!");

		// gn lilg 2012-12-18 add for common controls begin
		actionBar = getAmigoActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		mCustomView = getLayoutInflater().inflate(R.layout.actionbar_custom_view, null);
		selectCount = (TextView) mCustomView.findViewById(R.id.all_edit_selected_count_tv);
		selectCount.setText(getResources().getString(R.string.select_count, 0));
		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gn_com_title_bar));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
		
		// Gionee <lilg><2013-05-22> modify for super theme begin
		actionBar.setCustomView(mCustomView);		

        mSelectedAllButton = (AmigoButton) mCustomView.findViewById(R.id.selected_all);
        mSelectedAllButton.setText(R.string.all_secect);
        checked = false;
        mSelectedAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
            public void onClick(View v) {
                checked = !checked;
				Log.d("ExportItemSelectActivity------mSelectedAllButton checked changed, is: " + checked);
				if(checked){
            	    mSelectedAllButton.setText(R.string.all_no_select);
				}else{
            	    mSelectedAllButton.setText(R.string.all_secect);
                }
				getCheckedItemCount();
				if(checkedNum > 0){

					if(checkedNum != allItemNum){
						if(!checked){
							return;
						}
					}
				}

				actionAllSelect2(checked);
			}
		});
		// Gionee <lilg><2013-05-22> modify for super theme end
		// gn lilg 2012-12-18 add for common controls end

		// init button
		// gn lilg 2012-12-18 add for common controls begin
		//		btnCancel = (Button) findViewById(R.id.btn_cancel);
		//		btnExport = (Button) findViewById(R.id.btn_export);
		//		btnAllSelect = (Button) findViewById(R.id.btn_allselect);

		layout_btn_cancel = (LinearLayout) findViewById(R.id.layout_btn_cancel);
		layout_btn_export = (LinearLayout) findViewById(R.id.layout_btn_export);
		layout_btn_allSelect = (LinearLayout) findViewById(R.id.layout_btn_allselect);

		// gn lilg 2012-12-30 add for common controls begin
		button_export = (AmigoButton) findViewById(R.id.button_export);
		button_export.setOnClickListener(this);
		button_export.setEnabled(false);
		// gn lilg 2012-12-30 add for common controls end
		
		btnCancel = (ImageView) findViewById(R.id.btn_cancel);
		btnExport = (ImageView) findViewById(R.id.btn_export);
		btnAllSelect = (ImageView) findViewById(R.id.btn_allselect);

		textExport = (TextView) findViewById(R.id.text_export);
		textAllSelect = (TextView) findViewById(R.id.text_allselect);
		//		btnExport.setEnabled(false);
		//		btnExport.setText(getResources().getString(R.string.str_export));
		layout_btn_export.setClickable(false);
		btnExport.setImageResource(R.drawable.gn_com_export_dis);
		textExport.setText(getResources().getString(R.string.str_export));

		//		if (groupList.size() <= 0) {
		if (dataList.size() <= 0) {
			//			btnAllSelect.setEnabled(false);
			layout_btn_allSelect.setClickable(false);
			btnAllSelect.setImageResource(R.drawable.gn_com_select_all_dis);
		}

		//		btnCancel.setOnClickListener(this);
		//		btnExport.setOnClickListener(this);
		//		btnAllSelect.setOnClickListener(this);
		layout_btn_cancel.setOnClickListener(this);
		layout_btn_export.setOnClickListener(this);
		layout_btn_allSelect.setOnClickListener(this);
		// gn lilg 2012-12-18 add for common controls end

		//		handler = new Handler() {
		//			public void handleMessage(android.os.Message msg) {
		//				if (msg.what == Constants.UPDATE_BTN_EXPORT) {
		//					setButtonState();
		//				}
		//			};
		//		};

		// gn lilg 2012-12-25 annotate begin
		//		elvItems = (AmigoExpandableListView) findViewById(R.id.elv_items);
		//		expandableListAdapter = new ImportExportExpandableListAdapter(this,
		//				groupList, handler);
		//		elvItems.setOnChildClickListener(this);
		//		elvItems.setOnGroupClickListener(this);
		//		elvItems.setAdapter(expandableListAdapter);
		lvItems = (AmigoListView) findViewById(R.id.lv_items);
		listAdapter = new ExportItemsAdapter(this, dataList);
		lvItems.setAdapter(listAdapter);
		lvItems.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				Log.d("ExportItemSelectActivity------position: " + position + ",id: " + id);

				ExportItem item = dataList.get(position);
				item.setChecked(!item.isChecked());

				listAdapter.notifyDataSetChanged();

				setButtonState();
			}
		});
		// gn lilg 2012-12-25 annotate end
		
		// gn lilg 2012-12-30 add for common controls begin
		mDataExistInfo = (TextView) findViewById(R.id.tv_data_exist_info);
		// gn lilg 2012-12-30 add for common controls end
		
		sharedPreferences = getSharedPreferences(ImportExportActivity.SHARED_NAME, AmigoActivity.MODE_PRIVATE); 

		// gn lilg 20120919 add for CR00693893 start
		mProgressDialog = new AmigoProgressDialog(this,CommonUtils.getTheme());
		mProDialog = new AmigoProgressDialog(this,CommonUtils.getTheme());
		initComfirmDialog();
		// gn lilg 20120919 add for CR00693893 end

		Log.i("ExportItemSelectActivity------init resources end!");
	}

	private void initComfirmDialog(){
		AmigoAlertDialog.Builder builder = new AmigoAlertDialog.Builder(this,CommonUtils.getTheme());
		builder.setTitle(getResources().getString(R.string.str_export_confirm));
		builder.setMessage(getResources().getString(R.string.str_export_msg) +" "+
				(typeSelect == 0 ? getResources().getString(R.string.export_note_dialog_message_first) + getResources().getString(R.string.file_path) + ImportExportUtils.getExportFileName(this)+"\n" + getResources().getString(R.string.export_note_dialog_message_next) : getResources().getString(R.string.export_note_dialog_message_next_next) + getResources().getString(R.string.file_path) + ImportExportUtils.getExportFileName(this) +"\n"+ getResources().getString(R.string.export_note_dialog_message_end))+getResources().getString(R.string.export_note_dialog_message_end_end));
		builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("ExportItemSelectActivity------click button cancel in dialog!");
			}
		});
		builder.setPositiveButton(getResources().getString(R.string.str_export), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("ExportItemSelectActivity------click button confirm in dialog!");
				export();
			}
		});
		builder.setCancelable(true);
		comfirmDialog = builder.create();
	}

	@Override
	protected void onResume() {
		Log.i("ExportItemSelectActivity------onResume() start!");

		super.onResume();
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).registerVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end

		// gn lilg 2012-12-26 add for common controls begin
		//		if (groupList.size() <= 0) {
		if (dataList.size() <= 0) {
			// gn lilg 2012-12-26 add for common controls end
			
			// gn lilg 2012-12-30 modiry for common controls begin
//			String msg = getResources().getString(R.string.toast_export_no_data);
//			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			mDataExistInfo.setVisibility(View.VISIBLE);
			lvItems.setVisibility(View.GONE);
			// gn lilg 2012-12-30 modiry for common controls end
			return;
		}else{
			// gn lilg 2012-12-30 modiry for common controls begin
			mDataExistInfo.setVisibility(View.GONE);
			lvItems.setVisibility(View.VISIBLE);
			// gn lilg 2012-12-30 modiry for common controls end
		}

		// gn lilg 20120919 update for CR00693893 start
		if (comfirmDialog != null && !comfirmDialog.isShowing()) {

			if (mProgressDialog != null && !mProgressDialog.isShowing()) {

				if (mProDialog != null && !mProDialog.isShowing()) {

					mProDialog.setProgressStyle(AmigoProgressDialog.STYLE_SPINNER);
					mProDialog.setMessage(getResources().getString(
							R.string.data_loading));
					mProDialog.setCancelable(false);
					mProDialog.show();

					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							
							// gn lilg 2012-12-26 add for common controls begin
							/*for (int i = 0; i < groupList.size(); i++) {
								Group group = groupList.get(i);
								GroupInfo groupInfo = group.getGroupInfo();
								if (groupInfo.isFolder()) {
									List<ChildInfo> groupChildList = group
									.getChild();

									for (int j = 0; j < groupChildList.size(); j++) {
										ChildInfo childInfo = groupChildList
										.get(j);
										Note tempNote = dbo.queryOneNote(
												ExportItemSelectActivity.this,
												Integer.parseInt(childInfo
														.getDbId()));
										if (tempNote.getId() == null) {
											groupChildList.remove(tempNote);
											j--;
										}
									}
								} else {
									Note tempNote = dbo.queryOneNote(
											ExportItemSelectActivity.this,
											Integer.parseInt(group
													.getGroupInfo().getDbId()));
									if (tempNote.getId() == null) {
										groupList.remove(group);
										i--;
									}
								}
							}*/
							for (int i = 0; i < dataList.size(); i++) {
								ExportItem item = dataList.get(i);

								Note tempNote = dbo.queryOneNote(ExportItemSelectActivity.this,	Integer.parseInt(item.getDbId()));									
								if (tempNote.getId() == null) {
										dataList.remove(item);
										i--;
								}

							}
							// gn lilg 2012-12-26 add for common controls end

							return null;
						}

						@Override
						protected void onPostExecute(Void result) {

							// // dismiss progressDialog
							if (mProDialog != null) {
								mProDialog.dismiss();
							}

						};

					}.executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());

					notifyAdapterDataChanged();
					setButtonState();
				}
				// gn lilg 20120919 update for CR00693893 end

				Log.d("ExportItemSelectActivity------onResume() end!");
			}
		}
		Statistics.onResume(this);
	}
	
	@Override
	protected void onPause() {
		Log.i("ExportItemSelectActivity------onPause() begin");
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).unregisterVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		Log.d("ExportItemSelectActivity------onPause() end");
		super.onPause();
		Statistics.onPause(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// gn lilg 2012-12-18 add for common controls begin
		//		case R.id.btn_cancel:
		case R.id.layout_btn_cancel:
			// gn lilg 2012-12-18 add for common controls end
			// 点击取消按钮
			Log.i("ExportItemSelectActivity------click button calcel!");

			// 返回应用主页面
			HomeActivity.setInFolder(false);
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			// CR00733764
			setResult(CommonUtils.RESULT_ExportItemSelectActivity);
			break;
			// gn lilg 2012-12-18 add for common controls begin
			//		case R.id.btn_export:
		case R.id.layout_btn_export:
			// gn lilg 2012-12-18 add for common controls end
			// 点击导出按钮
			Log.d("ExportItemSelectActivity------click button export!");

			confirm();

			break;
			// gn lilg 2012-12-18 add for common controls begin
			//		case R.id.btn_allselect:
		case R.id.layout_btn_allselect:
			// gn lilg 2012-12-18 add for common controls end
			// 点击全选按钮
			Log.d("ExportItemSelectActivity------click button allSelect!");

			onClickBtnAllSelect();
			setButtonState();

			notifyAdapterDataChanged();

			break;
		case R.id.button_export:
			Log.i("ExportItemSelectActivity------click button export!");
			
			// Gionee <lilg><2013-03-19> add for get the state of export begin
			
			Log.d("ImportItemSelectActivity------isExporting: " + ImportExportUtils.isExporting() + ", isImporting: " + ImportExportUtils.isImporting);
			if(ImportExportUtils.isExporting()){
				// is exporting now
				Toast.makeText(this, getResources().getString(R.string.export_alert_exporting), Toast.LENGTH_SHORT).show();
			}else if(ImportExportUtils.isImporting){
				// is importing now
				Toast.makeText(this, getResources().getString(R.string.import_alert_importing), Toast.LENGTH_SHORT).show();
			}else{
				confirm();
			}
			
			// Gionee <lilg><2013-03-19> add for get the state of export begin
			break;
		default:
			Log.d("ExportItemSelectActivity------default!");
			break;
		}

	}

	@Override
	public boolean onGroupClick(AmigoExpandableListView parent, View v,
			int groupPosition, long id) {
		Log.i("ExportItemSelectActivity------on group click!");

		// update group checked state
		/*GroupInfo groupInfo = groupList.get(groupPosition).getGroupInfo();
		if (!groupInfo.isFolder()) {

			groupInfo.setChecked(!groupInfo.isChecked());

			notifyAdapterDataChanged();

			setButtonState();
		}*/

		return false;
	}

	@Override
	public boolean onChildClick(AmigoExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Log.i("ExportItemSelectActivity------on child click!");

		// update child checked sate
		/*ChildInfo childInfo = groupList.get(groupPosition).getChild(
				childPosition);
		childInfo.setChecked(!childInfo.isChecked());

		notifyAdapterDataChanged();*/

		// update this group checked state
		/*updateGroupCheckedState(groupPosition);

		setButtonState();*/

		return false;
	}

	private void notifyAdapterDataChanged() {
		// gn lilg 2012-12-25 annotate begin
		//		((ImportExportExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();
		// gn lilg 2012-12-25 annotate end
		((ExportItemsAdapter)listAdapter).notifyDataSetChanged();
	}

	@Override
	public void onGroupExpand(int groupPosition) {

	}

	@Override
	public void onGroupCollapse(int groupPosition) {

	}

	/*private void updateGroupCheckedState(int groupPosition){
		Log.i("ExportItemSelectActivity------update grou checked state!");

		int checkedNum = 0;
		for (ChildInfo c : groupList.get(groupPosition).getChild()) {
			if (c.isChecked()) {
				checkedNum++;
			}
		}

		if (checkedNum == 0) {
			// this group checked 0
			groupList.get(groupPosition).getGroupInfo().setCheckedBoxBg(Constants.GROUP_ITEM_NO_CHECKED);
			Log.i("ExportItemSelectActivity------group checked num: " + checkedNum);

		}else if(checkedNum == groupList.get(groupPosition).getChild().size()){
			// this group checked all
			groupList.get(groupPosition).getGroupInfo().setCheckedBoxBg(Constants.GROUP_ITEM_ALL_CHECKED);
			Log.i("ExportItemSelectActivity------group checked num: " + checkedNum);

		}else{
			groupList.get(groupPosition).getGroupInfo().setCheckedBoxBg(Constants.GROUP_ITEM_SOME_CHECKED);
			Log.i("ExportItemSelectActivity------group checked num: " + checkedNum);
		}

	}*/

	// update the state of the btn export and the btn allSelect
	/*private void setButtonState() {

		getCheckedItemCount();

		if(checkedNum > 0){
			Log.d("ExportItemSelectActivity------select count: " + checkedNum);

			// gn lilg 2012-12-18 add for common controls begin
//			btnExport.setEnabled(true);
//			btnExport.setText(getResources().getString(R.string.str_export)	+ "(" + checkedNum + ")");
			layout_btn_export.setClickable(true);
			btnExport.setImageResource(R.drawable.gn_com_export);
			textExport.setText(getResources().getString(R.string.str_export)	+ "(" + checkedNum + ")");
			// gn lilg 2012-12-18 add for common controls begin

			MAX_PROGRESS = checkedNum;

			// update button allselect text
			if (getCheckedItemCount() == allItemNum) {
				// all selected
//				btnAllSelect.setText(getResources().getString(R.string.str_no_select));
				textAllSelect.setText(getResources().getString(R.string.str_no_select));
			} else {
//				btnAllSelect.setText(getResources().getString(R.string.str_all_select));
				textAllSelect.setText(getResources().getString(R.string.str_all_select));
			}

		}else{
			Log.d("ExportItemSelectActivity------select count: " + checkedNum);

			// gn lilg 2012-12-18 add for common controls begin
//			btnExport.setEnabled(false);
//			btnExport.setText(getResources().getString(R.string.str_export));

			layout_btn_export.setClickable(false);
			btnExport.setImageResource(R.drawable.gn_com_export_dis);
			textExport.setText(getResources().getString(R.string.str_export));

			// update button allselect text
//			btnAllSelect.setText(getResources().getString(R.string.str_all_select));
			textAllSelect.setText(getResources().getString(R.string.str_all_select));
			// gn lilg 2012-12-18 add for common controls end
		}

	}*/
	private void setButtonState() {

		getCheckedItemCount();

		if(checkedNum > 0){
			Log.d("ExportItemSelectActivity------select count: " + checkedNum);

			layout_btn_export.setClickable(true);
			btnExport.setImageResource(R.drawable.gn_com_export);
			textExport.setText(getResources().getString(R.string.str_export)	+ "(" + checkedNum + ")");
			
			// gn lilg 2012-12-30 add for common controls begin
			button_export.setEnabled(true);
			selectCount.setText(getResources().getString(R.string.select_count, checkedNum));
			// gn lilg 2012-12-30 add for common controls end
			
			MAX_PROGRESS = checkedNum;

			// update button allselect text
			if (getCheckedItemCount() == allItemNum) {
				// all selected
				textAllSelect.setText(getResources().getString(R.string.str_no_select));
				
				// Gionee <lilg><2013-05-22> modify for super theme begin
				/*if(allSelectItem != null){
					allSelectItem.setIcon(R.drawable.gn_btn_check_on_light);
				}*/
            	mSelectedAllButton.setText(R.string.all_no_select);
                checked = true;
				// Gionee <lilg><2013-05-22> modify for super theme end
			} else {
				textAllSelect.setText(getResources().getString(R.string.str_all_select));
				
				// Gionee <lilg><2013-05-22> modify for super theme begin
				/*if(allSelectItem != null){
					allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);
				}*/
                checked = false;
            	mSelectedAllButton.setText(R.string.all_secect);
				// Gionee <lilg><2013-05-22> modify for super theme end
			}

		}else{
			Log.d("ExportItemSelectActivity------select count: " + checkedNum);

			layout_btn_export.setClickable(false);
			btnExport.setImageResource(R.drawable.gn_com_export_dis);
			textExport.setText(getResources().getString(R.string.str_export));
			
			// gn lilg 2012-12-30 add for common controls begin
			button_export.setEnabled(false);
			selectCount.setText(getResources().getString(R.string.select_count, 0));
			// gn lilg 2012-12-30 add for common controls end
			
			// update button allselect text
			textAllSelect.setText(getResources().getString(R.string.str_all_select));
			
			// Gionee <lilg><2013-05-22> modify for super theme begin
			/*if(allSelectItem != null){
				allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);
			}*/
            checked = false;
            mSelectedAllButton.setText(R.string.all_secect);

			// Gionee <lilg><2013-05-22> modify for super theme end
			// gn lilg 2012-12-18 add for common controls end
		}

	}
	
	private void setButtonState2() {

		getCheckedItemCount();

		if(checkedNum > 0){
			Log.d("ExportItemSelectActivity------select count: " + checkedNum);

			button_export.setEnabled(true);
			selectCount.setText(getResources().getString(R.string.select_count, checkedNum));			
			MAX_PROGRESS = checkedNum;
			
		}else{
			Log.d("ExportItemSelectActivity------select count: " + checkedNum);

			button_export.setEnabled(false);
			selectCount.setText(getResources().getString(R.string.select_count, 0));			
		}

	}

	private void onClickBtnAllSelect() {
		// button allSelect state

		// gn lilg 2012-12-26 add for common controls begin
		/*if (getCheckedItemCount() < allItemNum) {
			for (Group group : groupList) {

				if (group.getGroupInfo().isFolder()) {
					group.getGroupInfo().setCheckedBoxBg(
							Constants.GROUP_ITEM_ALL_CHECKED);
				} else {
					if (!group.getGroupInfo().isChecked()) {
						group.getGroupInfo().setChecked(true);
					}
				}

				for (ChildInfo child : group.getChild()) {
					if (!child.isChecked()) {
						child.setChecked(true);
					}
				}
			}
			// gn lilg 2012-12-18 add for common controls begin
//			btnAllSelect.setText(getResources().getString(R.string.str_no_select));
			textAllSelect.setText(getResources().getString(R.string.str_no_select));
			// gn lilg 2012-12-18 add for common controls end
		} else {
			for (Group group : groupList) {

				if (group.getGroupInfo().isFolder()) {
					group.getGroupInfo().setCheckedBoxBg(
							Constants.GROUP_ITEM_NO_CHECKED);
				} else {
					if (group.getGroupInfo().isChecked()) {
						group.getGroupInfo().setChecked(false);
					}
				}

				for (ChildInfo child : group.getChild()) {
					if (child.isChecked()) {
						child.setChecked(false);
					}
				}
			}
			// gn lilg 2012-12-18 add for common controls begin
//			btnAllSelect.setText(getResources().getString(R.string.str_all_select));
			textAllSelect.setText(getResources().getString(R.string.str_all_select));
			// gn lilg 2012-12-18 add for common controls end
		}*/
		// gn lilg 2012-12-26 add for common controls end

		if (getCheckedItemCount() < allItemNum) {
			for (ExportItem item : dataList) {
				if(!item.isChecked()){
					item.setChecked(true);
				}
			}
			textAllSelect.setText(getResources().getString(R.string.str_no_select));
			
			// Gionee <lilg><2013-05-22> modify for super theme begin
			/*if(allSelectItem != null){
				allSelectItem.setIcon(R.drawable.gn_btn_check_on_light);
			}*/
//			mAllSelect.setChecked(true);
			// Gionee <lilg><2013-05-22> modify for super theme end
		} else {
			for (ExportItem item : dataList) {
				if (item.isChecked()) {
					item.setChecked(false);
				}
			}
			textAllSelect.setText(getResources().getString(R.string.str_all_select));
			
			// Gionee <lilg><2013-05-22> modify for super theme begin
			/*if(allSelectItem != null){
				allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);
			}*/
//			mAllSelect.setChecked(false);
			// Gionee <lilg><2013-05-22> modify for super theme end
		}
	}
	
	private void onClickBtnAllSelect2(boolean checked) {
		if(checked){
			for (ExportItem item : dataList) {
				if(!item.isChecked()){
					item.setChecked(true);
				}
			}
		}else{
			for (ExportItem item : dataList) {
				if (item.isChecked()) {
					item.setChecked(false);
				}
			}
		}
	}

	private int getCheckedItemCount() {
		// the all num of checked in the expandableList
		checkedNum = 0;
		allItemNum = 0;

		// gn lilg 2012-12-26 add for common controls begin
		//		for (Group group : groupList) {
		//			if (group.getGroupInfo().isFolder()) {
		//				for (ChildInfo child : group.getChild()) {
		//					allItemNum++;
		//					if (child.isChecked()) {
		//						checkedNum++;
		//					}
		//				}
		//			} else {
		//				// root note
		//				allItemNum++;
		//				if (group.getGroupInfo().isChecked()) {
		//					checkedNum++;
		//				}
		//			}
		//		}
		for (ExportItem item : dataList) {
			if (item.isFolder()) {
				allItemNum += item.getSubNum();
				if(item.isChecked()){
					checkedNum += item.getSubNum();
				}
			} else {
				// root note
				allItemNum++;
				if (item.isChecked()) {
					checkedNum++;
				}
			}
		}
		// gn lilg 2012-12-26 add for common controls end

		return checkedNum;
	}

	private void confirm() {

		comfirmDialog.show();

	}

	private void export() {
		//gn pengwei 20120118 modify for CR00765638 begin
		if(StatisticalName.isFold == 0){
			Statistics.onEvent(ExportItemSelectActivity.this, Statistics.MAIN_APP_EXPORT);
		}else{
			Statistics.onEvent(ExportItemSelectActivity.this, Statistics.MAIN_APP_FOLDER_EXPORT);

		}
		//gn pengwei 20120118 modify for CR00765638 end
		String file_path = getResources().getString(R.string.file_path);
		if(typeSelect == 0){
			Log.d("export_note_message_sd");
			message=getResources().getString(R.string.export_note_message_sd, file_path , ImportExportUtils.exportFileName);
			//			message = getResources().getString(R.string.export_note_message_sd) + getResources().getString(R.string.file_path) + ImportExportUtils.exportFileName + getResources().getString(R.string.export_note_message);
		}else if(typeSelect == 1){
			Log.d("export_note_message_internal");
			message=getResources().getString(R.string.export_note_message_internal, file_path ,ImportExportUtils.exportFileName);
			//			message =getResources().getString(R.string.export_note_message_internal) + getResources().getString(R.string.file_path) + ImportExportUtils.exportFileName + getResources().getString(R.string.export_note_message);
		}else{

		}

		dialogShow();

		final ImportExportUtils backup = ImportExportUtils
		.getInstance(ExportItemSelectActivity.this);
		new AsyncTask<Void, Integer, Integer>() {
		    private int exportCount = 0;
			@Override
			protected Integer doInBackground(Void... params) {
				
				// Gionee <lilg><2013-03-19> add for set the state of export begin
				ImportExportUtils.setExporting(true);
				// Gionee <lilg><2013-03-19> add for set the state of export end

				int result = ImportExportUtils.STATE_SUCCESS;
				try {
					File fileDir = backup.getExportedTextFileDir(typeSelect);
					if (fileDir == null) {
						return ImportExportUtils.STATE_SYSTEM_ERROR;
					}

					// gn lilg 2012-12-26 add for common controls begin
					/*for (Group group : groupList) {
						GroupInfo groupInfo = group.getGroupInfo();
						if (groupInfo.isFolder()) {
							// folder and notes in it
							List<ChildInfo> childList = group.getChild();
							for (ChildInfo child : childList) {
								if (child.isChecked()) {
									result = backup.exportToText(fileDir,
											child.getDbId(),
											groupInfo.getDbId());
									if (result != ImportExportUtils.STATE_SUCCESS) {
										return ImportExportUtils.STATE_SYSTEM_ERROR;
									}
									publishProgress(exportCount);
									exportCount++;

								}
							}
						} else {
							// root note
							if (groupInfo.isChecked()) {
								result = backup.exportToText(fileDir,
										groupInfo.getDbId(),
										Constants.NO_FOLDER);
								if (result != ImportExportUtils.STATE_SUCCESS) {
									return ImportExportUtils.STATE_SYSTEM_ERROR;
								}
								publishProgress(exportCount);
								exportCount++;

							}
						}
					}*/

					for (ExportItem item : dataList) {
						if(item.isChecked()){
							if (item.isFolder()) {
								// folder and notes in it
								List<Note> noteList = getNoteListFromFolder(item);
								if(noteList != null){
									for (Note note : noteList) {
										result = backup.exportToText(fileDir, note, item);
										if (result != ImportExportUtils.STATE_SUCCESS) {
											return ImportExportUtils.STATE_SYSTEM_ERROR;
										}
										publishProgress(exportCount);
										exportCount++;
									}
								}
							} else {
								// root note
								result = backup.exportToText(fileDir, item.getDbId(), Constants.NO_FOLDER);
								if (result != ImportExportUtils.STATE_SUCCESS) {
									return ImportExportUtils.STATE_SYSTEM_ERROR;
								}
								publishProgress(exportCount);
								exportCount++;
							}
						}
					}
					// gn lilg 2012-12-26 add for common controls end

				} catch (Exception e) {
					result = ImportExportUtils.STATE_SYSTEM_ERROR;
				}
				return result;
			}

			protected void onProgressUpdate(Integer[] values) {

				if (values[0] > MAX_PROGRESS) {
					mProgressDialog.dismiss();
				} else {
					mProgressDialog
					.setMessage(message
							+ getResources()
							.getString(
									R.string.export_note_progress_message_first,
									(values[0] + 1), checkedNum));
					// mProgressDialog.setMessage(message + "\n\n正在导出第" +
					// (values[0] + 1) + "个便签(共"+ checkedNum +"个)");
				}
			}

			protected void onPostExecute(Integer result) {
			    if(null == result){
			        Log.d("onPostExecute-result=null");
			        return;
			    }
				// Gionee <lilg><2013-03-19> add for set the state of export begin
				ImportExportUtils.setExporting(false);
				// Gionee <lilg><2013-03-19> add for set the state of export end
				
				// gionee lilg 2013-01-28 modify for CR00768048 begin
				//gn pengwei 2013-1-8 modify for CR00761228 begin
				// dismiss the progressDialog only when the parent AmigoActivity is still alive.
				if(ExportItemSelectActivity.this != null && !ExportItemSelectActivity.this.isFinishing() && mProgressDialog != null){
	                //Gionee <pengwei><20130615> modify for CR00819335 begin
					try {
	                    if(!isFinishing()){
	                        mProgressDialog.dismiss();
	                    }
	                } catch (Exception e) {
	                    Log.e("ExportItemSelectActivity---onPostExecute---e == " + e);
	                }
					//Gionee <pengwei><20130615> modify for CR00819335 end				
					}
				//gn pengwei 2013-1-8 modify for CR00761228 end
				// gionee lilg 2013-01-28 modify for CR00768048 end
				exportCount = 0;
				if (result == ImportExportUtils.STATE_SUCCESS) {
					//gionee 20121204 jiating modify for CR00739261 begin

					CommonUtils.showToast(ExportItemSelectActivity.this,getResources().getString(R.string.gn_exportnote_complete));

					//gionee 20121204 jiating modify for CR00739261 end
					// 保存导出记录时间
					saveExportRecord();

					// 返回应用主页面
					HomeActivity.setInFolder(false);
					Intent intent = new Intent(ExportItemSelectActivity.this,
							HomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					// CR00733764
					setResult(CommonUtils.RESULT_ExportItemSelectActivity);
					finish();
				} else {
					showExportErrorDialog();
				}
			}

		}.executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());

	}

	private List<Note> getNoteListFromFolder(ExportItem folder){

		int id = -1;
		try{
			id = Integer.parseInt(folder.getDbId());
		}catch(Exception e){
			Log.e("ExportItemSelectActivity------error!" + e);
			return null;
		}

		return dbo.queryFromFolder(this, id);
	}

	private void showExportErrorDialog() {

		int msg = 0;
		if (typeSelect == 0) {
			msg = R.string.export_error_message_sdcard;
		} else if (typeSelect == 1) {
			msg = R.string.export_error_message_internal_memory;
		} else {
			msg = R.string.export_error_message_sdcard;
		}

		new AmigoAlertDialog.Builder(ExportItemSelectActivity.this)
		.setTitle(R.string.export_error_title)
		.setMessage(msg)
		// gionee lilg 2013-01-16 modify for new demands begin
//		.setIcon(android.R.drawable.ic_dialog_alert)
		// gionee lilg 2013-01-16 modify for new demands end
		.setPositiveButton(R.string.Ok,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				dialog.dismiss();
			}
		}).setCancelable(false).show();
	}

	private void dialogShow() {
	    if(null == mProgressDialog){
	        return;
	    }
		//GN pengwei 2012-11-22 modify  for find bugs start
		try {
		    mProgressDialog.dismiss();

			// gn lilg 20120919 update for CR00693893 start
			// mProgressDialog = new AmigoProgressDialog(this);
			// gn lilg 20120919 update for CR00693893 end

			// gionee lilg 2013-01-16 modify for new demands begin
//			mProgressDialog.setIconAttribute(android.R.attr.alertDialogIcon);
			// gionee lilg 2013-01-16 modify for new demands end
			mProgressDialog.setTitle(getResources().getString(
					R.string.export_note_progress_message_title));
			mProgressDialog.setProgressStyle(AmigoProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMax(MAX_PROGRESS);
			mProgressDialog.setMessage(message
					+ getResources().getString(
							R.string.export_note_progress_message_first, 1,
							checkedNum));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//GN pengwei 2012-11-22 modify  for find bugs start
	}


	private void saveExportRecord(){
		Log.i("ExportItemSelectActivity------save export record of this time!");

		// 保存导出记录时间

		Date date = ImportExportUtils.importExportTime;
		String time = DateUtils.format(date, getResources().getString(R.string.format_date_yyyymmdd));
		Log.d("ExportItemSelectActivity------record time: " + time);

		Editor editor = sharedPreferences.edit();
		editor.putString(ImportExportActivity.SHARED_KEY_RECORD_EXPORT_TIME,
				time);
		editor.commit();
	}

	@Override
	protected void onDestroy() {
		Log.i("ExportItemSelectActivity------onDestroy() start!");

//		if(mProgressDialog != null){
//			mProgressDialog.dismiss();
//		}

		// gn lilg 20121105 add for memory overflow start
		ImportExportUtils.close();
		// gn lilg 20121105 add for memory overflow end

		// gn lilg 20120919 add for CR00693893 start
		if (mProDialog != null) {
			mProDialog.dismiss();
		}
		// gn lilg 20120919 add for CR00693893 end

		super.onDestroy();
		Log.d("ExportItemSelectActivity------onDestroy() end!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("ExportItemSelectActivity------onCreateOptionsMenu!");

		// Gionee <lilg><2013-05-22> modify for super theme begin
		/*MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.export_actionbar_menu, menu);

		allSelectItem = menu.findItem(R.id.action_all_select);
		allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);*/
		// Gionee <lilg><2013-05-22> modify for super theme end
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("ExportItemSelectActivity------onOptionsItemSelected!");

		switch (item.getItemId()) {
		case android.R.id.home:
			Log.d("ExportItemSelectActivity------click back button!");
			finish();
			break;
		case R.id.action_all_select:
			Log.d("ExportItemSelectActivity------click all select button!");
			actionAllSelect();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void actionAllSelect(){
		Log.i("ExportItemSelectActivity------actionAllSelect begin!");

		onClickBtnAllSelect();
		setButtonState();

		notifyAdapterDataChanged();

		Log.d("ExportItemSelectActivity------actionAllSelect end!");
	}
	
	private void actionAllSelect2(boolean checked){
		Log.i("ExportItemSelectActivity------actionAllSelect begin!");

		onClickBtnAllSelect2(checked);
		setButtonState2();
		
		notifyAdapterDataChanged();

		Log.d("ExportItemSelectActivity------actionAllSelect end!");
	}

	//gionee 20121220 jiating modify for CR00747076 config begin
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i("ExportItemSelectActivity------onConfigurationChanged");
	}
	//gionee 20121220 jiating modify for CR00747076 config end

}
