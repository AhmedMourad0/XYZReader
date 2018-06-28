package com.example.xyzreader.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import static com.example.xyzreader.data.ItemsContract.*;

/**
 * Helper for loading a list of articles or a single article.
 */
public class ArticleLoader extends CursorLoader {

	public static ArticleLoader newAllArticlesInstance(Context context) {
		return new ArticleLoader(context, Items.buildDirUri());
	}

	public static ArticleLoader newInstanceForItemId(Context context, long itemId) {
		return new ArticleLoader(context, Items.buildItemUri(itemId));
	}

	private ArticleLoader(Context context, Uri uri) {
		super(context, uri, Query.PROJECTION, null, null, Items.DEFAULT_SORT);
	}

	public interface Query {

		String[] PROJECTION = {
				Items._ID,
				Items.TITLE,
				Items.PUBLISHED_DATE,
				Items.AUTHOR,
				Items.THUMB_URL,
				Items.PHOTO_URL,
				Items.ASPECT_RATIO,
				Items.BODY,
		};

		int _ID = 0;
		int TITLE = 1;
		int PUBLISHED_DATE = 2;
		int AUTHOR = 3;
		int THUMB_URL = 4;
		int PHOTO_URL = 5;
		int ASPECT_RATIO = 6;
		int BODY = 7;
	}
}
