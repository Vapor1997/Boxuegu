package cn.edu.gdmec.android.boxuegu.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import cn.edu.gdmec.android.boxuegu.R;

/**
 * Created by student on 17/12/27.
 */

public class VideoPlayActivity extends AppCompatActivity{
    private VideoView videoView;
    private MediaController controller;
    private String videoPath;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        videoPath=getIntent().getStringExtra("videoPath");
        position=getIntent().getIntExtra("position",0);
        init();
    }
    private void init(){
        videoView=(VideoView) findViewById(R.id.videoView);
        controller=new MediaController(this);
        videoView.setMediaController(controller);
        play();
    }
    private void play(){
        if ((TextUtils.isEmpty(videoPath))){
            Toast.makeText(this,"本地没有此视频，暂无法播放",Toast.LENGTH_SHORT).show();
            return;
        }
        String Uri="android.resource://"+getPackageName()+"/"+R.raw.video11;
        videoView.setVideoPath(Uri);
        videoView.start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Intent data=new Intent();
        data.putExtra("position",position);
        setResult(RESULT_OK,data);
        return super.onKeyDown(keyCode,event);
    }
}
