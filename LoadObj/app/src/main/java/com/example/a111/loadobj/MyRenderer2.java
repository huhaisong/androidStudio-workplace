package com.example.a111.loadobj;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.threed.jpct.*;
import com.threed.jpct.Matrix;
import com.threed.jpct.util.MemoryHelper;

import android.opengl.*;

public class MyRenderer2 implements GLSurfaceView.Renderer {

    // FrameBuffer对象
    private FrameBuffer fb;
    // World对象
    private World world;
    // RGBColor
    private RGBColor back = new RGBColor(50, 50, 100);
    // Object3D对象
    private Object3D plane = null;
    private Object3D ball1 = null;
    private Object3D ball2 = null;

    // SimpleVector
    // 通过设置组件的x,y,z向量来创建一个SimpleVector对象  ，表示小球的运动方向和速度
    private SimpleVector move = new SimpleVector(-4.0, 0.0, 4.0);

    private boolean collsion = false;//是否发生碰撞
    private SimpleVector tem;

    // FPS
    private int fps = 0;
    private long time = System.currentTimeMillis();

    // 默认构造
    // 对该项目的一些优化
    public MyRenderer2() {
        // 绘制的最多的Polygon数量,默认为4096,此处如果超过500，则不绘制
        Config.maxPolysVisible = 500;
        // 最远的合适的平面,默认为1000
        Config.farPlane = 1500;
        Config.glTransparencyMul = 0.1f;
        Config.glTransparencyOffset = 0.1f;
        // 使JPCT-AE这个引擎使用顶点而不是顶点数组缓冲对象，因为它可能会使某些硬件更快
        // 但在Samsung Galaxy,它并不能工作的很好，可能使之崩溃，这就是它默认为false的原因
        Config.useVBO = true;
        Texture.defaultToMipmapping(true);
        Texture.defaultTo4bpp(true);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        try {
            move();//实现上下左右键
            SimpleVector trsn = ball2.checkForCollisionSpherical(move, 10f);
            //求出两个球之间距离
            SimpleVector length = new SimpleVector(ball1.getTransformedCenter().x - ball2.getTransformedCenter().x,
                    ball1.getTransformedCenter().y - ball2.getTransformedCenter().y, ball1.getTransformedCenter().z - ball2.getTransformedCenter().z);

            //判断两球之间距离是否小于两球半径之和，若小于，则发生碰撞
            if (length.length() <= 20 + 5) {
                collsion = true;//碰撞发生
            }
            //如果发生碰撞
            if (collsion) {
                //判断小球是否已经跨越平面了
                if (ball2.getTranslation(tem).z < -100 || ball1.getTranslation(tem).z > 100) {
                    ball1.clearTranslation();
                    ball2.clearTranslation();
                    ball1.setVisibility(false);//球消失
                    ball2.setVisibility(false);
                } else {//小球设置新的运动方向
                    ball1.translate(new SimpleVector(1, 0, 3));
                    ball2.translate(new SimpleVector(-2, 0, -2));
                }
            }
            //或者if(!collsion)
            //如果没有碰撞
            else ball2.translate(trsn);
            SimpleVector axis = new SimpleVector(1, 0, 1);//球旋转时轴的方向
            ball2.rotateAxis(axis, (float) Math.toRadians(10));//实现球旋转

            // 以定义好的RGBColor清屏
            fb.clear(back);
            // 变换和灯光所有的多 边形
            world.renderScene(fb);
            // 绘制由renderScene产生的场景
            world.draw(fb);
            // 渲染显示图像
            fb.display();
            // fps加1
            fps += 1;
            // 打印输出fps
            if (System.currentTimeMillis() - time > 1000) {
                System.out.println(fps + "fps");
                fps = 0;
                time = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 打印异常信息
            Logger.log("Drawing thread terminated!", Logger.MESSAGE);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        if (fb != null) {
            fb = null;
        }
        // 新产生一个FrameBuffer对象
        fb = new FrameBuffer(gl, width, height);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        Logger.log("onCreate");
        // 混合渲染
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // 新建world对象
        world = new World();
        // 纹理
        TextureManager tm = TextureManager.getInstance();
        Texture texture2 = new Texture(LoadFile.bitmap1);
        Texture texture3 = new Texture(LoadFile.bitmap2);
        Texture texture4 = new Texture(LoadFile.bitmap3);
        tm.addTexture("texture2", texture2);
        tm.addTexture("texture3", texture3);
        tm.addTexture("texture4", texture4);
        // 初始化各3D元素
//		  plane = Primitives.getPlane(20, 10); // 得到平面
        //也可以采用上面得到平面的方法，这里采用的是加载3ds模型
        plane = loadModel("table.3ds", 4f);
        plane.translate(0, -30, 20);
        plane.rotateX(-(float) Math.PI / 2); // 从jpct-ae的坐标旋转到正常坐标系
        plane.rotateY((float) Math.PI / 2);
        ball1 = Primitives.getSphere(10); // 得到球体
        ball1.translate(0, -39, 0);

        ball2 = Primitives.getSphere(10);
        ball2.translate(50, -39, -50);

        //设置纹理贴图方式
        plane.calcTextureWrap();
        plane.setTexture("texture2");
        ball1.calcTextureWrapSpherical();
        ball1.setTexture("texture3");
        ball2.calcTextureWrapSpherical();
        ball2.setTexture("texture4");

        world.addObject(plane);
        world.addObject(ball1);
        world.addObject(ball2);
        //设置碰撞模式
        ball1.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
        ball2.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
        plane.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);

        // 设置环境光
        world.setAmbientLight(255, 255, 255);
        //这里设置光照的地方不成功，不能显示光照，不知道为什么？？？？？？？？？？？？
        //设置光照
        Light light = new Light(world);
        light.setPosition(new SimpleVector(ball1.getTransformedCenter().x,
                ball1.getTransformedCenter().y - 100, ball1.getTransformedCenter().z - 50));
        light.setIntensity(255, 0, 0);
        //以上3段代码没有效果
        // 编译所有对象
        world.buildAllObjects();

        // Camera相关
        Camera cam = world.getCamera();
        cam.setPositionToCenter(ball1);
        cam.align(ball1);//将相机方向对着物体的Z轴正方向
        //相机绕着X轴旋转20度
        cam.rotateCameraX((float) Math.toRadians(20));
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 250);
        // 向外以移动
        cam.moveCamera(Camera.CAMERA_MOVEUP, 60);

        //cam.lookAt(plane.getTransformedCenter());

        // 回收内存
        MemoryHelper.compact();

    }

    // 载入模型
    private Object3D loadModel(String filename, float scale) {
        // 将载入的3ds文件保存到model数组中
        Object3D[] model = Loader.load3DS(LoadFile2.loadf(filename), scale);
        // 取第一个3ds文件
        Object3D o3d = new Object3D(0);
        // 临时变量temp
        Object3D temp ;
        // 遍历model数组
        for (Object3D aModel : model) {
            // 给temp赋予model数组中的某一个
            temp = aModel;
            // 设置temp的中心为 origin (0,0,0)
            temp.setCenter(SimpleVector.ORIGIN);
            // 沿x轴旋转坐标系到正常的坐标系(jpct-ae的坐标中的y,x是反的)
            temp.rotateX((float) (-.5 * Math.PI));
            // 使用旋转矩阵指定此对象旋转网格的原始数据
            temp.rotateMesh();
            // new 一个矩阵来作为旋转矩阵
            temp.setRotationMatrix(new Matrix());
            // 合并o3d与temp
            o3d = Object3D.mergeObjects(o3d, temp);
            // 主要是为了从桌面版JPCT到android版的移徝(桌面版此处为o3d.build())
            o3d.compile();
        }
        // 返回o3d对象
        return o3d;
    }

    public void move() {
        Camera cam = world.getCamera();
        if (Jpct_Collsion3Activity.up) { // 按向上方向键
            cam.moveCamera(cam.getDirection(), -2);//摄像机向里面移动
        }
        if (Jpct_Collsion3Activity.down) {
            cam.moveCamera(cam.getDirection(), 2);//向外移动
        }
        if (Jpct_Collsion3Activity.left) {
            plane.rotateY((float) Math.toRadians(-10));// 向左旋转
        }
        if (Jpct_Collsion3Activity.right) {
            plane.rotateY((float) Math.toRadians(10)); // 向右旋转
        }
    }
}


