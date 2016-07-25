package com.example.a111.github_settings.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by 111 on 2016/5/10.
 */
public class BaseActivity extends Activity {

    Bundle bundle = new Bundle();
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);

    }

    protected void startActivity(Class target, String content) {

        intent = new Intent(getBaseContext(), target);
        bundle.putString("content", content);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivity(Class target) {

        intent = new Intent(getBaseContext(), target);
        startActivity(intent);
    }
}




















