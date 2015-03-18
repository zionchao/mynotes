package com.gionee.note.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;


import com.gionee.note.content.Constants;
import com.gionee.note.domain.MediaInfo;
import com.gionee.note.domain.Note;
import com.gionee.note.noteMedia.record.NoteMediaManager;
import com.gionee.note.utils.Log;

public class DBOperations {

	DBOpenHelper helper = null;

	private static DBOperations operations;

	private DBOperations(){
		
	}
	
	
	// gn lilg 2012-12-08 add for optimization begin
	private DBOperations(Context context) {
		if(helper == null){
			helper = new DBOpenHelper(context);
		}
	}
	
	public synchronized static DBOperations getInstances(Context context){
		if (operations == null) {
			operations = new DBOperations(context);
		}
		return operations;
	}
	
	public synchronized static void release(){
		if(operations != null){
			operations = null;
		}
	}

	private SQLiteDatabase getDatabase(Context context){
		SQLiteDatabase db = null;
		try{
			if(helper == null){
				Log.d("DBOperations------helper == null!");
				helper = new DBOpenHelper(context);
			}
			db = helper.getWritableDatabase();
		}catch(SQLiteException e){
			Log.e("DBOperations------get database exception!", e);
			if(null != helper){
			    db = helper.getReadableDatabase();
			}
		}catch(Exception ex){
			Log.e("DBOperations------get database exception!", ex);
		}
		return db;
	}
	// gn lilg 2012-12-08 add for optimization end
	
	private synchronized void close(SQLiteDatabase db) {
		if (db != null) {
			db.close();
		}
	}

	private void close(Cursor c) {
		if (c != null) {
			c.close();
		}
	}

	/**
	 * 
	 * @param context
	 *            the context of the application
	 * @param note
	 *            the new note will be created
	 * @return
	 */
	public synchronized long createNote(Context context, Note note) {
		Log.i("DBOperation------createNote start!");
		Log.d("DBOperation------insert: " + note.toString());

		long id = -1;
		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, createNote fail, return -1!");
			return id;
		}
		// gn lilg 2012-12-08 modify for optimization end

		ContentValues cv = new ContentValues();
		cv.put(DBOpenHelper.CONTENT, note.getContent());
		cv.put(DBOpenHelper.UPDATE_DATE, note.getUpdateDate());
		cv.put(DBOpenHelper.UPDATE_TIME, note.getUpdateTime());
		cv.put(DBOpenHelper.ALARM_TIME, note.getAlarmTime());
		cv.put(DBOpenHelper.BG_COLOR, note.getBgColor());
		cv.put(DBOpenHelper.IS_FOLDER, note.getIsFolder());
		cv.put(DBOpenHelper.PARENT_FOLDER, note.getParentFile());
		cv.put(DBOpenHelper.NOTE_FONT_SIZE, note.getNoteFontSize());
		cv.put(DBOpenHelper.NOTE_LIST_MODE, note.getNoteListMode());
		cv.put(DBOpenHelper.WIDGET_ID, note.getWidgetId());
		cv.put(DBOpenHelper.WIDGET_TYPE, note.getWidgetType());
		cv.put(DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS, note.getHaveNoteCount());
		cv.put(DBOpenHelper.NOTE_TITLE, note.getTitle());
		cv.put(DBOpenHelper.MEDIA_FOLDER_NAME, note.getMediaFolderName());
		cv.put(DBOpenHelper.NOTE_MEDIA_TYPE, note.getNoteMediaType());
		cv.put(DBOpenHelper.ADDRESS_NAME, note.getAddressName());
		cv.put(DBOpenHelper.ADDRESS_DETAIL, note.getAddressDetail());
		db.beginTransaction();
		try {

			id = db.insert(DBOpenHelper.TABLE_NAME, DBOpenHelper.CONTENT,
					cv);
			if (!Constants.PARENT_FILE_ROOT.equals(note.getParentFile())) {
				String updateSQL = " update " + DBOpenHelper.TABLE_NAME
				+ " set " + DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS
				+ " = " + DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS
				+ " + 1 where " + DBOpenHelper.ID + " = "
				+ note.getParentFile();

				Log.d("DBOperations------update sql: " + updateSQL);

				db.execSQL(updateSQL);
			}

			if (Constants.NOTE_NO_MEDIA!=note.getNoteMediaType()) {
				for(MediaInfo mediaInfo: note.getMediaInfos()){
					createMedia(id, mediaInfo, db);
				}
			} 
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------create note error", e);
		} finally {
			db.endTransaction();
			close(db);
		}

		Log.i("DBOperation------createNote end!");
		return id;
	}

	public synchronized void deleteNote(Context context, Note note) {
		Log.i("DBOperations------delete note start!");
		Log.d("DBOperations------delete,id:" + note);

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, deleteNote fail, return!");
			return;
		}
		// gn lilg 2012-12-08 modify for optimization end

		String id = String.valueOf(note.getId());

		db.beginTransaction();
		try {

			if (Constants.IS_FOLDER.equals(note.getIsFolder())) {
				db.delete(DBOpenHelper.TABLE_NAME,
						DBOpenHelper.ID + " = ? or "
						+ DBOpenHelper.PARENT_FOLDER + " = ? ",
						new String[] { id, id });
			} else {
				
				// Gionee <lilg><2013-04-16> modify for CR00795403 begin
				int deleteNum = db.delete(DBOpenHelper.TABLE_NAME, DBOpenHelper.ID + " = ? ", new String[] { id });
				Log.d("DBOperations------delete num: " + deleteNum);

				if(deleteNum != 0){
					if (!Constants.PARENT_FILE_ROOT.equals(note.getParentFile())) {
						String updateSQL = " update " + DBOpenHelper.TABLE_NAME	+ " set " + DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS + " = " + DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS	+ " - 1 where " + DBOpenHelper.ID + " = " + note.getParentFile();

						Log.d("DBOperations------update sql: " + updateSQL);

						db.execSQL(updateSQL);
					}
				}
				// Gionee <lilg><2013-04-16> modify for CR00795403 end
			}

			if (Constants.NOTE_NO_MEDIA!=note.getNoteMediaType()) {
				db.delete(DBOpenHelper.MEIDA_TABLENAME, DBOpenHelper.NOTE_ID
						+ " = ? ", new String[] { id });
			} 
			
			// gn lilg 2013-03-04 add for delete media items with the noteId begin
			db.delete(DBOpenHelper.MEIDA_TABLENAME, DBOpenHelper.NOTE_ID + " = ? ", new String[]{ id });
			// gn lilg 2013-03-04 add for delete media items with the noteId end
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------delete note error: " + e.toString());
		} finally {
			db.endTransaction();
			close(db);
		}
		Log.i("DBOperations------delete note end!");
	}

	public synchronized void updateNote(Context context, Note note) {
		Log.i("DBOperations------update note start!");
		Log.d("DBOperations------updateNote: " + note.toString());

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, updateNote fail, return!");
			return;
		}
		// gn lilg 2012-12-08 modify for optimization end

		ContentValues cv = new ContentValues();
		cv.put(DBOpenHelper.CONTENT, note.getContent());
		cv.put(DBOpenHelper.UPDATE_DATE, note.getUpdateDate());
		cv.put(DBOpenHelper.UPDATE_TIME, note.getUpdateTime());
		cv.put(DBOpenHelper.ALARM_TIME, note.getAlarmTime());
		cv.put(DBOpenHelper.BG_COLOR, note.getBgColor());
		cv.put(DBOpenHelper.IS_FOLDER, note.getIsFolder());
		cv.put(DBOpenHelper.PARENT_FOLDER, note.getParentFile());
		cv.put(DBOpenHelper.NOTE_FONT_SIZE, note.getNoteFontSize());
		cv.put(DBOpenHelper.NOTE_LIST_MODE, note.getNoteListMode());
		cv.put(DBOpenHelper.WIDGET_ID, note.getWidgetId());
		cv.put(DBOpenHelper.WIDGET_TYPE, note.getWidgetType());
		cv.put(DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS, note.getHaveNoteCount());
		cv.put(DBOpenHelper.NOTE_TITLE, note.getTitle());
		cv.put(DBOpenHelper.MEDIA_FOLDER_NAME, note.getMediaFolderName());
		cv.put(DBOpenHelper.NOTE_MEDIA_TYPE, note.getNoteMediaType());
		cv.put(DBOpenHelper.ADDRESS_NAME, note.getAddressName());
		cv.put(DBOpenHelper.ADDRESS_DETAIL, note.getAddressDetail());
		String whereClause = " _id = ? ";
		String[] whereArgs = new String[] { String.valueOf(note.getId()) };

		try {
			db.beginTransaction();
			db.update(DBOpenHelper.TABLE_NAME, cv, whereClause, whereArgs);
			if (Constants.NOTE_NO_MEDIA!=note.getNoteMediaType()) {

				//TODO jiating
				for(MediaInfo mediaInfo: note.getMediaInfos()){
					if(null==mediaInfo.getId()){
						createMedia(Long.parseLong(note.getId()), mediaInfo, db);

					}else if(Constants.MEDIA_DELETED==mediaInfo.getIsDelete()){
						db.delete(DBOpenHelper.MEIDA_TABLENAME, DBOpenHelper.MEDIA_ID + " = ? or "
								+DBOpenHelper.NOTE_ID
								+ " = ? ", new String[] {mediaInfo.getId(), note.getId() });
					}
				}

			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------update error: " + e.toString());
		} finally {
			db.endTransaction();
			close(db);
		}
		Log.i("DBOperations------update note end!");
	}

	//gn pengwei 20130301 add for CR00774669 begin
	public synchronized void updateNoteField(Context context, Note note,String insertField) {
		Log.i("DBOperations------update note start!");
		Log.d("DBOperations------updateNoteField: " + note.toString());

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
	try {
		if(db == null){
			Log.e("DBOperation------db == null, updateNote fail, return!");
			return;
		}
		// gn lilg 2012-12-08 modify for optimization end

		ContentValues cv = new ContentValues();
		cv.put(insertField, note.getAlarmTime());
		String whereClause = " _id = ? ";
		String[] whereArgs = new String[] { String.valueOf(note.getId()) };
			db.update(DBOpenHelper.TABLE_NAME, cv, whereClause, whereArgs);
		} catch (Exception e) {
			Log.e("DBOperations------update error: " + e.toString());
		} finally {
			close(db);
		}
		Log.i("DBOperations------update note end!");
	}
	//gn pengwei 20130301 add for CR00774669 end
	
	// Gionee <lilg><2013-03-13> add for update one note begin
	public synchronized void updateNote(Context context, ContentValues cv, String whereClause, String[] whereArgs) {
		Log.i("DBOperations------update note start!");
		Log.d("DBOperations------ContentValues: " + cv.toString());

		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, updateNote fail, return!");
			return;
		}

		try {
			db.beginTransaction();
			db.update(DBOpenHelper.TABLE_NAME, cv, whereClause, whereArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------update error: " + e.toString());
		} finally {
			db.endTransaction();
			close(db);
		}
		Log.i("DBOperations------update note end!");
	}
	// Gionee <lilg><2013-03-13> add for update one note end
	
	public synchronized void updateWidget(Context context, int appWidgetId,
			ContentValues values) {
		Log.i("DBOperations------update widget info start!" );

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, updateWidget fail, return!");
			return;
		}
		// gn lilg 2012-12-08 modify for optimization end

		String whereClause = DBOpenHelper.WIDGET_ID + " = ? ";
		String[] whereArgs = new String[] { String.valueOf(appWidgetId) };
		try {
			db.update(DBOpenHelper.TABLE_NAME, values, whereClause, whereArgs);
		} catch (Exception e) {
			Log.e("DBOperations------updateWidget error" );
		}
		close(db);
		Log.i("DBOperations------update widget info end!" );
	}

	public synchronized Note queryOneNote(Context context, int _id) {
		Log.i("DBOperations------query one note start!");
		Log.d("DBOperations------queryOneNote,id:" + _id);

		Note note = new Note();
		
		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryOneNote fail!");
			return note;
		}
		// gn lilg 2012-12-08 modify for optimization end
		
		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };
		Cursor c = null;
		try {
			c = db.query(DBOpenHelper.TABLE_NAME, columns, " _id = ? ",
					new String[] { String.valueOf(_id) }, null, null, null);
		} catch (Exception e) {
			Log.e("DBOperations------queryOneNote error :" + e);
			close(c);
		}

		if (c != null && c.moveToFirst() != false) {
			note.setId(c.getString(c.getColumnIndex(DBOpenHelper.ID)));
			note.setContent(c.getString(c.getColumnIndex(DBOpenHelper.CONTENT)));
			note.setUpdateDate(c.getString(c
					.getColumnIndex(DBOpenHelper.UPDATE_DATE)));
			note.setUpdateTime(c.getString(c
					.getColumnIndex(DBOpenHelper.UPDATE_TIME)));
			note.setAlarmTime(c.getString(c
					.getColumnIndex(DBOpenHelper.ALARM_TIME)));
			note.setBgColor(c.getString(c.getColumnIndex(DBOpenHelper.BG_COLOR)));
			note.setIsFolder(c.getString(c
					.getColumnIndex(DBOpenHelper.IS_FOLDER)));
			note.setParentFile(c.getString(c
					.getColumnIndex(DBOpenHelper.PARENT_FOLDER)));
			note.setNoteFontSize(c.getString(c
					.getColumnIndex(DBOpenHelper.NOTE_FONT_SIZE)));
			note.setNoteListMode(c.getString(c
					.getColumnIndex(DBOpenHelper.NOTE_LIST_MODE)));
			note.setWidgetId(c.getString(c
					.getColumnIndex(DBOpenHelper.WIDGET_ID)));
			note.setWidgetType(c.getString(c
					.getColumnIndex(DBOpenHelper.WIDGET_TYPE)));

			note.setHaveNoteCount(Integer.parseInt(c.getString(c
					.getColumnIndex(DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS))));
			note.setTitle(c.getString(c.getColumnIndex(DBOpenHelper.NOTE_TITLE)));

			note.setMediaFolderName(c.getString(c.getColumnIndex(DBOpenHelper.MEDIA_FOLDER_NAME)));

			note.setNoteMediaType(c.getInt(c.getColumnIndex(DBOpenHelper.NOTE_MEDIA_TYPE)));
			if(Constants.NOTE_NO_MEDIA!=c.getInt(c.getColumnIndex(DBOpenHelper.NOTE_MEDIA_TYPE))){
				//TODO jiating 
				note.setMediaInfos(queryMeidas(_id, db));
			}
			note.setAddressName(c.getString(c.getColumnIndex(DBOpenHelper.ADDRESS_NAME)));
			note.setAddressDetail(c.getString(c.getColumnIndex(DBOpenHelper.ADDRESS_DETAIL)));
		}
		this.close(c);
		this.close(db);

		Log.i("DBOperations------query one note end!");
		return note;
	}
	
	/**
	 * query note by note id
	 * @param db
	 * @param noteId
	 * @return
	 */
	private synchronized Note queryNoteById(SQLiteDatabase db, int noteId){
		Log.i("DBOperations------queryNoteById begin!");
		
		Note note = new Note();
		if(db == null){
			Log.e("DBOperation------db == null, queryNoteById fail!");
			return note;
		}
		
		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };
		Cursor c = null;
		try {
			c = db.query(DBOpenHelper.TABLE_NAME, columns, " _id = ? ",	new String[] { String.valueOf(noteId) }, null, null, null);
		} catch (Exception e) {
			Log.e("DBOperations------queryNoteById error :" + e);
			close(c);
		}

		if (c != null && c.moveToFirst() != false) {
			note.setId(c.getString(c.getColumnIndex(DBOpenHelper.ID)));
			note.setContent(c.getString(c.getColumnIndex(DBOpenHelper.CONTENT)));
			note.setUpdateDate(c.getString(c.getColumnIndex(DBOpenHelper.UPDATE_DATE)));
			note.setUpdateTime(c.getString(c.getColumnIndex(DBOpenHelper.UPDATE_TIME)));
			note.setAlarmTime(c.getString(c.getColumnIndex(DBOpenHelper.ALARM_TIME)));
			note.setBgColor(c.getString(c.getColumnIndex(DBOpenHelper.BG_COLOR)));
			note.setIsFolder(c.getString(c.getColumnIndex(DBOpenHelper.IS_FOLDER)));
			note.setParentFile(c.getString(c.getColumnIndex(DBOpenHelper.PARENT_FOLDER)));
			note.setNoteFontSize(c.getString(c.getColumnIndex(DBOpenHelper.NOTE_FONT_SIZE)));
			note.setNoteListMode(c.getString(c.getColumnIndex(DBOpenHelper.NOTE_LIST_MODE)));
			note.setWidgetId(c.getString(c.getColumnIndex(DBOpenHelper.WIDGET_ID)));
			note.setWidgetType(c.getString(c.getColumnIndex(DBOpenHelper.WIDGET_TYPE)));

			note.setHaveNoteCount(Integer.parseInt(c.getString(c.getColumnIndex(DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS))));
			note.setTitle(c.getString(c.getColumnIndex(DBOpenHelper.NOTE_TITLE)));

			note.setMediaFolderName(c.getString(c.getColumnIndex(DBOpenHelper.MEDIA_FOLDER_NAME)));

			note.setNoteMediaType(c.getInt(c.getColumnIndex(DBOpenHelper.NOTE_MEDIA_TYPE)));
			if(Constants.NOTE_NO_MEDIA!=c.getInt(c.getColumnIndex(DBOpenHelper.NOTE_MEDIA_TYPE))){
				note.setMediaInfos(queryMeidas(noteId, db));
			}

		}
		this.close(c);
		
		Log.i("DBOperations------queryNoteById end!");
		return note;
	}

	public synchronized Note queryNoteByFolderTitle(Context context,
			String folderTitle) {
		Log.i("DBOperations------query note by folder title start!");
		Log.d("DBOperations------queryNoteByFolderTitle, folderTitle:" + folderTitle);

		Note note = new Note();
		
		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryNoteByFolderTitle fail!");
			return note;
		}
		// gn lilg 2012-12-08 modify for optimization end
		
		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };
		Cursor c = null;

		try {
			c = db.query(DBOpenHelper.TABLE_NAME, columns,
					DBOpenHelper.IS_FOLDER + " = ? and "
					+ DBOpenHelper.NOTE_TITLE + " = ? ", new String[] {
					Constants.IS_FOLDER, String.valueOf(folderTitle) },
					null, null, null);
		} catch (Exception e) {
			this.close(c);
		}

		if (c != null && c.moveToFirst() != false) {
			note.setId(c.getString(c.getColumnIndex(DBOpenHelper.ID)));
			note.setHaveNoteCount(Integer.parseInt(c.getString(c
					.getColumnIndex(DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS))));
		}

		this.close(c);
		this.close(db);
		Log.i("DBOperations------query note by folder title end!");
		return note;
	}

	public synchronized List<Note> queryFromFolder(Context context, int _id) {
		Log.v("DBOperations------queryFromFolder,id:" + _id);

		List<Note> noteList = new ArrayList<Note>();
		
		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryFromFolder fail!");
			return noteList;
		}
		// gn lilg 2012-12-08 modify for optimization end

		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };
		String orderBy = DBOpenHelper.UPDATE_DATE + " desc ,"
		+ DBOpenHelper.UPDATE_TIME + " desc";

		Cursor cursor = null;
		try {
			cursor = db.query(DBOpenHelper.TABLE_NAME, columns,
					DBOpenHelper.PARENT_FOLDER + "  = ? ",
					new String[] { String.valueOf(_id) }, null, null, orderBy);
		} catch (Exception e) {
			Log.e("DBOperations------queryFromFolder error");
		}

		cursorToNote(cursor, noteList);

		this.close(cursor);
		this.close(db);
		return noteList;
	}

	

	public synchronized List<Note> queryNoteByWidgetId(Context context,
			int widgetId) {
		Log.d("DBOperations------queryNoteByWidgetId, widgetId:" + widgetId);

		List<Note> noteList = new ArrayList<Note>();

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryNoteByWidgetId fail!");
			return noteList;
		}
		// gn lilg 2012-12-08 modify for optimization end

		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };
		Cursor cursor = null;
		try {
			cursor = db
			.query(DBOpenHelper.TABLE_NAME, columns, " widgetId = ? ",
					new String[] { String.valueOf(widgetId) }, null,
					null, null);

		} catch (Exception e) {
			Log.e("DBOperations------queryNoteByWidgetId error");
		}

		cursorToNote(cursor, noteList);

		this.close(cursor);
		this.close(db);
		return noteList;
	}

	

	public synchronized String getDate() {
		Log.i("DBOperations------getDate!");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}

	public synchronized String getTime() {
		Log.i("DBOperations------getTime!");

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(new Date());
	}

	private synchronized void cursorToNoteForNote(Cursor cursor, List<Note> noteList) {
		if (cursor != null && cursor.moveToFirst() != false) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
			.moveToNext()) {
				Note note = new Note();
				note.setId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ID)));
				noteList.add(note);
			}
		}
	}

	private synchronized void cursorToNote(Cursor cursor, List<Note> noteList) {
		if (cursor != null && cursor.moveToFirst() != false) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
			.moveToNext()) {
				Note note = new Note();
				note.setId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ID)));
				note.setContent(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.CONTENT)));
				note.setUpdateDate(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.UPDATE_DATE)));
				note.setUpdateTime(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.UPDATE_TIME)));
				note.setAlarmTime(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ALARM_TIME)));
				note.setBgColor(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.BG_COLOR)));
				note.setIsFolder(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.IS_FOLDER)));
				note.setParentFile(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.PARENT_FOLDER)));
				note.setNoteFontSize(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.NOTE_FONT_SIZE)));
				note.setNoteListMode(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.NOTE_LIST_MODE)));

				note.setWidgetId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.WIDGET_ID)));
				note.setWidgetType(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.WIDGET_TYPE)));

				note.setHaveNoteCount(Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS))));
				note.setTitle(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.NOTE_TITLE)));
				note.setMediaFolderName(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.MEDIA_FOLDER_NAME)));
				note.setNoteMediaType(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.NOTE_MEDIA_TYPE)));
				//				if(!Constants.MEDIA_FOLDER_NAME.equals(cursor.getString(cursor
				//						.getColumnIndex(DBOpenHelper.MEDIA_FOLDER_NAME)))){
				//					//TODO jiating
				//					
				//					note.setMediaInfos(queryMeidas( ))
				//				}
				note.setAddressName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ADDRESS_NAME)));
				note.setAddressDetail(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ADDRESS_DETAIL)));
				noteList.add(note);
			}
		}
	}

	//Gionee <pengwei><20130824> modify for CR00834587 begin
	public synchronized void moveOneNote(Context context, Note note, Note toNote) {
		Log.i("DBOperations------move one note start!");

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, moveOneNote fail!");
			return;
		}
		// gn lilg 2012-12-08 modify for optimization end

		// Gionee <lilg><2013-04-16> add for CR00795403 begin
		if(note == null){
			Log.e("DBOperation------note == null!");
			return;
		}
		int _id = 0;
		try{
			_id = Integer.parseInt(note.getId());
		}catch(Exception e){
			Log.e("DBOperation------note id: " + note.getId(), e);
			return;
		}
		// Gionee <lilg><2013-04-16> add for CR00795403 end
		
		db.beginTransaction();
		try {
			
			// Gionee <lilg><2013-04-16> modify for CR00795403 begin
			Note tmpNote = queryNoteById(db, _id);
			
			if (toNote == null) {
				if (!Constants.PARENT_FILE_ROOT.equals(tmpNote.getParentFile())){

					String updateSQL = " update " + DBOpenHelper.TABLE_NAME
					+ " set " + DBOpenHelper.PARENT_FOLDER + " = '"
					+ Constants.PARENT_FILE_ROOT + "' where "
					+ DBOpenHelper.ID + " = " + note.getId();
					String updatePreParentSQL = " update "
						+ DBOpenHelper.TABLE_NAME + " set "
						+ DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS + " = "
						+ DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS + " - 1 where "
						+ DBOpenHelper.ID + " = " + note.getParentFile();

					Log.d("DBOperations------update sql: " + updateSQL);
					Log.d("DBOperations------update preparent sql: " + updatePreParentSQL);

					db.execSQL(updateSQL);
					db.execSQL(updatePreParentSQL);
				}
			} else {
				if(!toNote.getId().equals(tmpNote.getParentFile())){

					String updateSQL = " update " + DBOpenHelper.TABLE_NAME
					+ " set " + DBOpenHelper.PARENT_FOLDER + " = "
					+ toNote.getId() + " where " + DBOpenHelper.ID + " = "
					+ note.getId();
					String updatePreParentSQL = " update "
						+ DBOpenHelper.TABLE_NAME + " set "
						+ DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS + " = "
						+ DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS + " - 1 where "
						+ DBOpenHelper.ID + " = '" + note.getParentFile() + "'";

					String updateParentSQL = " update " + DBOpenHelper.TABLE_NAME
					+ " set " + DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS
					+ " = " + DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS
					+ " + 1 where " + DBOpenHelper.ID + " = '"
					+ toNote.getId() + "'";

					Log.v("DBOperations------update sql: " + updateSQL);
					Log.v("DBOperations------update preparent sql: " + updatePreParentSQL);
					Log.v("DBOperations------update parent sql: " + updateParentSQL);

					db.execSQL(updateSQL);
					db.execSQL(updatePreParentSQL);
					db.execSQL(updateParentSQL);
				}
			}
			// Gionee <lilg><2013-04-16> modify for CR00795403 end
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------create note error: " + e.toString());
		} finally {
				db.endTransaction();
				close(db);
		}

		Log.i("DBOperations------move one note end!");
	}
	//Gionee <pengwei><20130824> modify for CR00834587 end

	private synchronized long createMedia(long noteId,MediaInfo mediaInfo,SQLiteDatabase db ) {
		Log.d("DBOperations------insert: " + mediaInfo.toString());

		ContentValues cv = new ContentValues();
		cv.put(DBOpenHelper.NOTE_ID,noteId);
		cv.put(DBOpenHelper.MEDIA_TYPE,mediaInfo.getMediaType());
		cv.put(DBOpenHelper.MEDIA_FILE_NAME,mediaInfo.getMediaFileName());
		long id = -1;

		id = db.insert(DBOpenHelper.MEIDA_TABLENAME, DBOpenHelper.CONTENT,
				cv);

		return id;
	}
	
	/**
	 * insert a media info into the media table
	 * @param context
	 * @param note
	 * @param mediaInfo
	 * @return
	 */
	public synchronized long insertMedia(Context context, Note note, MediaInfo mediaInfo){
		Log.i("DBOperations------insert media begin!");
		
		if(note == null){
			Log.e("DBOperation------note == null, insert media fail!");
			return -1;
		}
		if(mediaInfo == null){
			Log.e("DBOperation------mediaInfo == null, insert media fail!");
			return -1;
		}
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, insert media fail!");
			return -1;
		}
		
		long noteId = -1;
		try{
			noteId = Long.valueOf(note.getId());
		}catch(Exception e){
			Log.e("DBOperation------exception!", e);
			return -1;
		}
		
		String mediaType = mediaInfo.getMediaType();
		String mediaFileName = mediaInfo.getMediaFileName();
		Log.d("DBOperations------mediaType: " + mediaType + ", mediaFileName: " + mediaFileName);
		
		ContentValues cv = new ContentValues();
		cv.put(DBOpenHelper.NOTE_ID, noteId);
		cv.put(DBOpenHelper.MEDIA_TYPE, mediaType);
		cv.put(DBOpenHelper.MEDIA_FILE_NAME, mediaFileName);
		
		long mediaItemId = -1;
		db.beginTransaction();
		try {
			mediaItemId = db.insert(DBOpenHelper.MEIDA_TABLENAME, DBOpenHelper.CONTENT, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------insert media error", e);
			return -1;
		} finally {
			db.endTransaction();
			close(db);
		}
		
		Log.i("DBOperations------insert media end!");
		return mediaItemId;
	}
	
	public synchronized long insertMedia(Context context, MediaInfo mediaInfo){
		Log.i("DBOperations------insert media begin!");
		
		if(mediaInfo == null){
			Log.e("DBOperation------mediaInfo == null, insert media fail!");
			return -1;
		}
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, insert media fail!");
			return -1;
		}
		
		String noteId = mediaInfo.getNoteId();
		String mediaType = mediaInfo.getMediaType();
		String mediaFileName = mediaInfo.getMediaFileName();
		Log.d("DBOperations------mediaType: " + mediaType + ", mediaFileName: " + mediaFileName);
		
		ContentValues cv = new ContentValues();
		cv.put(DBOpenHelper.NOTE_ID, noteId);
		cv.put(DBOpenHelper.MEDIA_TYPE, mediaType);
		cv.put(DBOpenHelper.MEDIA_FILE_NAME, mediaFileName);

        // Gionee <wangpan><2014-06-04> add for CR01273806 begin
		ContentValues cv1 = new ContentValues();
		cv1.put(DBOpenHelper.MEDIA_FOLDER_NAME, mediaFileName.substring(0, mediaFileName.lastIndexOf("/")));
        // Gionee <wangpan><2014-06-04> add for CR01273806 end
		
		long mediaItemId = -1;
		db.beginTransaction();
		try {
			mediaItemId = db.insert(DBOpenHelper.MEIDA_TABLENAME, DBOpenHelper.CONTENT, cv);
	        // Gionee <wangpan><2014-06-04> add for CR01273806 begin
	        db.update(DBOpenHelper.TABLE_NAME, cv1, DBOpenHelper.ID + " = ? ", new String[]{noteId});
	        // Gionee <wangpan><2014-06-04> add for CR01273806 end
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------insert media error", e);
			return -1;
		} finally {
			db.endTransaction();
			close(db);
		}
		
		Log.i("DBOperations------insert media end!");
		return mediaItemId;
	}
	

	private synchronized List<MediaInfo> queryMeidas(int noteId,SQLiteDatabase db) {
		Log.d("DBOperations--queryFromFolder,id:" + noteId);

		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };

		Cursor cursor = null;
		try {
			cursor = db.query(DBOpenHelper.MEIDA_TABLENAME, columns,
					DBOpenHelper.NOTE_ID + "  = ? ",
					new String[] { String.valueOf(noteId) }, null, null,null);
		} catch (Exception e) {
			Log.e("DBOperations------queryFromFolder error ");
		}

		List<MediaInfo> mediaInfoList = new ArrayList<MediaInfo>();

		cursorToMedia(cursor, mediaInfoList);

		this.close(cursor);

		return mediaInfoList;
	}

	
	
	public synchronized void deleteMediaByFileName(Context context, String fileName){
		Log.i("DBOperations------deleteMediaByFileName begin!");
		Log.d("DBOperations------deleteMediaByFileName, fileName:" + fileName);
		
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, insert media fail!");
			return;
		}
		
		db.beginTransaction();
		try {
			
			db.delete(DBOpenHelper.MEIDA_TABLENAME, DBOpenHelper.MEDIA_FILE_NAME + " = ? ", new String[] { fileName });

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBOperations------delete media note error: ", e);
		} finally {
			db.endTransaction();
			close(db);
		}
		
		Log.i("DBOperations------deleteMediaByFileName end!");
	}
		
	public synchronized List<MediaInfo> queryMeidas(Context context,String noteId) {
		Log.d("DBOperations--queryFromFolder,id:" + noteId);
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryAllNotesWithAlarm fail!");
			return null;
		}
		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };

		Cursor cursor = null;
		try {
			cursor = db.query(DBOpenHelper.MEIDA_TABLENAME, columns,
					DBOpenHelper.NOTE_ID + "  = ? ",
					new String[] { noteId }, null, null,null);
		} catch (Exception e) {
			Log.e("DBOperations------queryFromFolder error ");
		}

		List<MediaInfo> mediaInfoList = new ArrayList<MediaInfo>();

		cursorToMedia(cursor, mediaInfoList);

		this.close(cursor);

		return mediaInfoList;
	}

	private synchronized void cursorToMedia(Cursor cursor, List<MediaInfo> mediaInfoList) {
		if (cursor != null && cursor.moveToFirst() != false) {
			if(mediaInfoList!=null){
				mediaInfoList.clear();
			}
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
			.moveToNext()) {
				MediaInfo mediaInfo=new MediaInfo();
				mediaInfo.setId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.MEDIA_ID)));
				mediaInfo.setMediaFileName(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.MEDIA_FILE_NAME)));
				mediaInfo.setMediaType(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.MEDIA_TYPE)));
				mediaInfo.setNoteId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.NOTE_ID)));

				mediaInfoList.add(mediaInfo);
			}
		}
	}

	

	public synchronized List<Note> queryAllNotesHaveAlarm(Context context) {
		Log.i("DBOperations------queryAllNotesWithAlarm start!");

		List<Note> noteList = new ArrayList<Note>();

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryAllNotesHaveAlarm fail!");
			return noteList;
		}
		// gn lilg 2012-12-08 modify for optimization end
		
		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };

		Cursor cursor = null;
		try {
			cursor = db.query(DBOpenHelper.TABLE_NAME, columns,
					DBOpenHelper.IS_FOLDER + " = ? and " + DBOpenHelper.ALARM_TIME + " <>  ? " , new String[] { "no", "0" },
					null, null, null);
		} catch (Exception e) {
			Log.e("DBOperations------queryAllNotesHaveAlarm" + e);
		}

		cursorToNote(cursor, noteList);
		this.close(cursor);
		this.close(db);

		Log.i("DBOperations------queryAllNotesWithAlarm end!");
		return noteList;
	}

	public synchronized List<Note> queryAllNotesByAlarm(Context context,
			String time) {
		Log.i("DBOperations------queryAllNotesWithAlarm start!");

		List<Note> noteList = new ArrayList<Note>();

		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryAllNotesByAlarm fail!");
			return noteList;
		}
		// gn lilg 2012-12-08 modify for optimization end
		
		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };

		Cursor cursor = null;
		try {
			cursor = db.query(DBOpenHelper.TABLE_NAME, columns,
					DBOpenHelper.IS_FOLDER + " = ? and "
					+ DBOpenHelper.ALARM_TIME + " =  ? ", new String[] {
					"no",time }, null, null, null);
		} catch (Exception e) {
			Log.e("DBOperations------queryAllNotesByAlarm" + e);
		}

		cursorToNote(cursor, noteList);
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.ALARM_TIME, Constants.INIT_ALARM_TIME);
		String whereClause = DBOpenHelper.ALARM_TIME + " = ? ";
		String[] whereArgs = new String[] { time };
		try {
			db.update(DBOpenHelper.TABLE_NAME, values, whereClause, whereArgs);
		} catch (Exception e) {
			Log.e("DBOperations------updateWidget error" + e);
		}
		this.close(cursor);
		this.close(db);
		Log.i("DBOperations------queryAllNotesWithAlarm end!");
		return noteList;
	}
	
	private synchronized void cursorToNoteForId(Cursor cursor, List<Note> noteList) {
		if (cursor != null && cursor.moveToFirst() != false) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
			.moveToNext()) {
				Note note = new Note();
				note.setId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ID)));
				noteList.add(note);
			}
		}
	}
	
	//gionee pengwei 20130310 modify for CR00777980 begin
	private synchronized void cursorToNoteForUpdate(Cursor cursor, List<Note> noteList) {
	try {
		if (cursor != null && cursor.moveToFirst() != false) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
			.moveToNext()) {
				Note note = new Note();
				note.setId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ID)));
				note.setUpdateDate(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.UPDATE_DATE)));
				note.setUpdateTime(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.UPDATE_TIME)));
				note.setAlarmTime(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.ALARM_TIME)));
				note.setBgColor(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.BG_COLOR)));
				note.setIsFolder(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.IS_FOLDER)));
				note.setParentFile(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.PARENT_FOLDER)));
				note.setNoteFontSize(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.NOTE_FONT_SIZE)));
				note.setNoteListMode(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.NOTE_LIST_MODE)));

				note.setWidgetId(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.WIDGET_ID)));
				note.setWidgetType(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.WIDGET_TYPE)));

				note.setHaveNoteCount(Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.FOLDER_HAVE_NOTE_COUNTS))));
				note.setTitle(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.NOTE_TITLE)));
				note.setMediaFolderName(cursor.getString(cursor
						.getColumnIndex(DBOpenHelper.MEDIA_FOLDER_NAME)));
				note.setNoteMediaType(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.NOTE_MEDIA_TYPE)));
				//				if(!Constants.MEDIA_FOLDER_NAME.equals(cursor.getString(cursor
				//						.getColumnIndex(DBOpenHelper.MEDIA_FOLDER_NAME)))){
				//					//TODO jiating
				//					
				//					note.setMediaInfos(queryMeidas( ))
				//				}
				note.setAddressName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ADDRESS_NAME)));
				note.setAddressDetail(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ADDRESS_DETAIL)));
				noteList.add(note);
			}
		}
	} catch (Exception e) {
		// TODO: handle exception
		Log.v("DBOperations---cursorToNoteForUpdate---" + e);
	}
	}
	//gionee pengwei 20130310 modify for CR00777980 end
	
	//Gionee <pengwei><2013-3-11>  modify for CR00777980 begin
	public synchronized String  queryOneNoteTitle(Context context, int _id) {
		Log.i("DBOperations------query one note start!");
		Log.d("DBOperations------queryOneNote,id:" + _id);
		
		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryOneNote fail!");
			return "";
		}
		// gn lilg 2012-12-08 modify for optimization end
		
		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };
		Cursor c = null;
		try {
			c = db.query(DBOpenHelper.TABLE_NAME, columns, " _id = ? ",
					new String[] { String.valueOf(_id) }, null, null, null);
		} catch (Exception e) {
			Log.e("DBOperations------queryOneNote error :" + e);
			close(c);
		}
		String content = "";

		if (c != null && c.moveToFirst() != false) {
			content = c.getString(c.getColumnIndex(DBOpenHelper.CONTENT));

		}
		this.close(c);
		this.close(db);

		Log.i("DBOperations------query one note end!");
		return content;
	}
	//Gionee <pengwei><2013-3-11>  modify for CR00777980 end
	
	
	
	// Gionee <lilg><2013-03-20> add for media record number limit begin
	public synchronized int queryMediaRecordNumByNoteId(Context context, String noteId){
		Log.i("DBOperations------queryRecordNumByNoteId begin!");
		
		if(context == null || TextUtils.isEmpty(noteId)){
			Log.e("DBOperation------context == null or noteId == null or noteId is \"\"!");
			return -1;
		}
		
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryRecordNumByNoteId fail!");
			return -1;
		}
		
		String[] columns = new String[] {DBOpenHelper.MEDIA_ID};
		String selection = DBOpenHelper.NOTE_ID + " = ? and " + DBOpenHelper.MEDIA_TYPE + " = ? ";
		String[] selectionArgs = new String[] {noteId, NoteMediaManager.TYPE_MEDIA_RECORD};
		
		int resultCount = -1;
		Cursor cursor = db.query(DBOpenHelper.MEIDA_TABLENAME, columns, selection, selectionArgs, null, null, null);
		if(cursor != null){
			resultCount = cursor.getCount();
		}
		
		Log.i("DBOperations------queryRecordNumByNoteId end!");
		return resultCount;
	}
	// Gionee <lilg><2013-03-20> add for media record number limit end

	public synchronized List<Note> queryAllDatas(Context context,List<Note> noteList) {
		Log.i("DBOperations------queryFoldersAndNotes");

		if (noteList != null) {
			noteList.clear();
		} else {
			noteList = new ArrayList<Note>();
		}
		
		// gn lilg 2012-12-08 modify for optimization begin
		SQLiteDatabase db = getDatabase(context);
		if(db == null){
			Log.e("DBOperation------db == null, queryFoldersAndNotes fail!");
			return noteList;
		}
		// gn lilg 2012-12-08 modify for optimization end

		String[] columns = new String[] { DBOpenHelper.NOTE_ALL };
		String orderBy = DBOpenHelper.IS_FOLDER + "  desc , "
		+ DBOpenHelper.UPDATE_DATE + " desc , "
		+ DBOpenHelper.UPDATE_TIME + " desc";

		Cursor cursor = null;
		try {
			cursor = db.query(DBOpenHelper.TABLE_NAME, columns, null,
					null, null, null, orderBy);
		} catch (Exception e) {
			Log.e("DBOperations------queryFoldersAndNotes error");
		}
		
		cursorToNote(cursor, noteList);

		this.close(cursor);
		this.close(db);
		return noteList;
	}
	
}