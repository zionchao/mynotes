package com.gionee.note.content;

import android.content.Context;

import com.gionee.note.R;

public class ResourceParser {

	public static final int YELLOW           = 0;
//	public static final int BLUE             = 1;
//	public static final int GREEN            = 2;
	public static final int RED              = 3;

	private static int BG_DEFAULT_COLOR = YELLOW;

	public static class NoteBgResources {
		private final static int [] BG_EDIT_RESOURCES_WHITE = new int [] {
			R.drawable.edit_yellow_white,
			R.drawable.edit_blue_white,
			R.drawable.edit_green_white,
			R.drawable.edit_red_white
		};
		
		private final static int [] BOTTOM_BG_EDIT_RESOURCES_WHITE = new int [] {
			R.drawable.edit_yellow_white_bottom,
			R.drawable.edit_blue_white_bottom,
			R.drawable.edit_green_white_bottom,
			R.drawable.edit_red_white_bottom
		};
		private final static int [] BG_EDIT_TOP_WHITE = new int [] {
			R.drawable.note_time_location_yellow_bg,
			R.drawable.note_time_location_blue_bg,
			R.drawable.note_time_location_green_bg,
			R.drawable.note_time_location_red_bg
		};

		//        private final static int [] BG_EDIT_TITLE_RESOURCES = new int [] {
			//            R.drawable.edit_title_yellow,
			//            R.drawable.edit_title_blue,
		//            R.drawable.edit_title_green,
		//            R.drawable.edit_title_red
		//        };

		public static int getNoteBgResourceWhite(int id) {
			return BG_EDIT_RESOURCES_WHITE[id];
		}

		public static int getNoteBgBottomResourceWhite(int id){
			return BOTTOM_BG_EDIT_RESOURCES_WHITE[id];
		}
		
		public static int getNoteBgTopWhite(int id) {
			return BG_EDIT_TOP_WHITE[id];
		}

		//        public static int getNoteTitleBgResource(int id) {
		//            return BG_EDIT_TITLE_RESOURCES[id];
		//        }
	}

	public static int getDefaultBgId(Context context) {
		//        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
		//                NotesPreferenceActivity.PREFERENCE_SET_BG_COLOR_KEY, false)) {
		//            return (int) (Math.random() * NoteBgResources.BG_EDIT_RESOURCES.length);
		//        } else {
		//            return BG_DEFAULT_COLOR;
		//        }
		return BG_DEFAULT_COLOR;
	}

	public static class NoteItemBgResources {
		//        private final static int [] BG_FIRST_RESOURCES = new int [] {
		//            R.drawable.list_yellow_up,
		//            R.drawable.list_blue_up,
		//            R.drawable.list_white_up,
		//            R.drawable.list_green_up,
		//            R.drawable.list_red_up
		//        };


		private final static int [] HOME_NOTE_CONTENT_BG_NORMAL_RESOURCES_WHITE = new int [] {
			// gionee lilg 2013-01-15 modify begin
//			R.drawable.home_grid_note_content_yellow_white,
//			R.drawable.home_grid_note_content_blue_white,
//			R.drawable.home_grid_note_content_green_white,
//			R.drawable.home_grid_note_content_pink_white,
			R.drawable.item_grid_note_content_yellow_selector,
			R.drawable.item_grid_note_content_blue_selector,
			R.drawable.item_grid_note_content_green_selector,
			R.drawable.item_grid_note_content_pink_selector
			// gionee lilg 2013-01-15 modify end
		};

		private final static int [] HOME_NOTE_TITLE_BG_NORMAL_RESOURCES_WHITE = new int [] {
			// gionee lilg 2013-01-15 modify begin
//			R.drawable.home_grid_note_title_yellow_white,
//			R.drawable.home_grid_note_title_blue_white,
//			R.drawable.home_grid_note_title_green_white,
//			R.drawable.home_grid_note_title_pink_white,
			R.drawable.item_grid_note_title_yellow_selector,
			R.drawable.item_grid_note_title_blue_selector,
			R.drawable.item_grid_note_title_green_selector,
			R.drawable.item_grid_note_title_pink_selector
			// gionee lilg 2013-01-15 modify end
		};

		private final static int [] HOME_listview_NOTE_BG_NORMAL_RESOURCES_WHITE = new int [] {
			// gionee lilg 2013-01-15 modify begin
//			R.drawable.note_mini_yellow_icon_white,
//			R.drawable.note_mini_blue_icon_white,
//			R.drawable.note_mini_green_icon_white,
//			R.drawable.note_mini_pink_icon_white,
			R.drawable.item_list_note_yellow_selector,
			R.drawable.item_list_note_blue_selector,
			R.drawable.item_list_note_green_selector,
			R.drawable.item_list_note_pink_selector
			// gionee lilg 2013-01-15 modify end
		};

		public static int getHomeListviewNoteBgNormalResourcesWhite(int id) {
			return HOME_listview_NOTE_BG_NORMAL_RESOURCES_WHITE[id];
		}


		//		private final static int [] BG_LAST_RESOURCES = new int [] {
			//            R.drawable.list_yellow_down,
			//            R.drawable.list_blue_down,
			//            R.drawable.list_green_down,
			//            R.drawable.list_red_down,
			//        };
		//
		//        private final static int [] BG_SINGLE_RESOURCES = new int [] {
		//            R.drawable.list_yellow_single,
		//            R.drawable.list_blue_single,
		//            R.drawable.list_green_single,
		//            R.drawable.list_red_single
		//        };
		//
		//        public static int getNoteBgFirstRes(int id) {
		//            return BG_FIRST_RESOURCES[id];
		//        }

		//        public static int getNoteBgLastRes(int id) {
		//            return BG_LAST_RESOURCES[id];
		//        }
		//
		//        public static int getNoteBgSingleRes(int id) {
		//            return BG_SINGLE_RESOURCES[id];
		//        }


		public static int getHomeNoteContentBgNormalResWhite(int id) {
			return HOME_NOTE_CONTENT_BG_NORMAL_RESOURCES_WHITE[id];
		}

		public static int getHomeNotetTitleBgNormalResWhite(int id) {
			return HOME_NOTE_TITLE_BG_NORMAL_RESOURCES_WHITE[id];
		}


		//        public static int getFolderBgRes() {
			//            return R.drawable.list_folder;
		//        }
	}

	public static class WidgetBgResources {


//		private final static int [] BG_2X_RESOURCES_WHITE = new int [] {
//			R.drawable.widget_2x_yellow_white,
//			R.drawable.widget_2x_blue_white,
//			R.drawable.widget_2x_green_white,
//			R.drawable.widget_2x_pink_white
//		};
		
		private final static int [] BG_2X_RESOURCES_WHITE = new int [] {
		R.drawable.widget_2x_yellow_white_2x,
		R.drawable.widget_2x_blue_white_2x,
		R.drawable.widget_2x_green_white_2x,
		R.drawable.widget_2x_pink_white_2x
	};
		public static int getWidget2xBgResourceWhite(int id) {
			return BG_2X_RESOURCES_WHITE[id];
		}

		//



		
	}

	

	public static class exportBgRes {
		private final static int [] BG_RESOURCES = new int [] {
			// gionee lilg 2013-01-15 modify begin
//			R.drawable.gn_com_list_note_bg_yellow,
//			R.drawable.gn_com_list_note_bg_blue,
//			R.drawable.gn_com_list_note_bg_green,
//			R.drawable.gn_com_list_note_bg_pink,
			R.drawable.item_list_note_yellow_selector,
			R.drawable.item_list_note_blue_selector,
			R.drawable.item_list_note_green_selector,
			R.drawable.item_list_note_pink_selector
			// gionee lilg 2013-01-15 modify end
		};
		public static int getBgResource(int id) {
			return BG_RESOURCES[id];
		}
	}
	
	
	// gionee 20121226 jiating modify for theme begin
	private final static int[] NOTE_MOVE_TITLE_BG = new int[] {
			R.drawable.home_grid_note_title_yellow_move_white,
			R.drawable.home_grid_note_title_blue_move_white,
			R.drawable.home_grid_note_title_green_move_white,
			R.drawable.home_grid_note_title_pink_move_white };

	public static int getNoteMoveTitleBg(int id) {
		return NOTE_MOVE_TITLE_BG[id];
	}
	// gionee 20121226 jiating modify for theme end
	//gionee 20121226 jiating modify for theme begin
	private final static int [] NOTE_MOVE_CONTENT_BG = new int [] {
		R.drawable.home_grid_note_content_yellow_move_white,
		R.drawable.home_grid_note_content_blue_move_white,
		R.drawable.home_grid_note_content_green_move_white,
		R.drawable.home_grid_note_content_pink_move_white
	};
	
	public static int getNoteMoveContentBg(int id){
		return NOTE_MOVE_CONTENT_BG[id];
	}
	//gionee 20121226 jiating modify for theme end
	
	//gionee 20121226 jiating modify for theme begin
	private final static int [] NOTE_LIST_CONTENT_COLOR = new int [] {
		R.color.note_list_content_yellow,
		R.color.note_list_content_blue,
		R.color.note_list_content_green,
		R.color.note_list_content_pink
	};
	
	public static int getNotelistContentColor(int id){
		return NOTE_LIST_CONTENT_COLOR[id];
	}
	//gionee 20121226 jiating modify for theme end
	//gionee 20121226 jiating modify for theme begin
	private final static int [] NOTE_LIST_TIME_COLOR = new int [] {
		R.color.note_list_time_yellow,
		R.color.note_list_time_blue,
		R.color.note_list_time_green,
		R.color.note_list_time_pink
	};
	
	public static int getNotelistTimeColor(int id){
		return NOTE_LIST_TIME_COLOR[id];
	}
	//gionee 20121226 jiating modify for theme end
	
	//gionee 20121226 jiating modify for theme begin
	private final static int [] NOTE_GRID_CONTENT_COLOR = new int [] {
		R.color.note_grid_content_yellow,
		R.color.note_grid_content_blue,
		R.color.note_grid_content_green,
		R.color.note_grid_content_pink
	};
	
	public static int getNoteGridContentColor(int id){
		return NOTE_GRID_CONTENT_COLOR[id];
	}
	//gionee 20121226 jiating modify for theme end
	
	//gionee 20121226 jiating modify for theme begin
	private final static int [] NOTE_GRID_TIME_COLOR = new int [] {
		R.color.note_grid_time_yellow,
		R.color.note_grid_time_blue,
		R.color.note_grid_time_green,
		R.color.note_grid_time_pink
	};
	
	public static int getNoteGridTimeColor(int id){
		return NOTE_GRID_TIME_COLOR[id];
	}
	//gionee 20121226 jiating modify for theme end

}
