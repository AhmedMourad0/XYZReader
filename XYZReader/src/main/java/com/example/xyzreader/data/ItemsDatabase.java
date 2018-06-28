package com.example.xyzreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.xyzreader.data.ItemsProvider.*;

class ItemsDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "xyzreader.db";
	private static final int DATABASE_VERSION = 2;

	ItemsDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.ITEMS + " ("
				+ ItemsContract.ItemsColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ItemsContract.ItemsColumn.SERVER_ID + " TEXT,"
				+ ItemsContract.ItemsColumn.TITLE + " TEXT NOT NULL,"
				+ ItemsContract.ItemsColumn.AUTHOR + " TEXT NOT NULL,"
				+ ItemsContract.ItemsColumn.BODY + " TEXT NOT NULL,"
				+ ItemsContract.ItemsColumn.THUMB_URL + " TEXT NOT NULL,"
				+ ItemsContract.ItemsColumn.PHOTO_URL + " TEXT NOT NULL,"
				+ ItemsContract.ItemsColumn.ASPECT_RATIO + " REAL NOT NULL DEFAULT 1.5,"
				+ ItemsContract.ItemsColumn.PUBLISHED_DATE + " TEXT NOT NULL"
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Tables.ITEMS);
		onCreate(db);
	}
}
