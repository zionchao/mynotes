package com.gionee.note;

import java.util.ArrayList;
import java.util.List;


import com.gionee.note.R;
import com.gionee.note.adapter.ViewAdapter;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.StatisticalValue;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.Statistics;
import com.gionee.note.utils.UtilsQueryDatas;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.provider.AmigoSettings;
import amigo.widget.AmigoListView;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import amigo.widget.AmigoSearchView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
//Gionee <pengwei><20130809> modify for CR00834587 begin
public class SearchNoteActivity extends AmigoActivity{
	private AmigoSearchView searchKeys;
	private DBOperations dbo;
	private List<Note> allNotes;
	private GridView searchNoteGridview;
	private AmigoListView searchNoteListview;
	private ViewAdapter viewAdapter;
	
	private TextView noNoteText;
//	private ImageView backToHome;
	// jiating
	//gionee 20121226 jiating modify for theme begin
//	private View mListDiverBottom;
	//gionee 20121226 jiating modify for theme end
	
	//gionee 20121219 jiating modify for theme begin 
	private AmigoActionBar  mActionBar;
	private View mCustomView;
	//gionee 20121219 jiating modify for theme end

	//gionee 20121026  jiating CR00718021 begin
	private ContentObserver SearchGusestModeObserver = new ContentObserver(
			new Handler(Looper.getMainLooper())) {

		@Override
		public void onChange(boolean selfChange) {
			
			if(NoteApplication.GN_GUEST_MODE ){
				finish();
			}
		}

	};
	//gionee 20121026  jiating CR00718021 end
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		CommonUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		//gionee 20121026  jiating CR00718021 begin
		getContentResolver().registerContentObserver(
				CommonUtils.getUri(), false, SearchGusestModeObserver);
		//gionee 20121026  jiating CR00718021 end
		//gionee 20121219 jiating modify for theme begin 
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//gionee 20121219 jiating modify for theme end
		setContentView(R.layout.search_note_view_white);
		initActionBar();

		searchNoteGridview = (GridView) findViewById(R.id.search_note_gridview);
		searchNoteListview = (AmigoListView) findViewById(R.id.search_note_listview);
		
		noNoteText = (TextView) findViewById(R.id.home_no_note_text);
		
//		searchKeys = (AmigoSearchView) findViewById(R.id.et_search_note);
		
//		backToHome = (ImageView) findViewById(R.id.iv_back_from_search_note);

//		mListDiverBottom = findViewById(R.id.list_divide_Bottom);
		// searchKeys.requestFocus();
		
		

		
		allNotes = new ArrayList<Note>();
		
		UtilsQueryDatas.queryNotesByKey(allNotes,HomeActivity.mTempNoteList,null);
		
		viewAdapter = new ViewAdapter(SearchNoteActivity.this, allNotes);
		if(HomeActivity.isGridView){
			searchNoteGridview.setVisibility(View.VISIBLE);
			searchNoteListview.setVisibility(View.GONE);
			searchNoteGridview.setAdapter(viewAdapter);
//			mListDiverBottom.setVisibility(View.GONE);
		} else {
			searchNoteGridview.setVisibility(View.GONE);
			searchNoteListview.setVisibility(View.VISIBLE);
			searchNoteListview.setAdapter(viewAdapter);
			if (allNotes.size() < 1) {
//				mListDiverBottom.setVisibility(View.GONE);
			} else {
//				mListDiverBottom.setVisibility(View.VISIBLE);
			}
		}
	
		
//		backToHome.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
		
		
//		searchKeys.setOnQueryTextListener(new AmigoSearchView.OnQueryTextListener() {
//			@Override
//			public boolean onQueryTextSubmit(String query) {
//				InputMethodManager inputMethodManager = (InputMethodManager) SearchNoteActivity.this
//						.getSystemService(Context.INPUT_METHOD_SERVICE);
//				inputMethodManager.hideSoftInputFromWindow(searchKeys.getWindowToken(), 0);
//				return true;
//			}
//			
//			@Override
//			public boolean onQueryTextChange(String newText) {
//				dbo.queryAllRecordsByKeywords(SearchNoteActivity.this,allNotes,newText);
//				viewAdapter.updateView(-1);
//				if(allNotes.size() == 0){
//					noNoteText.setVisibility(View.VISIBLE);
//				}else{
//					noNoteText.setVisibility(View.GONE);
//				}
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
		searchNoteGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				clickGridOrListView(arg2);
			}
		}) ;
		searchNoteListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				clickGridOrListView(arg2);
			}
		}) ;
	}
	//gionee 20121219 jiating modify for theme begin 
	private void initActionBar() {
		mActionBar = this.getAmigoActionBar();
		mCustomView = getLayoutInflater().inflate(
				R.layout.gn_note_search_actionbar, null);
		mActionBar.setCustomView(mCustomView, new AmigoActionBar.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mActionBar.setIcon(R.drawable.gn_note_actionbar_icon);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		searchKeys=(AmigoSearchView) mCustomView.findViewById(R.id.gn_note_search_note);
		searchKeys.onActionViewExpanded();
		searchKeys.setIconified(false);
		searchKeys.setOnQueryTextListener(new AmigoSearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				InputMethodManager inputMethodManager = (InputMethodManager) SearchNoteActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(searchKeys.getWindowToken(), 0);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				UtilsQueryDatas.queryNotesByKey(allNotes,HomeActivity.mTempNoteList,newText);
				viewAdapter.updateView(-1);
				if(allNotes.size() == 0){
					noNoteText.setVisibility(View.VISIBLE);
				}else{
					noNoteText.setVisibility(View.GONE);
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// mActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_title_bg_white));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
	}
	
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   
	            	imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	            	
	            	finish();
	                return true;
	          
	        }
	        return super.onOptionsItemSelected(item);
	    }
	//gionee 20121219 jiating modify for theme end
		//Gionee <pengwei><20130615> modify for CR00810117 begin
		private void clickGridOrListView(int position) {
			try {
				Intent intent = new Intent();
				Note note = allNotes.get(position);

				intent.putExtra(DBOpenHelper.ID, Integer.parseInt(note.getId()));

				if (Constants.IS_FOLDER.equals(note.getIsFolder())) {
					intent.putExtra(DBOpenHelper.NOTE_TITLE, note.getTitle());
					intent.putExtra(Constants.IS_IN_FOLDER, true);
					intent.setAction(Constants.START_FOLDER_ACTIVITY_ACTION);

					sendBroadcast(intent);

				} else {
					if(!Constants.PARENT_FILE_ROOT.equals(note.getParentFile()) && note.getParentFile() != null && !"".equals(note.getParentFile())){
						intent.putExtra(DBOpenHelper.PARENT_FOLDER,
								Integer.parseInt(note.getParentFile()));
					}
					intent.setClass(SearchNoteActivity.this, NoteActivity.class);
					startActivity(intent);
				}
				finish();
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("SearchNoteActivity------clickGridOrListView---e == " + e);
			}
		}
		//Gionee <pengwei><20130615> modify for CR00810117 end
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// gn jiating 20121009 GN_GUEST_MODE begin
//		if(NoteApplication.GN_GUEST_MODE ){
//			finish();
//		}
		// gn jiating 20121009 GN_GUEST_MODE end
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).registerVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
	}
	
	@Override
	protected void onPause() {
		Log.i("SearchNoteActivity------onPause begin!");
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).unregisterVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		Log.i("SearchNoteActivity------onPause end!");
		super.onPause();
	}
	
	//gionee 20121026  jiating CR00718021 begin
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(SearchGusestModeObserver!=null){
			getContentResolver().unregisterContentObserver(SearchGusestModeObserver);
			SearchGusestModeObserver=null;
		}
	}
	//gionee 20121026  jiating CR00718021 end
	
//	@Override
//	public void finish() {
//		// TODO Auto-generated method stub
//		super.finish();
//		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
//        .hideSoftInputFromWindow(SearchNoteActivity.this
//                        .getCurrentFocus().getWindowToken(),
//                        InputMethodManager.HIDE_NOT_ALWAYS); 
//
//	}
	
	//gionee 20121220 jiating modify for CR00747076 config begin
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
	}
	
	//gionee 20121220 jiating modify for CR00747076 config end
}
