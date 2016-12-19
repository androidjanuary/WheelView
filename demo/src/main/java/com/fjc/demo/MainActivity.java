package com.fjc.demo;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;

import com.fjc.library.MyView;


public class MainActivity extends AppCompatActivity {

    private MyView myView;

    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        myView = (MyView) findViewById(R.id.myView);

        final int colors[] = new int[7];
        colors[0] = getResources().getColor(R.color.red);
        colors[1] = getResources().getColor(R.color.orange);
        colors[2] = getResources().getColor(R.color.yellow);
        colors[3] = getResources().getColor(R.color.green);
        colors[4] = getResources().getColor(R.color.cyan);
        colors[5] = getResources().getColor(R.color.blue);
        colors[6] = getResources().getColor(R.color.purple);

        myView.setColorDuration(500);
        myView.setOnCheckListener(new MyView.OnCheckListener() {
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
//                                int[] arr = new int[2];
//                                arr[0] = Color.parseColor("#ffffff");
//                                arr[1] = Color.parseColor("#000000");
                                myView.startColorAnimation(colors[i%7]);
                            }
                        });
                    }
                }).start();
            }
        });

//        String[] strs={"1","2","3"};
//        int[] imgs={R.drawable.menu_defult};
//        myView.setMenu(strs,imgs);


    }
}
