package com.ARTECH.vr_launcher;

import java.io.IOException;
import java.util.List;

import com.threed.jpct.util.BitmapHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

public class VRListImg {
    private List<ListItem> mList = null;
    public int Page = 0;
    public int PageSize = 8;
    public int RowNum = 4;
    public boolean mIsGame = false;
    public int PageCount = 0;
    private Bitmap mHotBitmap = null;
    private Bitmap mBitmap = null;
    private Bitmap mBmpbj = null;
    private Bitmap mBmpHotbj = null;
    public Context mContext;
    public int mDrid = 0;

    public VRListImg(Context context, String Name) {
        mContext = context;
        mBmpbj = null;
        try {
            mBmpbj = BitmapHelper.loadImage(mContext.getAssets().open(Name));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mBmpHotbj = null;
        try {
            mBmpHotbj = BitmapHelper.loadImage(mContext.getAssets().open("bj_hot.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Bitmap GetHotBmp() {
        return mHotBitmap;
    }

    public Bitmap GetBmp() {
        return mBitmap;
    }

    public void AddPage() {
        if (Page < PageCount - 1) {
            Page++;
        }
    }

    public void CutPage() {
        if (Page > 0) {
            Page--;
        }
    }

    public void SetList(List<ListItem> List, int menuId, String Name) {
        if (Name != null) {
            if (mBmpbj != null) {
                mBmpbj.recycle();
            }
            mBmpbj = null;

            try {
                mBmpbj = BitmapHelper.loadImage(mContext.getAssets().open(Name));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //	Log.e("AR110","SetList mBmpbj="+mBmpbj);
        }
        Page = 0;
        mList = List;
        if (mList != null) {
            PageCount = mList.size() / PageSize;
            if (mList.size() % PageSize > 0) {
                PageCount++;
            }
        }
        mIsGame = menuId == 3;
    }

    public int GetPageSize() {
        if (mList == null)
            return 0;
        int Index = Page * PageSize;
        int pagesize = mList.size() - Index;
        if (pagesize < 0)
            pagesize = 0;
        if (pagesize > PageSize)
            pagesize = PageSize;
        return pagesize;
    }

    public void Draw() {
        if (mBmpbj == null) {
            return;
        }
        int BoxWidth = 160;
        int BoxHeight = 112;
        int SUBWIDTH = 152;
        int SUBHEIGHT = 100;
        int LEFT = 4;
        int TOP = 98;
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = Bitmap.createBitmap(mBmpbj.getWidth(), mBmpbj.getHeight(), Bitmap.Config.ARGB_8888);
        mDrid++;
        if (mDrid > 100)
            mDrid = 0;
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawBitmap(mBmpbj, 0, 0, null);
        if (mList != null) {
            int Index = Page * PageSize;
            int i = 0;
            while (i < PageSize && Index < mList.size()) {//画每一个item
                ListItem item = mList.get(Index);
                if (item != null) {
                    long id = item.ThumbnailId;
                    Bitmap bitmap;
                    if (mIsGame) {//游戏
                        bitmap = null;
                        try {
                            bitmap = BitmapHelper.loadImage(mContext.getAssets().open(item.Path));
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        if (item.IsVideo)
                            bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                                    mContext.getContentResolver(), id, Thumbnails.MICRO_KIND, null);
                        else
                            bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                                    mContext.getContentResolver(), id, Thumbnails.MICRO_KIND, null);
                    }
                    if (bitmap != null) {
                        //canvas.drawBitmap(bitmap, 0, 0, null);
                        int left = LEFT + (i % RowNum) * (BoxWidth);
                        int top = TOP + (i / RowNum) * (BoxHeight);

                        //画视频或者图片或者游戏的缩略图
                        canvas.drawBitmap(bitmap,
                                new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                                new Rect(left, top, left + SUBWIDTH, top + SUBHEIGHT), null);
                        bitmap.recycle();
                        //如果是视频，画一个播放按钮
                        if (item.IsVideo) {
                            Bitmap videobitmap = null;
                            try {
                                videobitmap = BitmapHelper.loadImage(mContext.getAssets().open("video.png"));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (videobitmap != null) {
                                int tleft = (SUBWIDTH - videobitmap.getWidth()) / 2 + left;
                                int ttop = (SUBHEIGHT - videobitmap.getHeight()) / 2 + top;
                                canvas.drawBitmap(videobitmap, new Rect(0, 0, videobitmap.getWidth(), videobitmap.getHeight()), new Rect(tleft, ttop, tleft + videobitmap.getWidth(), ttop + videobitmap.getHeight()), null);
                                videobitmap.recycle();
                            }
                        }
                    } else {
                        Log.e("ar110", "null id=" + id);
                    }
                }
                i++;
                Index++;
            }
        }
        if (mHotBitmap != null) {
            mHotBitmap.recycle();
            mHotBitmap = null;
        }
        mHotBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mDrid++;
        if (mDrid > 100)
            mDrid = 0;
        Canvas hotcanvas = new Canvas(mHotBitmap);
        hotcanvas.drawBitmap(mBitmap, 0, 0, null);
        hotcanvas.drawBitmap(mBmpHotbj, 0, 0, null);
    }
}
