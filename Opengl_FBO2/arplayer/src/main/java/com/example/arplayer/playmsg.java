package com.example.arplayer;

public class playmsg {
    public String Path;
    public String Name;
    public long fileSize;
    public long mId;
    public boolean mIsVideo;
    public boolean mIs3D;

    public playmsg(String path, String name, long fsize, long id, boolean isVideo, boolean is3d) {
        Path = path;
        Name = name;
        fileSize = fsize;
        mId = id;
        mIsVideo = isVideo;
        mIs3D = is3d;
    }
}
