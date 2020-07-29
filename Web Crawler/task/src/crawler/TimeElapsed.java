package crawler;

import javax.swing.*;

public class TimeElapsed {

    private JLabel elapsedTimeValLabel;
    private boolean keepUpdatingTime;
    private Thread thread;

    long startTime = System.currentTimeMillis();

    TimeElapsed(JLabel elapsedTimeValLabel) {
        this.elapsedTimeValLabel = elapsedTimeValLabel;
        keepUpdatingTime = true;

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    updateTime();
                } catch (Exception ie) {
                    // do nothing
                }
            }
        };


        this.thread = new Thread(runnable);
        thread.start();

    }

    public void updateTime() {
        try {
            while (keepUpdatingTime) {
                elapsedTimeValLabel.setText(getTimeElapsed());
                Thread.currentThread().sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Exception in Thread Sleep : " + e);
        }
    }

    public String getTimeElapsed() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        elapsedTime = elapsedTime / 1000;

        String seconds = Integer.toString((int) (elapsedTime % 60));
        String minutes = Integer.toString((int) ((elapsedTime % 3600) / 60));
        String hours = Integer.toString((int) (elapsedTime / 3600));

        if (seconds.length() < 2)
            seconds = "0" + seconds;

        if (minutes.length() < 2)
            minutes = "0" + minutes;

        if (hours.length() < 2)
            hours = "0" + hours;

        return hours + ":" + minutes + ":" + seconds;
    }

    public void stop() {
        this.keepUpdatingTime = false;
    }
}
