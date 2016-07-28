package com.example.a3d_load.util;

import android.content.res.Resources;
import android.util.Log;

import com.example.a3d_load.MySurfaceView;
import com.example.a3d_load.model.LoadedObjectVertexAndIndex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadUtil2 {

    public static LoadedObjectVertexAndIndex loadFromFile(String fname, Resources r, MySurfaceView mv) {

        LoadedObjectVertexAndIndex lo = null;

        ArrayList<Float> alv = new ArrayList<Float>();//原始顶点坐标列表
        ArrayList<Short> indices = new ArrayList<Short>();//结果顶点坐标列表
        //顶点组装面索引列表--根据面的信息从文件中加载
        ArrayList<Integer> alFaceIndex = new ArrayList<Integer>();
        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            while ((temps = br.readLine()) != null) {
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {//此行为顶点坐标
                    alv.add(Float.parseFloat(tempsa[1]));
                    alv.add(Float.parseFloat(tempsa[2]));
                    alv.add(Float.parseFloat(tempsa[3]));
                }  else if (tempsa[0].trim().equals("f")) {//此行为三角形面
                    short index ;//三个顶点索引值的数组
                    //提取三角形第一个顶点的坐标
                    index = (short) (Integer.parseInt(tempsa[1].split("/")[0]) - 1);
                    indices.add(index);
                    //提取三角形第二个顶点的坐标
                    index = (short) (Integer.parseInt(tempsa[2].split("/")[0]) - 1);
                    indices.add(index);
                    //提取三角形第三个顶点的坐标
                    index = (short) (Integer.parseInt(tempsa[3].split("/")[0]) - 1);
                    indices.add(index);
                }
            }
            //生成顶点数组
            int size = alv.size();
            float[] vXYZ = new float[size];
            for (int i = 0; i < size; i++) {
                vXYZ[i] = alv.get(i);
            }
            size = indices.size();
            short[] index = new short[size];
            for (int i = 0; i < size ; i++) {
                index[i] = indices.get(i);
            }
            lo = new LoadedObjectVertexAndIndex(mv, vXYZ,index);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }
}
