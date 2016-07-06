package com.example.a111.myapplication1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a111.myapplication1.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * Created by 111 on 2016/5/6.
 */
public class ShowActivity extends Activity {

    ImageView mQRCode;
    TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show);
        mQRCode = (ImageView) findViewById(R.id.iv_QRCode);
        mContent = (TextView) findViewById(R.id.tv_content);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String content = bundle.getString("content");
            if (content != null) {
                Bitmap bitmap = EncodingUtils.createQRCode(content, 500, 500, null);
                mQRCode.setImageBitmap(bitmap);
                mContent.setText("二维码内容\n" + content);
            }else {
                mContent.setText("二维码内容\n内容为空！" );
            }
        }
    }
}
