package com.example.x.x2048;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.example.x.x2048.model.Grid;
import com.example.x.x2048.view.XGridView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mPreferences;
    private XGridView mXGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getSupportActionBar().hide();
            findViewById(R.id.btn_left).setOnClickListener(this);
            findViewById(R.id.btn_up).setOnClickListener(this);
            findViewById(R.id.btn_down).setOnClickListener(this);
            findViewById(R.id.btn_right).setOnClickListener(this);
        }

        mPreferences = getSharedPreferences("grid", MODE_PRIVATE);
        Grid.getInstance().load(mPreferences);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Game Over");

        findViewById(R.id.bt_back).setOnClickListener(this);
        findViewById(R.id.bt_restart).setOnClickListener(this);

        mXGridView = findViewById(R.id.x_grid_view);

        mXGridView.setOnFlashListener(new XGridView.OnFlashListener() {
            @Override
            public void onFlash(int direction) {
                if (Grid.getInstance().isGameOver()) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Toast.makeText(MainActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                    } else {
                        builder.create().show();
                    }
                }
            }
        });
        mXGridView.post(new Runnable() {
            @Override
            public void run() {
                mXGridView.setGrid();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Grid.getInstance().save(mPreferences);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Grid.getInstance().destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_restart:
                mXGridView.reStart();
                break;
            case R.id.bt_back:
                mXGridView.back();
                break;
            case R.id.btn_left:
                mXGridView.btnAction(Grid.MOVE_LEFT);
                break;
            case R.id.btn_up:
                mXGridView.btnAction(Grid.MOVE_UP);
                break;
            case R.id.btn_right:
                mXGridView.btnAction(Grid.MOVE_RIGHT);
                break;
            case R.id.btn_down:
                mXGridView.btnAction(Grid.MOVE_DOWN);
                break;
            default:
                break;
        }
    }
}
