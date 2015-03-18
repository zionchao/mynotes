package com.gionee.note;

import java.io.File;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.widget.AmigoButton;
import amigo.widget.AmigoListView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.FileUtils;
import com.gionee.note.utils.Log;

public class ImportTypeSelectActivity extends AmigoActivity{

	private AmigoListView lvImportTo;
	// gn lilg 2012-12-28 modify for common controls begin
	//	private TextView btnNext;
	private AmigoButton btnNext;
	// gn lilg 2012-12-28 modify for common controls end

	// sd card 剩余最小可用空间为 1M
	private static final int MIN_AVAILABLE_STORE = 3;
	private static final int ZERO_STORE = 0;

	private static String[] GENRES = null;

	// gn lilg 2012-12-18 add for common controls begin
	private AmigoActionBar actionBar;
	// gn lilg 2012-12-18 add for common controls end

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i("ImportTypeSelectActivity------onCreate() start!");

		CommonUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_type_select_layout_white);

		initData();

		initResources();

	}

	private void initData(){
		Log.i("ImportTypeSelectActivity------init data start!");

		// Gionee <lilg><2013-05-14> add for CR00808800 begin
		int sdcardState = FileUtils.checkSDCardState();
		if(sdcardState == FileUtils.ERROR_SDCARD_NOT_EXISTS_OR_UNAVAILABLE){
			// sd card not exists or unavailable
			Log.d("ExportTypeSelectActivity------sd card not exists or unavailable.");
			GENRES = new String[] { 
					getResources().getString(R.string.str_internal_memory)
			};
		}else{
			Log.d("ExportTypeSelectActivity------sd card exists");
			GENRES = new String[] { 
					getResources().getString(R.string.str_sdcard),
					getResources().getString(R.string.str_internal_memory)
			};
		}
		// Gionee <lilg><2013-05-14> add for CR00808800 end
		
		for(String item : GENRES){
			Log.i("ImportTypeSelectActivity------item: " + item);
		}

	}

	private void initResources(){
		Log.i("ImportTypeSelectActivity------init resources start!");

		// gn lilg 2012-12-18 add for common controls begin
		actionBar = getAmigoActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(R.string.str_import_note);
		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gn_com_title_bar));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
		// gn lilg 2012-12-18 add for common controls end

		lvImportTo = (AmigoListView) findViewById(R.id.lv_importTo);
		lvImportTo.setAdapter(new ArrayAdapter<String>(this, R.layout.import_type_select_item_layout_white, GENRES));

		lvImportTo.setItemsCanFocus(false);
		lvImportTo.setChoiceMode(AmigoListView.CHOICE_MODE_SINGLE);
		lvImportTo.setItemChecked(ImportExportActivity.EXPORT_TYPE_SDCARD, true);

		lvImportTo.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				Log.d("ImportTypeSelectActivity------position: " + position + ",id: " + id);
				Log.d("ImportTypeSelectActivity------checkedItemPosition: " + lvImportTo.getCheckedItemPosition());
			}
		});

		// gn lilg 2012-12-28 modify for common controls begin
		//		btnNext = (TextView) findViewById(R.id.btn_next);
		btnNext = (AmigoButton) findViewById(R.id.btn_next);
		// gn lilg 2012-12-28 modify for common controls end
		btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.i("ImportTypeSelectActivity------click button next!");

				// Gionee <lilg><2013-05-14> modify for CR00808800 begin
				
				if(GENRES.length == 1){
					Log.d("ExportTypeSelectActivity------GENRES length: " + GENRES.length);
					
					//内部存储器(即内置SD卡)

					//首先判断外部SD卡是否存在
					File sdCard2Path = new File(FileUtils.PATH_SDCARD2);
					File internalMemoryPath = null;
					Log.d("ImportTypeSelectActivity------sd card total store: " + FileUtils.getTotalStore(sdCard2Path.getPath()));

					if(FileUtils.getTotalStore(sdCard2Path.getPath()) <= ZERO_STORE){
						// 不存在
						internalMemoryPath = new File(FileUtils.PATH_SDCARD);
						Log.d("ImportTypeSelectActivity------internalMemoryPath: " + FileUtils.PATH_SDCARD);
					}else{
						// 存在
						internalMemoryPath = new File(FileUtils.PATH_SDCARD2);
						Log.d("ImportTypeSelectActivity------internalMemoryPath: " + FileUtils.PATH_SDCARD2);
					}

					Log.d("ImportTypeSelectActivity------internal memory total store: " + FileUtils.getTotalStore(internalMemoryPath.getPath()));

					if(FileUtils.getTotalStore(internalMemoryPath.getPath()) <= ZERO_STORE){
						Log.d("ImportTypeSelectActivity------internal memory exists: false");

						String msg = getResources().getString(R.string.import_note_no_internal); 
						showAlert(msg);
						return;
					}
					
					Intent intent = new Intent();
					intent.putExtra(ImportExportActivity.IMPORT_TYPE, 1);
					intent.setClass(ImportTypeSelectActivity.this, ImportItemSelectActivity.class);
					startActivityForResult(intent, CommonUtils.REQUEST_ImportTypeSelectActivity);
					
				}else if(GENRES.length == 2){
					Log.d("ExportTypeSelectActivity------GENRES length: " + GENRES.length);
					
					//判断SD卡是否存在
					if(lvImportTo.getCheckedItemPosition() == 0 ){
						File sdCard2Path = new File(FileUtils.PATH_SDCARD2);
						//外部SD卡
						Log.d("ImportTypeSelectActivity------sd card path exists: " + sdCard2Path.exists());

						if(FileUtils.getTotalStore(sdCard2Path.getPath()) <= ZERO_STORE){
							// 不存在或不可用
							Log.d("ImportTypeSelectActivity------SD卡不存在或不可用！");

							String msg =getResources().getString(R.string.import_note_no_sd); 
							showAlert(msg);
							return;
						}

						/*File sdCardPath = new File(FileUtils.PATH_SDCARD);
						Log.d(LOG_TAG, "sd card min available stort: " + FileUtils.getAvailableStore(sdCardPath.getPath()) + ", " + FileUtils.getAvailableStore(sdCardPath.getPath())/1024/1024);
						if((FileUtils.getAvailableStore(sdCardPath.getPath())/1024/1024) < MIN_AVAILABLE_STORE ){
							Log.e(LOG_TAG, "sd card min available stort < 1M!");
							String msg = "请检测SD卡可用空间是否大于1M?"; 
							showAlert(msg);
							return;
						}*/

					}else if(lvImportTo.getCheckedItemPosition() == 1 ){
						//内部存储器(即内置SD卡)

						//首先判断外部SD卡是否存在
						File sdCard2Path = new File(FileUtils.PATH_SDCARD2);
						File internalMemoryPath = null;
						Log.d("ImportTypeSelectActivity------sd card total store: " + FileUtils.getTotalStore(sdCard2Path.getPath()));

						if(FileUtils.getTotalStore(sdCard2Path.getPath()) <= ZERO_STORE){
							// 不存在
							internalMemoryPath = new File(FileUtils.PATH_SDCARD);
							Log.d("ImportTypeSelectActivity------internalMemoryPath: " + FileUtils.PATH_SDCARD);
						}else{
							// 存在
							internalMemoryPath = new File(FileUtils.PATH_SDCARD2);
							Log.d("ImportTypeSelectActivity------internalMemoryPath: " + FileUtils.PATH_SDCARD2);
						}

						Log.d("ImportTypeSelectActivity------internal memory total store: " + FileUtils.getTotalStore(internalMemoryPath.getPath()));

						if(FileUtils.getTotalStore(internalMemoryPath.getPath()) <= ZERO_STORE){
							Log.d("ImportTypeSelectActivity------internal memory exists: false");

							String msg = getResources().getString(R.string.import_note_no_internal); 
							showAlert(msg);
							return;
						}

						/*Log.d(LOG_TAG, "internal memory min available stort: " + FileUtils.getAvailableStore(internalMemoryPath.getPath()) + ", " + FileUtils.getAvailableStore(internalMemoryPath.getPath())/1024/1024);
						if((FileUtils.getAvailableStore(internalMemoryPath.getPath())/1024/1024) < MIN_AVAILABLE_STORE ){
							Log.e(LOG_TAG, "internal memory min available stort < 1M!");
							String msg = "请检测内置存储器可用空间是否大于1M?"; 
							showAlert(msg);
							return;
						}*/
					}else{
						Log.d("ImportTypeSelectActivity------typeSelect: " + lvImportTo.getCheckedItemPosition());
					}

					Intent intent = new Intent();
					intent.putExtra(ImportExportActivity.IMPORT_TYPE, lvImportTo.getCheckedItemPosition());
					intent.setClass(ImportTypeSelectActivity.this, ImportItemSelectActivity.class);
					startActivityForResult(intent, CommonUtils.REQUEST_ImportTypeSelectActivity);
				}else{
					Log.e("ExportTypeSelectActivity------GENRES length: " + GENRES.length);
				}
			
				// Gionee <lilg><2013-05-14> modify for CR00808800 end
			}
		});

	}
	// CR00733764
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CommonUtils.REQUEST_ImportTypeSelectActivity
				&& resultCode == CommonUtils.RESULT_ImportItemSelectActivity) {
			setResult(CommonUtils.RESULT_ImportTypeSelectActivity);
			finish();
		}
	}

	private void showAlert(String msg){
		Log.d("ImportTypeSelectActivity------show dialog start!");

		AmigoAlertDialog.Builder builder = new AmigoAlertDialog.Builder(this,CommonUtils.getTheme());
		builder.setTitle(getResources().getString(R.string.promat));
		builder.setMessage(msg);
		builder.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("ImportTypeSelectActivity------click button confirm in dialog!");
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	@Override
	protected void onDestroy() {
		Log.i("ImportTypeSelectActivity------onDestroy() start!");
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("ALLEditActivity------onOptionsItemSelected!");

		switch (item.getItemId()) {
		case android.R.id.home:
			Log.i("ALLEditActivity------click back button!");
			finish();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	//gionee 20121220 jiating modify for CR00747076 config begin
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.i("ImportTypeSelectActivity....onConfigurationChanged");
	}

	//gionee 20121220 jiating modify for CR00747076 config end

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("ImportTypeSelectActivity------onResume begin!");
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).registerVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		Log.d("ImportTypeSelectActivity------onResume end!");
	}
	
	@Override
	protected void onPause() {
		Log.i("ImportTypeSelectActivity------onPause begin!");
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).unregisterVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		Log.d("ImportTypeSelectActivity------onPause end!");
		super.onPause();
	}
}
