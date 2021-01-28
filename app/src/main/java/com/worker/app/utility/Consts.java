package com.worker.app.utility;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consts {
    private static Consts mInstance;
    private static final boolean DEBUG = true;

    public synchronized static Consts getInstance() {
        if (mInstance == null)
            mInstance = new Consts();
        return mInstance;
    }

    public String urlOppwa = "https://test.oppwa.com";
    public String USERID_PAYMENT = "8ac7a4c86712a863016713ec648503ea";
    public String PASS_PAYMENT = "2PzSK98J9X";
    public String ENTITY_PAYMENT = "8ac7a4ca700f61ba01701437d72f0e01";//"8ac7a4ca6712a863016713ef62aa010c";

    public final int ClickTimeSeconds = 3000;

        public final String PlacesAPI_KEY = "AIzaSyA2vAa9ZnFNl0WYCGyGSUHhH7p80PWgT-Y";
//    public final String PlacesAPI_KEY = "AIzaSyBu4MhS_dZoestWkMWpjrxb_w3kLGMTeSc";

    // public final String BASE_URL = "http://laundrydev.sketchservices.com/workers/api/";

//    public final String BASE_URL = "https://workers-app.com/api/";
    public final String BASE_URL = "http://157.175.51.107/worker-admin-api/api/";//"https://workers-app.com/worker_test/api/";//

    //below keys for firebase analytics
    public final String EVENT_NAME="event_name";
    public final String CONTRY_ID="country_id";
    public final String FIREBASE_USERID="user_id";
    public final String FIREBASE_WORKERID="worker_id";
    public final String AGE_RANGE_ID="age_range_id";
    public final String WORKER_CONTRACT_FEES="worker_contract_fees";
    public final String FIREBASE_ORDER_ID="order_id";
    public final String FIREBASE_AMOUNT="paid_amount";
    public final String FIREBASE_CHECKOUT_ID="paid_amount";

    public final String CHECKOUT_START = "e-commerce purchase started";
    public final String CHECKOUT = "e-commerce purchase";
    public final String CREATE_WORKER_REQUEST="Create worker Request";
    public final String CREATE_ORDER_REQUEST="Create Order Request";

    public final String HOME_URL = BASE_URL + "homepage.php";
    public final String fIND_WORKERS_URL = BASE_URL + "find_workers.php";
    public final String WORKER_DETAIL_URL = BASE_URL + "worker_detail.php";
    public final String LOGIN_URL = BASE_URL + "login.php";
    public final String SIGN_IN_URL = BASE_URL + "signup.php";
    public final String ADD_ORDER_REQUEST = BASE_URL + "add_order_request.php";
    public final String DELETE_ORDER_REQUEST = BASE_URL + "delete_request_order.php";
    public final String MY_WORKERS = BASE_URL + "my_workers.php";
    public final String FORGOT_PASSWORD = BASE_URL + "forget_password.php";
    public final String MY_ORDER_LIST = BASE_URL + "my_order_list.php";
    public final String CHANGE_PASSWORD = BASE_URL + "change_password.php";
    public final String UPDATE_PROFILE = BASE_URL + "update_profile.php";
    public final String MY_PROFILE = BASE_URL + "my_profile.php";
    public final String MATCHING_WORKERS_REQUEST = BASE_URL + "matching_workers_request.php";
    public final String DELETE_REQUEST = BASE_URL + "delete_request.php";
    public final String VISA_DETAIL = BASE_URL + "visa_detail.php";
    public final String UPDATE_ORDER = BASE_URL + "update_order.php";
    public final String WALLET_DETAIL = BASE_URL + "wallet_detail.php";
    public final String ORDER_PAY = BASE_URL + "order_pay.php";
    public final String NOTIFICATION_LIST = BASE_URL + "notification_list.php";
    public final String NOTIFICATION_ALERT = BASE_URL + "notification_alert.php";
    public final String UPDATE_USER_IDENTITY = BASE_URL + "update_user_identity.php";
    public final String REMOVE_USER_ORDER_IDENTITY = BASE_URL + "remove_user_order_identity.php";
    public final String ADD_VISA_DETAIL = BASE_URL + "add_visa_detail.php";
    public final String REMOVE_COUNTRY_VISA_DETAIL = BASE_URL + "remove_country_visa_detail.php";
    public final String CHECK_VISA_DETAIL = BASE_URL + "check_visa_detail.php";
    public final String NOTIFICATION_COUNT = BASE_URL + "notification_count.php";
    public final String UPDATE_DEVICE = BASE_URL + "update_device.php";
    public final String SETTING_UPDATE = BASE_URL + "setting_update.php";
    public final String SETTING_LIST = BASE_URL + "setting_list.php";
    public final String CANCEL_ORDER = BASE_URL + "cancel_order.php";
    public final String CONTENT = BASE_URL + "content.php";
    public final String ADD_CONTACT_US = BASE_URL + "add_contact_us.php";
    public final String REQUEST_WORKER_COUNT = BASE_URL + "request_worker_count.php";
    public final String PAYMENT_HYPER_PAY = BASE_URL + "payment_hyper_pay.php";
    public final String APPLY_COUPON = BASE_URL + "applied_coupon.php";

    public final String my_request_listing = BASE_URL + "my_request_listing.php";
    public final String request_order_list = BASE_URL + "request_order_list.php";
    public final String request_worker_detail = BASE_URL + "request_worker_detail.php";
    public final String country_list = BASE_URL + "country_list.php";
    public final String category_list = BASE_URL + "category_list.php";
    public final String request_add = BASE_URL + "request_add.php";
    public final String order_step_3 = BASE_URL + "order_step_3.php";
    public final String announcement_list = BASE_URL + "announcement_list.php";
    public final String city_list = BASE_URL + "city_list.php";
    public final String UPDATE_LANGUAGE = "update_language.php";

    public final String CATEGORY_LIST_TEST = BASE_URL + "category_list_test.php";
    public String Act_vity = "";

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

    private boolean isValidEmail(String email) {
        // TODO Auto-generated method stub
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern
                .compile(EMAIL_PATTERN);
        Matcher matcher = pattern
                .matcher(email);
        return matcher.matches();
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            // if(phone.length() < 6 || phone.length() > 13) {
            if (phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.e("** Const" + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                Log.e("** Const" + " File -", "isLocalStorageDocument ");
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                Log.e("** Const" + " File -", "isExternalStorageDocument ");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
                return Environment.getExternalStorageDirectory() + "/" + split[1];

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                Log.e("** Const" + " File -", "isDownloadsDocument ");
                try {

                    final String id = DocumentsContract.getDocumentId(uri);
                    Log.e("** Const", "getPath: id= " + id);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    List<String> segments = uri.getPathSegments();
                    if (segments.size() > 1) {
                        String rawPath = segments.get(1);
                        if (!rawPath.startsWith("/")) {
                            return rawPath.substring(rawPath.indexOf("/"));
                        } else {
                            return rawPath;
                        }
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                Log.e("** Const" + " File -", "isMediaDocument ");
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
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.e("** Const" + " File -", "scheme equals content ");

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                Log.e("** Const" + " File -", "isGooglePhotosUri");
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.e("** Const" + " File -", "scheme equals file ");
            return uri.getPath();
        }

        return "";
    }

    public boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    public boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public boolean isLocalStorageDocument(Uri uri) {
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return "";
    }
}
