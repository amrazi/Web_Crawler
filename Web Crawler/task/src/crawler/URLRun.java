package crawler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class URLRun implements ActionListener {
    private int parsedPagesVal;
    private Map<String, String> siteTable;
    private JTextField urlTextField;
    private JTextField depthTextField;
    private JTextField workersTextField;
    private JTextField timeTextField;
    private JCheckBox timeCheck;
    private JLabel elapsedTimeValLabel;
    private JLabel parsedPagesValLabel;
    private long startTime;
    private boolean timeLimitHit;

    // parsedPagesValLabel
    private Queue<String> queue;
    private Queue<String> nextDepthQueue;

    public URLRun(Map<String, String> siteTable, JTextField urlTextField, JTextField depthTextField, JTextField workersTextField, JTextField timeTextField, JCheckBox timeCheck, JLabel elapsedTimeValLabel, JLabel parsedPagesValLabel) {
        parsedPagesVal = 0;
        this.siteTable = siteTable;
        this.urlTextField = urlTextField;
        this.depthTextField = depthTextField;
        this.workersTextField = workersTextField;
        this.timeTextField = timeTextField;
        this.timeCheck = timeCheck;
        this.elapsedTimeValLabel = elapsedTimeValLabel;
        this.parsedPagesValLabel = parsedPagesValLabel;
        queue = new ConcurrentLinkedQueue<>();
        nextDepthQueue = new ConcurrentLinkedQueue<>();
        timeLimitHit = false;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String url = urlTextField.getText();
        siteTable.clear();
        queue.clear();
        nextDepthQueue.clear();
        queue.add(url);
        crawlQueueManager();
    }

    private void crawlQueueManager() {

        long timeLimit;

        if (timeCheck.isSelected()) {
            timeLimit = Long.parseLong(timeTextField.getText()) * 1000;
        } else {
            timeLimit = Long.MAX_VALUE;
        }

        int depth = Integer.parseInt(depthTextField.getText());
        int numThreads;
        try {
            numThreads = Integer.parseInt(workersTextField.getText());
        } catch (NumberFormatException nfe) {
            numThreads = 1;
        }

        TimeElapsed timer = new TimeElapsed(elapsedTimeValLabel);

        startTime = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                try {
                    timeLimitHitCalc(timeLimit);
                } catch (Exception ie) {
                    // do nothing
                }
            }
        }).start();


        for (int i = 0; i < depth + 1; i++) {

            if (timeLimitHit) {
                break;
            }

            ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);

            for (int j = 0; j < numThreads; j++) {
                threadPool.submit(new CrawlRunnable(queue, nextDepthQueue, siteTable, parsedPagesValLabel, parsedPagesVal, depth));
            }

            threadPool.shutdown();
            try {
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (!nextDepthQueue.isEmpty()) {
                queue.add(nextDepthQueue.poll());
            }

            parsedPagesVal = siteTable.size();
            parsedPagesValLabel.setText(Integer.toString(parsedPagesVal));
        }

        timer.stop();
    }

    private void timeLimitHitCalc(long timeLimit) {
        boolean keepChecking = true;
        {
            try {
                while (keepChecking) {
                    if (System.currentTimeMillis() - startTime > timeLimit) {
                        keepChecking = false;
                    }
                    Thread.currentThread().sleep(1000);
                }
            } catch (Exception e) {
                System.out.println("Exception in Thread Sleep : " + e);
            }
        }
    }


}
