package cz.merxbj.unip.gui;

import cz.merxbj.unip.application.MainTask;
import cz.merxbj.unip.application.UniPaaSProfiler;
import cz.merxbj.unip.common.CommonStatics;
import cz.merxbj.unip.common.CustomizedFileFilter;
import cz.merxbj.unip.common.MainTaskObserverResponse;
import cz.merxbj.unip.core.LogEvent;
import cz.merxbj.unip.core.LogReader;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author mrneo
 */
public class UniPaaSProfilerView extends javax.swing.JFrame implements Observer {

    private MainTask mainTask;
    private Thread mainThread;
    private File logFile;
    private LogReader reader; // it is here only for avoid multiple getReader() calling

    public UniPaaSProfilerView() {
        this("");
    }
    
    /**
     * Creates new form MainFrame
     */
    public UniPaaSProfilerView(String logFilePath) {
        initComponents();
        initFrame();

        this.logFileField.setText(logFilePath);
    }

    private void initFrame() {
        this.setTitle(UniPaaSProfiler.appName + " " + UniPaaSProfiler.version);
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logFileField = new javax.swing.JTextField();
        browseBtn = new javax.swing.JButton();
        fileLabel = new javax.swing.JLabel();
        frameSeparator = new javax.swing.JSeparator();
        mainScrollPanel = new JScrollPane();
        treeTable = new cz.merxbj.unip.gui.tt.TreeTable();
        startStopBtn = new javax.swing.JButton();
        statusBar = new javax.swing.JPanel();
        statusBarText = new javax.swing.JLabel();
        stusBarProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        logFileField.setToolTipText("Choose file ...");

        browseBtn.setText("browse");
        browseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseBtnActionPerformed(evt);
            }
        });

        fileLabel.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fileLabel.setText("File");

        mainScrollPanel.setBorder(null);

        treeTable.setFillsViewportHeight(true);
        mainScrollPanel.setViewportView(treeTable);

        startStopBtn.setText("start");
        startStopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopBtnActionPerformed(evt);
            }
        });

        statusBarText.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        javax.swing.GroupLayout statusBarLayout = new javax.swing.GroupLayout(statusBar);
        statusBar.setLayout(statusBarLayout);
        statusBarLayout.setHorizontalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarLayout.createSequentialGroup()
                .addComponent(statusBarText, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stusBarProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusBarLayout.setVerticalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(stusBarProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(statusBarText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameSeparator, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logFileField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startStopBtn)
                .addContainerGap())
            .addComponent(mainScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1020, Short.MAX_VALUE)
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(browseBtn)
                    .addComponent(logFileField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startStopBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frameSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startStopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStopBtnActionPerformed
        if (mainTask == null) {
            this.startNewTask();
        } else if (!mainTask.isRunning()) {
            this.startNewTask();
        } else {
            mainTask.fireStop();
        }
    }//GEN-LAST:event_startStopBtnActionPerformed

    private void browseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseBtnActionPerformed
        JFileChooser chooser = new JFileChooser(this.logFileField.getText());
        FileFilter filter = new CustomizedFileFilter("Log files (*.log, *.txt)", new String[]{"log", "txt"});
        chooser.addChoosableFileFilter(filter);

        int returnVal = chooser.showDialog(this, "Open log");

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.logFileField.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_browseBtnActionPerformed

    private void startNewTask() {
        logFile = new File(logFileField.getText());
        if ((!this.logFileField.getText().isEmpty()) && logFile.exists()) {
            setupProgressBar();
            mainTask = this.createNewTask();
            mainThread = new Thread(mainTask);
            mainThread.start();

            reader = mainTask.getReader();
        } else {
            CommonStatics.invokeErrorDialog("Please recheck file path you provided!");
        }
    }

    private MainTask createNewTask() {
        mainTask = new MainTask(logFile);
        mainTask.addObserver(this);
        mainTask.setRoot(this.getRootLogEvent());

        return mainTask;
    }

    private LogEvent getRootLogEvent() {
        LogEvent root = ((LogEvent) treeTable.getTreeTableModel().getRoot());
        root.setDescription(logFile.getName());

        return root;
    }

    private void setupProgressBar() {
        stusBarProgressBar.setMaximum(Math.round(logFile.length()));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MainTask) {
            MainTaskObserverResponse response = (MainTaskObserverResponse) arg;
            if (response.equals(MainTaskObserverResponse.UPDATESTATE)) {
                if (mainTask.isRunning()) {
                    this.startStopBtn.setText("stop");
                    this.updateStatusBarText("File reading started ...");
                } else {
                    this.startStopBtn.setText("start");
                    this.updateStatusBarText("File reading has been stopped ...");
                }
            } else if (response.equals(MainTaskObserverResponse.UPDATEPROGRESS)) {
                this.updateProgressBar(reader.getCurrentTotal());
            }
        }
    }

    public void updateStatusBarText(String text) {
        this.statusBarText.setText(text);
    }

    private void updateProgressBar(int value) {
        this.stusBarProgressBar.setValue(value);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseBtn;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JSeparator frameSeparator;
    private javax.swing.JTextField logFileField;
    private javax.swing.JScrollPane mainScrollPanel;
    private javax.swing.JButton startStopBtn;
    private javax.swing.JPanel statusBar;
    private javax.swing.JLabel statusBarText;
    private javax.swing.JProgressBar stusBarProgressBar;
    private cz.merxbj.unip.gui.tt.TreeTable treeTable;
    // End of variables declaration//GEN-END:variables
}
