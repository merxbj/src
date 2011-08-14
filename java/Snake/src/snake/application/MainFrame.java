package snake.application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import snake.core.Field;

public class MainFrame extends javax.swing.JFrame {

    public MainFrame() {
        initComponents();
    }

    public void init() {
        Field field = new Field();
        field.init();
        canvas.setField(field);
    }

    private void initComponents() {

        canvas = new MainCanvas();
        btnBegin = new JButton();
        btnEnd = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        btnBegin.setText("Begin Simulation");
        btnBegin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBeginActionPerformed(evt);
            }
        });

        btnEnd.setText("End Simulation");
        btnEnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(canvas, GroupLayout.PREFERRED_SIZE, 640, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEnd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBegin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBegin)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEnd))
                    .addComponent(canvas, GroupLayout.PREFERRED_SIZE, 480, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(109, Short.MAX_VALUE))
        );

        pack();
    }

    private void btnBeginActionPerformed(java.awt.event.ActionEvent evt) {
        canvas.setQuit(false);
        new Thread(canvas).start();
    }

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {
        canvas.setQuit(true);
    }

    private JButton btnBegin;
    private JButton btnEnd;
    private MainCanvas canvas;

}
