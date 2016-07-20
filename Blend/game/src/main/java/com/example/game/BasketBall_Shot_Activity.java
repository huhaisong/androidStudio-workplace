package com.example.game;

import static com.example.game.Constant.*;

import java.util.HashMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bn.tl.R;

enum WhichView {
    WELCOME_VIEW, CAIDAN_VIEW, SHEZHI_VIEW,
    GUANYU_VIEW, GAME_VIEW, BANGZHU_VIEW, JILU_VIEW, OVER_VIEW, JIAZAI_VIEW
}

public class BasketBall_Shot_Activity extends Activity {
    WhichView curr;//��ǰö��ֵ
    private GLGameView gameplay;//��Ϸ����
    public CaiDanView caidanjiemian;//�˵�����
    private GuanYuView guanyujiemian;//���ڽ���
    private YouXiuJieShuView jieshujiemian;//��Ϸ�������
    private ShengyinKGJiemian gamesound;//�Ƿ�����������
    private JiLuView lishijilu;//��ʷ��¼����
    Handler xiaoxichuli;//��Ϣ������
    MediaPlayer beijingyinyue;//��Ϸ�������ֲ�����
    SoundPool shengyinChi;//������
    HashMap<Integer, Integer> soundIdMap;//������������ID���Զ�������ID��Map
    CheckVersionDialog cvDialog;
    AndroidVersionDialog avDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chushihuaSounds();//��ʼ��������
        chushihuaScreen();//��ʼ����Ļ�ķֱ���
        curr = WhichView.WELCOME_VIEW;
        new Thread() {
            public void run() {
                try {    //����3D�м��ؽ����������Դ
                    Constant.loadWelcomeBitmap(BasketBall_Shot_Activity.this.getResources(),
                            new int[]{R.drawable.welcome, R.drawable.dott, R.drawable.bangzuwelcome});
                    //����3D�м��ؽ����shader�ַ�
                    ShaderManager.loadCodeFromFile(BasketBall_Shot_Activity.this.getResources());
                    SQLiteUtil.initDatabase();//������ݿ�
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
        //��ʼ����Ϣ������
        xiaoxichuli = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHENGYING_KG_JIEMIAN://�����������ý���
                        curr = WhichView.SHEZHI_VIEW;
                        gamesound = new ShengyinKGJiemian(BasketBall_Shot_Activity.this);
                        setContentView(gamesound);
                        break;
                    case CAIDAN_JIEMIAN://����˵�ѡ�����
                        curr = WhichView.CAIDAN_VIEW;
                        caidanjiemian = new CaiDanView(BasketBall_Shot_Activity.this);
                        setContentView(caidanjiemian);
                        break;
                    case JIAZAI_JIEMIAN://��������������е���Դ
                        curr = WhichView.JIAZAI_VIEW;
                        gameplay = new GLGameView(BasketBall_Shot_Activity.this);
                        Resources r = BasketBall_Shot_Activity.this.getResources();
                        gameplay.initObjectWelcome(r);
                        if (isBJmiusic)//���ű�������
                        {
                            beijingyinyue.start();
                        }
                        xiaoxichuli.sendEmptyMessage(YOUXI_JIEMIAN);
                        break;
                    case YOUXI_JIEMIAN:        //��Ϸ����
                        flag = true;
                        isnoPlay = true;//�Ƿ񲥷���Ƶ
                        setContentView(gameplay); //�����л�����Ϸ������
                        gameplay.requestFocus();
                        gameplay.setFocusableInTouchMode(true);
                        break;
                    case GUANYU_JIEMIAN://���ڽ���
                        curr = WhichView.GUANYU_VIEW;
                        guanyujiemian = new GuanYuView(BasketBall_Shot_Activity.this);
                        setContentView(guanyujiemian);
                        break;
                    case BANGZHU_JIEMIAN://�������
                        curr = WhichView.BANGZHU_VIEW;
                        isnoHelpView = true;//�ý���Ϊ�������
                        xiaoxichuli.sendEmptyMessage(JIAZAI_JIEMIAN);//������ؽ���
                        break;
                    case JIESHU_JIEMIAN://��Ϸ����
                        curr = WhichView.OVER_VIEW;
                        jieshujiemian = new YouXiuJieShuView(BasketBall_Shot_Activity.this, caidanjiemian);
                        setContentView(jieshujiemian);
                        break;
                    case CAIDAN_RETRY://�˵�����
                        curr = WhichView.CAIDAN_VIEW;
                        caidanjiemian = new CaiDanView(BasketBall_Shot_Activity.this);
                        setContentView(caidanjiemian);
                        break;
                    case JILU_JIEMIAN://��¼����
                        curr = WhichView.JILU_VIEW;
                        lishijilu = new JiLuView(BasketBall_Shot_Activity.this);//��¼����
                        setContentView(lishijilu);
                        break;
                }
            }
        };
        //������˵�����
        xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);
        //�жϵ�ǰAndroid�汾�ǲ��ǵ���2.2
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            this.showDialog(2);
        }
        //�жϵ�ǰϵͳ��֧�ֵ����opengles�汾�ǲ��Ǵ���2
        else if (this.getGLVersion() < 2) {
            this.showDialog(1);
        }

    }

    public int getGLVersion() //��ȡOPENGLES��֧�ֵ���߰汾
    {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        int majorVersion = info.reqGlEsVersion;
        majorVersion = majorVersion >>> 16;
        return majorVersion;
    }


    public Dialog onCreateDialog(int id) {
        Dialog result = null;
        switch (id) {
            case 1:
                cvDialog = new CheckVersionDialog(this);
                result = cvDialog;
                break;
            case 2:
                avDialog = new AndroidVersionDialog(this);
                result = avDialog;
                break;
        }
        return result;
    }

    public void onPrepareDialog(int id, Dialog dialog) {
        //�����ǵȴ�Ի����򷵻�
        switch (id) {
            case 1:
                Button bok = (Button) cvDialog.findViewById(R.id.ok_button);
                bok.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.exit(0);
                            }
                        }
                );
                break;
            case 2:
                Button ok = (Button) avDialog.findViewById(R.id.ok);
                ok.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.exit(0);
                            }
                        }
                );
                break;
        }
    }

    //��ʼ����Ļ�ֱ���
    public void chushihuaScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��֪ͨ��
        getWindow().setFlags//ȫ����ʾ
                (
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//������ʾ

        //��ȡ��Ļ�ֱ���
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int tempHeight = (int) (SCREEN_HEIGHT = dm.heightPixels);
        int tempWidth = (int) (SCREEN_WIDHT = dm.widthPixels);

        if (tempHeight > tempWidth) {
            SCREEN_HEIGHT = tempHeight;
            SCREEN_WIDHT = tempWidth;
        } else {
            SCREEN_HEIGHT = tempWidth;
            SCREEN_WIDHT = tempHeight;
        }
        float zoomx = SCREEN_WIDHT / 480;
        float zoomy = SCREEN_HEIGHT / 800;
        if (zoomx > zoomy) {
            ratio_width = ratio_height = zoomy;

        } else {
            ratio_width = ratio_height = zoomx;
        }
        sXtart = (SCREEN_WIDHT - 480 * ratio_width) / 2;
        sYtart = (SCREEN_HEIGHT - 800 * ratio_height) / 2;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode != 4) {
            return false;
        }
        if (curr == WhichView.SHEZHI_VIEW || curr == WhichView.GUANYU_VIEW
                || curr == WhichView.JILU_VIEW || curr == WhichView.BANGZHU_VIEW ||
                curr == WhichView.OVER_VIEW) {
            //��������ý���,���ڽ��棬��¼����,�������,��Ϸ�������
            xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//���ص��˵�����
            return true;
        }
        if (curr == WhichView.CAIDAN_VIEW) {//�˵�����
            System.exit(0);
            return true;
        }
        if (curr == WhichView.GAME_VIEW)//��Ϸ����
        {
            isnoHelpView = false;
            flag = false;//�ֳ�ֹͣ
            shipingJs = 0;//��Ƶ�ֳ�����
            xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//���ص��˵�����
            return true;
        }
        return true;
    }

    //���������ķ���
    public void chushihuaSounds() {
        beijingyinyue = MediaPlayer.create(this, R.raw.beijingyingyu);
        beijingyinyue.setLooping(true);//�Ƿ�ѭ��
        beijingyinyue.setVolume(0.2f, 0.2f);//������С
        shengyinChi = new SoundPool
                (
                        4,
                        AudioManager.STREAM_MUSIC,
                        100
                );
        soundIdMap = new HashMap<Integer, Integer>();
        soundIdMap = new HashMap<Integer, Integer>();
        soundIdMap.put(1, shengyinChi.load(this, R.raw.pengzhuang, 1));//��ײ����
        soundIdMap.put(2, shengyinChi.load(this, R.raw.levelend, 1));//��Ϸʱ���������
        soundIdMap.put(3, shengyinChi.load(this, R.raw.shoot, 1));//��������
    }

    //���������ķ���
    public void shengyinBoFang(int sound, int loop) {
        if (!isCJmiusic) {
            return;
        }
        AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        shengyinChi.play(soundIdMap.get(sound), volume, volume, 1, loop, 0.5f);
    }
}