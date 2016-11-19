package com.sp.toread;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by my on 2016/11/12.
 */
public class SearchActivity extends Activity {
    private SearchAdapter adapter;
    private String searchname="",title="",detail="",writer="";
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ArrayList<String> search_title_list=new ArrayList<String>(),search_url_list=new ArrayList<String>()
            ,search_writter_list=new ArrayList<String>(),search_details_list=new ArrayList<String>(),search_bitmapurl=new ArrayList<String>();
    private ArrayList<Bitmap> search_bookvover_list=new ArrayList<Bitmap>();
    private Bitmap bitmap;
    private Bitmap[] bs;
    private DBHelper dbHelper;
    private Cursor cursor;
    private HintPopupWindow hintPopupWindow;
    @Override
    protected void onCreate(Bundle savedInstace){
        super.onCreate(savedInstace);
        setContentView(R.layout.search_layout);
        Intent intent=getIntent();
        searchname=intent.getStringExtra("searchname");
        searchView=(SearchView)findViewById(R.id.search_again);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_search);
        adapter=new SearchAdapter(this,search_title_list,search_writter_list,search_details_list,search_bookvover_list);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        getbyjsoup();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchname = query;
                Log.e("0", "search_name:" + searchname);
                search_bookvover_list.clear();
                getbyjsoup();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        adapter.setOnBookClickListener(new SearchAdapter.onBookClickListener() {
            @Override
            public void onChoose(int position) {
                String title = search_title_list.get(position);
                String url = search_url_list.get(position);
                Intent intent = new Intent(SearchActivity.this, ListActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
        adapter.setOnBooklovedListener(new SearchAdapter.onBooklovedListener() {
            @Override
            public void onLoved(int position) {//改成弹出popupwindow->收藏等功能
                //sqlite添加title.and url
                /*String title=search_title_list.get(position);
                String url=search_url_list.get(position);
                dbHelper=new DBHelper(getBaseContext());
                cursor=dbHelper.select();
                dbHelper.insert(title, url);*/
                View view = linearLayoutManager.findViewByPosition(position);
                hintPopupWindow.showPopupWindow(view);
            }
        });
        dbHelper=new DBHelper(SearchActivity.this);
        cursor=dbHelper.select();
        //下面的操作是初始化弹出数据
        ArrayList<String> strList = new ArrayList<>();
        strList.add("加入书架");
        strList.add("选项2");
        strList.add("选项3");

        ArrayList<View.OnClickListener> clickList = new ArrayList<>();
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dbHelper.insert("gg", "hh");
                //cursor.requery();
                TabLayoutFragment.adddata(SearchActivity.this,"gg","gg");
                Toast.makeText(SearchActivity.this, "点击事件触发", Toast.LENGTH_SHORT).show();
            }
        };

        clickList.add(clickListener);
        clickList.add(clickListener);
        clickList.add(clickListener);

        //初始化
        hintPopupWindow = new HintPopupWindow(this, strList, clickList);
    }
    //按小说名字搜索
    public void getbyjsoup(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    search_url_list.clear();
                    search_title_list.clear();
                    search_writter_list.clear();
                    search_details_list.clear();
                    search_bitmapurl.clear();
                    Log.e("0", "search starts");
                    Document doc = Jsoup.connect("http://so.37zw.com/cse/search?q=" + searchname + "&click=1&s=2041213923836881982&nsid=")
                            .get();
                    // Log.e("0","doc:"+doc);
                    Elements items=doc.select("div.game-legend-a");
                    //Log.e("0","items:"+items);
                    for (Element Item : items) {
                        Log.e("0","Item:"+Item);
                        title=Item.select("h3").text();
                        detail=Item.select("p.result-game-item-desc").text();
                        String ur=Item.select("div.game-legend-a").attr("onclick");
                        ur=ur.substring(17,ur.length()- 1);
                        writer=Item.select("p.result-game-item-info-tag").first().text();
                        String IMG=Item.select("img").attr("src");
                        search_url_list.add(ur);
                        search_bitmapurl.add(IMG);

                        search_title_list.add(title);
                        search_writter_list.add(writer);
                        search_details_list.add(detail);
                        //search_bookvover_list.add(bitmap);
                        Log.e("0", "tt:" + title + "  detail:" + detail + "      ur:" + ur + "    writer:" + writer+"    img:"+IMG);
                    }
                    Log.e("0", "thread ends");
                } catch (IOException e){
                    getbyjsoup();
                    e.printStackTrace();
                }
                Message.obtain(mhandeler, 0).sendToTarget();

            }
        }).start();

    }
    Handler mhandeler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("0", "hhhhhhhhhhhhhh");

                    adapter.notifyDataSetChanged();
                    DoGetbitmap();
                    //toolbar.setTitle(name);
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
    //获取小说封面图片
    private void DoGetbitmap() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                for(int i=0;i<search_title_list.size();i++) {
                    Log.e("0","url;"+search_bitmapurl.get(i));
                    HttpGet httPost = new HttpGet(search_bitmapurl.get(i));
                    HttpClient client = new DefaultHttpClient();
                    // 请求超时
                    client.getParams().setParameter(
                            CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
                    // 读取超时
                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                            10000);
                    try {
                        HttpResponse httpResponse = client.execute(httPost);
                        byte[] bytes = new byte[1024];
                        bytes = EntityUtils.toByteArray(httpResponse.getEntity());
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        search_bookvover_list.add(bitmap);
                    } catch (IOException e) {
                        Log.e("0", "fail get bitmap");
                        e.printStackTrace();
                    }
                    Log.e("0", "success to get bitmap");
                }
                Log.e("0", "success to get bitmaps");
                Message.obtain(mhandeler,1).sendToTarget();
            }
        }).start();
    }

}
