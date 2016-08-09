package com.example.arplayer;

import java.io.File;
import java.util.ArrayList;

//import android.os.SystemProperties;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see
 */
public class PlayerListActivity extends Activity {
    private ListView mPlayerListView;
    private BaseAdapter mPlayerAdapter;
    private IntentFilter mIntentFilter;
    private ArrayList<playmsg> mPlayList = new ArrayList<playmsg>();
    private ArrayList<String> mList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.Vrtrid.KEY.action");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_playlist);

        // GetVideoFileList();
        // mPlayerAdapter.notifyDataSetChanged();
        // mPlayerListView.invalidate();
        GetFileList(true);
        GetFileList(false);

        mPlayerListView = (ListView) findViewById(R.id.Playerlist);

		/*String buildnumber = SystemProperties.get("gsm.project.baseband");
		
		if (buildnumber == null
				|| !(buildnumber.toLowerCase().contains("vsun"))) {
			mPlayerListView.setVisibility(View.GONE);
			
			return;
		}*/


        // mPlayerListView.setDrawingCacheEnabled(true);
        mPlayerAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mPlayList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
				/*
				 * final LineMsg btv; if(position>=mPlayList.size()) return
				 * null; if (convertView == null) { btv = new
				 * LineMsg(PlayerListActivity
				 * .this,mPlayList.get(position).Name,mPlayList
				 * .get(position).fileSize); }else { btv = (LineMsg)
				 * convertView; btv.setText(mPlayList.get(position).Name);
				 * btv.setSize(mPlayList.get(position).fileSize); } return btv;
				 */

                if (position >= mPlayList.size())
                    return null;

                LayoutInflater inflater = LayoutInflater.from(PlayerListActivity.this);
                View playerView;
                if (convertView == null) {
                    playerView = inflater.inflate(R.layout.player_thumbnail,
                            parent, false);
                } else {
                    playerView = convertView;
                }
                ImageView thumbnail = (ImageView) playerView.findViewById(R.id.thumbnail);
                ImageView video = (ImageView) playerView.findViewById(R.id.video);
                TextView title = (TextView) playerView.findViewById(R.id.title);
                TextView size = (TextView) playerView.findViewById(R.id.size);

                long id = mPlayList.get(position).mId;
                Bitmap bitmap;
                if (mPlayList.get(position).mIsVideo) {
                    video.setVisibility(View.VISIBLE);
                    bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                            getContentResolver(), id, Thumbnails.MICRO_KIND, null);
                } else {
                    video.setVisibility(View.INVISIBLE);
                    bitmap = Thumbnails.getThumbnail(
                            getContentResolver(), id, Thumbnails.MICRO_KIND, null);
                }
                thumbnail.setImageBitmap(bitmap);
                title.setText(mPlayList.get(position).Name);
                size.setText(getSize(mPlayList.get(position).fileSize));
                return playerView;
            }

        };

        if (mPlayerListView != null) {
            mPlayerListView.setAdapter(mPlayerAdapter);
            mPlayerAdapter.notifyDataSetChanged();
            mPlayerListView.invalidate();
        }

        mPlayerListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // Log.e("SWEET","play "+ mPlayList.get(position).Name);
                Log.e("PlayerListActivity",
                        ", mIsVideo = " + mPlayList.get(position).mIsVideo);
                // Bundle bundle = new Bundle();
                // SavePlayList();
                Intent intent;
                if (mPlayList.get(position).mIsVideo) {
                    intent = new Intent(PlayerListActivity.this,
                            PlayerActivity.class);
                } else {
                    intent = new Intent(PlayerListActivity.this,
                            ImagePlayerActivity.class);
                }

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("list", mList);
                bundle.putInt("index", position);
                intent.putExtra("playlist", bundle);
                // intent.putExtra("PlayPath",mPlayList.get(position).Path);
                PlayerListActivity.this.startActivity(intent);
            }

        });
    }

    private String getSize(long size) {
        String SizeStr = "";
        if (size > 1024 * 1024) {
            SizeStr = "" + size / 1024 / 1024 + "MB";
        } else if (size > 1024) {
            SizeStr = "" + size / 1024 + "KB";
        } else {
            SizeStr = "" + size + "B";
        }
        return SizeStr;
    }

    // public void addplayfile(File f)
    // {
    // playmsg msg=new playmsg(f.getPath(),f.getName(),f.length());
    // mPlayList.add(msg);
    // mList.add(f.getPath());
    // }

    private void GetFileList(boolean isVideo) {

        ContentResolver mContentResolver = this.getContentResolver();

        Cursor mCursor;
        if (isVideo) {
            mCursor = mContentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        } else {
            mCursor = mContentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        }
        mCursor.moveToFirst();

        int num = mCursor.getCount();
        // Log.e("ar110","num:"+num);

        if (num > 0) {
            do {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaColumns.DATA));
                long id = mCursor.getLong(mCursor.getColumnIndex("_ID"));
                File f = new File(path);
                playmsg msg = null;
                if (path.indexOf("/VRResources/3D/") > 0) {
                    msg = new playmsg(f.getPath(), f.getName(), f.length(), id, isVideo, true);
                } else {
                    msg = new playmsg(f.getPath(), f.getName(), f.length(), id, isVideo, false);
                }

                mPlayList.add(msg);
                mList.add(f.getPath());
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }

	/*
	 * private void GetVideoFileList() {
	 * 
	 * ContentResolver mContentResolver= this.getContentResolver();
	 * 
	 * Cursor mCursor =
	 * mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
	 * null,null,null); mCursor.moveToFirst(); int num=mCursor.getCount(); //
	 * Log.e("ar110","num:"+num); if(num>0) { do{ //String path =
	 * mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
	 * String path =
	 * mCursor.getString(mCursor.getColumnIndex(MediaColumns.DATA)); long id =
	 * mCursor.getLong(mCursor.getColumnIndex("_ID")); File f = new File(path);
	 * playmsg msg = null; if(path.indexOf("/VRResources/3D/")>0) { msg=new
	 * playmsg(f.getPath(),f.getName(),f.length(), id, true); } else { msg=new
	 * playmsg(f.getPath(),f.getName(),f.length(), id, false); }
	 * 
	 * mPlayList.add(msg); mList.add(f.getPath()); }while(mCursor.moveToNext());
	 * } mCursor.close();
	 * 
	 * 
	 * 
	 * /* String filepath="/mnt"; //try{ File f = new File(filepath); File[]
	 * files = f.listFiles(); if(files != null){ int count = files.length; for
	 * (int i = 0; i < count; i++) { File file = files[i];
	 * if(file.isDirectory()) { File fd=new File(file.getPath()+"/"+"Vrtrid");
	 * if(fd.isDirectory()) AutoScanPath(fd.getPath()); } } }
	 */

    // }

	/*
	 * private void AutoScanPath(String filePath) {
	 * 
	 * try{ File f = new File(filePath); File[] files = f.listFiles(); if(files
	 * != null){ int count = files.length; for (int i = 0; i < count; i++) {
	 * File file = files[i]; if(!file.isDirectory()) { String name =
	 * file.getName().toLowerCase(); if (name.endsWith(".ts") ||
	 * name.endsWith(".avi") || name.endsWith(".mp4")|| name.endsWith(".mpg") ||
	 * name.endsWith(".mkv") || name.endsWith(".3gp")) { //addplayfile(file); }
	 * 
	 * } } }
	 * 
	 * }catch(Exception ex){ ex.printStackTrace(); }
	 * 
	 * }
	 */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        Log.e("Sweet", "onKeyDown");
        return true;
    }

}
