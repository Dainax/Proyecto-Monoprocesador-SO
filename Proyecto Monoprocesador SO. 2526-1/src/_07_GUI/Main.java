package _07_GUI;

import java.awt.CardLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Danaz
 */
public class Main extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());

    private CardLayout cardLayout;
    private JPanel simulationPanel, configPanel, graphicsPanel, statsPanel;

    public Main() {
        initComponents();
        setTitle("Simulacion Monoprocesador");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        menuPanel1.initMoving(Main.this);

        cardLayout = new CardLayout();
        content.setLayout(cardLayout);

        simulationPanel = new SimulationPanel();
        configPanel = new ConfigPanel();
        graphicsPanel = new GraphicsPanel();
        statsPanel = new StatsPanel();

        content.add(simulationPanel, "simulation");
        content.add(configPanel, "config");
        content.add(graphicsPanel, "graphics");
        content.add(statsPanel, "stats");

        cardLayout.show(content, "simulation");

        menuPanel1.setMainFrame(this);

        //Imagen de la aplicacion
        String rutaIcono = "/_08_SourcesGUI/CpuIcon-blue.png";

        try {
            // Carga la imagen desde la ruta relativa del proyecto
            Image icono = new ImageIcon(getClass().getResource(rutaIcono)).getImage();

            // Establece la imagen como el icono de la ventana
            this.setIconImage(icono);

        } catch (NullPointerException e) {
            System.err.println("No se encontró el archivo de imagen en la ruta: " + rutaIcono);
           
        }

    }

    public void switchToPanel(String panelName) {
        cardLayout.show(content, panelName);  // Changed 'content' to 'jPanel1'
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        menuPanel1 = new _07_GUI.MenuPanel();
        content = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        menuPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.add(menuPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 670));

        content.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );

        jPanel1.add(content, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 1000, 670));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel content;
    private javax.swing.JPanel jPanel1;
    private _07_GUI.MenuPanel menuPanel1;
    // End of variables declaration//GEN-END:variables

}
