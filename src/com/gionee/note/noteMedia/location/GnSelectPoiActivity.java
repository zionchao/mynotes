package com.gionee.note.noteMedia.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gionee.note.NoteActivity;
import com.gionee.note.R;
import com.gionee.note.domain.NoteLocation;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import amigo.widget.AmigoListView;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

public class GnSelectPoiActivity extends AmigoActivity {

	private AmigoListView mListView;
	private PoiAdapter mAdapter;
//	private ArrayList<String> mArrayPoi;
	private ArrayList<NoteLocation> mMapPoi;
	private int mPos = 0;
	private View mCustomView;
	private TextView mHomeTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CommonUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gn_location_poi_list_white);
		initActionBar();
//		mArrayPoi = (ArrayList<String>) getIntent().getSerializableExtra("poi");
		mMapPoi = (ArrayList<NoteLocation>) getIntent().getSerializableExtra("poi");
		mAdapter = new PoiAdapter(this);
		mAdapter.setData(mMapPoi);
		mListView = (AmigoListView) findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				// TODO Auto-generated method stub
				onItemClickListener(adapter,view,position,id);
			}
		});
	}

	private void onItemClickListener(AdapterView<?> adapter, View view, int position, long id){
		onItemClick(position);
	}
	
	private void onItemClick(int pos) {
		if(mPos == pos) {
			mPos = -1;
		} else {
			mPos = pos;
		}
		mAdapter.notifyDataSetChanged();
		Intent i = new Intent("LOCATION");
		i.putExtra("poi", mAdapter.getItem(pos).getName());
		setResult(NoteActivity.RESULT_CODE_LOCATION_POI_OK, i);
		finish();
	}
	
	//gn pengwei 20121228 add for Common control begin
	private AmigoActionBar actionbar;
	private void initActionBar() {
		actionbar = this.getAmigoActionBar();
		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_title_bg_white));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
		mCustomView = LayoutInflater.from(actionbar.getThemedContext())
				.inflate(R.layout.gn_actionbar_title_view, null);
		actionbar.setCustomView(mCustomView, new AmigoActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mHomeTitle = (TextView) mCustomView
				.findViewById(R.id.actionbar_title);
        mHomeTitle.setText(getResources().getString(R.string.other_location));

		actionbar.setIcon(R.drawable.gn_note_actionbar_icon);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(false);

	}
	//gn pengwei 20121228 add for Common control end

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	private class PoiAdapter extends BaseAdapter {
//		private ArrayList<String> mData = new ArrayList<String>();
		private ArrayList<NoteLocation> mData = new ArrayList<NoteLocation>();
		private LayoutInflater mInflater;

		public PoiAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

//		public void setData(ArrayList<String> data) {
//			mData = data;
//		}

		public void setData(ArrayList<NoteLocation> data) {
			mData = data;
		}
		
		public int getCount() {
			return mData.size();
		}

		public NoteLocation getItem(int position) {
			return mData.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final int pos = position;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.gn_location_poi_item, null);
				holder = new ViewHolder();
				holder.mTextView = (TextView) convertView.findViewById(R.id.item_address);
				holder.mTextViewName = (TextView) convertView.findViewById(R.id.item_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mTextViewName.setText(mData.get(position).getName());
			holder.mTextView.setText(mData.get(position).getAddress());
			Log.v("GnSelectPoiActivity---getView---" + mData.get(position).getName());
			Log.v("GnSelectPoiActivity---getView---" + mData.get(position).getAddress());
//			convertView.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					onItemClick(pos);
//				}
//			});
			return convertView;
		}



		private class ViewHolder {
			TextView mTextView;
			TextView mTextViewName;
		}
	}
}
