package dana9919.apk.gl_meshdraw;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/** 
 * @author ׀להל
 * mail: dana9919@163.com
 * QQ:61092517
 */
public class MainActivity extends Activity {
	MyGame0824 rview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rview = new MyGame0824(this);
        rview.requestFocus();
        rview.setFocusableInTouchMode(true);
        
        setContentView(rview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
