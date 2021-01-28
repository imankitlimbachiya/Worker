package com.worker.app.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.worker.app.BaseActivity;
import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.utility.Pref.PreferenceKeys;
import com.worker.app.utility.Pref.PreferenceUtil;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFireBaseMessagingService.class.getSimpleName();

    private NotificationUtil notificationUtils;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", "## token " + s);
        PreferenceUtil.getPref(getApplicationContext()).edit().putString(PreferenceKeys.PrefFCMToken, s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "## From: " + remoteMessage.getFrom());
        if (remoteMessage == null)
            return;

        Log.e(TAG, "## Data Payload 1: " + remoteMessage.getData());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "## Data Payload: " + remoteMessage.getData());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "## Exception: onMessageReceived " + e.getMessage());
            }
        }else{
            Log.e(TAG, "## Data Payload empty ");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "## Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtil.isAppIsInBackground(getApplicationContext())) {
            Log.e("MFMS", "## handleNotification if");

            Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
            resultIntent.putExtra("message", message);
            // check for image attachment
            showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), message,
                    "", resultIntent);

            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(NotificationConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtil notificationUtils = new NotificationUtil(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            Log.e("MFMS", "## handleNotification else");
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "## push json: " + json.toString());

        try {
            String title = json.getString("title");
            String message = json.getString("body");
            Log.e(TAG, "## push json title : " + title);
            Log.e(TAG, "## push json message : " + message);
            if (!NotificationUtil.isAppIsInBackground(getApplicationContext())) {
                Log.e(TAG, "## handleDataMessage bg : ");
                // play notification sound
                //  NotificationUtil notificationUtils = new NotificationUtil(getApplicationContext());
                NotificationUtil notificationUtils = new NotificationUtil(this);
                notificationUtils.playNotificationSound();

                Intent resultIntent = new Intent(getApplicationContext(), BaseActivity.class);
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("message", message);
                // check for image attachment
                showNotificationMessage(getApplicationContext(), title, message, "", resultIntent);

                Intent pushNotification = new Intent(NotificationConfig.PUSH_NOTIFICATION);
                pushNotification.putExtra("title", title);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            } else {
                NotificationUtil notificationUtils = new NotificationUtil(this);
                notificationUtils.playNotificationSound();
                Log.e(TAG, "handleDataMessage fg : ");
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("message", message);

                showNotificationMessage(getApplicationContext(), title, message, "", resultIntent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "## Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "## Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}
