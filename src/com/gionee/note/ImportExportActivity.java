package com.gionee.note;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.widget.AmigoButton;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;

import com.gionee.note.content.NoteApplication;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.FileUtils;
import com.gionee.note.utils.ImportExportUtils;
import com.gionee.note.utils.Log;

public class ImportExportActivity extends AmigoActivity implements OnClickListener{

	private AmigoButton btnExport;
	private AmigoButton btnImport;

	private TextView tvRecordExportTime;
	private TextView tvRecordImportTime;

	private SharedPreferences sharedPreferences;
	public static final String SHARED_KEY_RECORD_EXPORT_TIME = "ImportExportActivity_exportRecordTime";
	public static final String SHARED_KEY_RECORD_IMPORT_TIME = "ImportExportActivity_importRecordTime";
	public static final String SHARED_NAME = "ImportExportActivity";

	public static final String EXPORT_TYPE = "exportType"; 
	public static final String IMPORT_TYPE = "importType";

	public static final int EXPORT_TYPE_SDCARD = 0;
	public static final int EXPORT_TYPE_INTERNAL_MEMORY = 1;
	
	// gn lilg 2012-12-18 add for common controls begin
	private AmigoActionBar actionbar;
	// gn lilg 2012-12-18 add for common controls end
	private View mCustomView;
	private TextView mHomeTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		CommonUtils.setTheme(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.import_export_main_layout_white);

		initResources();

		initData();

	}

	private void initResources(){
		Log.d("ImportExportActivity------init resources start!");
		
		// gn lilg 2012-12-18 add for common controls begin
		actionbar = getAmigoActionBar();
		mCustomView = LayoutInflater.from(actionbar.getThemedContext())
				.inflate(R.layout.gn_actionbar_title_view, null);
		actionbar.setCustomView(mCustomView, new AmigoActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mHomeTitle = (TextView) mCustomView
				.findViewById(R.id.actionbar_title);
        mHomeTitle.setText(getResources().getString(R.string.str_importExport));

		actionbar.setIcon(R.drawable.gn_note_actionbar_icon);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(false);

		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gn_com_title_bar));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
		// gn lilg 2012-12-18 add for common controls end

		btnExport = (AmigoButton) findViewById(R.id.btn_export);
		btnImport = (AmigoButton) findViewById(R.id.btn_import);

		btnExport.setOnClickListener(this);
		btnImport.setOnClickListener(this);

		tvRecordExportTime = (TextView) findViewById(R.id.tv_record_export_time);
		tvRecordImportTime = (TextView) findViewById(R.id.tv_record_import_time);

		sharedPreferences = getSharedPreferences(SHARED_NAME, AmigoActivity.MODE_PRIVATE);

	}

	private void initData(){
		Log.i("ImportExportActivity------init data start!");

		String defaultRecordExportTime = getResources().getString(R.string.str_defaultRecordExportTime);
		String defaultRecordImportTime = getResources().getString(R.string.str_defaultRecordImportTime);
		Log.d("ImportExportActivity------defaultRecordExportTime: " + defaultRecordExportTime+", defaultRecordImportTime: " + defaultRecordImportTime);

		String preRecordExportTime = getResources().getString(R.string.str_preRecordExportTime);
		String preRecordImportTime = getResources().getString(R.string.str_preRecordImportTime);
		Log.d("ImportExportActivity------preRecordExportTime: " + preRecordExportTime+ ", preRecordImportTime: " + preRecordImportTime);

		String msgExport = sharedPreferences.getString(SHARED_KEY_RECORD_EXPORT_TIME, defaultRecordExportTime);
		String msgImport = sharedPreferences.getString(SHARED_KEY_RECORD_IMPORT_TIME, defaultRecordImportTime);
		Log.d("ImportExportActivity------msgExport: " + msgExport+", msgImport: " + msgImport);

		if(defaultRecordExportTime.equals(msgExport)){
			tvRecordExportTime.setText(msgExport);
		}else{
			tvRecordExportTime.setText(preRecordExportTime + msgExport);
		}

		if(defaultRecordImportTime.equals(msgImport)){
			tvRecordImportTime.setText(msgImport);
		}else{
			tvRecordImportTime.setText(preRecordImportTime + msgImport);
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_export:
			Log.i("ImportExportActivity------click button export!");

			// Gionee <lilg><2013-05-14> add for CR00808800 begin
			boolean externalStorageExists = FileUtils.isExternalStorageExists();
			Log.d("ImportExportActivity------externalStorageExists: " + externalStorageExists);
			if(!externalStorageExists){
				Toast.makeText(this, getResources().getString(R.string.import_note_storage_unavailable), Toast.LENGTH_SHORT).show();
				return;
			}
			// Gionee <lilg><2013-05-14> add for CR00808800 end
			
			// Gionee <lilg><2013-06-25> add for CR00827998 begin
			boolean isExportingOrImporting = isExportingOrImporting();
			if(isExportingOrImporting){
				return;
			}
			// Gionee <lilg><2013-06-25> add for CR00827998 end
			
			Intent exportIntent = new Intent();
			exportIntent.setClass(this, ExportTypeSelectActivity.class);
			// CR00733764
			startActivityForResult(exportIntent, CommonUtils.REQUEST_ImportExportActivity);
			break;
		case R.id.btn_import:
			Log.i("ImportExportActivity------click button import!");

			// Gionee <lilg><2013-05-14> add for CR00808800 begin
			externalStorageExists = FileUtils.isExternalStorageExists();
			Log.d("ImportExportActivity------externalStorageExists: " + externalStorageExists);
			if(!externalStorageExists){
				Toast.makeText(this, getResources().getString(R.string.import_note_storage_unavailable), Toast.LENGTH_SHORT).show();
				return;
			}
			// Gionee <lilg><2013-05-14> add for CR00808800 end
			
			// Gionee <lilg><2013-06-25> add for CR00827998 begin
			isExportingOrImporting = isExportingOrImporting();
			if(isExportingOrImporting){
				return;
			}
			// Gionee <lilg><2013-06-25> add for CR00827998 end
			
			Intent importIntent = new Intent();
			importIntent.setClass(this, ImportTypeSelectActivity.class);
			// CR00733764
			startActivityForResult(importIntent, CommonUtils.REQUEST_ImportExportActivity);
			break;
		default:
			break;
		}

	}
	
	// Gionee <lilg><2013-06-25> add for CR00827998 begin
	private boolean isExportingOrImporting(){
		boolean res = false;
		
		Log.d("ImportExportActivity------isExporting: " + ImportExportUtils.isExporting() + ", isImporting: " + ImportExportUtils.isImporting);
		if(ImportExportUtils.isExporting()){
			// is exporting now
			Toast.makeText(this, getResources().getString(R.string.export_alert_exporting), Toast.LENGTH_SHORT).show();
			res = true;
		}else if(ImportExportUtils.isImporting){
			// is importing now
			Toast.makeText(this, getResources().getString(R.string.import_alert_importing), Toast.LENGTH_SHORT).show();
			res = true;
		}
		return res;
	}
	// Gionee <lilg><2013-06-25> add for CR00827998 end
	
	// CR00733764
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CommonUtils.REQUEST_ImportExportActivity	&& (resultCode == CommonUtils.RESULT_ExportTypeSelectActivity || resultCode == CommonUtils.RESULT_ImportTypeSelectActivity)) {
			finish();
		}
	}
	@Override
	protected void onDestroy() {
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
		super.onConfigurationChanged(newConfig);
		Log.i("ImportExportActivity------onConfigurationChanged");
	}
	//gionee 20121220 jiating modify for CR00747076 config end

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("ImportExportActivity------onResume begin!");
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).registerVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		Log.d("ImportExportActivity------onResume end!");
	}
	
	@Override
	protected void onPause() {
		Log.i("ImportExportActivity------onPause begin!");
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).unregisterVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		Log.d("ImportExportActivity------onPause end!");
		super.onPause();
	}
}
