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
        cpuPlot.getDomainAxis().setFixedAutoRange(180000.0);
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
        waitPlot.getDomainAxis().setFixedAutoRange(180000.0);
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
        thrPlot.getDomainAxis().setFixedAutoRange(180000.0);
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
        fairPlot.getDomainAxis().setFixedAutoRange(180000.0);
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
        CpuUtilization.setText(String.format("%.2f%%", cpu));
        AverageWaitTime.setText(String.format("Avg Response Time: %.2f ciclos", avgWait));
        Throughput.setText(String.format("Throughput: %.2f", throughput));
        Fairness.setText(String.format("Fairness: %.2f", fairness));
        try {
            long cycles = simulator.getOperatingSystem().getClock().getTotalCyclesElapsed();
            Clockcycles.setText(String.format("%d", cycles));
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

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Throughput = new javax.swing.JLabel();
        CpuUtilization = new javax.swing.JLabel();
        Fairness = new javax.swing.JLabel();
        AverageWaitTime = new javax.swing.JLabel();
        Clockcycles = new javax.swing.JLabel();
        chartContainer = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        jPanel3.setBackground(new java.awt.Color(13, 84, 141));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Tiempo de Respuesta:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, -1));

        jLabel6.setText("Equidad:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, -1));

        jLabel4.setText("Utilización del Procesador:");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, -1, -1));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Medidas de Rendimiento");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 320, 60));

        Throughput.setText("Thoroughtput");
        jPanel3.add(Throughput, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, 90, -1));

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

        jLabel1.setText("jLabel1");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 90, -1));

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
