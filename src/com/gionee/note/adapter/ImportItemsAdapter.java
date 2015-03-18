package com.gionee.note.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gionee.note.R;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.domain.ImportItem;

public class ImportItemsAdapter extends BaseAdapter {
	
	private Context context;
	private List<ImportItem> dataSource;
	private LayoutInflater inflater;
	
	class ItemHolder {
		TextView fileName;
		CheckBox checked;
	}
	
	public ImportItemsAdapter() {
	}
	
	public ImportItemsAdapter(Context context, List<ImportItem> dataSource) {
		this.context = context;
		this.dataSource = dataSource;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	}
	
	@Override
	public int getCount() {
		return dataSource.size();
	}
	
	@Override
	public Object getItem(int position) {
		return dataSource.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ItemHolder holder  = null;
		if(convertView == null){
		    convertView = inflater.inflate(R.layout.import_item_select_item_layout_white, null);
			
            holder = new ItemHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.tv_fileName);
            holder.checked = (CheckBox) convertView.findViewById(R.id.cb_checked);
            convertView.setTag(holder); 
		}else{
			holder = (ItemHolder) convertView.getTag();
		}

		ImportItem item = dataSource.get(position);
		if (item != null) {
		
			holder.fileName.setText(item.getFileName());
			holder.checked.setChecked(item.isChecked());
        }
		
		return convertView;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
}
