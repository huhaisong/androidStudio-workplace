package com.example.a111.loadobj;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.threed.jpct.*;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import android.opengl.*;

public class MyRenderer implements GLSurfaceView.Renderer {

    // FrameBuffer对象
    private FrameBuffer fb;
    // World对象
    private World world;
    // RGBColor
    private RGBColor back = new RGBColor(50, 50, 50);


    // FPS
    private int fps = 0;
    private long time = System.currentTimeMillis();

    // 默认构造
    // 对该项目的一些优化
    public MyRenderer() {
      /*  // 绘制的最多的Polygon数量,默认为4096,此处如果超过500，则不绘制
        Config.maxPolysVisible = 500;
        // 最远的合适的平面,默认为1000
        Config.farPlane = 1500;
        Config.glTransparencyMul = 0.1f;
        Config.glTransparencyOffset = 0.1f;
        // 使JPCT-AE这个引擎使用顶点而不是顶点数组缓冲对象，因为它可能会使某些硬件更快
        // 但在Samsung Galaxy,它并不能工作的很好，可能使之崩溃，这就是它默认为false的原因
        Config.useVBO = true;
        //这个很关键，百度一下mipmap
        Texture.defaultToMipmapping(false);
        Texture.defaultTo4bpp(true);*/
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
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
        // Object3D对象
        Object3D cube;
        Object3D cylinder1;
        Object3D cylinder2;
        Object3D cylinder3;
        Object3D cylinder4;
        Object3D sphere;
        // 混合渲染
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // 新建world对象
        world = new World();
        // 纹理
        TextureManager tm = TextureManager.getInstance();
        Texture texture2 = new Texture(BitmapHelper.rescale(LoadFile.bitmap1, 512, 512));
        Texture texture3 = new Texture(BitmapHelper.rescale(LoadFile.bitmap2, 512, 512));
        Texture texture4 = new Texture(BitmapHelper.rescale(LoadFile.bitmap3, 512, 512));
        tm.addTexture("texture2", texture2);
        tm.addTexture("texture3", texture3);
        tm.addTexture("texture4", texture4);

        // 画一个很薄的立方体
        cube = new Object3D(12); //12个三角形
        //下面的代码：upper(下)，lower(下)，left(左)，right（右），front（前），back(后);
        //8个顶点
        SimpleVector upperLeftFront = new SimpleVector(-60, 0, -90);
        SimpleVector upperRightFront = new SimpleVector(60, 0, -90);
        SimpleVector lowerLeftFront = new SimpleVector(-60, 5, -90);
        SimpleVector lowerRightFront = new SimpleVector(60, 5, -90);

        SimpleVector upperLeftBack = new SimpleVector(-60, 0, 90);
        SimpleVector upperRightBack = new SimpleVector(60, 0, 90);
        SimpleVector lowerLeftBack = new SimpleVector(-60, 5, 90);
        SimpleVector lowerRightBack = new SimpleVector(60, 5, 90);
        int t1 = tm.getTextureID("texture2");
        int t2 = tm.getTextureID("texture3");
        //画三角形
        //第一个参数是顶点位置()，2,3是纹理位置（u）,以此类推
        //最后一个参数是纹理的ID
        // Front
        cube.addTriangle(upperLeftFront, 0, 0, lowerLeftFront, 0, 1, upperRightFront, 1, 0, t1);
        cube.addTriangle(upperRightFront, 1, 0, lowerLeftFront, 0, 1, lowerRightFront, 1, 1, t1);

        // Back
        cube.addTriangle(upperLeftBack, 0, 0, upperRightBack, 1, 0, lowerLeftBack, 0, 1, t1);
        cube.addTriangle(upperRightBack, 1, 0, lowerRightBack, 1, 1, lowerLeftBack, 0, 1, t1);

        // Upper
        cube.addTriangle(upperLeftBack, 0, 0, upperLeftFront, 0, 1, upperRightBack, 1, 0, t2);
        cube.addTriangle(upperRightBack, 1, 0, upperLeftFront, 0, 1, upperRightFront, 1, 1, t2);

        // Lower
        cube.addTriangle(lowerLeftBack, 0, 0, lowerRightBack, 1, 0, lowerLeftFront, 0, 1, t1);
        cube.addTriangle(lowerRightBack, 1, 0, lowerRightFront, 1, 1, lowerLeftFront, 0, 1, t1);

        // Left
        cube.addTriangle(upperLeftFront, 0, 0, upperLeftBack, 1, 0, lowerLeftFront, 0, 1, t1);
        cube.addTriangle(upperLeftBack, 1, 0, lowerLeftBack, 1, 1, lowerLeftFront, 0, 1, t1);

        // Right
        cube.addTriangle(upperRightFront, 0, 0, lowerRightFront, 0, 1, upperRightBack, 1, 0, t1);
        cube.addTriangle(upperRightBack, 1, 0, lowerRightFront, 0, 1, lowerRightBack, 1, 1, t1);

        cube.setLighting(0);//没效果
        //cube.scale(10);//放大物体
        sphere = Primitives.getSphere(100.0f);
        //得到圆柱体1
        cylinder1 = Primitives.getCylinder(20, 3, 9);
        cylinder1.calcTextureWrapSpherical();
        cylinder1.setTexture("texture2");
        sphere.calcTextureWrapSpherical();
        sphere.setTexture("texture3");
        //圆柱体2是克隆圆柱体1
        cylinder2 = cylinder1.cloneObject();
        cylinder3 = cylinder1.cloneObject();
        cylinder4 = cylinder1.cloneObject();
        //移动圆柱体1到桌子下面
        cylinder1.translate(-58, 30, -90);
        cylinder2.translate(58, 30, -90);
        cylinder3.translate(-58, 30, 90);
        cylinder4.translate(58, 30, 90);
        sphere.translate(0,-30,0);

        //将圆柱体作为cube的孩子，这样，它们可以作为整体旋转平移
        cube.addChild(cylinder1);
        cube.addChild(cylinder2);
        cube.addChild(cylinder3);
        cube.addChild(cylinder4);
        cube.addChild(sphere);
        //加入到world
        world.addObject(cylinder1);
        world.addObject(cylinder2);
        world.addObject(cylinder3);
        world.addObject(cylinder4);
        world.addObject(sphere);
        world.addObject(cube);

        //设置环境光
        world.setAmbientLight(255, 255, 255);
        //这里设置光照的地方不成功，不能显示光照，没有效果
        //设置光照
        Light light = new Light(world);
        light.setPosition(new SimpleVector(cube.getTransformedCenter().x,
                cube.getTransformedCenter().y - 100, cube.getTransformedCenter().z));
        light.setIntensity(255, 0, 0);
        light.setDiscardDistance(14);
        //以上3段代码没有效果
        // 编译所有对象
        world.buildAllObjects();

        // Camera相关
        Camera cam = world.getCamera();
        cam.setPositionToCenter(cube);
        //cam.align(cube);//将相机方向对着物体的Z轴正方向
        //相机绕着X轴旋转20度
        cam.rotateCameraX((float) Math.toRadians(-10));
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 250);
        // 向外以移动
        cam.moveCamera(Camera.CAMERA_MOVEUP, 60);

        //cam.lookAt(plane.getTransformedCenter());

        // 回收内存
        MemoryHelper.compact();
    }
}