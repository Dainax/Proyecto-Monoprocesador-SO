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
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 *
 * @author Danaz
 */
public class SimplePCBPanel extends javax.swing.JPanel {

    public SimplePCBPanel(String name, String type, int instructions) {
        setLayout(new GridLayout(3, 1));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        setBackground(new Color(245, 245, 245));
        setPreferredSize(new Dimension(200, 70));

        nameLabel = new JLabel("Nombre: " + name);
        typeLabel = new JLabel("Tipo: " + type);
        instructionsLabel = new JLabel("Instrucciones: " + instructions);

        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        add(nameLabel);
        add(typeLabel);
        add(instructionsLabel);
        
        initUI();
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

        // Aplicar formato
        for (JLabel lbl : new JLabel[]{nameLabel, typeLabel, instructionsLabel}) {
            lbl.setFont(font);
            lbl.setForeground(textColor);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(lbl);
            add(Box.createVerticalStrut(3)); // espacio entre líneas
        }
    }

   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        typeLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        instructionsLabel = new javax.swing.JLabel();

        typeLabel.setForeground(new java.awt.Color(0, 0, 0));
        typeLabel.setText("Type:");

        nameLabel.setForeground(new java.awt.Color(0, 0, 0));
        nameLabel.setText("Nombre:");

        instructionsLabel.setForeground(new java.awt.Color(0, 0, 0));
        instructionsLabel.setText("PC:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(typeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(instructionsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(nameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(typeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instructionsLabel)
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
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
}
