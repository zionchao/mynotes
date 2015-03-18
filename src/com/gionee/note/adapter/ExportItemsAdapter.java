package com.gionee.note.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.note.R;
import com.gionee.note.content.ResourceParser;
import com.gionee.note.domain.ExportItem;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;

public class ExportItemsAdapter extends BaseAdapter {

	private Context context;
	private List<ExportItem> dataSource;
	private LayoutInflater inflater;

	class ItemHolder {
		// folder:0, note:1
		Integer type;
		TextView title;
		TextView subNum;
		TextView updateDate;
		// TODO add bg color
		// Gionee <wangpan><2014-05-15> modify for CR01249465 begin
		RelativeLayout noteBackground;
		RelativeLayout folderBackground;
		// Gionee <wangpan><2014-05-15> modify for CR01249465 end
		CheckBox checked;
	}

	public ExportItemsAdapter() {
	}

	public ExportItemsAdapter(Context context, List<ExportItem> dataSource) {
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
		Log.i("ExportItemsAdapter------getView begin!");

		ItemHolder holder = null;
		ExportItem exportItem = dataSource.get(position);

		if (exportItem != null) {
			boolean isFolder = exportItem.isFolder();

			if (isFolder) {
				// is folder
				Log.i("ExportItemsAdapter------is folder");
				Log.i("ExportItemsAdapter------folder info: " + exportItem.toString());
				if (convertView != null	&& (holder = (ItemHolder) convertView.getTag()).type == 0) {
					holder = (ItemHolder) convertView.getTag();
				} else {
					convertView = inflater.inflate(R.layout.export_item_select_item_folder_layout, null);
					holder = new ItemHolder();
					holder.title = (TextView) convertView.findViewById(R.id.title);
					holder.subNum = (TextView) convertView.findViewById(R.id.sub_num);
					holder.updateDate = (TextView) convertView.findViewById(R.id.update_date);
					holder.checked = (CheckBox) convertView.findViewById(R.id.checkbox);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 begin
					holder.folderBackground = (RelativeLayout) convertView.findViewById(R.id.info_folder);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 end
					holder.type = 0;
					convertView.setTag(holder);
				}
				String body = exportItem.getTitle();
				// gionee 20121231 pengwei add begin
				if (body != null) {
					if (body.length() > 9) {
						holder.title.setText(body.substring(0, 10).replace("\n", " ") + "...");
					} else {
						holder.title.setText(body.replace("\n", " "));
					}
				}else{
					holder.title.setText("");
				}
				// gionee 20121231 pengwei add end
				holder.subNum.setText("(" + exportItem.getSubNum() + ")");
				holder.updateDate.setText(exportItem.getUpdateDate());
				holder.checked.setChecked(exportItem.isChecked());
				holder.folderBackground.setBackgroundResource(R.drawable.item_list_folder_selector);
				
				// gionee lilg 2013-01-23 add for GUI style begin
				//holder.title.setTextColor(context.getResources().getColor(R.color.note_grid_content_yellow));
				//holder.subNum.setTextColor(context.getResources().getColor(R.color.note_grid_content_yellow));
				//holder.updateDate.setTextColor(context.getResources().getColor(R.color.note_grid_time_yellow));
				// gionee lilg 2013-01-23 add for GUI style end
			} else {
				// is note
				Log.i("ExportItemsAdapter------is note");
				Log.i("ExportItemsAdapter------note info: "	+ exportItem.toString());
				if (convertView != null	&& (holder = (ItemHolder) convertView.getTag()).type == 1) {
					holder = (ItemHolder) convertView.getTag();
				} else {
					convertView = inflater.inflate(R.layout.export_item_select_item_note_layout, null);
					holder = new ItemHolder();
					holder.title = (TextView) convertView.findViewById(R.id.title);
					holder.updateDate = (TextView) convertView.findViewById(R.id.update_date);
					holder.checked = (CheckBox) convertView.findViewById(R.id.checkbox);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 begin
					holder.noteBackground = (RelativeLayout) convertView.findViewById(R.id.info_note);
		            // Gionee <wangpan><2014-05-15> modify for CR01249465 end
					holder.type = 1;
					convertView.setTag(holder);
				}
				// gn lilg 2013-02-27 add for delete the media info in the note content begin
//				holder.title.setText(exportItem.getTitle());
				String noteContent = CommonUtils.noteContentPreDeal(exportItem.getTitle());
				holder.title.setText(noteContent);
				// gn lilg 2013-02-27 add for delete the media info in the note content end
				
				holder.updateDate.setText(exportItem.getUpdateDate());
				holder.checked.setChecked(exportItem.isChecked());
				holder.noteBackground.setBackgroundResource(getBackground(exportItem.getBgColor()));
				
				// gionee lilg 2013-01-23 add for GUI style begin
				//holder.title.setTextColor(ResourceParser.getNoteGridContentColor(Integer.parseInt(exportItem.getBgColor())));
				//holder.updateDate.setTextColor(ResourceParser.getNoteGridTimeColor(Integer.parseInt(exportItem.getBgColor())));
				// gionee lilg 2013-01-23 add for GUI style end
			}
		}

		Log.i("ExportItemsAdapter------getView end!");
		return convertView;
	}

	private int getBackground(String bgColor) {
		Log.i("ExportItemsAdapter------bgColor: " + bgColor);

		if (bgColor == null || "".equals(bgColor)) {
			return ResourceParser.exportBgRes.getBgResource(ResourceParser.YELLOW);
		}

		int color = ResourceParser.YELLOW;
		try {
			color = Integer.parseInt(bgColor);
		} catch (Exception e) {
			Log.e("ExportItemsAdapter------error: " + e.getMessage());
		}

		if (color < ResourceParser.YELLOW || color > ResourceParser.RED) {
			color = ResourceParser.YELLOW;
		}

		return ResourceParser.exportBgRes.getBgResource(color);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
