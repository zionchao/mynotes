package com.gionee.note;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Set;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.app.AmigoProgressDialog;
import amigo.widget.AmigoButton;
import amigo.widget.AmigoListView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.note.adapter.ImportItemsAdapter;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.StatisticalName;
import com.gionee.note.content.StatisticalValue;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.ImportItem;
import com.gionee.note.domain.MediaInfo;
import com.gionee.note.domain.Note;
import com.gionee.note.noteMedia.record.NoteMediaManager;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.DateUtils;
import com.gionee.note.utils.ImportCheckedFileDescComparator;
import com.gionee.note.utils.ImportExportUtils;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.Statistics;
import com.gionee.note.utils.UtilsQueryDatas;
import amigo.widget.AmigoButton;

public class ImportItemSelectActivity extends AmigoActivity implements OnClickListener{

	private AmigoListView lvItems;

	// gn lilg 2012-12-18 add for common controls begin
	//	private Button btnCancel;
	//	private Button btnImport;
	//	private Button btnAllSelect;

	private LinearLayout layout_btn_cancel;
	private LinearLayout layout_btn_import;
	private LinearLayout layout_btn_allSelect;

	private ImageView btnCancel;
	private ImageView btnImport;
	private ImageView btnAllSelect;

	private TextView textImport;
	private TextView textAllSelect;

	private AmigoButton button_import;
	// gn lilg 2012-12-18 add for common controls end

	private ImportItemsAdapter adapter;
	private AmigoProgressDialog mProgressDialog;
	private SharedPreferences sharedPreferences;

	private Integer typeSelect;

	private static int MAX_PROGRESS;
	private boolean isStop = false;
	private String message = "";

	private Set<String> importIndexSet;
	private int index;

	private static List<ImportItem> fileList = new ArrayList<ImportItem>();
	private static Map<String, Boolean> checkedFileMap = new HashMap<String, Boolean>();
	private Map<String, List<String>> directoryList;

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
		Log.i("ImportItemSelectActivity------onCreate() start!");

		CommonUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_item_select_layout_white);

		Statistics.setReportCaughtExceptions(true);
		initData();

		initResources();

	}

	private void initData(){
		Log.i("ImportItemSelectActivity------init data start!");

		// get export type
		Intent intent = getIntent();
		typeSelect = intent.getExtras().getInt(ImportExportActivity.IMPORT_TYPE);
		Log.d("ImportItemSelectActivity------type select: " + typeSelect);

		initFileList();

	}

	private void initFileList(){
		Log.i("ImportItemSelectActivity------init file list start!");

		// init the file list to display
		directoryList = ImportExportUtils.getInstance(ImportItemSelectActivity.this).getDirectoryList(this, typeSelect);
		if(directoryList == null || directoryList.size() <= 0){
			Log.d("ImportItemSelectActivity------the file list is null or the size <= 0!");

			fileList.clear();
		}else{
			Set<String> dirs = directoryList.keySet();
			if(dirs == null || dirs.size() <= 0){
				Log.d("ImportItemSelectActivity------the directorys of the file list is null or the size <= 0!");

				fileList.clear();
			}else{

				fileList.clear();
				for(String dir : dirs){

					Boolean checked = checkedFileMap.get(dir);
					if(checked != null && checked == true){
						fileList.add(new ImportItem(dir, true));
						Log.d("ImportItemSelectActivity------fileName: " + dir + ",isChecked: true");
					}else{
						fileList.add(new ImportItem(dir, false));
						Log.d("ImportItemSelectActivity------fileName: " + dir + ",isChecked: false");
					}

					//					fileList.add(new ImportItem(dir, false));
					Log.d("ImportItemSelectActivity------fileName: " + dir);
				}
				Collections.sort(fileList, new ImportCheckedFileDescComparator());
			}
		}

	}
	private TextView selectCount;
	private void initResources(){
		Log.i("ImportItemSelectActivity------init resources start!");

		// gn lilg 2012-12-18 add for common controls begin
		actionBar = getAmigoActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		mCustomView = getLayoutInflater().inflate(R.layout.actionbar_custom_view, null);
		selectCount = (TextView) mCustomView.findViewById(R.id.all_edit_selected_count_tv);
		selectCount.setText(getResources().getString(R.string.select_count, 0));
		actionBar.setCustomView(mCustomView);
        mSelectedAllButton = (AmigoButton) mCustomView.findViewById(R.id.selected_all);
        mSelectedAllButton.setText(R.string.all_secect);
        checked = false;
        mSelectedAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
            public void onClick(View v) {
                checked = !checked;
				Log.d("ImportItemSelectActivity------mSelectedAllButton checked changed, is: " + checked);
				if(checked){
            	    mSelectedAllButton.setText(R.string.all_no_select);
				}else{
            	    mSelectedAllButton.setText(R.string.all_secect);
                }

				if(getCheckedItemCount() < fileList.size()){
					if(!checked){
						return;
					}
				}

				actionAllSelect2(checked);
			}
		});
		// Gionee <lilg><2013-05-23> modify for super theme end
		// gn lilg 2012-12-18 add for common controls end

		// gn lilg 2012-12-18 add for common controls begin
		//		btnCancel = (Button) findViewById(R.id.btn_cancel);
		//		btnImport = (Button) findViewById(R.id.btn_import);
		//		btnAllSelect = (Button) findViewById(R.id.btn_allselect);

		layout_btn_cancel = (LinearLayout) findViewById(R.id.layout_btn_cancel);
		layout_btn_import = (LinearLayout) findViewById(R.id.layout_btn_import);
		layout_btn_allSelect = (LinearLayout) findViewById(R.id.layout_btn_allselect);

		btnCancel = (ImageView) findViewById(R.id.btn_cancel);
		btnImport = (ImageView) findViewById(R.id.btn_import);
		btnAllSelect = (ImageView) findViewById(R.id.btn_allselect);

		textImport = (TextView) findViewById(R.id.text_import);
		textAllSelect = (TextView) findViewById(R.id.text_allselect);

		//		btnImport.setEnabled(false);
		layout_btn_import.setClickable(false);
		btnImport.setImageResource(R.drawable.gn_com_import_dis);

		button_import = (AmigoButton) findViewById(R.id.button_import);
		button_import.setOnClickListener(this);
		button_import.setEnabled(false);

		if(fileList.size() <= 0){
			//			btnAllSelect.setEnabled(false);
			layout_btn_allSelect.setClickable(false);
			btnAllSelect.setImageResource(R.drawable.gn_com_select_all_dis);
		}else{
			//			btnAllSelect.setEnabled(true);
			layout_btn_allSelect.setClickable(true);
			btnAllSelect.setImageResource(R.drawable.gn_com_select_all);
		}

		//		btnCancel.setOnClickListener(this);
		//		btnImport.setOnClickListener(this);
		//		btnAllSelect.setOnClickListener(this);

		layout_btn_cancel.setOnClickListener(this);
		layout_btn_import.setOnClickListener(this);
		layout_btn_allSelect.setOnClickListener(this);
		// gn lilg 2012-12-18 add for common controls end

		lvItems = (AmigoListView) findViewById(R.id.lv_items);

		adapter = new ImportItemsAdapter(this, fileList);
		lvItems.setAdapter(adapter);

		lvItems.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				Log.d("ImportItemSelectActivity------position: " + position + ",id: " + id);

				ImportItem item = fileList.get(position);
				item.setChecked(!item.isChecked());

				adapter.notifyDataSetChanged();

				setBtnImportState();
				setBtnAllSelectState();
			}
		});

		// gn lilg 2012-12-30 add for common controls begin
		mDataExistInfo = (TextView) findViewById(R.id.tv_data_exist_info);
		// gn lilg 2012-12-30 add for common controls end

		sharedPreferences = getSharedPreferences(ImportExportActivity.SHARED_NAME, AmigoActivity.MODE_PRIVATE);

		// 当导入过程中用户点击导入对话框中取消按钮，在弹出的确认对话框中，点击取消：
		// 保存一次导入是否一次性操作，若导入过程中按取消终止过，则保存此次导入过的文件，下次导入时便不再导入
		// 当此次导入正常结束，或取消后点击确定按钮，也视为正常结束
		importIndexSet = new HashSet<String>();
		index = 1;

	}

	@Override
	protected void onResume() {

		super.onResume();
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).registerVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end

		// reload file list to display
		initFileList();
		adapter.notifyDataSetChanged();

		setBtnImportState();
		setBtnAllSelectState();

		if(fileList.size() <= 0){
			Log.d("ImportItemSelectActivity------the size of the file list <= 0");

			// gn lilg 2012-12-30 modiry for common controls begin
			//			String msg = getResources().getString(R.string.toast_import_no_data);
			//			Toast.makeText(ImportItemSelectActivity.this, msg, Toast.LENGTH_LONG).show();
			mDataExistInfo.setVisibility(View.VISIBLE);
			lvItems.setVisibility(View.GONE);
			// gn lilg 2012-12-30 modiry for common controls end

			// gn lilg 2012-12-18 add for common controls begin
			//			btnAllSelect.setEnabled(false);
			layout_btn_allSelect.setClickable(false);
			btnAllSelect.setImageResource(R.drawable.gn_com_select_all_dis);
			// gn lilg 2012-12-18 add for common controls end
		}else{
			// gn lilg 2012-12-18 add for common controls begin
			//			btnAllSelect.setEnabled(true);
			layout_btn_allSelect.setClickable(true);
			btnAllSelect.setImageResource(R.drawable.gn_com_select_all);
			// gn lilg 2012-12-18 add for common controls end

			// gn lilg 2012-12-30 modiry for common controls begin
			mDataExistInfo.setVisibility(View.GONE);
			lvItems.setVisibility(View.VISIBLE);
			// gn lilg 2012-12-30 modiry for common controls end
		}
		Statistics.onResume(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.layout_btn_cancel:
			Log.d("ImportItemSelectActivity------click button calcel!");

			// 返回应用主页面
			HomeActivity.setInFolder(false);
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			break;
		case R.id.layout_btn_import:
			Log.d("ImportItemSelectActivity------click button import!");
			//gn pengwei 20121126 add for statistics begin
			if(StatisticalName.isFold == 0){
				Statistics.onEvent(ImportItemSelectActivity.this, Statistics.MAIN_APP_IMPORT);

			}else{
				Statistics.onEvent(ImportItemSelectActivity.this, Statistics.MAIN_APP_FOLDER_IMPORT);
			}
			//gn pengwei 20121126 add for statistics end
			importNote();

			break;
		case R.id.layout_btn_allselect:
			Log.d("ImportItemSelectActivity------click button allSelect!"+"list count: " + lvItems.getCount());

			if(getCheckedItemCount() < fileList.size()){
				for(ImportItem item : fileList){
					item.setChecked(true);
				}
			}else{
				for(ImportItem item : fileList){
					item.setChecked(false);
				}
			}

			setBtnAllSelectState();
			setBtnImportState();

			adapter.notifyDataSetChanged();

			break;
		case R.id.button_import:
			Log.i("ImportItemSelectActivity------click button import!");
			
			// Gionee <lilg><2013-03-19> add for get the state of import begin
			
			Log.d("ImportItemSelectActivity------isExporting: " + ImportExportUtils.isExporting() + ", isImporting: " + ImportExportUtils.isImporting);
			if(ImportExportUtils.isExporting()){
				// is exporting now
				Toast.makeText(this, getResources().getString(R.string.export_alert_exporting), Toast.LENGTH_SHORT).show();
			}else if(ImportExportUtils.isImporting){
				// is importing now
				Toast.makeText(this, getResources().getString(R.string.import_alert_importing), Toast.LENGTH_SHORT).show();
			}else{
				importNote();
			}
			
			// Gionee <lilg><2013-03-19> add for get the state of import begin
			
			break;
		default:

			break;
		}

	}

	private void setBtnImportState(){
		Log.i("ImportItemSelectActivity------set button import state!");

		// button import state
		int count = getCheckedItemCount();
		if(count > 0){
			Log.d("ImportItemSelectActivity------select count: " + count);

			// gn lilg 2012-12-18 add for common controls begin
			//			btnImport.setEnabled(true);
			//			btnImport.setText( getResources().getString(R.string.str_import) + "( " + count + " )");

			layout_btn_import.setClickable(true);
			btnImport.setImageResource(R.drawable.gn_com_import);
			textImport.setText(getResources().getString(R.string.str_import) + "( " + count + " )");
			// gn lilg 2012-12-18 add for common controls end

			// gn lilg 2012-12-30 add for common controls begin
			button_import.setEnabled(true);
			selectCount.setText(getResources().getString(R.string.select_count, count));
			// gn lilg 2012-12-30 add for common controls end

			MAX_PROGRESS = getCheckedFileTotalNum(getCheckedDirectoryList());

		}else{
			Log.d("ImportItemSelectActivity------select count: " + count);

			// gn lilg 2012-12-18 add for common controls begin
			//			btnImport.setEnabled(false);
			//			btnImport.setText(getResources().getString(R.string.str_import));

			layout_btn_import.setClickable(false);
			btnImport.setImageResource(R.drawable.gn_com_import_dis);
			textImport.setText(getResources().getString(R.string.str_import));
			// gn lilg 2012-12-18 add for common controls end

			// gn lilg 2012-12-30 add for common controls begin
			button_import.setEnabled(false);
			selectCount.setText(getResources().getString(R.string.select_count, 0));
			// gn lilg 2012-12-30 add for common controls end
		}
	}

	private void setBtnAllSelectState(){
		Log.i("ImportItemSelectActivity------set button allSelect state!");

		// button allSelect state
		if(getCheckedItemCount() < fileList.size()){
			// gn lilg 2012-12-18 add for common controls begin
			//			btnAllSelect.setText(getResources().getString(R.string.str_all_select));
			textAllSelect.setText(getResources().getString(R.string.str_all_select));
			// gn lilg 2012-12-18 add for common controls end

			// gn lilg 2012-12-30 add for common controls begin
			// Gionee <lilg><2013-05-22> modify for super theme begin
			/*if(allSelectItem != null){
				allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);
			}*/
            checked = false;
            mSelectedAllButton.setText(R.string.all_secect);

			// Gionee <lilg><2013-05-22> modify for super theme end
			// gn lilg 2012-12-30 add for common controls end
		}else{
			// gn lilg 2012-12-18 add for common controls begin
			//			btnAllSelect.setText(getResources().getString(R.string.str_no_select));
			textAllSelect.setText(getResources().getString(R.string.str_no_select));
			// gn lilg 2012-12-18 add for common controls end

			// gn lilg 2012-12-30 add for common controls begin
			// Gionee <lilg><2013-05-22> modify for super theme begin
			/*if(allSelectItem != null){
				allSelectItem.setIcon(R.drawable.gn_btn_check_on_light);
			}*/
            checked = true;
            mSelectedAllButton.setText(R.string.all_no_select);
			// Gionee <lilg><2013-05-22> modify for super theme end
			// gn lilg 2012-12-30 add for common controls end
		}
	}

	private int getCheckedItemCount(){

		if(fileList.size() == 0){
			return -1;
		}

		int num = 0;
		for(ImportItem item : fileList){
			if(item.isChecked()){
				++ num;
			}
		}
		Log.d("ImportItemSelectActivity------checked item count: " + num);

		return num;
	}

	
	private void importNote(){
		//gn pengwei 20120118 modify for CR00765638 begin
		//gn pengwei 20121126 add for statistics begin
		if(StatisticalName.isFold == 0){
			Statistics.onEvent(ImportItemSelectActivity.this, Statistics.MAIN_APP_IMPORT);

		}else{
			Statistics.onEvent(ImportItemSelectActivity.this, Statistics.MAIN_APP_FOLDER_IMPORT);
		}
		//gn pengwei 20121126 add for statistics end
		//gn pengwei 20120118 modify for CR00765638 end
		if(typeSelect == 0){
            Log.d("import_note_message_sd");
			message = getResources().getString(R.string.import_note_message_sd) + getResources().getString(R.string.file_path);
		}else if(typeSelect == 1){
            Log.d("import_note_message_internal");
			message = getResources().getString(R.string.import_note_message_internal) + getResources().getString(R.string.file_path);
		}else{
			Log.d("ImportItemSelectActivity------typeSelect: " + typeSelect);
		}

		final ImportExportUtils backup = ImportExportUtils.getInstance(ImportItemSelectActivity.this);
		AsyncTask<Void, String, Integer> asyncTask = new AsyncTask<Void, String, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				Log.i("ImportItemSelectActivity------doInBackground!");
				
				// Gionee <lilg><2013-03-19> add for set the state of import begin
				ImportExportUtils.isImporting = true;
				// Gionee <lilg><2013-03-19> add for set the state of import end

				int result = ImportExportUtils.STATE_SUCCESS;

				// get the dir with it`s file list
				Map<String, List<String>> checkedDirList = getCheckedDirectoryList();
				if(checkedDirList == null || checkedDirList.size() <= 0){
					Log.d("ImportItemSelectActivity------the checked directory list == null or the size <= 0");

					return ImportExportUtils.STATE_SYSTEM_ERROR;
				}

				// for each
				Set<Entry<String, List<String>>> entrySet = checkedDirList.entrySet();
				if(entrySet == null || entrySet.size() <= 0){
					Log.d("ImportItemSelectActivity------the entrySet of the checkedDirList == null or the size <= 0");

					return ImportExportUtils.STATE_SYSTEM_ERROR;
				}

				// Gionee <lilg><2013-03-14> add for get the absolute import path begin 
				String importPath = ImportExportUtils.getPathByType(ImportItemSelectActivity.this, typeSelect);
				Log.d("ImportItemSelectActivity------importPath: " + importPath);
				// Gionee <lilg><2013-03-14> add for get the absolute import path end

				String dir = "";
				List<String> files = null;
				out: for(Entry<String, List<String>> entry : entrySet){

					dir = entry.getKey();
					files = entry.getValue();

					Log.d("ImportItemSelectActivity------dir: " + dir);

					if(files == null || files.size() <= 0){
						continue;
					}

					File tmpFile = null;
					String[] tmpFiles = null;
					String[] txtFiles = null;
					for(String fileName : files){
						Log.d("ImportItemSelectActivity------fileName: " + fileName);

						if(isStop){
							Log.d("ImportItemSelectActivity------isStop: " + isStop);

							break out;
						}

						// Gionee <lilg><2013-03-14> add for import note media info begin

						tmpFile = new File(importPath + "/" + dir + "/" + fileName);
						if(tmpFile.exists() && tmpFile.isFile()){
							// Compatible before release
							Log.d("ImportItemSelectActivity------" + dir + "/" + fileName + " is file!");

							if(importIndexSet.contains(fileName)){
								Log.d("ImportItemSelectActivity------contains: " + fileName);

								continue;
							}
							// import
							//							backup.importFromText(typeSelect, dir + "/" + fileName);
							long noteInsertedId = backup.importFromText(typeSelect, dir, fileName);

							importIndexSet.add(fileName);
							publishProgress(index++ + "", dir + "/" + fileName);
							Log.d("ImportItemSelectActivity------dir+fileName: " + dir + "/" + fileName);
							// Gionee <wangpan><2014-08-20> add for CR01360175 begin
							Note queryOneNote = DBOperations.getInstances(ImportItemSelectActivity.this).queryOneNote(ImportItemSelectActivity.this, (int)noteInsertedId);
                            int posInt = UtilsQueryDatas.sortNote(queryOneNote,HomeActivity.mTempNoteList);
                            HomeActivity.mTempNoteList.add(posInt,queryOneNote);
                            // Gionee <wangpan><2014-08-20> add for CR01360175 end
						}else if(tmpFile.exists() && tmpFile.isDirectory()){
							Log.d("ImportItemSelectActivity------" + dir + "/" + fileName + " is directory!");

							// all files
							tmpFiles = tmpFile.list();

							// txt file
							txtFiles = tmpFile.list(new FilenameFilter(){
								@Override
								public boolean accept(File dir, String filename) {
									if(filename.endsWith(NoteMediaManager.SUFFIX_TXT)){
										return true;
									}
									return false;
								}
							});
							if(tmpFiles != null && tmpFiles.length > 0 && txtFiles != null && txtFiles.length > 0){
								Log.d("ImportItemSelectActivity------txt file: " + txtFiles[0]);

								if(importIndexSet.contains(txtFiles[0])){
									Log.d("ImportItemSelectActivity------contains: " + txtFiles[0]);
									continue;
								}
								// import
								//								backup.importFromText(typeSelect, dir + "/" + fileName + "/" + txtFiles[0]); 
								long noteInsertedId = backup.importFromText(typeSelect, dir + "/" + fileName, txtFiles[0]); 
								Log.d("ImportItemSelectActivity------noteInsertedId: " + noteInsertedId);
								
								// import the media info into the table MediaItems 
								if(noteInsertedId != -1){
									for(String tmpFileName : tmpFiles){
										if(tmpFileName.endsWith(NoteMediaManager.SUFFIX_TXT)){
											Log.d("ImportItemSelectActivity------text file: " + tmpFileName);
											continue;
										}else if(tmpFileName.endsWith(NoteMediaManager.SUFFIX_MP3)){
											Log.d("ImportItemSelectActivity------mp3 file: " + tmpFileName);
											
											MediaInfo mediaInfo = new MediaInfo();
											mediaInfo.setNoteId(String.valueOf(noteInsertedId));
											mediaInfo.setMediaType(NoteMediaManager.TYPE_MEDIA_RECORD);
											
//											mediaInfo.setMediaFileName(importPath + dir + "/" + fileName + "/" + tmpFileName);
											if(typeSelect == ImportExportActivity.EXPORT_TYPE_SDCARD){
												mediaInfo.setMediaFileName(typeSelect + importPath + dir + "/" + fileName + "/" + tmpFileName);
												Log.d("ImportItemSelectActivity------note media file name: " + importPath + dir + "/" + fileName + "/" + tmpFileName);
											}else if(typeSelect == ImportExportActivity.EXPORT_TYPE_INTERNAL_MEMORY){
												mediaInfo.setMediaFileName(typeSelect + importPath + "/" + dir + "/" + fileName + "/" + tmpFileName);
												Log.d("ImportItemSelectActivity------note media file name: " + importPath + "/" + dir + "/" + fileName + "/" + tmpFileName);
											}
											backup.importNoteMedia(mediaInfo);
											continue;
										}else {
											Log.d("ImportItemSelectActivity------other file: " + tmpFileName);
											continue;
										}
									}

									importIndexSet.add(txtFiles[0]);
									publishProgress(index++ + "", dir + "/" + fileName + "/" + txtFiles[0]);
									Log.d("ImportItemSelectActivity------dir+fileName: " + dir + "/" + fileName + txtFiles[0]);
									// Gionee <wangpan><2014-06-06> add for CR01273806 begin
									Note queryOneNote = DBOperations.getInstances(ImportItemSelectActivity.this).queryOneNote(ImportItemSelectActivity.this, (int)noteInsertedId);
									int posInt = UtilsQueryDatas.sortNote(queryOneNote,HomeActivity.mTempNoteList);
                             // Gionee <caody><2014-11-22> add for CR01414673 begin
                             if (null != HomeActivity.mTempNoteList) {
                                 HomeActivity.mTempNoteList.add(posInt,queryOneNote);
                              }
                             // Gionee <caody><2014-11-22> add for CR01414673 end
									// Gionee <wangpan><2014-06-06> add for CR01273806 end
								}

							}
						}
						// Gionee <lilg><2013-03-14> add for import note media info end
					}
				}

				return result;
			}

			protected void onProgressUpdate(String[] values) {
				if (Integer.parseInt(values[0]) > MAX_PROGRESS) {
					mProgressDialog.dismiss();
				} else {
					mProgressDialog.setMessage(message + values[1] + getResources().getString(R.string.import_note_progress_message_first) + " "+values[0]+" " + getResources().getString(R.string.import_note_progress_message_next)+ " "+MAX_PROGRESS +" "+getResources().getString(R.string.import_note_progress_message_end));
				}
			};

			protected void onPostExecute(Integer result) {
				Log.i("ImportItemSelectActivity------onPostExecute!");
				
				// Gionee <lilg><2013-03-19> add for set the state of export begin
				ImportExportUtils.isImporting = false;
				// Gionee <lilg><2013-03-19> add for set the state of export end
				
				// gionee lilg 2013-01-28 modify for CR00768048 begin
				//gn pengwei 2013-1-8 modify for CR00761228 begin
				// dismiss the progressDialog only when the parent AmigoActivity is still alive.
				//Gionee <pengwei><20130615> modify for CR00808824 begin
				try {
                    if (!isFinishing() && mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
				} catch (Exception e) {
				    // TODO: handle exception
				    Log.e("ImportItemSelectActivity---onPostExecute---e == " + e);
				}
				//Gionee <pengwei><20130615> modify for CR00808824 end			
				//gn pengwei 2013-1-8 modify for CR00761228 end
				// gionee lilg 2013-01-28 modify for CR00768048 end

				Log.d("ImportItemSelectActivity------onPostExecute:" + isStop);

				if(!isStop){
					importIndexSet.clear();
					index = 1;
				}

				if(ImportExportUtils.STATE_SUCCESS == result && !isStop){
					Log.d("ImportItemSelectActivity------import success and save the record of this time!");

					// 保存导出记录时间
					saveImportRecord();
				}
				
				// Gionee <lilg><2013-03-13> modify for CR00782943 begin 
				//gionee 20121204 jiating modify for CR00739261 begin
				//                CommonUtils.showToast(ImportItemSelectActivity.this, getResources().getString(R.string.gn_importnote_complete));
				//gionee 20121204 jiating modify for    CR00739261  end
				// Gionee <lilg><2013-03-13> modify for CR00782943 end

				if(!isStop){
					
					// Gionee <lilg><2013-03-13> add for CR00782943 begin 
					CommonUtils.showToast(ImportItemSelectActivity.this, getResources().getString(R.string.gn_importnote_complete));
					// Gionee <lilg><2013-03-13> add for CR00782943 begin 
					
					// 返回应用主页面
					HomeActivity.setInFolder(false);
					Intent intent = new Intent(ImportItemSelectActivity.this, HomeActivity.class); 
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					startActivity(intent); 
					// CR00733764
					setResult(CommonUtils.RESULT_ImportItemSelectActivity);
					finish();
				}
			};

		};

		asyncTask.executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());

		dialogShow(asyncTask);
	}

	private void dialogShow(final AsyncTask<Void, String, Integer> asyncTask) {

		mProgressDialog = new AmigoProgressDialog(this,CommonUtils.getTheme());
		// gionee lilg 2013-01-16 modify for new demands begin
		//		mProgressDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		// gionee lilg 2013-01-16 modify for new demands end
		mProgressDialog.setTitle(getResources().getString(R.string.import_note_progress_message_title));
		mProgressDialog.setProgressStyle(AmigoProgressDialog.STYLE_SPINNER);
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				/* User clicked No so do some stuff */
				isStop = true;
				confirm();
			}
		});

		mProgressDialog.setMessage(message + getMessage() + getResources().getString(R.string.import_note_progress_message_content)+" "+ MAX_PROGRESS +" "+getResources().getString(R.string.import_note_progress_message_end));
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	private String getMessage(){

		String msg = "";

		// get the dir with it`s file list
		Map<String, List<String>> checkedDirList = getCheckedDirectoryList();
		if(checkedDirList == null || checkedDirList.size() <= 0){
			return null;
		}

		// for each
		Set<Entry<String, List<String>>> entrySet = checkedDirList.entrySet();
		if(entrySet == null || entrySet.size() <= 0){
			return null;
		}

		String dir = ""; 
		List<String> files = null;
		out: for(Entry<String, List<String>> entry : entrySet){
			dir = entry.getKey();
			files = entry.getValue();

			if(files == null || files.size() <= 0){
				continue;
			}
			for(String fileName : files){
				msg = dir + "/" + fileName;
				break out;
			}
		}

		return msg;
	}

	private void confirm(){
		AmigoAlertDialog.Builder builder = new AmigoAlertDialog.Builder(this,CommonUtils.getTheme());
		builder.setTitle(getResources().getString(R.string.Cancel));
		builder.setMessage(getResources().getString(R.string.import_note_dialog_message_content));
		builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("ImportItemSelectActivity------click button cancel in dialog!");

				isStop = false;
				importNote();

			}
		});
		builder.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("ImportItemSelectActivity------click button confirm in dialog!");
				Log.d("ImportItemSelectActivity------import index set when click positive button: " + importIndexSet);

				importIndexSet.clear();
				index = 1;

				saveImportRecord();

				//返回导入导出主界面
				Intent intent = new Intent();
				intent.setClass(ImportItemSelectActivity.this, ImportExportActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	private void saveImportRecord(){
		Log.i("ImportItemSelectActivity------save import record of this time!");

		// 保存导出记录时间
		Date date = new Date();
		String time = DateUtils.format(date, getResources().getString(R.string.format_date_yyyymmdd));
		Log.d("ImportItemSelectActivity---record time: " + time);

		Editor editor = sharedPreferences.edit();
		editor.putString(ImportExportActivity.SHARED_KEY_RECORD_IMPORT_TIME, time);
		editor.commit();
	}

	private Map<String, List<String>> getCheckedDirectoryList(){

		Map<String, List<String>> checkedDirList = new HashMap<String, List<String>>();

		for(ImportItem item : fileList){
			String dirName = item.getFileName();
			boolean checked = item.isChecked();
			if(!checked){
				continue;
			}
			checkedDirList.put(dirName, directoryList.get(dirName));
		}

		return checkedDirList;
	}

	// get the num all of the checked note file 
	private int getCheckedFileTotalNum(Map<String, List<String>> checkedDirList){

		int num = 0;

		Set<Entry<String, List<String>>> entrySet = checkedDirList.entrySet();
		if(entrySet == null || entrySet.size() <= 0){
			return 0;
		}
		List<String> files = null;
		for(Entry<String, List<String>> entry : entrySet){
			files = entry.getValue();
			if(files == null || files.size() <= 0){
				continue;
			}
			num += files.size();
		}

		return num;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("ImportItemSelectActivity------onPause() start!");

		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).unregisterVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		// save the check state of the file list
		checkedFileMap.clear();
		for(ImportItem item : fileList){
			if(item.isChecked()){
				checkedFileMap.put(item.getFileName(), item.isChecked());
			}
		}
		Statistics.onPause(this);
	}

	@Override
	protected void onDestroy() {

		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
		// gn lilg 20121105 add for memory overflow start
		ImportExportUtils.close();
		// gn lilg 20121105 add for memory overflow end
		// clear the check state of the file list
		checkedFileMap.clear();

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("ImportItemSelectActivity------onCreateOptionsMenu!");

		// Gionee <lilg><2013-05-22> modify for super theme begin
		/*MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.import_actionbar_menu, menu);

		allSelectItem = menu.findItem(R.id.action_all_select);
		allSelectItem.setIcon(R.drawable.gn_btn_check_off_light);*/
		// Gionee <lilg><2013-05-22> modify for super theme end

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("ImportItemSelectActivity------onOptionsItemSelected!");

		switch (item.getItemId()) {
		case android.R.id.home:
			Log.i("ImportItemSelectActivity------click back button!");
			finish();
			break;
		case R.id.action_all_select:
			Log.i("ImportItemSelectActivity------click all select button!");
			actionAllSelect();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void actionAllSelect(){
		Log.i("ImportItemSelectActivity------actionAllSelect begin!");

		if(getCheckedItemCount() < fileList.size()){
			for(ImportItem item : fileList){
				item.setChecked(true);
			}
		}else{
			for(ImportItem item : fileList){
				item.setChecked(false);
			}
		}

		setBtnAllSelectState();
		setBtnImportState();

		adapter.notifyDataSetChanged();

		Log.d("ImportItemSelectActivity------actionAllSelect end!");
	}
	
	private void actionAllSelect2(boolean checked){
		Log.i("ImportItemSelectActivity------actionAllSelect begin!");

		if(checked){
			for(ImportItem item : fileList){
				item.setChecked(true);
			}
		}else{
			for(ImportItem item : fileList){
				item.setChecked(false);
			}
		}

		setBtnImportState();

		adapter.notifyDataSetChanged();

		Log.d("ImportItemSelectActivity------actionAllSelect end!");
	}

	//gionee 20121220 jiating modify for CR00747076 config begin
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.d("ImportItemSelectActivity------onConfigurationChanged");
	}

	//gionee 20121220 jiating modify for CR00747076 config end

}
