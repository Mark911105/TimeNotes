package com.marco.timernotes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.marco.timernotes.db.SqliteHelpUtils;

/**
 * User: KdMobiB
 * Date: 2016/5/26
 * Time: 11:28
 * 检测开机启动广播，如果检测到，就直接启动服务
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            System.out.println("检测到开机广播--->");
//            context.startService(new Intent(context, BackgroundToastService.class));
            new SqliteHelpUtils(context).resetBootData();//重启后数据丢失，重新拉数据
        }
    }
}
