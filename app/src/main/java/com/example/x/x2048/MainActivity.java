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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGrid = Grid.getInstance();
        mPreferences = getSharedPreferences("grid", MODE_PRIVATE);
        mGrid.load(mPreferences);

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

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Game Over");


        final XGridView xGridView = findViewById(R.id.x_grid_view);
        xGridView.setOnFlashListener(new XGridView.OnFlashListener() {
            @Override
            public void onFlash(int direction) {
                int[] arr=mGrid.getArr();
                StringBuilder a=new StringBuilder();
                for(int i=0;i<16;i++){
                    a.append(arr[i]).append(" ");
                    if(i==3||i==7||i==11){
                        a.append("\n");
                    }
                }
                ((TextView)findViewById(R.id.textView)).setText(a);
            }
        });
       /* xGridView.setOnFlashListener(new XGridView.OnFlashListener() {
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

        setGrid();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGrid.save(mPreferences);
    }



    void setGrid() {
        int[] arr = mGrid.getArr();
        for (int i = 0; i < 16; i++) {
            //mBlocks[i].setBackgroundResource(drawMap.get(arr[i]));
        }
    }

    void setNewBlock() {
        ArrayMap<Integer, Integer> map = mGrid.getNewList();
        Set<Integer> set = map.keySet();
        Animation scale = new ScaleAnimation((float) 1.1, 1, (float) 1.1, 1, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
        scale.setDuration(50);
        for (Integer c : set) {
            //mBlocks[c].startAnimation(scale);
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
