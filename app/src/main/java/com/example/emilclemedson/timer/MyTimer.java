package com.example.emilclemedson.timer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;



class MyTimer implements Parcelable {
    private int time = 0;
    private ArrayList<String> lapStrings = new ArrayList();
    private int lapTime = 0;
    private Timer timer = new Timer();
    private static final int DELAY = 20;
    private MainActivity label;
    private boolean running = false;
    private boolean stopped = true;

    MyTimer(MainActivity label) {
        this.label = label;
        setTime();
    }

    private MyTimer(Parcel in) {
        lapStrings = new ArrayList<String>();
        time = in.readInt();
        lapTime = in.readInt();
        running = in.readByte() != 0;
        stopped = in.readByte() != 0;
        lapStrings = in.readArrayList(null);
    }

    public void setLabel(MainActivity label) {
        this.label = label;
        setTime();
    }

    private String timeToString(final int theTime) {
        int ms = theTime % (1000 / DELAY);
        String strMs = (ms < 10) ? "0"+ms : Integer.toString(ms);
        int s = (theTime / (1000 / DELAY)) % 60;
        String strS = (s < 10) ? "0"+s : Integer.toString(s);
        int m = theTime / (60 * 1000 / DELAY);
        String strM = (m < 10) ? "0"+m : Integer.toString(m);
        //return String.format("%s.%s:%s", strM,  strS, strMs);
        return label.getString(R.string.clock, strM, strS, strMs);
    }

    private void setTime() {
        label.setClock(timeToString(time));
    }

    public boolean getStopped() {
        return stopped;
    }

    public boolean getRunning() {
        return running;
    }

    public void addLap() {
        int lap = lapStrings.size() + 1;
        //String lapString = String.format("Lap %d: %s", lap, timeToString(time-lapTime));
        String lapString = label.getString(R.string.Lap, lap, timeToString(time-lapTime));
        lapStrings.add(lapString);
        lapTime = time;
        showLaps();
    }

    public void showLaps() {
        label.addLapTime(lapStrings);
    }

    public int getTime() {
        return time;
    }

    public void start() {
        running = true;
        stopped = false;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (running)
                    tick();
            }
        }, 0, DELAY);
    }

    public void pause() {
        running = false;
        timer.cancel();
    }

    public void stop() {
        stopped = true;
        pause();
    }

    public void reset() {
        lapStrings = new ArrayList<String>();
        label.addLapTime(lapStrings);
        lapTime = 0;
        time = 0;
    }

    private void tick() {
        time++;
        setTime();
    }

    public static final Creator<MyTimer> CREATOR = new Creator<MyTimer>() {
        @Override
        public MyTimer createFromParcel(Parcel in) {
            return new MyTimer(in);
        }

        @Override
        public MyTimer[] newArray(int size) {
            return new MyTimer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(time);
        dest.writeInt(lapTime);
        dest.writeByte((byte) (running ? 1 : 0));
        dest.writeByte((byte) (stopped ? 1 : 0));
        dest.writeList(lapStrings);
    }
}