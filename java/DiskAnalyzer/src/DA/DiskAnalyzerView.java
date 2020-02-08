/*
 * DiskAnalyzerView.java
 */
package DA;

import DA.application.MainTask;
import DA.common.FileSystemViewExt;
import DA.Tree.RootsTree;
import DA.TreeMap.SimpleFile;
import DA.TreeMap.TreeMapView;
import DA.TreeMap.TreeMapViewHistory;
import java.util.Observable;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observer;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskService;

/**
 * The application's main frame.
 */
public class DiskAnalyzerView extends FrameView implements Observer{

    private FileSystemViewExt fsve = new FileSystemViewExt();
    private ApplicationContext appContext = Application.getInstance().getContext();
    private TaskService taskService = appContext.getTaskService();
    private TaskMonitor taskMonitor = appContext.getTaskMonitor();
    private TreeMapViewHistory history = TreeMapViewHistory.getInstance();
    private MainTask mainTask = null;

    public DiskAnalyzerView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                    startBtn.setText("stop");
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                    startBtn.setText("run");
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        this.getFrame().pack();
        this.getFrame().setLocationRelativeTo(null);
        init();
    }

    private void init() {
        history.addObserver(this);
        historyBack.setEnabled(false);
        historyForward.setEnabled(false);
       /* lessDetail.setEnabled(false);
        moreDetail.setEnabled(false);*/
    }
    
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DiskAnalyzer.getApplication().getMainFrame();
            aboutBox = new DiskAnalyzerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DiskAnalyzer.getApplication().show(aboutBox);
    }

    private void startNewTask() {
        SimpleFile selectedSimpleFile = ((RootsTree) rootsTree).getSelected();
        if (selectedSimpleFile != null) {
            File selectedFile = new File(selectedSimpleFile.getPath());

            mainTask = new MainTask(Application.getInstance());
            mainTask.initializeTask(selectedFile, canvas);
            taskService.execute(mainTask);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "You must choose where to start.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        splitter = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        startBtn = new javax.swing.JButton();
        treeScrollPane = new javax.swing.JScrollPane();
        rootsTree = new RootsTree();
        canvas = new DA.TreeMap.TreeMapView();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        toolBar = new javax.swing.JToolBar();
        historyBack = new javax.swing.JButton();
        historyForward = new javax.swing.JButton();
        lessDetail = new javax.swing.JButton();
        moreDetail = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N

        splitter.setDividerLocation(150);
        splitter.setDividerSize(2);
        splitter.setName("splitter"); // NOI18N

        leftPanel.setName("leftPanel"); // NOI18N

        startBtn.setText("start scan"); // NOI18N
        startBtn.setName("startBtn"); // NOI18N
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBtnActionPerformed(evt);
            }
        });

        treeScrollPane.setName("treeScrollPane"); // NOI18N

        rootsTree.setName("rootsTree"); // NOI18N
        treeScrollPane.setViewportView(rootsTree);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(startBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
            .addComponent(treeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                .addComponent(treeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startBtn))
        );

        splitter.setLeftComponent(leftPanel);

        canvas.setName("canvas"); // NOI18N

        javax.swing.GroupLayout canvasLayout = new javax.swing.GroupLayout(canvas);
        canvas.setLayout(canvasLayout);
        canvasLayout.setHorizontalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 847, Short.MAX_VALUE)
        );
        canvasLayout.setVerticalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 429, Short.MAX_VALUE)
        );

        splitter.setRightComponent(canvas);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText("File"); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(DA.DiskAnalyzer.class).getContext().getActionMap(DiskAnalyzerView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText("Help"); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 830, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(DA.DiskAnalyzer.class).getContext().getResourceMap(DiskAnalyzerView.class);
        historyBack.setText(resourceMap.getString("historyBack.text")); // NOI18N
        historyBack.setFocusable(false);
        historyBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        historyBack.setName("historyBack"); // NOI18N
        historyBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        historyBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyBackActionPerformed(evt);
            }
        });
        toolBar.add(historyBack);

        historyForward.setText(resourceMap.getString("historyForward.text")); // NOI18N
        historyForward.setFocusable(false);
        historyForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        historyForward.setName("historyForward"); // NOI18N
        historyForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        historyForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyForwardActionPerformed(evt);
            }
        });
        toolBar.add(historyForward);

        lessDetail.setText(resourceMap.getString("lessDetail.text")); // NOI18N
        lessDetail.setFocusable(false);
        lessDetail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lessDetail.setName("lessDetail"); // NOI18N
        lessDetail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lessDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lessDetailActionPerformed(evt);
            }
        });
        toolBar.add(lessDetail);

        moreDetail.setText(resourceMap.getString("moreDetail.text")); // NOI18N
        moreDetail.setFocusable(false);
        moreDetail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moreDetail.setName("moreDetail"); // NOI18N
        moreDetail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moreDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreDetailActionPerformed(evt);
            }
        });
        toolBar.add(moreDetail);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(toolBar);
    }// </editor-fold>//GEN-END:initComponents

    public void update(Observable o, Object arg) {
            historyBack.setEnabled(history.hasHistory());
    }
    
    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        canvas.repaint();

        if (mainTask != null) {
            if (mainTask.isStarted()) {
                mainTask.fireStop();
                mainTask.cancel(true);
                mainTask = null;
            } else {
                startNewTask();
            }
        } else {
            startNewTask();
        }
    }//GEN-LAST:event_startBtnActionPerformed

    private void historyBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyBackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_historyBackActionPerformed

    private void lessDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lessDetailActionPerformed
        if (TreeMapView.CURRENT_DEPTH >= 1) {
            TreeMapView.CURRENT_DEPTH--;
            moreDetail.setEnabled(true);
        }
        else {
            lessDetail.setEnabled(false);
        }
        canvas.repaint();
    }//GEN-LAST:event_lessDetailActionPerformed

    private void moreDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreDetailActionPerformed
        if (TreeMapView.CURRENT_DEPTH <= TreeMapView.MAX_DEPTH) {
            TreeMapView.CURRENT_DEPTH++;
            lessDetail.setEnabled(true);
        }
        else {
            moreDetail.setEnabled(false);
        }
        canvas.repaint();
    }//GEN-LAST:event_moreDetailActionPerformed

    private void historyForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyForwardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_historyForwardActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DA.TreeMap.TreeMapView canvas;
    private javax.swing.JButton historyBack;
    private javax.swing.JButton historyForward;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JButton lessDetail;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton moreDetail;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTree rootsTree;
    private javax.swing.JSplitPane splitter;
    private javax.swing.JButton startBtn;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
