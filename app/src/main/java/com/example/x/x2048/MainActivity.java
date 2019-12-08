package com.example.x.x2048;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.x.x2048.model.Grid;
import com.example.x.x2048.view.XGridView;

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Game Over");

        final XGridView xGridView = findViewById(R.id.x_grid_view);

        ((Button) findViewById(R.id.bt_restart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xGridView.reStart();
            }
        });
        ((Button) findViewById(R.id.bt_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xGridView.back();
            }
        });

        xGridView.setOnFlashListener(new XGridView.OnFlashListener() {
            @Override
            public void onFlash(int direction) {
                if (mGrid.isGameOver()) {
                    builder.create().show();
                }
            }
        });
        xGridView.post(new Runnable() {
            @Override
            public void run() {
                xGridView.setGrid();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGrid.save(mPreferences);
    }
}
