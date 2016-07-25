package com.example.lines3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AirMoveView.MoveInterface {
    private Button btnMode;
    private TextView tvValue;
    private AirMoveView moveView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMode = (Button) findViewById(R.id.btn_mode);
        tvValue = (TextView) findViewById(R.id.tv_value);
        moveView = (AirMoveView) findViewById(R.id.move_view);
        moveView.setMoveInterface(this);
        btnMode.setOnClickListener(this);

    }

    int mode = 0;
    @Override
    public void onClick(View v) {
        moveView.changeMode(mode);
        if (mode == 0) {
            mode = 1;
        } else {
            mode = 0;
        }
    }

    @Override
    public void getCurrentDegrees(int degress) {
        tvValue.setText("当前度数：" + degress + "度");
    }
}
