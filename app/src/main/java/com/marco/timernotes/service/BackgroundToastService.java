package com.marco.timernotes.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.marco.timernotes.db.SqliteHelpUtils;
import com.marco.timernotes.entity.ItemNotes;
import com.marco.timernotes.utils.ToastUtils;

import java.util.ArrayList;

/**
 * User: KdMobiB
 * Date: 2016/5/26
 * Time: 11:33
 */
public class BackgroundToastService extends Service {
    public static final String action = "com.marco.timernotes.backgroundtoastservice.action";

    private SqliteHelpUtils sq;
    private ArrayList<ItemNotes> items     = new ArrayList<ItemNotes>();
    private boolean              isRunning = false;
    private ToastUtils toastUtils;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 遍历列表里面的数据，如果有数据在一秒内的话，则弹出状态栏显示
     */
    @Override
    public void onCreate() {
        super.onCreate();

        sq = new SqliteHelpUtils(this);
        toastUtils = new ToastUtils(this);
//        items = sq.readData();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        isRunning = true;
                        while (isRunning) {
                            try {
                                Thread.sleep(1000);
                                long time;
                                System.out.println("service is running" );
                                for (ItemNotes item : items) {
                                    time = System.currentTimeMillis();
                                    //每隔一秒判断一次，如果有内容在一秒之内，则弹窗显示
                                    if (time > item.getHintTime() && time < item.getHintTime() + 1000) {
//                                        判断如果在区间就弹出提示
                                        toastUtils.showMessage2Notification(item.getTitle(), item.getContent());
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        items = sq.readData();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
    public static final String updateAction = "com.marco.updatebackgroundservice.action";

//    使用startservice来重新更新数据，不使用内部类广播
//        registerReceiver(updateRecevice,new IntentFilter(updateAction));

//    public BroadcastReceiver updateRecevice = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (items!=null&&sq!=null){
//                items.clear();
//                items.addAll(sq.readData());
//                System.out.println("itemNotes--->" + items.size());
//            }
//        }
//    };
}
