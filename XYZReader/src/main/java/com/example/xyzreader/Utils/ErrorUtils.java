package com.example.xyzreader.Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.xyzreader.R;

public final class ErrorUtils {
	/**
	 * A not so serious error
	 *
	 * @param context   context
	 * @param throwable the error throwable
	 */
	public static void general(final Context context, final Throwable throwable) {

		if (throwable == null)
			Toast.makeText(context,
					R.string.error,
					Toast.LENGTH_LONG
			).show();
		else if (throwable.getCause() == null)
			Toast.makeText(context,
					context.getString(R.string.error_no_cause,
							throwable.getLocalizedMessage()
					), Toast.LENGTH_LONG
			).show();
		else
			Toast.makeText(context,
					context.getString(R.string.error_cause,
							throwable.getLocalizedMessage(),
							throwable.getCause().getLocalizedMessage()
					), Toast.LENGTH_LONG
			).show();

		if (throwable != null) {

			throwable.printStackTrace();

			if (throwable.getCause() != null)
				throwable.getCause().printStackTrace();
		}
	}
}

