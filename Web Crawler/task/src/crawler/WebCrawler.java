package crawler;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebCrawler extends JFrame {
    Map<String, String> siteTable = new ConcurrentHashMap<>();

    public WebCrawler() {
        super("Hello App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 330);
        setLocationRelativeTo(null);

        initComponents();

        setLayout(null);
        setVisible(true);
    }

    private void initComponents() {

        // URL

        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setBounds(10, 10, 110, 30);
        add(urlLabel);

        JTextField urlTextField = new JTextField();
        urlTextField.setName("UrlTextField");
        urlTextField.setBounds(120, 10, 545, 30);
        add(urlTextField);

        JToggleButton button = new JToggleButton("Run");
        button.setName("RunButton");
        button.setBounds(675, 10, 100, 30);
        add(button);
        // button.addActionListener after depthTextField

        // workers

        JLabel workersLabel = new JLabel("Workers:");
        workersLabel.setBounds(10, 50, 110, 30);
        add(workersLabel);

        JTextField workersTextField = new JTextField();
        //workersTextField.setName("UrlTextField");
        workersTextField.setBounds(120, 50, 655, 30);
        add(workersTextField);

        // depth

        JLabel depthLabel = new JLabel("Maximum depth:");
        depthLabel.setBounds(10, 90, 110, 30);
        add(depthLabel);

        JTextField depthTextField = new JTextField();
        depthTextField.setName("DepthTextField");
        depthTextField.setBounds(120, 90, 550, 30);
        add(depthTextField);

        JCheckBox depthCheck = new JCheckBox("Enabled");
        depthCheck.setName("DepthCheckBox");
        depthCheck.setBounds(680, 90, 100, 30);
        add(depthCheck);

        // time limit settings

        JLabel timeLabel = new JLabel("Time limit:");
        timeLabel.setBounds(10, 130, 110, 30);
        add(timeLabel);

        JTextField timeTextField = new JTextField();
        //timeTextField.setName("timeTextField");
        timeTextField.setBounds(120, 130, 485, 30);
        add(timeTextField);

        JLabel secLabel = new JLabel("seconds");
        secLabel.setBounds(615, 130, 55, 30);
        add(secLabel);

        JCheckBox timeCheck = new JCheckBox("Enabled");
        //timeCheck.setName("timeCheck");
        timeCheck.setBounds(680, 130, 100, 30);
        add(timeCheck);

        // elapsed time output

        JLabel elapsedTimeLabel = new JLabel("Elapsed time:");
        elapsedTimeLabel.setBounds(10, 170, 110, 30);
        add(elapsedTimeLabel);

        JLabel elapsedTimeValLabel = new JLabel("0:00");
        elapsedTimeValLabel.setBounds(120, 170, 110, 30);
        add(elapsedTimeValLabel);

        // parsed pages

        JLabel parsedPagesLabel = new JLabel("Parsed pages:");
//        parsedPagesLabel.setName("ParsedLabel");
        parsedPagesLabel.setBounds(10, 210, 110, 30);
        add(parsedPagesLabel);

        JLabel parsedPagesValLabel = new JLabel("0");
        parsedPagesValLabel.setName("ParsedLabel");
        parsedPagesValLabel.setBounds(120, 210, 110, 30);
        add(parsedPagesValLabel);

        // (now can addActionListener bc now have depth, worker, and time limit info)
        button.addActionListener(new URLRun(siteTable, urlTextField, depthTextField, workersTextField, timeTextField, timeCheck, elapsedTimeValLabel, parsedPagesValLabel));

        // export

        JLabel exportLabel = new JLabel("Export");
        exportLabel.setBounds(10, 250, 110, 30);
        add(exportLabel);

        JTextField exportTextField = new JTextField();
        exportTextField.setName("ExportUrlTextField");
        exportTextField.setBounds(120, 250, 545, 30);
        add(exportTextField);

        JButton exportButton = new JButton("Export");
        exportButton.setName("ExportButton");
        exportButton.setBounds(675, 250, 100, 30);
        add(exportButton);
        exportButton.addActionListener(new ExportResults(siteTable, exportTextField));

    }

}