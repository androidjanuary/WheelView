package com.fjc.weardemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;


import com.fjc.library.WheelView;

/**
 * Created by FJC on 2016/12/12.
 */
public class SecondActivity extends Activity {


    WheelView wheelView;

    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                init(stub);
            }
        });

    }

    private void init(WatchViewStub stub) {

        wheelView = (WheelView) stub.findViewById(R.id.myView);
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
                                int[] arr=new int[2];
//                                arr[0]=Color.parseColor("#ff0000");
//                                arr[1]=Color.parseColor("#00ff00");
                                wheelView.startColorAnimation(colors[i%7]);
                            }
                        });
                    }
                }).start();
            }
        });
    }

}
