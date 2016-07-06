package com.zhf.wifidemo.wifi;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhf.wifidemo.R;
import com.zhf.wifidemo.broadcast.WIFIBroadcast;
import com.zhf.wifidemo.broadcast.WIFIBroadcast.EventHandler;
import com.zhf.wifidemo.data.WFOperateEnum;
import com.zhf.wifidemo.ui.CreateAPProcess;
import com.zhf.wifidemo.ui.GifView;
import com.zhf.wifidemo.ui.WFSearchAnimationFrameLayout;
import com.zhf.wifidemo.ui.WFSearchProcess;
import com.zhf.wifidemo.wifi.utils.WifiAdmin;

/**
 * ������
 *
 * @author ZHF
 */
public class MainActivity extends Activity implements EventHandler {
    //��Ϣ�¼�
    public static final int m_nWifiSearchTimeOut = 0;// ������ʱ
    public static final int m_nWTScanResult = 1;// ������wifi���ؽ��
    public static final int m_nWTConnectResult = 2;// ������wifi�ȵ�
    public static final int m_nCreateAPResult = 3;// �����ȵ���
    public static final int m_nUserResult = 4;// �û����������������(��)
    public static final int m_nWTConnected = 5;// ������Ӻ�Ͽ�wifi��3.5���ˢ��adapter

    //һЩ����
    public static final String PACKAGE_NAME = "com.zhf.wifidemo.wifi";  //Ӧ�ð���
    public static final String FIRST_OPEN_KEY = "version";  //�汾����Ϣ
    public static final String WIFI_AP_HEADER = "zhf_";
    public static final String WIFI_AP_PASSWORD = "zhf12345";
    //wifi�����¼���ö�٣�-->���㵯���Ի������¼�
    private int wFOperateEnum = WFOperateEnum.NOTHING;

    //�����Ҫ����
    public WFSearchProcess m_wtSearchProcess; //WiFi����������߳�
    public WifiAdmin m_wiFiAdmin; //Wifi������
    public CreateAPProcess m_createAPProcess; //����Wifi�ȵ��߳�

    //��ؿؼ�
    private WFSearchAnimationFrameLayout m_FrameLWTSearchAnimation;  //�Զ����״ﶯ������
    private GifView m_gifRadar;  //wifi�źŶ�������

    private LinearLayout m_LinearLIntroduction; //��һ�δ�Ӧ�ó������

    private LinearLayout m_linearLCreateAP; //�����ȵ�View
    private ProgressBar m_progBarCreatingAP; //�����ȵ�����
    private TextView m_textVPromptAP; //�����ȵ���������

    private Button m_btnBack; //���ϽǷ��ذ�ť
    private Button m_btnSearchWF; //���Ͻ�wifi������ť
    private Button m_btnCreateWF; //����wifi�ȵ�
    private ListView m_listVWT; //��ʾ��Ϣ

    private LinearLayout m_LinearLDialog; //���ѶԻ���
    private TextView m_textVContentDialog;  //�Ի����ı�����
    private Button m_btnConfirmDialog, m_btnCancelDialog; //���ѶԻ����ϵİ�ť

    private TextView m_textVWTPrompt; //�м�������ʾ

    ArrayList<ScanResult> m_listWifi = new ArrayList();//��⵽�ȵ���Ϣ�б�
    private WTAdapter m_wTAdapter; //�����б�������

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case m_nWifiSearchTimeOut: // ������ʱ
                    m_wtSearchProcess.stop();
                    m_FrameLWTSearchAnimation.stopAnimation();
                    m_listWifi.clear();  //�����б�
                    //���ÿؼ�
                    m_textVWTPrompt.setVisibility(View.VISIBLE);
                    m_textVWTPrompt.setText("��Ҫ���������������Ͻ����������򴴽��µ��ȵ�...");
                    break;

                case m_nWTScanResult:  //ɨ�赽���
                    m_listWifi.clear();
                    if (m_wiFiAdmin.mWifiManager.getScanResults() != null) {
                        for (int i = 0; i < m_wiFiAdmin.mWifiManager.getScanResults().size(); i++) {
                            ScanResult scanResult = m_wiFiAdmin.mWifiManager.getScanResults().get(i);
                            //��ָ�������ȵ�Ƚϣ�������Ĺ��˵���
                            if (scanResult.SSID.startsWith(WIFI_AP_HEADER)) {
                                m_listWifi.add(scanResult);
                            }
                        }
                        if (m_listWifi.size() > 0) {
                            m_wtSearchProcess.stop();
                            m_FrameLWTSearchAnimation.stopAnimation();
                            m_textVWTPrompt.setVisibility(View.GONE);
                            //�����б?��ʾ�����������ȵ�
                            m_wTAdapter.setData(m_listWifi);
                            m_wTAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case m_nWTConnectResult:  //���ӽ��
                    m_wTAdapter.notifyDataSetChanged(); //ˢ�����������
                    break;
                case m_nCreateAPResult:  //����wifi�ȵ���
                    m_createAPProcess.stop();
                    m_progBarCreatingAP.setVisibility(View.GONE); //��ת�����
                    if ((m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin.getWifiApState() == 13) && (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {
                        //���ÿؼ�
                        m_textVWTPrompt.setVisibility(View.GONE);
                        m_linearLCreateAP.setVisibility(View.VISIBLE);
                        m_btnCreateWF.setVisibility(View.VISIBLE);
                        m_gifRadar.setVisibility(View.VISIBLE);
                        m_btnCreateWF.setBackgroundResource(R.drawable.x_ap_close);

                        m_textVPromptAP.setText("�ȵ㴴���ɹ���" + "\n�ȵ���" + m_wiFiAdmin.getApSSID() + "\n�������룺zhf12345");
                    } else {
                        m_btnCreateWF.setVisibility(View.VISIBLE);
                        m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create);
                        m_textVPromptAP.setText("�ȵ㴴��ʧ�ܣ���������´����������������ȵ�");
                    }
                    break;
                case m_nUserResult:
                    //�����û���������
                    break;
                case m_nWTConnected:  //������Ӻ�Ͽ�wifi��3.5s��ˢ��
                    m_wTAdapter.notifyDataSetChanged();
                    break;

            }

        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wt_main);
        //����Wifi
        m_wtSearchProcess = new WFSearchProcess(this);
        //����Wifi�ȵ�
        m_createAPProcess = new CreateAPProcess(this);
        //wifi������
        m_wiFiAdmin = WifiAdmin.getInstance(this);

        //��ʼ��View
        initView();
    }

    /**
     * ��ʼ��View
     **/
    private void initView() {
        // ����㲥
        WIFIBroadcast.ehList.add(this);

        /******************************ʵ���**************************************/
        m_linearLCreateAP = (LinearLayout) findViewById(R.id.create_ap_llayout_wt_main);  //�����ȵ�View
        m_progBarCreatingAP = (ProgressBar) findViewById(R.id.creating_progressBar_wt_main);  //�����ȵ�����
        m_textVPromptAP = (TextView) findViewById(R.id.prompt_ap_text_wt_main); //�����ȵ���������

        m_FrameLWTSearchAnimation = ((WFSearchAnimationFrameLayout) findViewById(R.id.search_animation_wt_main));// ����ʱ�Ķ���
        m_listVWT = ((ListView) findViewById(R.id.wt_list_wt_main));// ���������ȵ�listView
        //ע��˴�
        m_wTAdapter = new WTAdapter(this, m_listWifi);
        m_listVWT.setAdapter(m_wTAdapter);

        m_textVWTPrompt = (TextView) findViewById(R.id.wt_prompt_wt_main); //�м���������
        m_gifRadar = (GifView) findViewById(R.id.radar_gif_wt_main); //gif����

        //���ѶԻ��򲼾�
        m_LinearLDialog = (LinearLayout) findViewById(R.id.dialog_layout_wt_main);
        m_textVContentDialog = (TextView) findViewById(R.id.content_text_wtdialog);
        m_btnConfirmDialog = (Button) findViewById(R.id.confirm_btn_wtdialog);
        m_btnCancelDialog = (Button) findViewById(R.id.cancel_btn_wtdialog);

        //���ϽǷ��ؼ�
        m_btnBack = (Button) findViewById(R.id.back_btn_wt_main);
        m_btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); //�൱�ڵ���ϵͳ���ؼ����ǰActivity
            }
        });

        //���Ͻ������ȵ㰴ť
        m_btnSearchWF = (Button) findViewById(R.id.search_btn_wt_main);
        m_btnSearchWF.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!m_wtSearchProcess.running) { //�����߳�û�п���
                    //1.��ǰ�ȵ��wifi������    WIFI_STATE_ENABLED 3 //WIFI_AP_STATE_ENABLED  13
                    if (m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin.getWifiApState() == 13) {
                        wFOperateEnum = WFOperateEnum.SEARCH; //����wifi�¼�
                        m_LinearLDialog.setVisibility(View.VISIBLE); ///wifi��ʾ�Ի�����ʾ
                        m_textVContentDialog.setText("�Ƿ�رյ�ǰ�ȵ�ȥ���������ȵ㣿");
                        return;  //����˷��������ɶԻ����������¼�
                    }
                    //2.��ǰû���ȵ��wifi������
                    if (!m_wiFiAdmin.mWifiManager.isWifiEnabled()) { //���wifiû��
                        m_wiFiAdmin.OpenWifi();
                    }
                    m_textVWTPrompt.setVisibility(View.VISIBLE); //�м���ʾ����
                    m_textVWTPrompt.setText("��������������ȵ�...");
                    m_linearLCreateAP.setVisibility(View.GONE); //����wifi�ȵ㲼����ʧ
                    m_gifRadar.setVisibility(View.GONE); //�ȵ����Ӷ�����ʧ
                    m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create); //��İ�ť���֡�������
                    //��ʼ����wifi
                    m_wiFiAdmin.startScan();
                    m_wtSearchProcess.start(); //���������߳�
                    m_FrameLWTSearchAnimation.startAnimation(); //�������ƶ���
                } else {//�����߳̿����ţ��ٴε����ť
                    //��������
                    m_wtSearchProcess.stop();
                    m_wiFiAdmin.startScan();    //��ʼ����wifi
                    m_wtSearchProcess.start();
                }
            }
        });

        //�м䴴��wifi�ȵ㰴ť
        m_btnCreateWF = (Button) findViewById(R.id.create_btn_wt_main);
        m_btnCreateWF.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_wiFiAdmin.getWifiApState() == 4) { // WIFI_STATE_UNKNOWN
                    Toast.makeText(getApplicationContext(), "����豸��֧���ȵ㴴��!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_wiFiAdmin.mWifiManager.isWifiEnabled()) { //Ŀǰ����wifi
                    wFOperateEnum = WFOperateEnum.CREATE;  //wifi�ȵ㴴���¼�
                    m_LinearLDialog.setVisibility(View.VISIBLE); //�Ի������
                    m_textVContentDialog.setText("�����ȵ��رյ�ǰ��WiFi��ȷ�ϼ���");
                    return;
                }
                if ((m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin.getWifiApState() == 13)
                        && (!m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {//Ŀǰ�����������ȵ���
                    wFOperateEnum = WFOperateEnum.CREATE;  //wifi�ȵ㴴���¼�
                    m_LinearLDialog.setVisibility(View.VISIBLE);
                    m_textVContentDialog.setText("ϵͳ�ȵ㱻ռ�ã���ȷ�������ȵ��Դ����ļ���");
                    return;
                }
                if (((m_wiFiAdmin.getWifiApState() == 3) || (m_wiFiAdmin.getWifiApState() == 13))
                        && (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {//Ŀǰ�������Լ�ָ����Wifi�ȵ�
                    wFOperateEnum = WFOperateEnum.CLOSE;  //wifi�ȵ�ر��¼�
                    m_LinearLDialog.setVisibility(View.VISIBLE);
                    m_textVContentDialog.setText("�ر��ȵ���жϵ�ǰ���䣬��ȷ����ô����");
                    return;
                }
                if (m_wtSearchProcess.running) {
                    m_wtSearchProcess.stop(); //ֹͣ�߳�
                    m_FrameLWTSearchAnimation.stopAnimation(); //ֹͣ����
                }

                /******************��������ȵ�ʱû������wifi���ȵ�����*****************************/
                //�ر�Wifi
                m_wiFiAdmin.closeWifi();
                //�����ȵ㣨���֣����룬��������,wifi/ap���ͣ�
                m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER + getLocalHostName(), WIFI_AP_PASSWORD, 3, "ap"), true);
                m_createAPProcess.start(); //���������ȵ��߳�

                //��wifi��Ϣ�б����õ�listview��
                m_listWifi.clear();
                m_wTAdapter.setData(m_listWifi);
                m_wTAdapter.notifyDataSetChanged();
                //���ò���
                m_linearLCreateAP.setVisibility(View.VISIBLE); //�ȵ㲼�ֿ���
                m_progBarCreatingAP.setVisibility(View.VISIBLE);
                m_textVPromptAP.setText("���ڴ����ȵ�"); //���������
                m_btnCreateWF.setVisibility(View.GONE); //���һ�β����ٵ�
                m_textVWTPrompt.setVisibility(View.GONE);
            }
        });

        //�Ի���ȷ�ϰ�ť
        m_btnConfirmDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_LinearLDialog.setVisibility(View.GONE); //�öԻ��򲼾���ʧ
                switch (wFOperateEnum) { //���wifi�����¼�
                    case WFOperateEnum.CLOSE:  //�ر�wifi�ȵ�
                        //���ò���
                        m_textVWTPrompt.setVisibility(View.VISIBLE);
                        m_textVWTPrompt.setText("�ȵ��ѹرգ����Խ�����������ˣ�"); //�м���������
                        m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create); //��ť���ָĻء�������
                        m_gifRadar.setVisibility(View.GONE); //�ȵ㶯��ֹͣ
                        m_linearLCreateAP.setVisibility(View.GONE); //�²������ȵ㲼�ֲ�����

                        //�ر��ȵ�
                        m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(m_wiFiAdmin.getApSSID(), "zhf123456", 3, "ap"), false);
                        break;
                    case WFOperateEnum.CREATE:  //����wifi�ȵ�
                        if (m_wtSearchProcess.running) {
                            m_wtSearchProcess.stop();  //����wifi�߳�ֹͣ
                            m_FrameLWTSearchAnimation.stopAnimation(); //����wifi����ֹͣ
                        }
                        //�ر�wifi
                        m_wiFiAdmin.closeWifi();
                        //����WiFi�ȵ�
                        m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER + getLocalHostName(), WIFI_AP_PASSWORD, 3, "ap"), true);
                        m_createAPProcess.start();
                        //ˢ��listView����
                        m_listWifi.clear();
                        m_wTAdapter.setData(m_listWifi);
                        m_wTAdapter.notifyDataSetChanged();
                        //���ò���
                        m_linearLCreateAP.setVisibility(View.VISIBLE);
                        m_progBarCreatingAP.setVisibility(View.VISIBLE); //��ת�����
                        m_btnCreateWF.setVisibility(View.GONE);
                        m_textVWTPrompt.setVisibility(View.GONE);
                        m_textVPromptAP.setText("���ڴ����ȵ�..."); //���������
                        break;
                    case WFOperateEnum.SEARCH:  //���������ȵ�
                        //���ò���
                        m_textVWTPrompt.setVisibility(View.VISIBLE);
                        m_textVWTPrompt.setText("��������������ȵ�...");
                        m_linearLCreateAP.setVisibility(View.GONE); //�����ȵ㲼�ֲ�����
                        m_btnCreateWF.setVisibility(View.VISIBLE);
                        m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create); //��ť���ָĻء�������
                        m_gifRadar.setVisibility(View.GONE); //�ȵ㶯��ֹͣ
                        m_linearLCreateAP.setVisibility(View.GONE); //�²������ȵ㲼�ֲ�����

                        //�����ȵ��̹߳ر�
                        if (m_createAPProcess.running)
                            m_createAPProcess.stop();
                        //�ر��ȵ�
                        m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(m_wiFiAdmin.getApSSID(), WIFI_AP_PASSWORD, 3, "ap"), false);
                        //��wifi
                        m_wiFiAdmin.OpenWifi();
                        m_wtSearchProcess.start();
                        m_FrameLWTSearchAnimation.startAnimation();

                        break;
                }
            }
        });
        //�Ի���ȡ��ť�¼�
        m_btnCancelDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //�Ի��򲼾���ʧ
                m_LinearLDialog.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Wifi�Ƿ�����
     **/
    private boolean isWifiConnect() {
        boolean isConnect = true;
        //������������״̬�ļ��
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected())
            isConnect = false;
        return isConnect;
    }

    /**
     * ��ȡwifi�ȵ�״̬
     **/
    public boolean getWifiApState() {
        try {
            WifiManager localWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            Method m = localWifiManager.getClass().getMethod("getWifiApState", new Class[0]);
            int i = (Integer) (m.invoke(localWifiManager, new Object[0]));
            return (3 == i) || (13 == i);  //WIFI_STATE_ENABLED 3  //WIFI_AP_STATE_ENABLED  13
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ��ȡ�ֻ���Ϣ
     **/
    public String getLocalHostName() {
        String str1 = Build.BRAND; //����
        String str2 = Build.MODEL;  //����
        if (-1 == str2.toUpperCase().indexOf(str1.toUpperCase()))
            str2 = str1 + "_" + str2;
        return str2;
    }

    private void init() {
        //�߳��Ƿ�������
        if (this.m_wtSearchProcess.running || this.m_createAPProcess.running) {
            return;
        }
        //û��������wifi������wifi�ȵ�
        if (!isWifiConnect() && !getWifiApState()) {
            m_wiFiAdmin.OpenWifi();
            m_wtSearchProcess.start(); //��������wifi��ʱ����߳�
            m_wiFiAdmin.startScan(); //��������wifi
            //������������
            m_FrameLWTSearchAnimation.startAnimation();

            //���ÿؼ�
            m_textVWTPrompt.setVisibility(View.VISIBLE);
            m_textVWTPrompt.setText(" ��������������ȵ�...");
            m_linearLCreateAP.setVisibility(View.GONE);
            m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create);
        }
        //������wifi
        if (isWifiConnect()) {
            this.m_wiFiAdmin.startScan();
            this.m_wtSearchProcess.start();
            this.m_FrameLWTSearchAnimation.startAnimation();
            //���ÿؼ�
            this.m_textVWTPrompt.setVisibility(0);
            this.m_textVWTPrompt.setText("��������������ȵ�...");
            this.m_linearLCreateAP.setVisibility(View.GONE);
            this.m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create);
            this.m_gifRadar.setVisibility(View.GONE);

            m_listWifi.clear();
            if (m_wiFiAdmin.mWifiManager.getScanResults() != null) {
                for (int i = 0; i < m_wiFiAdmin.mWifiManager.getScanResults().size(); i++) {
                    //ʶ����Լ��Զ����ӵ�wifi
                    if (m_wiFiAdmin.mWifiManager.getScanResults().get(i).SSID.startsWith(WIFI_AP_HEADER)) {
                        m_listWifi.add(m_wiFiAdmin.mWifiManager.getScanResults().get(i)); //��ָ��wifi��ӽ�ȥ
                    }
                }
                m_wTAdapter.setData(m_listWifi); //�����ӵ���Ϣ��ӵ�listView��
                m_wTAdapter.notifyDataSetChanged();
            }
            //������wifi�ȵ�
            if (getWifiApState()) {
                if (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER)) {
                    //���ÿؼ�
                    m_textVWTPrompt.setVisibility(View.GONE);
                    m_linearLCreateAP.setVisibility(View.VISIBLE);
                    m_progBarCreatingAP.setVisibility(View.GONE);
                    m_btnCreateWF.setVisibility(View.VISIBLE);
                    m_gifRadar.setVisibility(View.VISIBLE);
                    m_btnCreateWF.setBackgroundResource(R.drawable.x_ap_close);
                    m_textVPromptAP.setText("\n�ȵ���" + m_wiFiAdmin.getApSSID() + "\n�������룺zhf12345");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        WIFIBroadcast.ehList.remove(this);
    }


    @Override
    public void handleConnectChange() {
        Message msg = mHandler.obtainMessage(m_nWTConnectResult);
        mHandler.sendMessage(msg);
    }

    @Override
    public void scanResultsAvaiable() {
        Message msg = mHandler.obtainMessage(m_nWTScanResult);
        mHandler.sendMessage(msg);
    }

    @Override
    public void wifiStatusNotification() {
        m_wiFiAdmin.mWifiManager.getWifiState(); //��ȡ��ǰwifi״̬
    }
}
