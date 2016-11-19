package com.sp.toread;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by my on 2016/10/27.
 */
public class TabLayoutFragment extends Fragment {
    public static String TABLAYOUT_FRAGMENT = "tab_fragment";
    private int type;
    private DBHelper dbHelper;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private String url="http://www.37zw.com";
    private int _id=0;
    private ListViewAdapter adapter;
    private ListView listView;
    private TextView textView;
    private ArrayList<String> titles=new ArrayList<String>(),urls=new ArrayList<String>();

    public static TabLayoutFragment newInstance(int type) {
        TabLayoutFragment fragment = new TabLayoutFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TABLAYOUT_FRAGMENT, type);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (int) getArguments().getSerializable(TABLAYOUT_FRAGMENT);
        }
    }
    public static void adddata(Context context,String title,String name){
        DBHelper dbHelper1=new DBHelper(context);
        dbHelper1.insert(title, name);
        Cursor cursor=dbHelper1.select();
        cursor.requery();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(type==1){
            Log.e("0","oncreate mine");
            View view = inflater.inflate(R.layout.mine, container, false);
            dbHelper=new DBHelper(getContext());
            cursor=dbHelper.select();

            recyclerView=(RecyclerView)view.findViewById(R.id.recycle_mine);
            myRecyclerAdapter=new MyRecyclerAdapter(getContext(),cursor,dbHelper);

            StaggeredGridLayoutManager staggeredGridLayoutManager= new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            //设置布局管理器
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            //设置Adapter
            recyclerView.setAdapter(myRecyclerAdapter);

            Log.e("0", "getcount:" + myRecyclerAdapter.getItemCount());
            //设置增加或删除条目的动画
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            return view;

        } else{
            Log.e("0","oncreate online");
            getsomeInteresting();
            View view = inflater.inflate(R.layout.online, container, false);
            listView=(ListView)view.findViewById(R.id.list_content);
            textView=(TextView)view.findViewById(R.id.item_title);
            adapter=new ListViewAdapter(getContext(),R.layout.list_item,titles,false);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String title =titles.get(position);
                    String url = urls.get(position);
                    Intent intent = new Intent(getContext(), ListActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
    Handler mhandeler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("0", "hhhhhhhhhhhhhh");
                    adapter.notifyDataSetChanged();
                    //toolbar.setTitle(name);
                    break;
                default:
                    break;
            }
        }
    };
    public void getsomeInteresting(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    titles.clear();urls.clear();
                    Log.e("0", "thread starts");
                    Document doc = Jsoup.connect(url).get();
                    Elements items=doc.select("div.novelslist");
                    Elements hh=items.select("li");
                    for (Element Item : hh) {
                        String title=Item.select("li").text();
                        String ur=Item.select("a").attr("href");
                        Log.e("0","title="+title+"  url="+ur);
                        titles.add(title);
                        urls.add(ur);
                    }
                    Log.e("0","thread ends");
                }catch (IOException e){
                    e.printStackTrace();
                }
                Message.obtain(mhandeler,0).sendToTarget();
            }
        }).start();
    }
}