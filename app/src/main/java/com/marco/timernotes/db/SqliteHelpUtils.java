package com.marco.timernotes.db;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import com.marco.timernotes.R;
import com.marco.timernotes.entity.ItemNotes;
import com.marco.timernotes.receiver.ShowNotificationRecevice;
import com.marco.timernotes.utils.ToastUtils;

import java.util.ArrayList;

/**
 * User: KdMobiB
 * Date: 2016/5/25
 * Time: 17:22
 */
public class SqliteHelpUtils extends SQLiteOpenHelper {
    public final static String DbTableName = "timenotes";
    public final static String DbUserName  = "user";
    public  Context      context;
    private AlarmManager am;


    /**
     * @param context 上下文对象
     *                name 数据库名称
     *                factory
     *                version 数据库版本
     */
    public SqliteHelpUtils(Context context) {
        super(context, DbTableName, null, 2);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE user(" +
                        "notesId integer primary key ," +
                        "title TEXT DEFAULT \"\"," +
                        "content TEXT DEFAULT \"\"," +
                        "time INTEGER DEFAULT \"\"," +
                        "hinttime INTEGER DEFAULT \"\"" +
                        ")"
                  );
    }

    /**
     * 数据库更新的时候需要做的处理
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                "CREATE TABLE user(" +
                        "notesId integer primary key ," +
                        "title TEXT DEFAULT \"\"," +
                        "content TEXT DEFAULT \"\"," +
                        "time INTEGER DEFAULT \"\"," +
                        "hinttime INTEGER DEFAULT \"\"" +
                        ")"
                  );
    }

    /**
     * 写入数据
     * <p/>
     * name      数据表名
     *
     * @param itemNotes 数据
     */
    public void insertData(ItemNotes itemNotes) {
        SQLiteDatabase sb = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("notesId", itemNotes.getNoteid());
        values.put("title", itemNotes.getTitle());
        values.put("content", itemNotes.getContent());
        values.put("time", itemNotes.getTime());
        values.put("hinttime", itemNotes.getHintTime());
        if (sb.insert(DbUserName, null, values) == -1) {
            new ToastUtils(context).showMessage(context.getResources().getString(R.string.toast_add_failure));
        } else {
            new ToastUtils(context).showMessage(context.getResources().getString(R.string.toast_add_success));
            startAlarmService(itemNotes);
        }
        sb.close();
    }

    private void startAlarmService(ItemNotes itemNotes) {
        Intent showNotificationIntent = new Intent(ShowNotificationRecevice.action);
        showNotificationIntent.putExtra("title",itemNotes.getTitle());
        showNotificationIntent.putExtra("content",itemNotes.getContent());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, itemNotes.getNoteid(), showNotificationIntent, 0);
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, itemNotes.getHintTime(), pendingIntent);
    }

    private void stopAlarmService(ItemNotes itemNotes) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent showNotificationIntent = new Intent(ShowNotificationRecevice.action);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, itemNotes.getNoteid(), showNotificationIntent, 0);
        am.cancel(pendingIntent);
    }

    /**
     * 插入数据
     *
     * @param title
     * @param content
     * @param time
     * @param hinttime
     */
    public void insertData(String title, String content, long time, long hinttime, long noteIds) {
        SQLiteDatabase sb = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("notesId", noteIds);
        values.put("title", title);
        values.put("content", content);
        values.put("time", time);
        values.put("hint", hinttime);
        if (sb.insert(DbUserName, null, values) == -1) {
            new ToastUtils(context).showMessage(context.getResources().getString(R.string.toast_add_failure));
        } else {
            new ToastUtils(context).showMessage(context.getResources().getString(R.string.toast_add_success));
        }
        sb.close();
    }

    /**
     * 获取数据
     *
     * @return
     */
    public ArrayList<ItemNotes> readData() {
        ArrayList<ItemNotes> list = new ArrayList<ItemNotes>();
        SQLiteDatabase sb = getReadableDatabase();
        Cursor c = sb.query(DbUserName, null, null, null, null, null, null);
        while (c.moveToNext()) {
            ItemNotes itemNotes = new ItemNotes();
            String title = c.getString(c.getColumnIndex("title"));
            String content = c.getString(c.getColumnIndex("content"));
            long time = c.getLong(c.getColumnIndex("time"));
            long hintTime = c.getLong(c.getColumnIndex("hinttime"));
            int notesId = c.getInt(c.getColumnIndex("notesId"));

            itemNotes.setNoteid(notesId);
            itemNotes.setTime(time);
            itemNotes.setTitle(title);
            itemNotes.setContent(content);
            itemNotes.setHintTime(hintTime);
            list.add(itemNotes);
        }
        c.close();
        sb.close();
        System.out.println("sqHelpUtilsCursor--->" + list.size());
        return list;
    }

    /**
     * 修改数据
     *
     * @param itemNotes
     */
    public void modifyData(ItemNotes itemNotes) {
        if (itemNotes == null || itemNotes.getNoteid() == 0) {
            return;
        }

        SQLiteDatabase sb = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("notesId", itemNotes.getNoteid());
        values.put("title", itemNotes.getTitle());
        values.put("content", itemNotes.getContent());
        values.put("time", itemNotes.getTitle());
        values.put("hinttime", itemNotes.getHintTime());
        sb.update(DbUserName, values, "notesId=?", new String[]{String.valueOf(itemNotes.getNoteid())});
        sb.close();
        startAlarmService(itemNotes);//关闭以前的定时器重新开启一个计时器
    }

    /**
     * 删除数据
     *
     * @param itemNotes
     */
    public void deleteData(ItemNotes itemNotes) {
        if (itemNotes == null || itemNotes.getNoteid() == 0) {
            return;
        }

        SQLiteDatabase sb = getWritableDatabase();
        int result = sb.delete(DbUserName, "notesId=?", new String[]{String.valueOf(itemNotes.getNoteid())});

        if (result != 1) {
            new ToastUtils(context).showMessage(context.getResources().getString(R.string.toast_del_failure));
        } else {
            new ToastUtils(context).showMessage(context.getResources().getString(R.string.toast_del_success));
            stopAlarmService(itemNotes);
        }
        sb.close();

    }

    /**
     * 开机后重新拉取数据并设置闹钟
     */
    public void resetBootData(){
        ArrayList<ItemNotes> list = new ArrayList<>();
        list.addAll(readData());
        for (ItemNotes itemNotes:list){
            startAlarmService(itemNotes);
        }
    }
}
