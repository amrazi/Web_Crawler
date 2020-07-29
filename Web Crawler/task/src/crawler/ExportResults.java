package crawler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class ExportResults implements ActionListener {
    private Map<String, String> siteTable;
    private JTextField exportTextField;

    public ExportResults(Map<String, String> siteTable, JTextField exportTextField) {
        this.siteTable = siteTable;
        this.exportTextField = exportTextField;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        final String exportPath = exportTextField.getText();
        System.out.println("exporting siteTable, reproduced below");
        System.out.println(siteTable);

        File file = new File(exportPath);
        try (PrintWriter printWriter = new PrintWriter(file)) {

            int count = 0;

            if (siteTable.size() > 0) {
                for (Map.Entry<String, String> entry : siteTable.entrySet()) {

                    count++;

                    printWriter.println(entry.getKey());
                    if (count == siteTable.size()) {
                        printWriter.print(entry.getValue());
                    } else {
                        printWriter.println(entry.getValue());
                    }

                }
            }
        } catch (IOException ioe) {
            System.out.println("Error writing file: IOException");
        }
    }
}
