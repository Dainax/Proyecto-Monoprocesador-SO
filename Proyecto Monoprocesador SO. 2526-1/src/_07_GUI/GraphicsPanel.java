/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package _07_GUI;

import java.lang.String; 

import _01_ApplicationPackage.Simulator;
import _04_OperatingSystem.PolicyType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.SwingUtilities;

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
private static class PolicyCharts {
    final TimeSeries cpuSeries;
    final TimeSeries avgWaitSeries;
    final TimeSeries throughputSeries;
    final TimeSeries fairnessSeries;
    final javax.swing.JPanel containerPanel;

    PolicyCharts(TimeSeries cpu, TimeSeries wait, TimeSeries thr, TimeSeries fair, javax.swing.JPanel panel) {
        this.cpuSeries = cpu;
        this.avgWaitSeries = wait;
        this.throughputSeries = thr;
        this.fairnessSeries = fair;
        this.containerPanel = panel;
    }
}
    private Simulator simulator;
    private TimeSeries cpuSeries;
    private TimeSeries avgWaitSeries;
    private TimeSeries throughputSeries;
    private TimeSeries fairnessSeries;
    private PolicyCharts[] chartsByOrdinal = new PolicyCharts[PolicyType.values().length];
    private PolicyType currentPolicy = null;
    private javax.swing.Timer refreshTimer;

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
        startAutoRefreshPerPolicy();
    }

    
    private PolicyCharts createChartsForPolicy(PolicyType policy) {
        TimeSeries cpuSeries = new TimeSeries("CPU - " + policy);
        TimeSeries avgWaitSeries = new TimeSeries("AvgWait - " + policy);
        TimeSeries throughputSeries = new TimeSeries("Throughput - " + policy);
        TimeSeries fairnessSeries = new TimeSeries("Fairness - " + policy);

        // CPU chart
        TimeSeriesCollection cpuDataset = new TimeSeriesCollection(cpuSeries);
        JFreeChart cpuChart = ChartFactory.createTimeSeriesChart(
            "CPU Utilization (" + policy + ")", "Tiempo", "CPU %", cpuDataset, false, true, false);
        XYPlot cpuPlot = cpuChart.getXYPlot();
        cpuPlot.getRangeAxis().setRange(0, 100);
        cpuPlot.getDomainAxis().setFixedAutoRange(300000.0);
        XYLineAndShapeRenderer cpuRenderer = new XYLineAndShapeRenderer();
        cpuRenderer.setSeriesPaint(0, Color.RED);
        cpuRenderer.setSeriesShapesVisible(0, false);
        cpuPlot.setRenderer(cpuRenderer);
        ChartPanel cpuChartPanel = new ChartPanel(cpuChart);

        // Avg Wait chart
        TimeSeriesCollection waitDataset = new TimeSeriesCollection(avgWaitSeries);
        JFreeChart waitChart = ChartFactory.createTimeSeriesChart(
            "Average Waiting Time (" + policy + ")", "Tiempo", "Ciclos", waitDataset, false, true, false);
        XYPlot waitPlot = waitChart.getXYPlot();
        waitPlot.getRangeAxis().setRange(0, 200);
        waitPlot.getDomainAxis().setFixedAutoRange(300000.0);
        XYLineAndShapeRenderer waitRenderer = new XYLineAndShapeRenderer();
        waitRenderer.setSeriesPaint(0, Color.BLUE);
        waitRenderer.setSeriesShapesVisible(0, false);
        waitPlot.setRenderer(waitRenderer);
        ChartPanel waitChartPanel = new ChartPanel(waitChart);

        // Throughput chart
        TimeSeriesCollection thrDataset = new TimeSeriesCollection(throughputSeries);
        JFreeChart thrChart = ChartFactory.createTimeSeriesChart(
            "Throughput (" + policy + ")", "Tiempo", "Procesos/s", thrDataset, false, true, false);
        XYPlot thrPlot = thrChart.getXYPlot();
        thrPlot.getRangeAxis().setRange(0, 0.1);
        thrPlot.getDomainAxis().setFixedAutoRange(300000.0);
        XYLineAndShapeRenderer thrRenderer = new XYLineAndShapeRenderer();
        thrRenderer.setSeriesPaint(0, Color.GREEN.darker());
        thrRenderer.setSeriesShapesVisible(0, false);
        thrPlot.setRenderer(thrRenderer);
        ChartPanel thrChartPanel = new ChartPanel(thrChart);

        // Fairness chart
        TimeSeriesCollection fairDataset = new TimeSeriesCollection(fairnessSeries);
        JFreeChart fairChart = ChartFactory.createTimeSeriesChart(
            "Fairness (" + policy + ")", "Tiempo", "Valor", fairDataset, false, true, false);
        XYPlot fairPlot = fairChart.getXYPlot();
        fairPlot.getRangeAxis().setRange(0, 100);
        fairPlot.getDomainAxis().setFixedAutoRange(300000.0);
        XYLineAndShapeRenderer fairRenderer = new XYLineAndShapeRenderer();
        fairRenderer.setSeriesPaint(0, Color.ORANGE);
        fairRenderer.setSeriesShapesVisible(0, false);
        fairPlot.setRenderer(fairRenderer);
        ChartPanel fairChartPanel = new ChartPanel(fairChart);

        javax.swing.JPanel panel = new javax.swing.JPanel(new GridLayout(2, 2, 4, 4));
        panel.add(cpuChartPanel);
        panel.add(waitChartPanel);
        panel.add(thrChartPanel);
        panel.add(fairChartPanel);

        return new PolicyCharts(cpuSeries, avgWaitSeries, throughputSeries, fairnessSeries, panel);
    }

    private void switchPolicyByOrdinal(PolicyType newPolicy) {
        if (newPolicy == null) return;
        if (newPolicy.equals(currentPolicy)) return;

        currentPolicy = newPolicy;
        int idx = newPolicy.ordinal();

        if (chartsByOrdinal[idx] == null) {
            chartsByOrdinal[idx] = createChartsForPolicy(newPolicy);
        }
        final PolicyCharts pc = chartsByOrdinal[idx];

        SwingUtilities.invokeLater(() -> {
            chartContainer.removeAll();
            chartContainer.setLayout(new BorderLayout());
            chartContainer.add(pc.containerPanel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
        });
    }

     private void startAutoRefreshPerPolicy() {
        if (refreshTimer != null && refreshTimer.isRunning()) return;

        refreshTimer = new javax.swing.Timer(1000, e -> {
            if (simulator == null) return;

            PolicyType policyNow = simulator.getOperatingSystem().getScheduler().getCurrentPolicy();
            if (policyNow == null) return;

            if (currentPolicy == null || !currentPolicy.equals(policyNow)) {
                switchPolicyByOrdinal(policyNow);
            }

            PolicyCharts active = chartsByOrdinal[policyNow.ordinal()];
            if (active != null) {
                double cpu = simulator.getCPUProductivePercentage();
                double avgWait = simulator.getAverageWaitingTime();
                double throughput = simulator.calculateThroughput();
                double fairness = simulator.getTotalFairness();

                Millisecond now = new Millisecond();
                try {
                    active.cpuSeries.addOrUpdate(now, cpu);
                    active.avgWaitSeries.addOrUpdate(now, avgWait);
                    active.throughputSeries.addOrUpdate(now, throughput);
                    active.fairnessSeries.addOrUpdate(now, fairness);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                SwingUtilities.invokeLater(() -> updateLabels(cpu, avgWait, throughput, fairness));
            }
        });

        refreshTimer.setInitialDelay(0);
        refreshTimer.start();
    }
    
   private void updateLabels(double cpu, double avgWait, double throughput, double fairness) {
        CpuUtilization.setText(String.format("Productividad del CPU: %.2f%%", cpu));
        AverageWaitTime.setText(String.format("Tiempo promedio de espera: %.2f ciclos", avgWait));
        Throughput.setText(String.format("Throughput: %.2f", throughput));
        Fairness.setText(String.format("Equidad: %.2f", fairness));
        try {
            long cycles = simulator.getOperatingSystem().getClock().getTotalCyclesElapsed();
            Clockcycles.setText("Ciclos Totales de Reloj: "+String.format("%d", cycles));
        } catch (Exception ex) {
            // ignore
        }
    }
public void stopRefreshing() {
        if (refreshTimer != null) refreshTimer.stop();
    }




           
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jColorChooser1 = new javax.swing.JColorChooser();
        jFrame1 = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        Clockcycles = new javax.swing.JLabel();
        chartContainer = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        Throughput = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        AverageWaitTime = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        CpuUtilization = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        Fairness = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(13, 84, 141));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Clockcycles.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Clockcycles.setForeground(new java.awt.Color(255, 255, 255));
        Clockcycles.setText("Ciclos Totales de Reloj");
        jPanel3.add(Clockcycles, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 610, 290, -1));

        chartContainer.setBackground(new java.awt.Color(0, 0, 70));

        javax.swing.GroupLayout chartContainerLayout = new javax.swing.GroupLayout(chartContainer);
        chartContainer.setLayout(chartContainerLayout);
        chartContainerLayout.setHorizontalGroup(
            chartContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );
        chartContainerLayout.setVerticalGroup(
            chartContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 570, Short.MAX_VALUE)
        );

        jPanel3.add(chartContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, 610, 570));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("<html>Métricas Generales<br> del Sistema</html>");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 350, -1));

        jPanel5.setBackground(new java.awt.Color(0, 0, 70));

        Throughput.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Throughput.setForeground(new java.awt.Color(255, 255, 255));
        Throughput.setText("Throughtput");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("(Procesos por Ciclo)");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Throughput, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(Throughput, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 360, -1));

        jPanel6.setBackground(new java.awt.Color(0, 0, 70));

        AverageWaitTime.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        AverageWaitTime.setForeground(new java.awt.Color(255, 255, 255));
        AverageWaitTime.setText("WaitTime");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(AverageWaitTime, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(AverageWaitTime, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 360, 90));

        jPanel7.setBackground(new java.awt.Color(0, 0, 70));

        CpuUtilization.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CpuUtilization.setForeground(new java.awt.Color(255, 255, 255));
        CpuUtilization.setText("CPU utilization");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CpuUtilization, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(CpuUtilization, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        jPanel3.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 360, 90));

        jPanel8.setBackground(new java.awt.Color(0, 0, 70));

        Fairness.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Fairness.setForeground(new java.awt.Color(255, 255, 255));
        Fairness.setText("Fairness");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(Fairness, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(Fairness, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 360, 90));

        jPanel9.setBackground(new java.awt.Color(0, 0, 70));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Medidas de Rendimiento Por Planificación");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 573, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 610, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AverageWaitTime;
    private javax.swing.JLabel Clockcycles;
    private javax.swing.JLabel CpuUtilization;
    private javax.swing.JLabel Fairness;
    private javax.swing.JLabel Throughput;
    private javax.swing.JPanel chartContainer;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    // End of variables declaration//GEN-END:variables
}
