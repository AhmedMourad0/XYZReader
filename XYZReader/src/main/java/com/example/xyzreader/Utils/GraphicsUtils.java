package com.example.xyzreader.Utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

public final class GraphicsUtils {

	public static int extractDominantDarkMutedColor(ImageView imageView) {
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		Palette palette = new Palette.Builder(bitmap).generate();
		int defaultColor = 0xFF333333;
		return palette.getDarkMutedColor(defaultColor);
	}
}
