package com.fjc.demo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fjc.library.WheelView;


public class MainActivity extends AppCompatActivity {

    private WheelView wheelView;

    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        wheelView = (WheelView) findViewById(R.id.wheelView);
        final int colors[] = new int[7];
        colors[0] = getResources().getColor(R.color.red);
        colors[1] = getResources().getColor(R.color.orange);
        colors[2] = getResources().getColor(R.color.yellow);
        colors[3] = getResources().getColor(R.color.green);
        colors[4] = getResources().getColor(R.color.cyan);
        colors[5] = getResources().getColor(R.color.blue);
        colors[6] = getResources().getColor(R.color.purple);
        wheelView.setColorDuration(500);
        wheelView.setOnCheckListener(new WheelView.OnCheckListener() {
            @Override
            public void onCheck(int position) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        i++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wheelView.startColorAnimation(colors[i % 7]);
                            }
                        });
                    }
                }).start();
            }
        });

        String[] strs = {"菜单1", "菜单2", "菜单3", "菜单4"};
        int[] imgs = {R.drawable.menu_defult};
        wheelView.setMenu(strs, imgs);
        wheelView.setMenuTextColor(Color.RED);

    }
}
