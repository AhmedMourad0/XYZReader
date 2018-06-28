package com.example.xyzreader.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.Utils.GraphicsUtils;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.ArticleDetailsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder> {

	private Cursor cursor;
	private final Activity activity;

	public ArticlesRecyclerAdapter(Activity activity) {
		this.activity = activity;
	}

	@Override
	public long getItemId(int position) {
		cursor.moveToPosition(position);
		return cursor.getLong(ArticleLoader.Query._ID);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		final Context context = parent.getContext();

		View view = LayoutInflater.from(context).inflate(R.layout.list_item_article, parent, false);

		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		cursor.moveToPosition(position);
		holder.bind(cursor, getItemId(position), activity);
	}

	public void swapCursor(final Cursor cursor) {
		this.cursor = cursor;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return cursor.getCount();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_thumbnail)
		ImageView thumbnailView;

		@BindView(R.id.item_title)
		TextView titleView;

		@BindView(R.id.item_article_subtitle)
		TextView subtitleView;

		private static final String TAG = ArticlesRecyclerAdapter.class.toString();

		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());

		private final SimpleDateFormat outputFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

		final Picasso picasso;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			picasso = Picasso.get();
		}

		private void bind(final Cursor cursor, final long itemId, final Activity activity) {

			final Context context = itemView.getContext();

			titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));

			subtitleView.setText(context.getString(R.string.list_article_subtitle,
					outputFormat.format(parsePublishedDate(cursor)),
					cursor.getString(ArticleLoader.Query.AUTHOR)
			));

			thumbnailView.getLayoutParams().height =
					(int) (thumbnailView.getLayoutParams().width / cursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

			picasso.load(cursor.getString(ArticleLoader.Query.THUMB_URL)).into(thumbnailView, new Callback() {
				@Override
				public void onSuccess() {
					thumbnailView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
					itemView.setBackgroundColor(GraphicsUtils.extractDominantDarkMutedColor(thumbnailView));
				}

				@Override
				public void onError(Exception e) {

				}
			});

			itemView.setOnClickListener(view1 -> {

				Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
						thumbnailView,
						context.getString(R.string.transition_image)).toBundle();

				final Intent intent = new Intent(context, ArticleDetailsActivity.class);
				intent.setData(ItemsContract.Items.buildItemUri(itemId));
				context.startActivity(intent, bundle);
			});
		}

		private Date parsePublishedDate(final Cursor cursor) {

			try {

				String date = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);

				return dateFormat.parse(date);

			} catch (ParseException ex) {

				Log.e(TAG, ex.getMessage());
				Log.i(TAG, "passing today's date");

				return new Date();
			}
		}
	}
}