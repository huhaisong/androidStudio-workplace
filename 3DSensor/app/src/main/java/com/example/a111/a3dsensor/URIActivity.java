package com.example.a111.a3dsensor;

import android.app.Activity;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;

/**
 * Created by 111 on 2016/7/11.
 */
public class URIActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        getContentResolver().registerContentObserver(
                Uri.parse("content://com.ljq.providers.personprovider/person"),
                true, new PersonObserver(new Handler()));
    }

    public class PersonObserver extends ContentObserver {
        public PersonObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            //此处可以进行相应的业务处理
        }
    }
}
