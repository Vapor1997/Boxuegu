package cn.edu.gdmec.android.boxuegu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.gdmec.android.boxuegu.R;
import cn.edu.gdmec.android.boxuegu.adapter.VideoListAdapter;
import cn.edu.gdmec.android.boxuegu.bean.VideoBean;
import cn.edu.gdmec.android.boxuegu.utils.AnalysisUtils;
import cn.edu.gdmec.android.boxuegu.utils.DBUtils;

/**
 * Created by student on 17/12/27.
 */

public class VideoListActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_intro,tv_video,tv_chapter_intro;
    private ListView lv_video_list;
    private ScrollView sv_chapter_intro;
    private VideoListAdapter adapter;
    private List<VideoBean> videoList;
    private int chapterId;
    private String intro;
    private DBUtils db;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        chapterId=getIntent().getIntExtra("id",0);
        intro=getIntent().getStringExtra("intro");
        db=DBUtils.getInstance(VideoListActivity.this);
        initData();
        init();
    }
    private void init(){
        tv_intro=(TextView) findViewById(R.id.tv_intro);
        tv_video=(TextView) findViewById(R.id.tv_video);
        lv_video_list=(ListView) findViewById(R.id.lv_video_list);
        tv_chapter_intro=(TextView) findViewById(R.id.tv_chapter_intro);
        sv_chapter_intro=(ScrollView) findViewById(R.id.sv_chapter_intro);
        adapter=new VideoListAdapter(this,new VideoListAdapter.OnSelectListener(){
            @Override
            public void onSelect(int position, ImageView iv){
                adapter.setSelectedPosition(position);
                VideoBean bean=videoList.get(position);
                String videoPath=bean.videoPath;
                adapter.notifyDataSetChanged();
                if (TextUtils.isEmpty(videoPath)){
                    Toast.makeText(VideoListActivity.this,"本地没有此视频，暂无法播放",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if (readLoginStatus()){
                        String userName= AnalysisUtils.redLoginUserName(VideoListActivity.this);
                        db.saveVideoPlayList(videoList.get(position),userName);
                    }
                    Intent intent=new Intent(VideoListActivity.this,VideoPlayActivity.class);
                    intent.putExtra("videoPath",videoPath);
                    intent.putExtra("position",position);
                    startActivityForResult(intent,1);
                }
            }
        });
        lv_video_list.setAdapter(adapter);
        tv_intro.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        adapter.setData(videoList);
        tv_chapter_intro.setText(intro);
        tv_intro.setBackgroundColor(Color.parseColor("#30B4FF"));
        tv_video.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tv_intro.setTextColor(Color.parseColor("#FFFFFF"));
        tv_video.setTextColor(Color.parseColor("#000000"));
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_intro:
                lv_video_list.setVisibility(View.GONE);
                sv_chapter_intro.setVisibility(View.VISIBLE);
                tv_intro.setBackgroundColor(Color.parseColor("#30B4FF"));
                tv_video.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tv_intro.setTextColor(Color.parseColor("#FFFFFF"));
                tv_video.setTextColor(Color.parseColor("#000000"));
                break;
            case R.id.tv_video:
                lv_video_list.setVisibility(View.VISIBLE);
                sv_chapter_intro.setVisibility(View.GONE);
                tv_intro.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tv_video.setBackgroundColor(Color.parseColor("#30B4FF"));
                tv_intro.setTextColor(Color.parseColor("#000000"));
                tv_video.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            default:
                break;
        }
    }
    private void initData(){
        JSONArray jsonArray;
        InputStream is=null;
        try{
            is=getResources().getAssets().open("data.json");
            jsonArray=new JSONArray(read(is));
            videoList=new ArrayList<VideoBean>();
            for (int i=0;i<jsonArray.length();i++){
                VideoBean bean=new VideoBean();
                JSONObject jsonObj=jsonArray.getJSONObject(i);
                if (jsonObj.getInt("chapterId")==chapterId){
                    bean.chapterId=jsonObj.getInt("chapterId");
                    bean.videoId=Integer.parseInt(jsonObj.getString("videoId"));
                    bean.title=jsonObj.getString("title");
                    bean.secondTitle=jsonObj.getString("secondTitle");
                    bean.videoPath=jsonObj.getString("videoPath");
                    videoList.add(bean);
                }
                bean=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private String read(InputStream in){
        BufferedReader reader=null;
        StringBuilder sb=null;
        String line=null;
        try{
            sb=new StringBuilder();
            reader=new BufferedReader(new InputStreamReader(in));
            while ((line=reader.readLine())!=null){
                sb.append(line);
                sb.append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
            return "";
        }finally {
            try {
                if (in!=null){
                    in.close();
                }
                if (reader!=null){
                    reader.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private boolean readLoginStatus(){
        SharedPreferences sp=getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        boolean isLogin=sp.getBoolean("isLogin",false);
        return isLogin;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (data!=null){
            int position=data.getIntExtra("position",0);
            adapter.setSelectedPosition(position);
            lv_video_list.setVisibility(View.VISIBLE);
            sv_chapter_intro.setVisibility(View.GONE);
            tv_intro.setBackgroundColor(Color.parseColor("#FFFFFF"));
            tv_video.setBackgroundColor(Color.parseColor("#30B4FF"));
            tv_intro.setTextColor(Color.parseColor("#000000"));
            tv_video.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
}
