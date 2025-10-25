package _07_GUI;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import _04_OperatingSystem.Process1;
import static _04_OperatingSystem.ProcessState.READY;
import static _04_OperatingSystem.ProcessState.RUNNING;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 *
 * @author Danaz
 */
public class PCBPanel extends javax.swing.JPanel {

    private final Process1 process;

    public PCBPanel(Process1 p) {
        this.process = p;
        initUI();
        updateColorByState();
    }

    private void initUI() {
        // Fondo y bordes
        setBackground(new Color(55, 58, 60)); // gris oscuro
        setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2, true));
        setPreferredSize(new Dimension(160, 120));

        // Layout vertical
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Fuente y color
        Font font = new Font("Segoe UI", Font.PLAIN, 12);
        Color textColor = new Color(0,0,0);

        // Crear labels
        idLabel = new JLabel("ID: " + process.getPID());
        nameLabel = new JLabel("Nombre: " + process.getPName());
        stateLabel = new JLabel("Estado: " + process.getPState());
        pcLabel = new JLabel("PC: " + process.getPC());
        marLabel = new JLabel("MAR: " + process.getMAR());

        // Aplicar formato
        for (JLabel lbl : new JLabel[]{idLabel, nameLabel, stateLabel, pcLabel, marLabel}) {
            lbl.setFont(font);
            lbl.setForeground(textColor);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(lbl);
            add(Box.createVerticalStrut(3)); // espacio entre líneas
        }
    }

    public void refresh() {
        stateLabel.setText("Estado: " + process.getState());
        pcLabel.setText("PC: " + process.getRemainingInstructions());
        marLabel.setText("MAR: " + process.getMAR());
        repaint();
    }

    private void updateColorByState() {
        switch (process.getPState()) {
            case READY ->
                setBackground(new Color(200, 255, 200));
            case BLOCKED ->
                setBackground(new Color(255, 220, 180));
            case NEW ->
                setBackground(new Color(220, 220, 255));
            case RUNNING ->
                setBackground(new Color(255, 255, 180));
            case TERMINATED ->
                setBackground(new Color(230, 230, 230));
            default ->
                setBackground(Color.LIGHT_GRAY);
        }
    }

    public Process1 getProcess() {
        return process;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        idLabel = new javax.swing.JLabel();
        stateLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        pcLabel = new javax.swing.JLabel();
        marLabel = new javax.swing.JLabel();

        idLabel.setForeground(new java.awt.Color(0, 0, 0));
        idLabel.setText("ID:");

        stateLabel.setForeground(new java.awt.Color(0, 0, 0));
        stateLabel.setText("Estado:");

        nameLabel.setForeground(new java.awt.Color(0, 0, 0));
        nameLabel.setText("Nombre:");

        pcLabel.setForeground(new java.awt.Color(0, 0, 0));
        pcLabel.setText("PC:");

        marLabel.setForeground(new java.awt.Color(0, 0, 0));
        marLabel.setText("MAR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(idLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(stateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pcLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(marLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(idLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JLabel idLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel marLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pcLabel;
    private javax.swing.JLabel stateLabel;
    // End of variables declaration//GEN-END:variables
}
