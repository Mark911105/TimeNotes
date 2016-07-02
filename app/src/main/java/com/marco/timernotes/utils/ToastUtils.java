package com.marco.timernotes.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;
import com.marco.timernotes.R;
import com.marco.timernotes.ui.MainActivity;

/**
 * User: KdMobiB
 * Date: 2016/5/26
 * Time: 11:39
 */
public class ToastUtils {
    private static Context context;

    public ToastUtils(Context context) {
        this.context = context;
    }

    public void showMessage2Notification(final String title, final String msg) {
        NotificationManager NOTIFICATION_MANAGER = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 设置通知的事件消息
        Intent notificationIntent = new Intent(context, MainActivity.class); // 点击该通知后要跳转的Activity
        PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification;
        if (Build.VERSION.SDK_INT >= 16) {
            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(contentItent)
                    .build();
        } else {
            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(contentItent)
                    .getNotification();
        }

        notification.defaults |= Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NOTIFICATION_MANAGER.notify(1, notification);
    }

    public void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
