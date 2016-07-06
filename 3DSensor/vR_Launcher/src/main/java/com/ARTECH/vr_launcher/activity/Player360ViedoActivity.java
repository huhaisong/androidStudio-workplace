package com.ARTECH.vr_launcher.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ARTECH.vr_launcher.R;
import com.ARTECH.vr_launcher.surface.VideoView360;

//
public class Player360ViedoActivity extends Activity {
    private VideoView360 surface;
    //	private Boolean vrmode=false;
    private View mView = null;
    private Boolean mLeftDown = false;
    private Boolean mRightDown = false;
    private ArrayList<String> mPlayList = null;
    private int mCurrentIndex = -1;
    //	private String mPlaypath;
    private TextView mText1;
    private TextView mText2;

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
    private int vol = 50;
    //private Boolean Inited=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mPlaypath=getIntent().getStringExtra("PlayPath");
        Bundle bundle = getIntent().getExtras().getBundle("playlist");
        mPlayList = bundle.getStringArrayList("list");
        mCurrentIndex = bundle.getInt("index");
            /*if(bundle.getInt("Vr")==3)
			{
				vrmode=false;
			}else
			{
				vrmode=true;
			}*/
        setContentView(R.layout.activity_player360);
        mView = (View) findViewById(R.id.VRBox);
        mView.setVisibility(View.INVISIBLE);
        surface = (VideoView360) findViewById(R.id.surface360);
        surface.SetPara(mHandler, mPlayList, mCurrentIndex);

        mText1 = (TextView) findViewById(R.id.Msg0);
        mText2 = (TextView) findViewById(R.id.Msg1);

        //	surface.getHolder().addCallback(mSHCallback);

        surface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surface.pause();
            }

        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private int mIy = -1;

    public void onResume() {
        //Log.e("Ar110","onResume");

        super.onResume();
        surface.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
        surface.onPause();
    }

    private void audioSet() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int audioMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
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

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 8888:
                    if (msg.arg1 == 1)
                        Player360ViedoActivity.this.finish();
                    break;
                case 3333:
                    if (msg.arg1 == 1)
                        mView.setVisibility(View.VISIBLE);
                    else
                        mView.setVisibility(View.INVISIBLE);
                    break;
            }

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

                    mText1.setText(String.format(Player360ViedoActivity.this.getString(R.string.pause)));
                    mText2.setText(String.format(Player360ViedoActivity.this.getString(R.string.pause)));
                    break;
                case PLAYERROR:

                    mText1.setText(String.format(Player360ViedoActivity.this.getString(R.string.PlayerError)));
                    mText2.setText(String.format(Player360ViedoActivity.this.getString(R.string.PlayerError)));
                    break;
                case PLAYEND:

                    mText1.setText(String.format(Player360ViedoActivity.this.getString(R.string.Playerend)));
                    mText2.setText(String.format(Player360ViedoActivity.this.getString(R.string.Playerend)));
                    break;
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
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
            this.finish();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            mHandler.sendEmptyMessage(Voiceminus);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            mHandler.sendEmptyMessage(Voiceplus);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (surface.GetInited()) {
                mLeftDown = true;
                GetViedoTime();
                mHandler.sendEmptyMessage(SEEKLEFT);
            }

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (surface.GetInited()) {
                mRightDown = true;
                GetViedoTime();
                mHandler.sendEmptyMessage(SEEKRIGHT);
            }
        }
        return true;
    }

    protected void onStop() {
        super.onStop();
        if (surface != null)
            surface.stop();
    }
}
