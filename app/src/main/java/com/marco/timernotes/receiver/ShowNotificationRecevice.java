package com.marco.timernotes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.marco.timernotes.R;
import com.marco.timernotes.utils.ToastUtils;

/**
 * User: KdMobiB
 * Date: 2016/5/27
 * Time: 14:09
 */
public class ShowNotificationRecevice extends BroadcastReceiver {
    public static final String action = "com.marco.timernotes.receiver.ShowNotificationRecevice";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        if (TextUtils.isEmpty(title)){
            title = context.getString(R.string.notification_title_default);
        }

        if (TextUtils.isEmpty(content)){
            content = context.getString(R.string.notification_content_default);
        }
        new ToastUtils(context).showMessage2Notification(title, content);
    }
}
