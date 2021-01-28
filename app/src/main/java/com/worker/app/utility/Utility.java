package com.worker.app.utility;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
	@SuppressLint("NewApi")
	
	public static String lastFragment = "";

	@SuppressLint("NewApi")
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	// 1 minute = 60 seconds
	// 1 hour = 60 x 60 = 3600
	// 1 day = 3600 x 24 = 86400
	public static String printDifference(Date startDate, Date endDate) {
		// int day = 0;
		// int hh = 0;
		// int mm = 0;
		// try {
		// SimpleDateFormat dateFormat = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss");
		// Date oldDate = startDate;
		// Date cDate = endDate;
		// Long timeDiff = cDate.getTime() - oldDate.getTime();
		// day = (int) TimeUnit.MILLISECONDS.toDays(timeDiff);
		// hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS
		// .toHours(day));
		// mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) -
		// TimeUnit.HOURS
		// .toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// if (day == 0) {
		// return hh + " H " + mm + " M";
		// } else if (hh == 0) {
		// return mm + " M";
		// } else {
		// return day + " D " + hh + " H " + mm + " M";
		// }
		// milliseconds
		long different = endDate.getTime() - startDate.getTime();

		System.out.println("startDate : " + startDate);
		System.out.println("endDate : " + endDate);
		System.out.println("different : " + different);

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
				elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
		String retValue = "";

		if (elapsedDays > 0) {
			if (elapsedDays == 1)
				retValue = elapsedDays + " day ago ";
			else
				retValue = elapsedDays + " days ago ";
		} else if (elapsedHours > 0) {
			if (elapsedHours == 1)
				retValue = elapsedHours + " hour ago ";
			else
				retValue = elapsedHours + " hours ago ";
		} else if (elapsedMinutes > 0) {
			if (elapsedMinutes == 1)
				retValue = elapsedMinutes + " min ago ";
			else
				retValue = elapsedMinutes + " mins ago ";
		} else if (elapsedSeconds > 0) {
			if (elapsedSeconds == 1)
				retValue = elapsedSeconds + " sec ago ";
			else
				retValue = elapsedSeconds + " secs ago ";
		} else {
			retValue = "just now";
		}
		// if (elapsedDays == 0) {
		// retValue = " " + elapsedHours + " hours " + elapsedMinutes
		// + " mins " + elapsedSeconds + " sec ago";
		// } else {
		// retValue = elapsedDays + " days " + elapsedHours + " hours "
		// + elapsedMinutes + " min " + elapsedSeconds + " secs ago";
		// }
		//
		// retValue = retValue.replace(" 0 hours", "");
		// retValue = retValue.replace(" 0 mins", "");
		// retValue = retValue.replace(" 0 secs", "");

		return retValue.trim();

	}

	public static String getCurrentDate() {
		Calendar c = Calendar.getInstance();
		String yy = c.get(Calendar.YEAR) + "";
		String mm = c.get(Calendar.MONTH) + "";
		String dd = c.get(Calendar.DAY_OF_MONTH) + "";
		String hh = c.get(Calendar.HOUR_OF_DAY) + "";
		String min = c.get(Calendar.MINUTE) + "";
		String ss = c.get(Calendar.SECOND) + "";
		String dateTime = dd + "-" + mm + "-" + yy + " " + hh + ":" + min + ":"
				+ ss;
		return dateTime;

	}

	public static Date strTodate(String date) {
		String dateString = date;
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd-MM-yyyy hh:mm:ss");
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedDate;
	}

	private static final int MAX_HEIGHT = 230;
	private static final int MAX_WIDTH = 230;

	public static Bitmap decodeSampledBitmap(Context context, Uri selectedImage)
			throws IOException {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		InputStream imageStream = context.getContentResolver().openInputStream(
				selectedImage);
		BitmapFactory.decodeStream(imageStream, null, options);
		imageStream.close();

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH,
				MAX_HEIGHT);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		imageStream = context.getContentResolver().openInputStream(
				selectedImage);
		Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

		img = rotateImageIfRequired(context, img, selectedImage);
		return img;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee a final image
			// with both dimensions larger than or equal to the requested height
			// and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	private static Bitmap rotateImageIfRequired(Context context, Bitmap img,
			Uri selectedImage) {

		// Detect rotation
		int rotation = getRotation(context, selectedImage);
		if (rotation != 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);
			Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
					img.getHeight(), matrix, true);
			img.recycle();
			return rotatedImg;
		} else {
			return img;
		}
	}

	/**
	 * Get the rotation of the last image added.
	 * 
	 * @param context
	 * @param selectedImage
	 * @return
	 */
	private static int getRotation(Context context, Uri selectedImage) {
		int rotation = 0;
		ContentResolver content = context.getContentResolver();

		Cursor mediaCursor = content.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
						"orientation", "date_added" }, null, null,
				"date_added desc");

		if (mediaCursor != null && mediaCursor.getCount() != 0) {
			while (mediaCursor.moveToNext()) {
				rotation = mediaCursor.getInt(0);
				break;
			}
		}
		mediaCursor.close();
		return rotation;
	}

	

	public static String getLastFragment() {
		return lastFragment;
	}

	public static void setLastFragment(String name) {
		lastFragment = name;
	}


	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static int getCurrentDate(String date) {
		return 0;
	}
}
