package com.example.xyzreader.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

import static android.widget.FrameLayout.LayoutParams;

public class LongTextRecyclerAdapter extends RecyclerView.Adapter<LongTextRecyclerAdapter.ViewHolder> {

	private final List<String> textPieces;

	public LongTextRecyclerAdapter(final String longText) {

		String[] pieces = longText.split("(\r\n|\n)");

		this.textPieces = new ArrayList<>(pieces.length);

		textPieces.addAll(Arrays.asList(pieces));
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		final Context context = parent.getContext();
		final Resources res = context.getResources();

		TextView view = new TextView(context);

		view.setLineSpacing(0f, 1.4f);
		view.setPadding(0, 0, 0, 8);
		view.setTextColor(Color.BLACK);
		view.setLinkTextColor(Color.BLUE);

		TextViewCompat.setTextAppearance(view, android.R.style.TextAppearance_Large);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		params.leftMargin = (int) res.getDimension(R.dimen.detail_inner_horiz_margin);
		params.rightMargin = (int) res.getDimension(R.dimen.detail_inner_horiz_margin);

		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(textPieces.get(position));
	}

	@Override
	public int getItemCount() {
		return textPieces.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}

		private void bind(String piece) {
			((TextView) itemView).setText(Html.fromHtml(piece));
		}
	}
}