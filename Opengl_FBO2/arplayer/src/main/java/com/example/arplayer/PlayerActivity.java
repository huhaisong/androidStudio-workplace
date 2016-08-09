package com.example.arplayer;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class PlayerActivity extends Activity {
    // private SurfaceHolder mSurfaceHolder = null;
    // private MediaPlayer mMediaPlayer = null;
    private VideoView surface;
    private View mBottun;
    private View vRButtonView;
    private Boolean vrmode = false;
    private Boolean mLeftDown = false;
    private Boolean mRightDown = false;
    private ArrayList<String> mPlayList = null;
    private int mCurrentIndex = -1;
    // private String mPlaypath;
    private TextView mText1;
    private TextView mText2;

    private TypeVideoInterface mTypeVideoInterface;

    private long Duration = 0;
    private long CurrentPosition = 0;
    private long Seeklength = 0;
    public static final int PLAYPAUSE = 3001;
    public static final int PLAYERROR = 3002;
    public static final int PLAYEND = 3003;
    public static final int MSGSTOP = 4066;
    public static final int Voiceplus = 4067;
    public static final int Voiceminus = 4068;
    public static final int HIDEMSG = 4060;
    public static final int SEEKLEFT = 4061;
    public static final int SEEKRIGHT = 4062;
    public static final int SEEKLEFTAdd = 4063;
    public static final int SEEKRIGHTAdd = 4064;

    public static final int VIDEO_PTAH = 0x100001;
    private int vol = 50;

    // private Boolean Inited=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // mPlaypath=getIntent().getStringExtra("PlayPath");
            Bundle bundle = getIntent().getExtras().getBundle("playlist");
            mPlayList = bundle.getStringArrayList("list");
            mCurrentIndex = bundle.getInt("index");
            setContentView(R.layout.activity_player);
            surface = (VideoView) findViewById(R.id.surface);

            mTypeVideoInterface = new TypeVideoInterface();

            surface.SetPara(mHandler, mPlayList, mCurrentIndex);
            mText1 = (TextView) findViewById(R.id.Msg0);
            mText2 = (TextView) findViewById(R.id.Msg1);

            mBottun =  findViewById(R.id.button);
            vRButtonView = findViewById(R.id.vr_button);

            // surface.getHolder().addCallback(mSHCallback);
            mBottun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vrmode) {
                        vrmode = false;
                    } else {
                        vrmode = true;
                    }
                    surface.SetVrMode(vrmode);
                }

            });

            vRButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vrmode) {
                        vrmode = false;
                    } else {
                        vrmode = true;
                    }
                    surface.SetVrMode(vrmode);

                }

            });

            surface.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surface.pause();
                }

            });
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private class TypeVideoInterface implements VsunInterface.VideoInterface {
        @Override
        public void onType(String path) {
            if (path.contains("/VRResources/3D/")) {

                if (mBottun != null)
                    mBottun.setVisibility(View.GONE);
                if (vRButtonView != null)
                    vRButtonView.setVisibility(View.GONE);

                if (!vrmode)
                    vrmode = true;

            } else {

                if (mBottun != null)
                    mBottun.setVisibility(View.VISIBLE);
                if (vRButtonView != null)
                    vRButtonView.setVisibility(View.VISIBLE);

                if (vrmode)
                    vrmode = false;
            }

            surface.SetVrMode(vrmode);
        }
    }

    private void audioSet() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int audioMax = audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int audioSet = audioMax * vol / 100;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioSet, 0);

        mText1.setText(String.format(this.getString(R.string.Volume), vol));
        mText2.setText(String.format(this.getString(R.string.Volume), vol));
        mHandler.sendEmptyMessageDelayed(HIDEMSG, 2000);
    }

    private void ShowSeekTime() {
        long dmm = Duration / 1000 / 60;
        long dss = Duration / 1000 % 60;
        long cmm = CurrentPosition / 1000 / 60;
        long css = CurrentPosition / 1000 % 60;

        mText1.setText(String.format(this.getString(R.string.SEEK), cmm, css, dmm, dss));
        mText2.setText(String.format(this.getString(R.string.SEEK), cmm, css, dmm, dss));
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (surface.GetInited() == false)
                return;
            switch (msg.what) {
                case MSGSTOP:
                    surface.pause();
                    break;
                case Voiceplus:
                    if (vol < 100)
                        vol = vol + 10;
                    else
                        vol = 100;
                    if (vol > 100)
                        vol = 100;
                    audioSet();
                    break;
                case Voiceminus:
                    if (vol > 0)
                        vol = vol - 10;
                    if (vol < 0)
                        vol = 0;
                    audioSet();
                    break;
                case HIDEMSG:
                    mText1.setText("");
                    mText2.setText("");
                    break;
                case SEEKRIGHT:
                    if (mRightDown == true) {
                        mHandler.sendEmptyMessage(SEEKRIGHTAdd);
                    } else {
                        mHandler.sendEmptyMessageDelayed(HIDEMSG, 2000);
                        if (surface != null)
                            surface.seekTo((int) CurrentPosition);
                    }
                    break;
                case SEEKRIGHTAdd:
                    if (CurrentPosition + Seeklength < Duration) {
                        CurrentPosition = CurrentPosition + Seeklength;
                        ShowSeekTime();
                    }
                    if (mRightDown) {
                        sendEmptyMessageDelayed(SEEKRIGHTAdd, 100);
                    }
                    break;
                case SEEKLEFT:
                    if (mLeftDown == true) {
                        mHandler.sendEmptyMessage(SEEKLEFTAdd);
                    } else {
                        mHandler.sendEmptyMessageDelayed(HIDEMSG, 2000);
                        if (surface != null)
                            surface.seekTo((int) CurrentPosition);
                    }
                    break;
                case SEEKLEFTAdd:
                    if (CurrentPosition - Seeklength > 0) {
                        CurrentPosition = CurrentPosition - Seeklength;
                        ShowSeekTime();
                    } else {
                        CurrentPosition = 0;
                        ShowSeekTime();
                    }
                    if (mLeftDown) {
                        sendEmptyMessageDelayed(SEEKLEFTAdd, 100);
                    }
                    break;
                case PLAYPAUSE:

                    mText1.setText(String.format(PlayerActivity.this
                            .getString(R.string.pause)));
                    mText2.setText(String.format(PlayerActivity.this
                            .getString(R.string.pause)));
                    break;
                case PLAYERROR:

                    mText1.setText(String.format(PlayerActivity.this
                            .getString(R.string.PlayerError)));
                    mText2.setText(String.format(PlayerActivity.this
                            .getString(R.string.PlayerError)));
                    break;
                case PLAYEND:

                    mText1.setText(String.format(PlayerActivity.this
                            .getString(R.string.Playerend)));
                    mText2.setText(String.format(PlayerActivity.this
                            .getString(R.string.Playerend)));
                    break;

                case VIDEO_PTAH:
                    Bundle data = msg.getData();
                    String pathVideo = null;
                    if (data != null)
                        pathVideo = data.getString("video_path");

                    if (mTypeVideoInterface != null)
                        mTypeVideoInterface.onType(pathVideo);
            }
        }
    };

    private void GetViedoTime() {
        if (surface != null) {
            Duration = surface.getDuration();
            CurrentPosition = surface.getCurrentPosition();
            Seeklength = Duration / 100;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        Log.e("AR100", "onKeyUp " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            mHandler.sendEmptyMessage(Voiceminus);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            mHandler.sendEmptyMessage(Voiceplus);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (surface.GetInited()) {
                mLeftDown = true;
                GetViedoTime();
                mHandler.sendEmptyMessage(SEEKLEFT);
            }
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (surface.GetInited()) {
                mRightDown = true;
                GetViedoTime();
                mHandler.sendEmptyMessage(SEEKRIGHT);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return false;
        }

        // return true;
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // super.onKeyUp(keyCode, event);
        Log.e("AR100", "onKeyUp " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            mHandler.sendEmptyMessage(MSGSTOP);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mLeftDown = false;
            mHandler.sendEmptyMessage(SEEKLEFT);
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mRightDown = false;
            mHandler.sendEmptyMessage(SEEKRIGHT);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (vrmode) {
                vrmode = false;
            } else {
                vrmode = true;
            }
            surface.SetVrMode(vrmode);
            return false;
        }
        // return true;
        return super.onKeyUp(keyCode, event);
    }

    protected void onStop() {
        super.onStop();
        if (surface != null)
            surface.stop();
    }
}
