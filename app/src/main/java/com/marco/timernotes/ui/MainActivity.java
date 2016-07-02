package com.marco.timernotes.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.marco.timernotes.R;
import com.marco.timernotes.db.SqliteHelpUtils;
import com.marco.timernotes.entity.ItemNotes;
import com.marco.timernotes.utils.TimeUtils;

import java.util.ArrayList;

/**
 * 显示事件列表，单击修改，长按删除
 */
public class MainActivity extends Activity {
    private ListView lv;
    private ArrayList<ItemNotes> itemNotes = new ArrayList<ItemNotes>();
    private NotesAdapter    notesAdapter;
    private ItemNotes       item;
    private SqliteHelpUtils sqHelpUtils;
//    private Intent          serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqHelpUtils = new SqliteHelpUtils(this);
        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(notesAdapter = new NotesAdapter());
        initListener();
        getData();
//        serviceIntent = new Intent(MainActivity.this, BackgroundToastService.class);
//        serviceIntent.setAction(BackgroundToastService.updateAction);
//        startService(serviceIntent);
    }

    private void initListener() {
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        position -= lv.getHeaderViewsCount();
                        item = itemNotes.get(position);
                        startActivityForResult(WriteNotesActivity.createIntent(MainActivity.this, item), WriteNotesActivity.INTENT_WRITENOTES);
                    }
                }
                                 );
        lv.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getString(R.string.dialog_title))
                                .setMessage(getString(R.string.dialog_msg))
                                .setPositiveButton(
                                        getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteData(position);
                                            }
                                        }
                                                  ).create().show();
                        return false;
                    }
                }
                                     );
    }

    /**
     * 删除数据
     *
     * @param position
     */
    private void deleteData(int position) {
        item = itemNotes.get(position);
        sqHelpUtils.deleteData(item);
        getData();
//        startService(serviceIntent);
    }

    public void onAddClick(View view) {
        item = null;
        startActivityForResult(WriteNotesActivity.createIntent(MainActivity.this, item), WriteNotesActivity.INTENT_WRITENOTES);
    }

    /**
     * 更新数据
     */
    public void getData() {
        itemNotes.clear();
        itemNotes.addAll(sqHelpUtils.readData());
        System.out.println("sqHelpUtils--->" + itemNotes.size());
        notesAdapter.notifyDataSetChanged();
    }

    /**
     * 适配器
     */
    private class NotesAdapter extends BaseAdapter {
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public ItemNotes getItem(int position) {
            return itemNotes.get(position);
        }

        @Override
        public int getCount() {
            return itemNotes.size();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            NotesHolder notesHolder;
            if (view == null) {
                notesHolder = new NotesHolder();
                view = View.inflate(MainActivity.this, R.layout.item_notes_list, null);
                notesHolder.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                notesHolder.tvContent = (TextView) view.findViewById(R.id.tvContent);
                notesHolder.TvTime = (TextView) view.findViewById(R.id.tvTime);
                view.setTag(notesHolder);
            } else {
                notesHolder = (NotesHolder) view.getTag();
            }

            ItemNotes item = getItem(position);

            if (TextUtils.isEmpty(item.getTitle())) {
                notesHolder.tvTitle.setVisibility(View.GONE);
            } else {
                notesHolder.tvTitle.setText(item.getTitle());
                notesHolder.tvTitle.setVisibility(View.VISIBLE);
            }
            notesHolder.tvContent.setText(TextUtils.isEmpty(item.getContent()) ? getString(R.string.notes_no_content) : item.getContent());
            if (item.getHintTime() == 0) {
                notesHolder.TvTime.setText(getString(R.string.modify_time_label) + "\t\t" + TimeUtils.fixTime(item.getTime()));
            } else {
                notesHolder.TvTime.setText(getString(R.string.btn_hinttime_label) + TimeUtils.fixTime(item.getHintTime()) + "\t\t" + getString(R.string.modify_time_label) + TimeUtils.fixTime(item.getTime()));
            }
            return view;
        }
    }

    private class NotesHolder {
        private TextView tvTitle;
        private TextView tvContent;
        private TextView TvTime;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == WriteNotesActivity.INTENT_WRITENOTES && data != null) {
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");
            long time = data.getLongExtra("time", System.currentTimeMillis());
            long hinttime = data.getLongExtra("hinttime", 0l);
//            如果item为空，即是点击添加笔记的内容
            if (item == null) {
                item = new ItemNotes();
                item.setNoteid(item.hashCode());//用对象的hashcode作为唯一码
                item.setTitle(title);
                item.setContent(content);
                item.setTime(time);
                item.setHintTime(hinttime);
                itemNotes.add(0, item);
                sqHelpUtils.insertData(item);
            } else {
                item.setTitle(title);
                item.setContent(content);
                item.setTime(time);
                item.setHintTime(hinttime);
                sqHelpUtils.modifyData(item);
            }
            getData();

//            startService(serviceIntent); 启动服务
//            sendBroadcast(new Intent(BackgroundToastService.updateAction));//发送服务数据更新广播
        }
    }
}
