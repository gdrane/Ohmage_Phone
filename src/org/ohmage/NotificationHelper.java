package org.ohmage;

import org.ohmage.activity.LoginActivity;
import org.ohmage.activity.UploadQueueActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {

	public static void showAuthNotification(Context context) {
		NotificationManager noteManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification note = new Notification();
		
		Intent intentToLaunch = new Intent(context, LoginActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToLaunch, 0);
		String title = "Authentication error!";
		String body = "Tap here to re-enter credentials.";
		note.icon = android.R.drawable.stat_notify_error;
		note.tickerText = "Authentication error!";
		note.defaults |= Notification.DEFAULT_ALL;
		note.when = System.currentTimeMillis();
		note.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
		note.setLatestEventInfo(context, title, body, pendingIntent);
		noteManager.notify(1, note);
	}

	public static void showUploadErrorNotification(Context context) {
		NotificationManager noteManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification note = new Notification();
		
		Intent intentToLaunch = new Intent(context, UploadQueueActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToLaunch, 0);
		String title = "Upload error!";
		String body = "An error occurred while trying to upload survey responses.";
		note.icon = android.R.drawable.stat_notify_error;
		note.tickerText = "Upload error!";
		note.defaults |= Notification.DEFAULT_ALL;
		note.when = System.currentTimeMillis();
		note.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
		note.setLatestEventInfo(context, title, body, pendingIntent);
		noteManager.notify(2, note);
	}
}
