package com.example.emilclemedson.timer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView label;
    private Button pauseButton;
    private Button startButton;
    private Button lapButton;
    private MyTimer timer;
    private LinearLayout lapFrame;

    private Runnable createSetTextRunnable(final String timeStr){
        return new Runnable(){
            public void run(){
                label.setText(timeStr);
            }
        };
    }

    private Runnable createLapTextRunnable(final ArrayList<String> lapStrings){
        return new Runnable(){
            public void run(){
                int numChild = lapFrame.getChildCount();
                if (lapStrings.size() == 0) {
                    lapFrame.removeViews(0, numChild);
                    numChild = 0;
                }
                for (int i = numChild; i < lapStrings.size(); i++) {
                    TextView lapText = new TextView(MainActivity.this);
                    lapText.setText(lapStrings.get(i));
                    lapFrame.addView(lapText, 0);
                }
            }
        };
    }

    public void  addLapTime(final ArrayList<String> lapStrings){
        runOnUiThread(createLapTextRunnable(lapStrings));
    }

    public void setClock(final String time) {
        runOnUiThread(createSetTextRunnable(time));
    }

    private void updateUI() {
        lapButton.setEnabled(true);
        if (timer.getRunning()) {
            pauseButton.setText(R.string.button_pause);
        } else {
            pauseButton.setText(R.string.button_resume);
        }
        if (timer.getStopped()) {
            pauseButton.setEnabled(false);
            pauseButton.setText(R.string.button_pause);
            startButton.setText(R.string.button_start);
            lapButton.setEnabled(false);
        } else {
            pauseButton.setEnabled(true);
            startButton.setText(R.string.button_stop);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("start", "start");
        setContentView(R.layout.activity_main);
        label = (TextView) findViewById(R.id.textView);
        pauseButton = (Button) findViewById(R.id.PauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.getTime() == 0)
                    return;
                if (timer.getRunning()) {
                    timer.pause();
                } else {
                    timer.start();
                }
                updateUI();
            }
        });
        startButton = (Button) findViewById(R.id.StartButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.getStopped()) {
                    timer.reset();
                    timer.start();
                } else {
                    timer.stop();
                }
                updateUI();
            }
        });
        lapFrame = (LinearLayout) findViewById(R.id.LapFrame);
        lapButton = (Button) findViewById(R.id.LapButton);
        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                timer.addLap();
            }
        });


        if (savedInstanceState != null) {
            timer = savedInstanceState.getParcelable("timer");
            if (timer != null)
                timer.setLabel(this);
            else
                timer = new MyTimer(this);
        } else {
            timer = new MyTimer(this);
        }
        updateUI();
        timer.showLaps();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("timer", timer);
    }
}
