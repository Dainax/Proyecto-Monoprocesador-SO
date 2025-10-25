package _07_GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import _04_OperatingSystem.Process1;
import static _04_OperatingSystem.ProcessState.READY;
import static _04_OperatingSystem.ProcessState.RUNNING;
import java.awt.Font;


/**
 *
 * @author Danaz
 */
public class PCBPanel extends javax.swing.JPanel {

    private final Process1 process;

    public PCBPanel(Process1 p) {
        this.process = p;

        setLayout(new GridLayout(2, 2, 5, 2));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        setBackground(new Color(245, 245, 245));
        setPreferredSize(new Dimension(140, 50));

        nameLabel = new JLabel("Nombre: " + p.getName());
        pidLabel = new JLabel("PID: " + p.getPID());
        stateLabel = new JLabel("Estado: " + p.getState());
        pcLabel = new JLabel("Ciclos: " + p.getRemainingInstructions());
        marLabel = new JLabel("Ciclos: " + p.getRemainingInstructions());

        Font font = new Font("Segoe UI", Font.PLAIN, 11);
        nameLabel.setFont(font);
        pidLabel.setFont(font);
        stateLabel.setFont(font);
        pcLabel.setFont(font);
        marLabel.setFont(font);
        
        add(nameLabel);
        add(pidLabel);
        add(stateLabel);
        add(pcLabel);
        add(marLabel);

        updateColorByState();
    }

 
    private void updateColorByState() {
        switch (process.getPState()) {
            case READY -> setBackground(new Color(200, 255, 200));
            case BLOCKED -> setBackground(new Color(255, 220, 180));
            case NEW -> setBackground(new Color(220, 220, 255));
            case RUNNING -> setBackground(new Color(255, 255, 180));
            case TERMINATED -> setBackground(new Color(230, 230, 230));
            default -> setBackground(Color.LIGHT_GRAY);
        }
    }

    public void refresh() {
        stateLabel.setText("Estado: " + process.getState());
        pcLabel.setText("Ciclos: " + process.getRemainingInstructions());
        //updateColorByState();
        repaint();
    }

    public Process1 getProcess() {
        return process;
    }
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pidLabel = new javax.swing.JLabel();
        stateLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        pcLabel = new javax.swing.JLabel();
        marLabel = new javax.swing.JLabel();

        pidLabel.setText("ID:");

        stateLabel.setText("Estado:");

        nameLabel.setText("Nombre:");

        pcLabel.setText("PC:");

        marLabel.setText("MAR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pidLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(stateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pcLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(marLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pidLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(stateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pcLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(marLabel)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel marLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pcLabel;
    private javax.swing.JLabel pidLabel;
    private javax.swing.JLabel stateLabel;
    // End of variables declaration//GEN-END:variables
}
