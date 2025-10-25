package _07_GUI;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Danaz
 */
public class ConfigPanel extends javax.swing.JPanel {
  
    // Instancia de Preferences para persistir datos
    private static final Preferences prefs = Preferences.userNodeForPackage(ConfigPanel.class);
    
    // Claves para guardar los valores
    private static final String POLICY_KEY = "policyComboBox";
    private static final String CYCLES_KEY = "systemCyclesSpinner";

    public ConfigPanel() {
        initComponents();
        
        // Configurar el modelo del spinner si no lo hiciste en el diseñador
        systemCycles.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        
        // Cargar los valores guardados al inicializar el panel
        loadSavedValues();
        
         // Configuración spinners
        instructionsSpinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));  // Min 1, Max 1000, Step 1
        cyclesSpinner.setModel(new SpinnerNumberModel(1, 1,1, 1));  // Min 0, no max ESTO DEPENDE DEL INSTRUCTION
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

     private void loadSavedValues() {
        // Cargar el índice seleccionado del ComboBox
        int selectedIndex = prefs.getInt(POLICY_KEY, -1);  // -1 indica que no hay nada guardado
        if (selectedIndex == -1) {
            // Si no hay guardado, establecer "Priority" como por defecto
            policyComboBox.setSelectedItem("Priority");
        } else if (selectedIndex >= 0 && selectedIndex < policyComboBox.getItemCount()) {
            policyComboBox.setSelectedIndex(selectedIndex);
        }
        // Cargar el valor del Spinner (por defecto 1 si no hay guardado)
        int cyclesValue = prefs.getInt(CYCLES_KEY, 1);
        systemCycles.setValue(cyclesValue);
    }


// Método para guardar solo la política (llámalo desde el botón savePolicy)
    private void savePolicy() {
        prefs.putInt(POLICY_KEY, policyComboBox.getSelectedIndex());
    }
    // Método para guardar solo los ciclos (llámalo desde el botón saveSystemCycles)
    private void saveSystemCycles() {
        prefs.putInt(CYCLES_KEY, (Integer) systemCycles.getValue());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        planPolicy = new javax.swing.JPanel();
        policyComboBox = new javax.swing.JComboBox<>();
        savePolicy = new javax.swing.JButton();
        label3 = new java.awt.Label();
        watchCycles = new javax.swing.JPanel();
        saveSystemCycles = new javax.swing.JButton();
        systemCycles = new javax.swing.JSpinner();
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
        processToJson = new javax.swing.JScrollPane();
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

        policyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "Round Robin", "SPN", "SRT", "HRRN", "Priority" }));

        savePolicy.setText("Guardar Cambios");
        savePolicy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePolicyActionPerformed(evt);
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
                    .addComponent(savePolicy, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(savePolicy)
                .addGap(15, 15, 15))
        );

        jPanel2.add(planPolicy, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 240, 160));

        watchCycles.setBackground(new java.awt.Color(0, 0, 70));
        watchCycles.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        saveSystemCycles.setText("Guardar Cambios");
        saveSystemCycles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSystemCyclesActionPerformed(evt);
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
                    .addComponent(saveSystemCycles, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(watchCyclesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watchCyclesLayout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(16, 16, 16))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watchCyclesLayout.createSequentialGroup()
                            .addComponent(systemCycles, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(67, 67, 67)))))
        );
        watchCyclesLayout.setVerticalGroup(
            watchCyclesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watchCyclesLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(systemCycles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(saveSystemCycles)
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

        cpuBoundRadio.setForeground(new java.awt.Color(255, 255, 255));
        cpuBoundRadio.setText("CPU Bound");

        ioBoundRadio.setForeground(new java.awt.Color(255, 255, 255));
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
        jPanel2.add(processToJson, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 370, 240, 250));

        label8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        label8.setForeground(new java.awt.Color(255, 255, 255));
        label8.setText("Procesos cargados");
        jPanel2.add(label8, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 340, -1, -1));

        createJsonButton.setText("Crear JSON");
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
        // TODO add your handling code here:
    }//GEN-LAST:event_submitButtonActionPerformed

    private void savePolicyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePolicyActionPerformed
        savePolicy();
        javax.swing.JOptionPane.showMessageDialog(this, "Política guardada exitosamente.");
    }//GEN-LAST:event_savePolicyActionPerformed

    private void saveSystemCyclesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSystemCyclesActionPerformed
       saveSystemCycles();
        javax.swing.JOptionPane.showMessageDialog(this, "Ciclos del sistema guardados exitosamente.");
    }//GEN-LAST:event_saveSystemCyclesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton cpuBoundRadio;
    private javax.swing.JButton createJsonButton;
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
    private javax.swing.JScrollPane processToJson;
    private javax.swing.JButton savePolicy;
    private javax.swing.JButton saveSystemCycles;
    private javax.swing.JButton submitButton;
    private javax.swing.JSpinner systemCycles;
    private javax.swing.JPanel watchCycles;
    // End of variables declaration//GEN-END:variables
}
