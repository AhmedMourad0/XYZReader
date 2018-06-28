package com.example.xyzreader.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.Utils.GraphicsUtils;
import com.example.xyzreader.adapters.LongTextRecyclerAdapter;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
@SuppressLint("GoogleAppIndexingApiWarning")
public class ArticleDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "ArticleDetailsActivity";

	@BindView(R.id.details_photo)
	ImageView photoView;

	@BindView(R.id.details_share_fab)
	FloatingActionButton fab;

	@BindView(R.id.details_title)
	TextView titleView;

	@BindView(R.id.details_subtitle)
	TextView bylineView;

	@BindView(R.id.details_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.details_meta_bar)
	LinearLayout metaBar;

	private Cursor cursor;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());
	// Use default locale format
	private final SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

	private Unbinder unbinder;

	private int articleId;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_details);

		if (savedInstanceState == null && getIntent() != null && getIntent().getData() != null)
			articleId = (int) ItemsContract.Items.getItemId(getIntent().getData());

		unbinder = ButterKnife.bind(this);

		getSupportLoaderManager().initLoader(1, null, this);

		fab.setOnClickListener(view -> startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
				.setType("text/plain")
				.setText("Some sample text")
				.getIntent(), getString(R.string.action_share)
		)));

		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

		populateUi();
	}

	private Date parsePublishedDate() {
		try {
			String date = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
			return dateFormat.parse(date);
		} catch (ParseException ex) {
			Log.e(TAG, ex.getMessage());
			Log.i(TAG, "passing today's date");
			return new Date();
		}
	}

	private void populateUi() {

		if (cursor != null) {

			titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));

			bylineView.setText(Html.fromHtml(outputFormat.format(parsePublishedDate()) +
					"&nbsp;&nbsp;by <font color='#ffffff'>" +
					cursor.getString(ArticleLoader.Query.AUTHOR) +
					"</font>"
			));

			Picasso.get()
					.load(cursor.getString(ArticleLoader.Query.PHOTO_URL))
					.into(photoView, new Callback() {
						@Override
						public void onSuccess() {

							final int dominantDarkMutedColor = GraphicsUtils.extractDominantDarkMutedColor(photoView);

							metaBar.setBackgroundColor(dominantDarkMutedColor);

							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
								Window window = getWindow();
								window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
								window.setStatusBarColor(dominantDarkMutedColor);
							}
						}

						@Override
						public void onError(Exception e) {

						}
					});

			final LongTextRecyclerAdapter adapter = new LongTextRecyclerAdapter(cursor.getString(ArticleLoader.Query.BODY));
			recyclerView.setAdapter(adapter);
		}
	}

	@Override
	public void onDestroy() {
		unbinder.unbind();
		super.onDestroy();
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return ArticleLoader.newInstanceForItemId(this, articleId);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {

		if (this.cursor != null) {
			this.cursor.close();
		}

		this.cursor = cursor;

		if (this.cursor != null && !this.cursor.moveToFirst()) {
			Log.e(TAG, "Error reading item detail cursor");
			this.cursor.close();
			this.cursor = null;
		}

		populateUi();
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
		cursor = null;
		populateUi();
	}
}
