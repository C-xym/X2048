package com.example.x.x2048;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.example.x.x2048.model.Grid;
import com.example.x.x2048.view.XGridView;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Grid mGrid;
    private SharedPreferences mPreferences;

    private static final int SET_GRID = 0;
    private MyHandler mHandler;
    private MyThread mThread;
    private Executor mExecutor;

    private View[] mBlocks = new View[16];

    private final ArrayMap<Integer, Integer> drawMap = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] viewId = new int[]{
                R.id.block_0,
                R.id.block_1,
                R.id.block_2,
                R.id.block_3,
                R.id.block_4,
                R.id.block_5,
                R.id.block_6,
                R.id.block_7,
                R.id.block_8,
                R.id.block_9,
                R.id.block_10,
                R.id.block_11,
                R.id.block_12,
                R.id.block_13,
                R.id.block_14,
                R.id.block_15
        };
        for (int i = 0; i < 16; i++) {
            mBlocks[i] = findViewById(viewId[i]);
        }

        mGrid = Grid.getInstance();
        mPreferences = getSharedPreferences("grid", MODE_PRIVATE);
        mGrid.load(mPreferences);

        mHandler = new MyHandler();
        mThread = new MyThread();

        drawMap.put(0, R.drawable.blank);
        drawMap.put(2, R.drawable.block_0002);
        drawMap.put(4, R.drawable.block_0004);
        drawMap.put(8, R.drawable.block_0008);
        drawMap.put(16, R.drawable.block_0016);
        drawMap.put(32, R.drawable.block_0032);
        drawMap.put(64, R.drawable.block_0064);
        drawMap.put(128, R.drawable.block_0128);
        drawMap.put(256, R.drawable.block_0256);
        drawMap.put(512, R.drawable.block_0512);
        drawMap.put(1024, R.drawable.block_1024);
        drawMap.put(2048, R.drawable.block_2048);
        drawMap.put(4096, R.drawable.block_4096);


        ((Button) findViewById(R.id.bt_restart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGrid.reStart();
                setGrid();
            }
        });
        ((Button) findViewById(R.id.bt_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGrid.back();
                setGrid();
            }
        });

        mExecutor = Executors.newSingleThreadExecutor();

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Game Over");


        final XGridView xGridView = findViewById(R.id.x_grid_view);
        xGridView.setOnFlashListener(new XGridView.OnFlashListener() {
            @Override
            public void onFlash(int direction) {

                ArrayMap<Integer, Integer> map = mGrid.getMoveList();
                Set<Integer> from = map.keySet();
                switch (direction) {
                    case Grid.MOVE_UP:
                        for (Integer c : from) {
                            int t = map.get(c);
                            int n = c / 4 - t / 4;
                            translateUp(mBlocks[c], n);
                        }
                        break;
                    case Grid.MOVE_RIGHT:
                        for (Integer c : from) {
                            int t = map.get(c);
                            int n = t - c;
                            translateRight(mBlocks[c], n);
                        }
                        break;
                    case Grid.MOVE_DOWN:
                        for (Integer c : from) {
                            int t = map.get(c);
                            int n = t / 4 - c / 4;
                            translateDown(mBlocks[c], n);
                        }
                        break;
                    case Grid.MOVE_LEFT:
                        for (Integer c : from) {
                            int t = map.get(c);
                            int n = c - t;
                            translateLeft(mBlocks[c], n);
                        }
                        break;
                }

                mExecutor.execute(mThread);
                if (mGrid.isGameOver()) {
                    builder.create().show();
                }
            }
        });

        setGrid();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGrid.save(mPreferences);
    }

    class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
                Message msg = mHandler.obtainMessage(SET_GRID);
                mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SET_GRID:
                    setGrid();
                    setNewBlock();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    void setGrid() {
        int[] arr = mGrid.getArr();
        for (int i = 0; i < 16; i++) {
            mBlocks[i].setBackgroundResource(drawMap.get(arr[i]));
        }
    }

    void setNewBlock() {
        ArrayMap<Integer, Integer> map = mGrid.getNewList();
        Set<Integer> set = map.keySet();
        Animation scale = new ScaleAnimation((float) 1.1, 1, (float) 1.1, 1, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
        scale.setDuration(50);
        for (Integer c : set) {
            mBlocks[c].startAnimation(scale);
        }
    }

    void translateUp(View view, int n) {
        Animation translate = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, -n);
        translate.setDuration(100);
        view.startAnimation(translate);
    }

    void translateRight(View view, int n) {
        Animation translate = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, n,
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        translate.setDuration(100);
        view.startAnimation(translate);
    }

    void translateDown(View view, int n) {
        Animation translate = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, n);
        translate.setDuration(100);
        view.startAnimation(translate);
    }

    void translateLeft(View view, int n) {
        Animation translate = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, -n,
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        translate.setDuration(100);
        view.startAnimation(translate);
    }
}
