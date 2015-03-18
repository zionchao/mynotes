package com.gionee.note.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.utils.Log;

public class NotesProvider extends ContentProvider {

	private SQLiteDatabase sqlitedb;
	private DBOpenHelper dbOpenHelper;
	private static HashMap<String, String> sNotesProjectionMap;

	public static final String AUTHORITY = "com.gionee.provider.notes";

	/**
	 * The content:// style URL for this table
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DBOpenHelper.TABLE_NAME);
	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.note";
	/**
	 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
	 */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.note";

	private static final int ITEMS = 1;
	private static final int ITEM_ID = 2;

	private static final UriMatcher sUriMatcher;
	static {

		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "items", ITEMS);
		sUriMatcher.addURI(AUTHORITY, "items/#", ITEM_ID);

		sNotesProjectionMap = new HashMap<String, String>();
		sNotesProjectionMap.put(DBOpenHelper.ID, DBOpenHelper.ID);
		sNotesProjectionMap.put(DBOpenHelper.CONTENT, DBOpenHelper.CONTENT);
		sNotesProjectionMap.put(DBOpenHelper.UPDATE_DATE, DBOpenHelper.UPDATE_DATE);
		sNotesProjectionMap.put(DBOpenHelper.UPDATE_TIME, DBOpenHelper.UPDATE_TIME);
		sNotesProjectionMap.put(DBOpenHelper.ALARM_TIME, DBOpenHelper.ALARM_TIME);
		sNotesProjectionMap.put(DBOpenHelper.BG_COLOR, DBOpenHelper.BG_COLOR);
		sNotesProjectionMap.put(DBOpenHelper.IS_FOLDER, DBOpenHelper.IS_FOLDER);
		sNotesProjectionMap.put(DBOpenHelper.PARENT_FOLDER, DBOpenHelper.PARENT_FOLDER);
	}

	@Override
	public boolean onCreate() {
		Log.i("NotesProvider------onCreate!");

		dbOpenHelper = new DBOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,	String[] selectionArgs, String sortOrder) {
		Log.i("NotesProvider------query start!");
		Log.d("NotesProvider------uri: " + uri);
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			qb.setTables(DBOpenHelper.TABLE_NAME);
			qb.setProjectionMap(sNotesProjectionMap);
			break;
		case ITEM_ID:
			qb.setTables(DBOpenHelper.TABLE_NAME);
			qb.setProjectionMap(sNotesProjectionMap);
			qb.appendWhere(DBOpenHelper.ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			Log.e("NotesProvider------unknown uri: " + uri);
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		sqlitedb = dbOpenHelper.getReadableDatabase();
		Cursor c = qb.query(sqlitedb, projection, selection, selectionArgs, null, null, sortOrder);

		// Tell the cursor what uri to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);

		Log.i("NotesProvider------query end!");
		return c;
	}

	@Override
	public String getType(Uri uri) {
		Log.i("NotesProvider------getType!");
		Log.d("NotesProvider------uri: " + uri);
		
		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			return CONTENT_TYPE;
		case ITEM_ID:
			return CONTENT_ITEM_TYPE;
		default:
			Log.e("NotesProvider------unknown uri: " + uri);
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
