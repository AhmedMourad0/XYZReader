package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import com.example.xyzreader.R;
import com.example.xyzreader.Utils.ErrorUtils;
import com.example.xyzreader.adapters.ArticlesRecyclerAdapter;
import com.example.xyzreader.bus.RxBus;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailsActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity
		implements LoaderManager.LoaderCallbacks<Cursor>, AppBarLayout.OnOffsetChangedListener {

	@BindView(R.id.main_swipe_refresh)
	SwipeRefreshLayout refreshLayout;

	@BindView(R.id.main_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.main_appbar)
	AppBarLayout appBar;

	@BindView(R.id.main_toolbar)
	Toolbar toolbar;

	private ArticlesRecyclerAdapter adapter;

	private Disposable refreshingDisposable;
	private Disposable dbDisposable;

	private Unbinder unbinder;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_list);

		unbinder = ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		setTitle("");

		getSupportLoaderManager().initLoader(0, null, this);

		dbDisposable = Single.fromCallable(() -> getContentResolver().query(ItemsContract.Items.buildDirUri(),
				new String[]{ItemsContract.Items._ID},
				null,
				null,
				null)).subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.map(cursor -> cursor.getCount() < 6)
				.subscribe(needsSync -> {
					if (needsSync)
						refresh();
				}, throwable -> ErrorUtils.general(this, throwable));

		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
				getResources().getInteger(R.integer.list_column_count),
				StaggeredGridLayoutManager.VERTICAL
		));

		adapter = new ArticlesRecyclerAdapter(this);
		adapter.setHasStableIds(true);
		recyclerView.setAdapter(adapter);

		initializeRefreshLayout();
	}

	private void initializeRefreshLayout() {

		refreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, android.R.color.white));

		refreshLayout.setColorSchemeResources(
				R.color.refresh_progress_1,
				R.color.refresh_progress_2,
				R.color.refresh_progress_3
		);

		refreshLayout.setOnRefreshListener(this::refresh);
	}

	private void refresh() {
		startService(new Intent(this, UpdaterService.class));
	}

	@Override
	protected void onStart() {
		super.onStart();

		Observable<Boolean> relay = RxBus.getInstance()
				.getListRefreshingRelay()
				.observeOn(AndroidSchedulers.mainThread());

		if (refreshingDisposable == null)
			relay = relay.skip(1);

		refreshingDisposable = relay.subscribe(refreshing -> refreshLayout.setRefreshing(refreshing),
				throwable -> ErrorUtils.general(this, throwable)
		);

		appBar.addOnOffsetChangedListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		refreshingDisposable.dispose();
		dbDisposable.dispose();
		appBar.removeOnOffsetChangedListener(this);
	}

	@Override
	protected void onDestroy() {
		unbinder.unbind();
		super.onDestroy();
	}

	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		refreshLayout.setEnabled(verticalOffset == 0);
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return ArticleLoader.newAllArticlesInstance(this);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
		adapter.swapCursor(cursor);
		refreshLayout.setRefreshing(false);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		recyclerView.setAdapter(null);
	}
}
