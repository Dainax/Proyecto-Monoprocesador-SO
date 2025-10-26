/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package _07_GUI;
import _01_ApplicationPackage.Simulator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.lang.String; 
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


/**
 *
 * @author Danaz
 */
public class GraphicsPanel extends javax.swing.JPanel {

    /**
     * Creates new form GraphicsPanel
     */
    public GraphicsPanel() {
        initComponents();
    }

    private Simulator simulator;
    private TimeSeries cpuSeries;
    private TimeSeries avgWaitSeries;
    private TimeSeries throughputSeries;
    private TimeSeries fairnessSeries;

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
        initChart();
        startAutoRefresh();
        
   
    }

    
    public void initChart() {
        chartContainer.setLayout(new GridLayout(2, 2, 5, 5)); // 2x2 con separación de 5 px

    // CPU
    cpuSeries = new TimeSeries("CPU Utilization");
    TimeSeriesCollection cpuDataset = new TimeSeriesCollection(cpuSeries);
    JFreeChart cpuChart = ChartFactory.createTimeSeriesChart(
        "CPU Utilization",
        "Tiempo",
        "CPU %",
        cpuDataset,
        false, true, false
    );
    XYPlot cpuPlot = cpuChart.getXYPlot();
XYLineAndShapeRenderer cpuRenderer = new XYLineAndShapeRenderer();
cpuRenderer.setSeriesPaint(0, Color.RED);
cpuRenderer.setSeriesShapesVisible(0, false);
cpuPlot.setRenderer(cpuRenderer);
    ChartPanel cpuChartPanel = new ChartPanel(cpuChart);

    // Avg Wait
    avgWaitSeries = new TimeSeries("Avg Wait Time");
    TimeSeriesCollection waitDataset = new TimeSeriesCollection(avgWaitSeries);
    JFreeChart waitChart = ChartFactory.createTimeSeriesChart(
        "Average Waiting Time",
        "Tiempo",
        "Ciclos",
        waitDataset,
        false, true, false
    );
    
    XYPlot waitPlot = waitChart.getXYPlot();
XYLineAndShapeRenderer waitRenderer = new XYLineAndShapeRenderer();
waitRenderer.setSeriesPaint(0, Color.BLUE);       // línea azul
waitRenderer.setSeriesShapesVisible(0, false);
waitPlot.setRenderer(waitRenderer);
    ChartPanel waitChartPanel = new ChartPanel(waitChart);

    // Throughput
    throughputSeries = new TimeSeries("Throughput");
    TimeSeriesCollection throughputDataset = new TimeSeriesCollection(throughputSeries);
    JFreeChart throughputChart = ChartFactory.createTimeSeriesChart(
        "Throughput",
        "Tiempo",
        "Procesos/s",
        throughputDataset,
        false, true, false
    );
    
    XYPlot throughputPlot = throughputChart.getXYPlot();
XYLineAndShapeRenderer throughputRenderer = new XYLineAndShapeRenderer();
throughputRenderer.setSeriesPaint(0, Color.GREEN); // línea verde
throughputRenderer.setSeriesShapesVisible(0, false);
throughputPlot.setRenderer(throughputRenderer);
    ChartPanel throughputChartPanel = new ChartPanel(throughputChart);

    // Fairness
    fairnessSeries = new TimeSeries("Fairness");
    TimeSeriesCollection fairnessDataset = new TimeSeriesCollection(fairnessSeries);
    JFreeChart fairnessChart = ChartFactory.createTimeSeriesChart(
        "Fairness",
        "Tiempo",
        "Valor",
        fairnessDataset,
        false, true, false
    );
    
    XYPlot fairnessPlot = fairnessChart.getXYPlot();
XYLineAndShapeRenderer fairnessRenderer = new XYLineAndShapeRenderer();
fairnessRenderer.setSeriesPaint(0, Color.ORANGE);  // línea naranja
fairnessRenderer.setSeriesShapesVisible(0, false);
fairnessPlot.setRenderer(fairnessRenderer);
    ChartPanel fairnessChartPanel = new ChartPanel(fairnessChart);

        // Limpiar contenido previo
chartContainer.removeAll();

// Agregar los gráficos
chartContainer.add(cpuChartPanel);
chartContainer.add(waitChartPanel);
chartContainer.add(throughputChartPanel);
chartContainer.add(fairnessChartPanel);

// Refrescar UI
chartContainer.revalidate();
chartContainer.repaint();


}

    private void updateLabels() {
    Throughput.setText(String.format("Throughput: %.2f", this.simulator.calculateThroughput()));
    CpuUtilization.setText(String.format("CPU Utilization: %.2f%%", this.simulator.getCPUProductivePercentage()));



    Fairness.setText(String.format("Fairness: %.2f ", this.simulator.getTotalFairness()));
    AverageWaitTime.setText(String.format("Avg Response Time: %.2f ciclos", this.simulator.getAverageWaitingTime()));
    Clockcycles.setText(String.valueOf(this.simulator.getOperatingSystem().getClock().getTotalCyclesElapsed()));

}
    private void startAutoRefresh() {
    javax.swing.Timer timer = new javax.swing.Timer(1000, e -> updateChart());
    timer.start();
}
    
   private void updateChart() {
    if (simulator == null) return;

    Millisecond now = new Millisecond();

    cpuSeries.addOrUpdate(now, simulator.getCPUProductivePercentage());
    avgWaitSeries.addOrUpdate(now, simulator.getAverageWaitingTime());
    throughputSeries.addOrUpdate(now, simulator.calculateThroughput());
    fairnessSeries.addOrUpdate(now, simulator.getTotalFairness());

    updateLabels(); // actualizar labels también si los tenés
}





           
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Throughput = new javax.swing.JLabel();
        CpuUtilization = new javax.swing.JLabel();
        Fairness = new javax.swing.JLabel();
        AverageWaitTime = new javax.swing.JLabel();
        Clockcycles = new javax.swing.JLabel();
        chartContainer = new javax.swing.JPanel();

        jPanel3.setBackground(new java.awt.Color(13, 84, 141));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Tiempo de Respuesta:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, -1));

        jLabel6.setText("Equidad:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, -1));

        jLabel4.setText("Utilización del Procesador:");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, -1, -1));

        jLabel3.setText("Throughput:");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 80, 20));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Medidas de Rendimiento");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 320, 60));

        Throughput.setText("Thoroughtput");
        jPanel3.add(Throughput, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 130, 300, -1));

        CpuUtilization.setText("CPU utilization");
        jPanel3.add(CpuUtilization, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 330, 210, 20));

        Fairness.setText("Fairness");
        jPanel3.add(Fairness, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 210, 220, -1));

        AverageWaitTime.setText("WaitTime");
        jPanel3.add(AverageWaitTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 270, 380, 20));

        Clockcycles.setText("Ciclos totales de reloj");
        jPanel3.add(Clockcycles, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 390, 210, -1));

        javax.swing.GroupLayout chartContainerLayout = new javax.swing.GroupLayout(chartContainer);
        chartContainer.setLayout(chartContainerLayout);
        chartContainerLayout.setHorizontalGroup(
            chartContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 630, Short.MAX_VALUE)
        );
        chartContainerLayout.setVerticalGroup(
            chartContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );

        jPanel3.add(chartContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, 630, 640));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(91, 91, 91))
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
    private javax.swing.JLabel AverageWaitTime;
    private javax.swing.JLabel Clockcycles;
    private javax.swing.JLabel CpuUtilization;
    private javax.swing.JLabel Fairness;
    private javax.swing.JLabel Throughput;
    private javax.swing.JPanel chartContainer;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
