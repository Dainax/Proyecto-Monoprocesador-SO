package _07_GUI;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Danaz
 */
public class SimulationPanel extends javax.swing.JPanel {

    public SimulationPanel() {
        initComponents();

        // Configuración spinners
        instructionsSpinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));  // Min 1, Max 1000, Step 1
        cyclesSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));  // Min 0, no max ESTO DEPENDE DEL INSTRUCTION
        ioTimeSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));  // Min 0, no max

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

        // Add listener for submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndCreateProcess();
            }
        });
    }

    private void validateAndCreateProcess() {
        // Get values
        String name = nameField.getText().trim();
        int instructions = (Integer) instructionsSpinner.getValue();
        boolean isCpuBound = cpuBoundRadio.isSelected();
        boolean isIoBound = ioBoundRadio.isSelected();
        int cycles = (Integer) cyclesSpinner.getValue();
        int ioTime = (Integer) ioTimeSpinner.getValue();

        // Validación de atributos vacios
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del proceso no puede ser vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isCpuBound && !isIoBound) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona CPU Bound o I/O Bound.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación instrucciones 1-1000
        if (instructions < 1 || instructions > 1000) {
            JOptionPane.showMessageDialog(this, "Número de instrucción debe estar entre 1 y 1000.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Otra validación de mutua exclusión de tipo de proceso
        if (isCpuBound && isIoBound) {
            JOptionPane.showMessageDialog(this, "Proceso no puede ser CPU Bound y I/O Bound a la vez.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //Validaciones extras
        if (isCpuBound) {
            if (cycles != 0) {
                JOptionPane.showMessageDialog(this, "Cycles for interruption should not be set for CPU Bound processes.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ioTime != 0) {
                JOptionPane.showMessageDialog(this, "I/O time should not be set for CPU Bound processes.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Pasó las validaciones, entonces llama al SO
        try {
            //SO.createProcess(name, instructions, isCpuBound, cycles, ioTime);  // Adjust to your actual method
            JOptionPane.showMessageDialog(this, "Proceso creado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Limpieza de los inputs
            resetFields();

        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Error creando el Proceso: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        nameField.setText("");
        instructionsSpinner.setValue(1);
        cyclesSpinner.setValue(1);
        ioTimeSpinner.setValue(1);
        cpuBoundRadio.setSelected(false);
        ioBoundRadio.setSelected(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        label2 = new java.awt.Label();
        label4 = new java.awt.Label();
        cpuBoundRadio = new javax.swing.JRadioButton();
        ioBoundRadio = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cyclesSpinner = new javax.swing.JSpinner();
        ioTimeSpinner = new javax.swing.JSpinner();
        instructionsSpinner = new javax.swing.JSpinner();
        nameField = new javax.swing.JTextField();
        submitButton = new javax.swing.JButton();
        label5 = new java.awt.Label();
        jLabel19 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        typeProcessRunning = new javax.swing.JLabel();
        modeProcessRunning = new javax.swing.JLabel();
        marProcessRunning = new javax.swing.JLabel();
        pcProcessRunning = new javax.swing.JLabel();
        idProcessRunning = new javax.swing.JLabel();
        nameProcessRunning = new javax.swing.JLabel();
        CPUphoto = new javax.swing.JLabel();
        cycleWatchTime = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        readySList = new javax.swing.JScrollPane();
        newList = new javax.swing.JScrollPane();
        blokedSList = new javax.swing.JScrollPane();
        readyList = new javax.swing.JScrollPane();
        finishedList = new javax.swing.JScrollPane();
        blockedList = new javax.swing.JScrollPane();
        jLabel13 = new javax.swing.JLabel();
        plannerLog = new javax.swing.JLabel();
        jToggleButton2 = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(13, 84, 141));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 70));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label2.setForeground(new java.awt.Color(255, 255, 255));
        label2.setText("Tipo de Proceso:");

        label4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label4.setForeground(new java.awt.Color(255, 255, 255));
        label4.setText("N° de Intrucciones:");

        cpuBoundRadio.setForeground(new java.awt.Color(255, 255, 255));
        cpuBoundRadio.setText("CPU Bound");

        ioBoundRadio.setForeground(new java.awt.Color(255, 255, 255));
        ioBoundRadio.setText("I/O Bound");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("<html>Cantidad de ciclos<br> para la interrupción I/O:</html>");
        jLabel2.setToolTipText("");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("<html>Cantidad de ciclos de duración<br>de la interrupción I/O:</html>  ");
        jLabel3.setToolTipText("");

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });

        submitButton.setText("Crear Nuevo Proceso");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        label5.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label5.setForeground(new java.awt.Color(255, 255, 255));
        label5.setText("Nombre del Proceso:");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Nuevo Proceso");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(instructionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(cpuBoundRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(ioBoundRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(ioTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nameField))))
                        .addGap(0, 55, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instructionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cpuBoundRadio)
                    .addComponent(ioBoundRadio))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ioTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(submitButton)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, 440, 280));

        jPanel3.setOpaque(false);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("PC:");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Proceso:");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("CPU");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("ID:");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("MAR:");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Modo:");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Tipo:");

        typeProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        typeProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        typeProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        typeProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        typeProcessRunning.setText("N/A");

        modeProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        modeProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        modeProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        modeProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        modeProcessRunning.setText("N/A");

        marProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        marProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        marProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        marProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        marProcessRunning.setText("N/A");

        pcProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        pcProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        pcProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        pcProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pcProcessRunning.setText("N/A");

        idProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        idProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        idProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        idProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        idProcessRunning.setText("N/A");

        nameProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        nameProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        nameProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        nameProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameProcessRunning.setText("N/A");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pcProcessRunning)
                            .addComponent(modeProcessRunning)
                            .addComponent(nameProcessRunning)
                            .addComponent(typeProcessRunning)
                            .addComponent(marProcessRunning)
                            .addComponent(idProcessRunning)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jLabel7)))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel9)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(nameProcessRunning)
                        .addGap(10, 10, 10)
                        .addComponent(idProcessRunning)
                        .addGap(10, 10, 10)
                        .addComponent(pcProcessRunning)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(marProcessRunning)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(modeProcessRunning)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeProcessRunning)))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 240, 280));

        CPUphoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/_08_SourcesGUI/cpu_texture.png"))); // NOI18N
        jPanel1.add(CPUphoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 240, 280));

        cycleWatchTime.setBackground(new java.awt.Color(255, 255, 255));
        cycleWatchTime.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        cycleWatchTime.setForeground(new java.awt.Color(255, 255, 255));
        cycleWatchTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cycleWatchTime.setText("0");
        jPanel1.add(cycleWatchTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 540, 240, -1));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Ciclo de Reloj Global");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 500, 240, -1));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Cola Ready S.");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 330, -1, -1));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Cola Ready");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, -1, -1));

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Cola Finished ");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 330, -1, -1));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Cola Blocked");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, -1, -1));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Cola Blocked S.");
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 330, -1, -1));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Cola New");
        jPanel1.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 330, -1, -1));
        jPanel1.add(readySList, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 360, 118, 288));
        jPanel1.add(newList, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 360, 120, 290));
        jPanel1.add(blokedSList, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 360, 120, 290));
        jPanel1.add(readyList, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, 150, 250));
        jPanel1.add(finishedList, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 360, 120, 290));
        jPanel1.add(blockedList, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 120, 290));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Log del Planificador");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 580, 240, -1));

        plannerLog.setBackground(new java.awt.Color(255, 255, 255));
        plannerLog.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        plannerLog.setForeground(new java.awt.Color(255, 255, 255));
        plannerLog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plannerLog.setText("...");
        jPanel1.add(plannerLog, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 610, 230, 30));

        jToggleButton2.setBackground(new java.awt.Color(0, 0, 70));
        jToggleButton2.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jToggleButton2.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButton2.setText("Iniciar Simulación");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jToggleButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 320, 240, 40));

        jButton1.setBackground(new java.awt.Color(0, 0, 70));
        jButton1.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Precargar una simulación");
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 450, 240, 30));

        jButton2.setBackground(new java.awt.Color(0, 0, 70));
        jButton2.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Reiniciar Simulación");
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 370, 240, 30));

        jButton3.setBackground(new java.awt.Color(0, 0, 70));
        jButton3.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Generación automática 20 procesos");
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 410, 240, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_submitButtonActionPerformed

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CPUphoto;
    private javax.swing.JScrollPane blockedList;
    private javax.swing.JScrollPane blokedSList;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cpuBoundRadio;
    private javax.swing.JLabel cycleWatchTime;
    private javax.swing.JSpinner cyclesSpinner;
    private javax.swing.JScrollPane finishedList;
    private javax.swing.JLabel idProcessRunning;
    private javax.swing.JSpinner instructionsSpinner;
    private javax.swing.JRadioButton ioBoundRadio;
    private javax.swing.JSpinner ioTimeSpinner;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToggleButton jToggleButton2;
    private java.awt.Label label2;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private javax.swing.JLabel marProcessRunning;
    private javax.swing.JLabel modeProcessRunning;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameProcessRunning;
    private javax.swing.JScrollPane newList;
    private javax.swing.JLabel pcProcessRunning;
    private javax.swing.JLabel plannerLog;
    private javax.swing.JScrollPane readyList;
    private javax.swing.JScrollPane readySList;
    private javax.swing.JButton submitButton;
    private javax.swing.JLabel typeProcessRunning;
    // End of variables declaration//GEN-END:variables
}
