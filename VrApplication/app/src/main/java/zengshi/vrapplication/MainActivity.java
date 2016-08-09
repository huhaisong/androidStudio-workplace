package zengshi.vrapplication;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends ListActivity {


    private ArrayList<playmsg> mPlayList = new ArrayList<>();
    private ArrayList<String> mList = new ArrayList<>();
    private BaseAdapter mPlayerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetFileList(true);
        GetFileList(false);

        mPlayerAdapter=new BaseAdapter() {
            @Override
            public int getCount () {
                return mPlayList.size();
            }

            @Override
            public Object getItem ( int position){
                return null;
            }

            @Override
            public long getItemId ( int position){
                return 0;
            }

            @Override
            public View getView ( int position, View convertView, ViewGroup parent){


                if (position >= mPlayList.size())
                    return null;
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View playerView;
                if (convertView == null) {
                    playerView = inflater.inflate(R.layout.player_thumbnail, parent, false);
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
                    video.setVisibility(android.view.View.VISIBLE);
                    bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                            getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                } else {
                    video.setVisibility(android.view.View.INVISIBLE);
                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                            getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                }
                thumbnail.setImageBitmap(bitmap);
                title.setText(mPlayList.get(position).Name);
                size.setText(getSize(mPlayList.get(position).fileSize));
                return playerView;
            }

        };
        setListAdapter(mPlayerAdapter);
    }


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
        if (num > 0) {
            do {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns.DATA));
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

}
