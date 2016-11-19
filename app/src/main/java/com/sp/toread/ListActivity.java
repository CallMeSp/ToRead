package com.sp.toread;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.util.AsyncListUtil;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by my on 2016/11/12.
 */
public class ListActivity extends Activity {
    private ListView listView;
    private TextView textView;
    private ToggleButton toggleButton;
    private ListViewAdapter adapter;
    private ArrayList<String> catalogitems=new ArrayList<String>(),nextchapter_url_list=new ArrayList<String>();
    private String url,mstitle;
    private boolean state;
    private DBHelper dbHelper;
    private Cursor cursor;
    private int _id=0,pos_from_text=0;
    private HintPopupWindow hintPopupWindow;

    @Override
    protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.listactivity_layout);

        state=false;

        adapter=new ListViewAdapter(this,R.layout.list_item,catalogitems,state);
        listView=(ListView)findViewById(R.id.catalague_list);
        listView.setAdapter(adapter);
        textView=(TextView)findViewById(R.id.catalog_title);
        toggleButton=(ToggleButton)findViewById(R.id.change_sequence);
        Intent intent=getIntent();
        mstitle=intent.getStringExtra("title");
        textView.setText(mstitle);
        url=intent.getStringExtra("url");
        try {
            pos_from_text=Integer.valueOf(intent.getStringExtra("fromtext")).intValue();
        }catch (NumberFormatException e){
            Log.e("0","error");
        }

        gett(url);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if (state) {
                    position = catalogitems.size() - position - 1;
                }*/
                Intent intent = new Intent(ListActivity.this, TextContent.class);
                intent.putExtra("titlelist", catalogitems);
                intent.putExtra("urllist", nextchapter_url_list);
                String a = nextchapter_url_list.get(position);
                intent.putExtra("url", a);
                intent.putExtra("position", "" + position);
                intent.putExtra("state",""+state);
                intent.putExtra("fromlist_1",mstitle);
                intent.putExtra("fromlist_2", url);

                Log.e("0", "position=" + position);
                startActivity(intent);
                finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//写个popupwindow事件。弹出添加书签功能。
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                hintPopupWindow.showPopupWindow(view);
                return true;
            }
        });
        toggleButton.setChecked(false);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changestate();
                Collections.reverse(catalogitems);
                Collections.reverse(nextchapter_url_list);
                Message.obtain(mhandeler, 0, ".......").sendToTarget();
            }
        });
        /*holder.sectionToggleButton.setChecked(section.isExpanded);
        holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
            }
        });*/
        ArrayList<String> strList = new ArrayList<>();
        strList.add("添加书签");
        strList.add("收藏本章");
        strList.add("选项3");

        ArrayList<View.OnClickListener> clickList = new ArrayList<>();
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListActivity.this, "点击事件触发", Toast.LENGTH_SHORT).show();
            }
        };

        clickList.add(clickListener);
        clickList.add(clickListener);
        clickList.add(clickListener);

        //初始化
        hintPopupWindow = new HintPopupWindow(this, strList, clickList);
    }
    private void changestate(){
        if (state){
            state=false;
        }else {
            state=true;
        }
    }
    private void gett(String uu){//根据url得到章节list.然后通过handler刷新ui
        final String uuu=uu;
        catalogitems.clear();nextchapter_url_list.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("0", "thread starts");
                    Document doc = Jsoup.connect(uuu).get();
                    Elements items=doc.select("dd");
                    for (Element Item : items) {
                        String title=Item.select("dd").text();
                        String ur=Item.select("a").attr("href");
                        ur=uuu+ur;
                        //Log.e("0", "title:" + title +"      ur:"+ur);
                        catalogitems.add(title);
                        nextchapter_url_list.add(ur);
                        //Message.obtain(mhandeler, 0).sendToTarget();
                    }
                    Log.e("0","thread ends");
                }catch (IOException e){
                    e.printStackTrace();
                }
                Message.obtain(mhandeler, 0, ".......").sendToTarget();
            }
        }).start();
    }
    Handler mhandeler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("0", "hhhhhhhhhhhhhh+"+state);
                    adapter.notifyDataSetChanged();
                    //toolbar.setTitle(name);
                    listView.setSelection(pos_from_text);
                    break;
                default:
                    break;
            }
        }
    };
}
