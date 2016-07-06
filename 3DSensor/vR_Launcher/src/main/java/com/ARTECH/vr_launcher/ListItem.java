package com.ARTECH.vr_launcher;

public class ListItem {
	public String Path;
	public long ThumbnailId;
	public Boolean IsVideo;
	
	public ListItem(String path,long id,Boolean viedo)
	{
		Path=path;
		ThumbnailId=id;
		IsVideo=viedo;
	}
}
