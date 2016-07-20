package com.example.game;

import java.util.Date;
import java.util.Vector;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteUtil 
{
	static SQLiteDatabase sld;
	//���������ݿ�ķ���
    public static void createOrOpenDatabase()
    {
    	try
    	{
	    	sld=SQLiteDatabase.openDatabase
	    	(
	    			"/data/data/com.bn.tl/mydb", //��ǰӦ�ó���ֻ�����Լ��İ��´�����ݿ�
	    			null, 								//CursorFactory
	    			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY //��д�����������򴴽�
	    	);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
  //�ر���ݿ�ķ���
    public static void closeDatabase()
    {
    	try
    	{
	    	sld.close();    
    	}
		catch(Exception e)
		{
            e.printStackTrace();
		}
    }
    //����
    public static void createTable(String sql)
    {
    	createOrOpenDatabase();//����ݿ�
    	try
    	{
        	sld.execSQL(sql);//����
    	}
		catch(Exception e)
		{
            e.printStackTrace();
		}
    	closeDatabase();//�ر���ݿ�
    }
  //�����¼�ķ���
    public static void insert(String sql)
    {
    	createOrOpenDatabase();//����ݿ�
    	try
    	{
        	sld.execSQL(sql);
    	}
		catch(Exception e)
		{
            e.printStackTrace();
		}
		closeDatabase();//�ر���ݿ�
    }
    //��ѯ�ķ���
    public static Vector<Vector<String>> query(String sql)
    {
    	createOrOpenDatabase();//����ݿ�
    	Vector<Vector<String>> vector=new Vector<Vector<String>>();//�½���Ų�ѯ��������
    	try
    	{
           Cursor cur=sld.rawQuery(sql, new String[]{});
        	while(cur.moveToNext())
        	{
        		Vector<String> v=new Vector<String>();
        		int col=cur.getColumnCount();		//����ÿһ�ж������ֶ�
        		for( int i=0;i<col;i++)
				{
					v.add(cur.getString(i));					
				}				
				vector.add(v);
        	}
        	cur.close();		
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		closeDatabase();//�ر���ݿ�
		return vector;
    }  
    
  //������ݿ�
    public  static void initDatabase(){
    	//������
    	String sql="create table if not exists paihangbang(grade int(4),time char(20));";
    	createTable(sql);
    }
    //����ʱ��ķ���
    public static void insertTime(int grade)
    {
    	Date d=new Date();
        String curr_time=(d.getYear()+1900)+"-"+(d.getMonth()+1<10?"0"+
        		(d.getMonth()+1):(d.getMonth()+1))+"-"+d.getDate()+"-"+
        		d.getHours()+"-"+d.getMinutes()+"-"+d.getSeconds();
    	String sql_insert="insert into paihangbang values("+grade+","+"'"+curr_time+"');";
    	insert(sql_insert);
    }
}
