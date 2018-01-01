package cn.edu.gdmec.android.boxuegu.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.gdmec.android.boxuegu.bean.UserBean;
import cn.edu.gdmec.android.boxuegu.bean.VideoBean;
import cn.edu.gdmec.android.boxuegu.sqlite.SQLiteHelper;

/**
 * Created by student on 17/12/27.
 */

public class DBUtils {
    private static DBUtils instance=null;
    private static SQLiteHelper helper;
    private static SQLiteDatabase db;
    public DBUtils(Context context){
        helper=new SQLiteHelper(context);
        db=helper.getWritableDatabase();
    }
    public static DBUtils getInstance(Context context){
        if (instance==null){
            instance=new DBUtils(context);
        }
        return instance;
    }
    public void saveUserInfo(UserBean bean){
        ContentValues cv=new ContentValues();
        cv.put("userName",bean.userName);
        cv.put("nickName",bean.nickName);
        cv.put("sex",bean.sex);
        cv.put("signature",bean.signature);
        db.insert(SQLiteHelper.U_USERINFO,null,cv);
    }
    public UserBean getUserInfo(String userName){
        String sql="SELECT * FROM "+SQLiteHelper.U_USERINFO+" WHERE userName=?";
        Cursor cursor=db.rawQuery(sql,new String[]{userName});
        UserBean bean=null;
        while (cursor.moveToNext()){
            bean=new UserBean();
            bean.userName=cursor.getString(cursor.getColumnIndex("userName"));
            bean.nickName=cursor.getString(cursor.getColumnIndex("nickName"));
            bean.sex=cursor.getString(cursor.getColumnIndex("sex"));
            bean.signature=cursor.getString(cursor.getColumnIndex("signature"));
        }
        cursor.close();
        return bean;
    }
    public void updateUserInfo(String key,String value,String userName){
        ContentValues cv=new ContentValues();
        cv.put(key,value);
        db.update(SQLiteHelper.U_USERINFO,cv,"userName=?",new String[]{userName});
    }
    public void saveVideoPlayList(VideoBean bean,String userName){
        if (hasVideoPlay(bean.chapterId,bean.videoId,userName)){
            boolean isDelete=delVideoPlay(bean.chapterId,bean.videoId,userName);
            if (!isDelete){
                return;
            }
        }
        ContentValues cv=new ContentValues();
        cv.put("userName",userName);
        cv.put("chapterId",bean.chapterId);
        cv.put("videoId",bean.videoId);
        cv.put("videoPath",bean.videoPath);
        cv.put("title",bean.title);
        cv.put("secondTitle",bean.secondTitle);
        db.insert(SQLiteHelper.U_VIDEO_PLAY_LIST,null,cv);
    }
    public boolean hasVideoPlay(int chapterId,int videoId,String userName){
        boolean hasVideo=false;
        String sql="SELECT * FROM "+SQLiteHelper.U_VIDEO_PLAY_LIST+" WHERE chapterId=? AND videoId=? AND userName=?";
        Cursor cursor=db.rawQuery(sql,new String[]{chapterId+"",videoId+"",userName});
        if (cursor.moveToFirst()){
            hasVideo=true;
        }
        cursor.close();
        return hasVideo;
    }
    public boolean delVideoPlay(int chapterId,int videoId,String userName){
        boolean delSuccess=false;
        int row=db.delete(SQLiteHelper.U_VIDEO_PLAY_LIST,"chapterId=? AND videoId=? AND userName=?",
                new String[]{chapterId+"",videoId+"",userName});
        if (row>0){
            delSuccess=true;
        }
        return delSuccess;
    }
    public List<VideoBean> getVideoHistory(String userName){
        String sql="SELECT * FROM "+SQLiteHelper.U_VIDEO_PLAY_LIST+" WHERE userName=?";
        Cursor cursor=db.rawQuery(sql,new String[]{userName});
        List<VideoBean> vbl=new ArrayList<VideoBean>();
        VideoBean bean=null;
        while (cursor.moveToNext()){
            bean=new VideoBean();
            bean.chapterId=cursor.getInt(cursor.getColumnIndex("chapterId"));
            bean.videoId=cursor.getInt(cursor.getColumnIndex("videoId"));
            bean.videoPath=cursor.getString(cursor.getColumnIndex("videoPath"));
            bean.title=cursor.getString(cursor.getColumnIndex("title"));
            bean.secondTitle=cursor.getString(cursor.getColumnIndex("secondTitle"));
            vbl.add(bean);
            bean=null;
        }
        cursor.close();
        return vbl;
    }
}
