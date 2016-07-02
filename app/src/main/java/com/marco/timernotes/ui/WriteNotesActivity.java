package com.marco.timernotes.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.marco.timernotes.R;
import com.marco.timernotes.utils.TimeUtils;
import com.marco.timernotes.entity.ItemNotes;
import com.marco.timernotes.utils.ToastUtils;

import java.util.Calendar;

/**
 * User: Administrator
 * Date: 2016/5/25
 * Time: 0:19
 */
public class WriteNotesActivity extends Activity {
    public static final int INTENT_WRITENOTES = 10086;
    private String title, content, timeStr;
    private long time, hintTime;
    private EditText etTitle, etContent;
    private TextView tvTime;
    private Button   btnTime;
    private Activity activity;

    public static Intent createIntent(Context context, ItemNotes item) {
        if (item == null) {
            return new Intent(context, WriteNotesActivity.class);
        }

        return new Intent(context, WriteNotesActivity.class)
                .putExtra("title", item.getTitle())
                .putExtra("time", item.getTime())
                .putExtra("hinttime", item.getHintTime())
                .putExtra("content", item.getContent());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);
        activity = this;
        initView();
    }

    private void initView() {
        btnTime = (Button) findViewById(R.id.btnTime);
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        time = getIntent().getLongExtra("time", System.currentTimeMillis());
        hintTime = getIntent().getLongExtra("hinttime", 0l);
        tvTime = (TextView) findViewById(R.id.tvTime);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);

        timeStr = TimeUtils.fixTime(time);
        tvTime.setText(getString(R.string.modify_time_label) + timeStr);

        if (!TextUtils.isEmpty(title)) {
            etTitle.setText(title);
        }

        if (!TextUtils.isEmpty(content)) {
            etContent.setText(content);
        }
    }

    /**
     * 点击返回按钮
     *
     * @param view
     */
    public void onBackClick(View view) {
        finish();
    }

    public void onSaveClick(View view) {
        save();
    }

    public void onSelectTime(View view) {
        showTimePicker();
    }

    private TimePicker timePicker;
    private Calendar   newDate;

    private void showTimePicker() {
        newDate = Calendar.getInstance();
        newDate.set(Calendar.SECOND, 0);

        timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        timePicker.setOnTimeChangedListener(
                new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newDate.set(Calendar.MINUTE, minute);
                    }
                }
                                           );

        new AlertDialog.Builder(this).setTitle(getString(R.string.dialog_time_title))
                .setView(timePicker)
                .setPositiveButton(
                        "确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                                    newDate.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                    newDate.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                                }
                                hintTime = newDate.getTime().getTime();
                                btnTime.setText(getString(R.string.btn_hinttime_label) + TimeUtils.fixTime(hintTime));
                            }
                        }
                                  )
                .create().show();
    }

    /**
     * 返回保存的数据
     * 判断标题和内容不为空
     * 时间如果为初始值，则不提示事件
     */
    public void save() {
        title = etTitle.getText().toString().trim();
        content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError(getString(R.string.error_title_null));
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(content)) {
            etContent.setError(getString(R.string.error_content_null));
            etContent.requestFocus();
            return;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("title", TextUtils.isEmpty(title) ? "" : title);
        returnIntent.putExtra("content", TextUtils.isEmpty(content) ? "" : content);
        returnIntent.putExtra("time", time);
        returnIntent.putExtra("hinttime", hintTime);
        setResult(RESULT_OK, returnIntent);

        finish();
    }
}
