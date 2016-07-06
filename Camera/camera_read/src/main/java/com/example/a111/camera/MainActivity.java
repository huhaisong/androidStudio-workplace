package com.example.a111.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends Activity implements View.OnClickListener {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private AutoFitTextureView textureView;
    private String mCameraId = "0";
    private Size previewSize;
    private CameraDevice cameraDevice;  //系统摄像头
    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest previewRequest;
    private CameraCaptureSession captureSession;  //程序需要预览,拍照时都由该方法控制
    private ImageReader imageReader;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

                    //当TextureView可用时，打开摄像头
                    openCamera(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {//摄像头被打开

            MainActivity.this.cameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {//摄像头断开连接

            camera.close();
            MainActivity.this.cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {//打开摄像头出现错误

            camera.close();
            MainActivity.this.cameraDevice = null;
            MainActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = (AutoFitTextureView) findViewById(R.id.ttv);
        textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        captureStillPicture();
    }


    //打开摄像头
    private void openCamera(int width, int height) {

        setUpCameraOutputs(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            /**
             * 第一个参数代表要打开的摄像头的ID（一般0代表后置，1代表前置）
             * 第二个参数代表摄像头的监听 which is invoked once the camera is opened
             * 第三个参数代表执行callback的Handler
             */
            manager.openCamera(mCameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Camera
     * 1.设置textureView的最佳预览尺寸
     * 2.当imageReader获取到的照片数据可用时进行保存
     *
     * @param width  textureView的宽度
     * @param height textureView的高度
     */
    private void setUpCameraOutputs(int width, int height) {

        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {

            /**
             * 1.设置最佳尺寸
             */
            //获取指定摄像头的特性
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            //获取摄像头支持的配置属性
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //获取摄像头支持的最大尺寸
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            //获取最佳的预览尺寸
            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, largest);
            //根据选中的预览尺寸来调整预览组件（TextureView）的长宽比
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == 1) {
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());

            } else {
                textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }


            /**
             * 1.创建ImageReader对象，获取摄像头抓取的图像数据并保存
             */
            //创建一个ImageReader对象，用于获取摄像头的图像数据
            imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {//当照片数据可用时，调用该方法

                    //获取捕获的照片数据
                    Image image = reader.acquireNextImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    //使用IO流将照片写入指定文件
                    File file = new File(getExternalFilesDir(null), "pic.jpg");
                    buffer.get(bytes);
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        outputStream.write(bytes);
                        Toast.makeText(MainActivity.this, "保存：" + file, Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        image.close();
                    }
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //获取最佳的预览尺寸
    private Size chooseOptimalSize(Size[] outputSizes, int width, int height, Size largest) {
        //手机摄像头支持的大过预览surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        int w = largest.getWidth();
        int h = largest.getHeight();
        for (Size option : outputSizes) {
            if (option.getHeight() == option.getWidth() * h / w && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        //如果找到多个预览尺寸，获取其中面积最小的，找不到则返回第一个
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return outputSizes[0];
        }
    }

    //创建预览Session
    private void createCameraPreviewSession() {

        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            //创建作为预览的CaptureRequest.Builder
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //将textureView的surface（texture）作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(new Surface(texture));
            //创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {//配置成功
                            Log.i("test", "-----------配置成功");
                            if (cameraDevice == null) {
                                return;
                            }
                            //当摄像头已经准备好时，开始显示预览
                            captureSession = session;
                            try {
                                //设置自动对焦
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                //设置自动曝光
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                //开始显示相机预览
                                previewRequest = previewRequestBuilder.build();
                                //设置预览时连续捕获图像数据
                                captureSession.setRepeatingRequest(previewRequest, null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {//配置失败

                            Log.i("test", "-----------配置失败");
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //捕获静态图像（拍照）
    private void captureStillPicture() {

        if (cameraDevice == null) {
            return;
        }

        try {
            //创建作为拍照的CaptureRequest.Builder
            final CaptureRequest.Builder captureRequestBuilder = cameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            //将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(imageReader.getSurface());
            //设置自动对焦模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //设置自动曝光模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            //获取设备方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            //根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    ORIENTATIONS.get(rotation));
            //停止连续取景
            captureSession.stopRepeating();
            //捕获静态图像（拍照）
            captureSession.capture(captureRequestBuilder.build(),
                    new CameraCaptureSession.CaptureCallback() {
                        //拍照完成调用该方法
                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session,
                                                       CaptureRequest request,
                                                       TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);

                            try {
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                                        CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                //打开连续取景
                                captureSession.setRepeatingRequest(previewRequest, null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //比较两个Size的比较器
    private class CompareSizesByArea implements java.util.Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth()
                    * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

}
