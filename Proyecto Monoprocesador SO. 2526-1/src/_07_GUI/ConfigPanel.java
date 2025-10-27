package _07_GUI;

import _01_ApplicationPackage.Simulator;
import _02_DataStructures.SimpleList;
import _02_DataStructures.SimpleNode;
import _02_DataStructures.SimpleProcess;
import _04_OperatingSystem.OperatingSystem;
import _04_OperatingSystem.PolicyType;
import _04_OperatingSystem.ProcessType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Danaz
 */
public class ConfigPanel extends javax.swing.JPanel {

    private Simulator simulator;
    private SimpleList<SimpleProcess> processList = new SimpleList<>();

    public ConfigPanel() {
        initComponents();

        processToJsonPane.setLayout(new BoxLayout(processToJsonPane, BoxLayout.Y_AXIS));
        processToJsonScroll.setViewportView(processToJsonPane);

        // Configurar del spinner CONFIGURACION GENERAL
        cycleSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        // Configurar del spinner CONFIGURACION JSON
        cyclesToJson.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        // Configuración spinners
        instructionsSpinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));  // Min 1, Max 1000, Step 1
        cyclesSpinner.setModel(new SpinnerNumberModel(1, 1, 1, 1));  // Min 1, Max 1, Step 1
        ioTimeSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));  // Min 1, Max 1000, Step 1

        // Sincroniza dinámicamente el máximo de cyclesSpinner con instructionsSpinner
        instructionsSpinner.addChangeListener(e -> {
            int instructionsValue = (Integer) instructionsSpinner.getValue();
            SpinnerNumberModel cyclesModel = (SpinnerNumberModel) cyclesSpinner.getModel();

            // Actualiza el máximo permitido
            cyclesModel.setMaximum(instructionsValue);

            // Si el valor actual excede el nuevo máximo, ajústalo
            if ((Integer) cyclesSpinner.getValue() > instructionsValue) {
                cyclesSpinner.setValue(instructionsValue);
            }
        });

        // Grupo de botones de radio (Mutuamente Excluyente)
        ButtonGroup boundGroup = new ButtonGroup();
        boundGroup.add(cpuBoundRadio);
        boundGroup.add(ioBoundRadio);

        // Initially, disable cycles spinner (assuming CPU bound by default or none selected)
        cyclesSpinner.setEnabled(false);
        ioTimeSpinner.setEnabled(false);

        // Add listeners for radio buttons to enable/disable cycles spinner
        cpuBoundRadio.addActionListener(e -> {
            cyclesSpinner.setEnabled(false);
            ioTimeSpinner.setEnabled(false);
        });

        ioBoundRadio.addActionListener(e -> {
            cyclesSpinner.setEnabled(true);
            ioTimeSpinner.setEnabled(true);
        });

    }

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    private void resetFields() {
        nameField.setText("");
        instructionsSpinner.setValue(1);
        cyclesSpinner.setValue(1);
        ioTimeSpinner.setValue(1);
        cpuBoundRadio.setSelected(false);
        ioBoundRadio.setSelected(false);
    }

    public void refreshConfig() {
        if (simulator != null) {
            OperatingSystem os = simulator.getOperatingSystem();

            // Actualiza política
            PolicyType currentPolicy = os.getScheduler().getCurrentPolicy();
            policyComboBox.setSelectedItem(currentPolicy.name());

            // 🔥 Convertir ms → s antes de mostrar
            long currentCycleMillis = os.getClock().getClockDuration();
            int currentCycleSeconds = (int) (currentCycleMillis / 1000);
            cycleSpinner.setValue(currentCycleSeconds);
        }
    }

    private void savePolicy(PolicyType newpolicy) {

        if (simulator != null) {
            simulator.getOperatingSystem().getScheduler().setCurrentPolicy(newpolicy);

            // 🔥 Forzar actualización del UI
            SwingUtilities.invokeLater(() -> {
                simulator.getSimulationPanel().updateQueues(simulator.getOperatingSystem());
                simulator.getSimulationPanel().updateCPU(simulator.getOperatingSystem().getCpu());
            });
        }
    }

    private void saveSystemCycles() {
        int seconds = (Integer) cycleSpinner.getValue();
        long milliseconds = seconds * 1000L;

        if (simulator != null) {
            // Actualiza la duración del ciclo del reloj del sistema operativo
            simulator.getOperatingSystem().getClock().setClockDuration(milliseconds);

            // 🔥 Refrescar también UI (colas y CPU) por si hay dependencias visuales
            SwingUtilities.invokeLater(() -> {
                SimulationPanel panel = simulator.getSimulationPanel();
                if (panel != null) {
                    panel.updateQueues(simulator.getOperatingSystem());
                    panel.updateCPU(simulator.getOperatingSystem().getCpu());
                }
            });
        }
    }

    private void validateAndCreateSimpleProcess() {
        String name = nameField.getText().trim();
        int instructions = (Integer) instructionsSpinner.getValue();
        boolean isCpuBound = cpuBoundRadio.isSelected();
        boolean isIoBound = ioBoundRadio.isSelected();
        ProcessType type;
        int cycles;
        int ioTime;

        // 🔹 Validaciones
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del proceso no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isCpuBound && !isIoBound) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar si el proceso es CPU Bound o I/O Bound.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (instructions < 1 || instructions > 1000) {
            JOptionPane.showMessageDialog(this, "El número de instrucciones debe estar entre 1 y 1000.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isCpuBound) {
            type = ProcessType.CPU_BOUND;
            cycles = -1;
            ioTime = -1;
        } else {
            type = ProcessType.IO_BOUND;
            cycles = (Integer) cyclesSpinner.getValue();
            ioTime = (Integer) ioTimeSpinner.getValue();

            if (cycles < 1 || ioTime < 1) {
                JOptionPane.showMessageDialog(this, "Los valores de ciclos e I/O deben ser mayores que 0 para procesos I/O Bound.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 🔹 Crear proceso y agregarlo a la lista
        SimpleProcess process = new SimpleProcess(name, instructions, type, cycles, ioTime);
        processList.insertLast(process);

        // 🔹 Mostrar visualmente el proceso en el panel
        SimplePCBPanel pcbPanel = new SimplePCBPanel(process.getName(), process.getType().toString(), process.getInstructions());
        processToJsonPane.add(pcbPanel);

        processToJsonPane.revalidate();
        processToJsonPane.repaint();

        // 🔹 Limpiar campos
        resetFields();
        processToJsonPane.revalidate();
        processToJsonPane.repaint();

        JOptionPane.showMessageDialog(this, "Proceso agregado correctamente a la lista.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveProcessesToJson() {
    try {
        // 🔹 Obtener duración del ciclo desde el spinner
        int seconds = (Integer) cyclesToJson.getValue();
        long clockDurationMs = seconds * 1000L;

        // 🔹 Crear el archivo JSON
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar configuración de simulación");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("{\n");
                writer.write("  \"clockDurationMs\": " + clockDurationMs + ",\n");
                writer.write("  \"processes\": [\n");

                SimpleNode<SimpleProcess> node = processList.GetpFirst();
                while (node != null) {
                    SimpleProcess process = node.GetData();
                    writer.write("    {\n");
                    writer.write("      \"name\": \"" + process.getName() + "\",\n");
                    writer.write("      \"instructions\": " + process.getInstructions() + ",\n");
                    writer.write("      \"type\": \"" + process.getType() + "\",\n");
                    writer.write("      \"cyclesForIO\": " + process.getCyclesForIO() + ",\n");
                    writer.write("      \"ioDuration\": " + process.getIoDuration() + "\n");
                    writer.write("    }");
                    node = node.GetNxt();
                    if (node != null) writer.write(",");
                    writer.write("\n");
                }

                writer.write("  ]\n");
                writer.write("}");
            }

            JOptionPane.showMessageDialog(this, "Configuración guardada exitosamente en JSON.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // 🔹 Limpiar panel de procesos
            cyclesToJson.setValue(1);
            processToJsonPane.removeAll();
            processToJsonPane.revalidate();
            processToJsonPane.repaint();

            // 🔹 Reiniciar la lista de procesos en memoria
            processList = new SimpleList<>();

        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al guardar la configuración: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        planPolicy = new javax.swing.JPanel();
        policyComboBox = new javax.swing.JComboBox<>();
        savePolicyButton = new javax.swing.JButton();
        label3 = new java.awt.Label();
        watchCycles = new javax.swing.JPanel();
        saveCycleButton = new javax.swing.JButton();
        cycleSpinner = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        label2 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        cyclesSpinner = new javax.swing.JSpinner();
        cpuBoundRadio = new javax.swing.JRadioButton();
        ioBoundRadio = new javax.swing.JRadioButton();
        ioTimeSpinner = new javax.swing.JSpinner();
        instructionsSpinner = new javax.swing.JSpinner();
        submitButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        label7 = new java.awt.Label();
        cyclesToJson = new javax.swing.JSpinner();
        processToJsonScroll = new javax.swing.JScrollPane();
        processToJsonPane = new javax.swing.JPanel();
        label8 = new java.awt.Label();
        createJsonButton = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(13, 84, 141));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Configuración General de la Simulación");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        planPolicy.setBackground(new java.awt.Color(0, 0, 70));
        planPolicy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        policyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Priority", "FIFO", "ROUND ROBIN", "SPN", "SRT", "HRRN" }));

        savePolicyButton.setText("Guardar Cambios");
        savePolicyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePolicyButtonActionPerformed(evt);
            }
        });

        label3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        label3.setForeground(new java.awt.Color(255, 255, 255));
        label3.setText("Política de Planificación");

        javax.swing.GroupLayout planPolicyLayout = new javax.swing.GroupLayout(planPolicy);
        planPolicy.setLayout(planPolicyLayout);
        planPolicyLayout.setHorizontalGroup(
            planPolicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(planPolicyLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(planPolicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(savePolicyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(planPolicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(policyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        planPolicyLayout.setVerticalGroup(
            planPolicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, planPolicyLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(policyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(savePolicyButton)
                .addGap(15, 15, 15))
        );

        jPanel2.add(planPolicy, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 240, 160));

        watchCycles.setBackground(new java.awt.Color(0, 0, 70));
        watchCycles.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        saveCycleButton.setText("Guardar Cambios");
        saveCycleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCycleButtonActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Duración de Ciclos de Reloj en Segundos");

        javax.swing.GroupLayout watchCyclesLayout = new javax.swing.GroupLayout(watchCycles);
        watchCycles.setLayout(watchCyclesLayout);
        watchCyclesLayout.setHorizontalGroup(
            watchCyclesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watchCyclesLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(watchCyclesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveCycleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(watchCyclesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watchCyclesLayout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(16, 16, 16))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watchCyclesLayout.createSequentialGroup()
                            .addComponent(cycleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(67, 67, 67)))))
        );
        watchCyclesLayout.setVerticalGroup(
            watchCyclesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watchCyclesLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(cycleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(saveCycleButton)
                .addGap(19, 19, 19))
        );

        jPanel2.add(watchCycles, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 90, 320, 160));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Creación de JSON - Simulación Precargada");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, -1, -1));

        jPanel3.setBackground(new java.awt.Color(0, 0, 70));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label2.setForeground(new java.awt.Color(255, 255, 255));
        label2.setText("Tipo de Proceso:");

        label4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label4.setForeground(new java.awt.Color(255, 255, 255));
        label4.setText("N° de Intrucciones:");

        label5.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label5.setForeground(new java.awt.Color(255, 255, 255));
        label5.setText("Nombre del Proceso:");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("<html>Cantidad de ciclos<br> para la interrupción I/O:</html>");
        jLabel2.setToolTipText("");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("<html>Cantidad de ciclos de duración<br>de la interrupción I/O:</html>  ");
        jLabel3.setToolTipText("");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Nuevo Proceso para JSON");

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });

        cpuBoundRadio.setForeground(new java.awt.Color(0, 0, 0));
        cpuBoundRadio.setText("CPU Bound");

        ioBoundRadio.setForeground(new java.awt.Color(0, 0, 0));
        ioBoundRadio.setText("I/O Bound");

        submitButton.setText("Crear Nuevo Proceso");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(instructionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(cpuBoundRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(ioBoundRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(ioTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nameField))))
                        .addGap(0, 55, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instructionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cpuBoundRadio)
                    .addComponent(ioBoundRadio))
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ioTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(submitButton)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 440, 280));

        jPanel7.setBackground(new java.awt.Color(0, 0, 70));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        label7.setForeground(new java.awt.Color(255, 255, 255));
        label7.setText("Duración de Ciclos de Reloj");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 24, Short.MAX_VALUE)
                .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cyclesToJson, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cyclesToJson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 340, 240, 100));

        javax.swing.GroupLayout processToJsonPaneLayout = new javax.swing.GroupLayout(processToJsonPane);
        processToJsonPane.setLayout(processToJsonPaneLayout);
        processToJsonPaneLayout.setHorizontalGroup(
            processToJsonPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 238, Short.MAX_VALUE)
        );
        processToJsonPaneLayout.setVerticalGroup(
            processToJsonPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 248, Short.MAX_VALUE)
        );

        processToJsonScroll.setViewportView(processToJsonPane);

        jPanel2.add(processToJsonScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 370, 240, 250));

        label8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        label8.setForeground(new java.awt.Color(255, 255, 255));
        label8.setText("Procesos cargados");
        jPanel2.add(label8, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 340, -1, -1));

        createJsonButton.setText("Crear JSON");
        createJsonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createJsonButtonActionPerformed(evt);
            }
        });
        jPanel2.add(createJsonButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 460, 240, 40));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
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

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFieldActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        validateAndCreateSimpleProcess();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void savePolicyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePolicyButtonActionPerformed
            if (simulator == null) {
                JOptionPane.showMessageDialog(this, "No hay simulación activa.");
                return;
            }

            String selected = (String) policyComboBox.getSelectedItem();
            PolicyType newPolicy = switch (selected.toUpperCase()) {
                case "FIFO" ->
                    PolicyType.FIFO;
                case "ROUND ROBIN", "RR" ->
                    PolicyType.ROUND_ROBIN;
                case "SPN" ->
                    PolicyType.SPN;
                case "SRT" ->
                    PolicyType.SRT;
                case "HRRN" ->
                    PolicyType.HRRN;
                case "PRIORITY" ->
                    PolicyType.Priority;
                default ->
                    PolicyType.FIFO;
            };
             System.out.println(newPolicy);
            savePolicy(newPolicy);
            JOptionPane.showMessageDialog(this,
                    "Política de planificación actualizada a: " + newPolicy,
                    "Configuración guardada",
                    JOptionPane.INFORMATION_MESSAGE);
 
    }//GEN-LAST:event_savePolicyButtonActionPerformed

    private void saveCycleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCycleButtonActionPerformed
        
            if (simulator == null) {
                JOptionPane.showMessageDialog(this, "No hay simulación activa.");
                return;
            }

            long newDuration = ((Number) cycleSpinner.getValue()).longValue();
            if (newDuration <= 0) {
                JOptionPane.showMessageDialog(this, "La duración debe ser mayor a 0.");
                return;
            }

            simulator.getOperatingSystem().getClock().setClockDuration(newDuration);

            JOptionPane.showMessageDialog(this,
                    "Duración de ciclo actualizada a " + newDuration + " segundos",
                    "Configuración guardada",
                    JOptionPane.INFORMATION_MESSAGE);
       

        saveSystemCycles();
        javax.swing.JOptionPane.showMessageDialog(this, "Ciclos del sistema guardados exitosamente.");
    }//GEN-LAST:event_saveCycleButtonActionPerformed

    private void createJsonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createJsonButtonActionPerformed
        saveProcessesToJson();
    }//GEN-LAST:event_createJsonButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton cpuBoundRadio;
    private javax.swing.JButton createJsonButton;
    private javax.swing.JSpinner cycleSpinner;
    private javax.swing.JSpinner cyclesSpinner;
    private javax.swing.JSpinner cyclesToJson;
    private javax.swing.JSpinner instructionsSpinner;
    private javax.swing.JRadioButton ioBoundRadio;
    private javax.swing.JSpinner ioTimeSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label7;
    private java.awt.Label label8;
    private javax.swing.JTextField nameField;
    private javax.swing.JPanel planPolicy;
    private javax.swing.JComboBox<String> policyComboBox;
    private javax.swing.JPanel processToJsonPane;
    private javax.swing.JScrollPane processToJsonScroll;
    private javax.swing.JButton saveCycleButton;
    private javax.swing.JButton savePolicyButton;
    private javax.swing.JButton submitButton;
    private javax.swing.JPanel watchCycles;
    // End of variables declaration//GEN-END:variables
}
