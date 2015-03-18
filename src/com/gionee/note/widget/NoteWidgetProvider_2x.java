/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gionee.note.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;


import com.gionee.note.R;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.Notes;
import com.gionee.note.content.ResourceParser;
import com.gionee.note.utils.Log;

public class NoteWidgetProvider_2x extends NoteWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i("NoteWidgetProvider_2x------onUpdate!");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	protected int getLayoutId() {
		Log.i("NoteWidgetProvider_2x------get layout id: widget_2x!");
		return R.layout.widget_2x;
	}

	@Override
	protected int getBgResourceId(int bgId) {
		Log.d("NoteWidgetProvider_2x------get bg resource id: " + bgId + "!");

		return ResourceParser.WidgetBgResources.getWidget2xBgResourceWhite(bgId);
	}

	@Override
	protected int getWidgetType() {
		Log.i("NoteWidgetProvider_2x------get widget type: widget_2x!");
		return Notes.TYPE_WIDGET_2X;
	}

}
