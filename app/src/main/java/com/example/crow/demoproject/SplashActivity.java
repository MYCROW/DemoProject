package com.example.crow.demoproject;

import android.content.Intent;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends Activity {
    private static final int START_ACTIVITY = 0x1;
    private boolean InMainActivity = false;//主页面是否已启动
    private long delayMills = 3000;//启动页面延迟时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //按钮点击开启活动
        Button button = (Button) findViewById(R.id.skip_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InMainActivity = true;
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        });
        //延时开启活动
        handler.sendEmptyMessageDelayed(START_ACTIVITY,delayMills);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            System.out.println("InMainActivity = " + InMainActivity);
            //如果InMainActivity == false，则进入MainActivity，为了避免重复进入MainActivity
            if (InMainActivity == false) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case START_ACTIVITY:
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
        }
    };
}
