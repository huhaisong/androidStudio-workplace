package zengshi.test;

public class ListItem {
	public String Path;
	public long ThumbnailId;
	public Boolean IsVideo;
	
	public ListItem(String path, long id, Boolean viedo)
	{
		Path=path;
		ThumbnailId=id;
		IsVideo=viedo;
	}
}
