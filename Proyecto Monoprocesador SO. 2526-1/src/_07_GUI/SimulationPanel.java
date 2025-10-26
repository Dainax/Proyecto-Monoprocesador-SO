package _07_GUI;

import _01_ApplicationPackage.Simulator;
import _02_DataStructures.SimpleList;
import _02_DataStructures.SimpleNode;
import _02_DataStructures.SimpleProcess;
import _03_LowLevelAbstractions.CPU;
import _04_OperatingSystem.OperatingSystem;
import _04_OperatingSystem.Process1;
import _04_OperatingSystem.ProcessType;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Danaz
 */
public class SimulationPanel extends javax.swing.JPanel {

    private Simulator simulator;
    private javax.swing.Timer clockTimer;

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    public SimulationPanel() {
        initComponents();

        uniformScrollPaneSizes();
        ///////////////INPUTS DE CREAR PROCESOS///////////////////////////////////////////////    
        // Configuración spinners
        instructionsSpinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));  // Min 1, Max 1000, Step 1
        cyclesSpinner.setModel(new SpinnerNumberModel(1, 1, 1, 1));  // Min 0, no max ESTO DEPENDE DEL INSTRUCTION
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

        ///////////////////////////////////////////////////////////////////////

        //TextArea del planner Log
        Color bg =new Color(13,84,141);
        plannerLog.setEditable(false);
        plannerLog.setOpaque(true);
        plannerLog.setLineWrap(true);
        plannerLog.setWrapStyleWord(true);
        plannerLog.setBorder(null); 
        plannerLog.setBackground(bg);
        jScrollPane1.setOpaque(false);             // el scrollpane por sí mismo no necesita pintar
        jScrollPane1.getViewport().setOpaque(true);
        jScrollPane1.getViewport().setBackground(bg);
        jScrollPane1.repaint();
        plannerLog.repaint();


        // === CONFIGURAR PANELES INTERNOS PARA CADA SCROLL ===
        newPanel = new JPanel();
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        scrollNew.setViewportView(newPanel);

        readyPanel = new JPanel();
        readyPanel.setLayout(new BoxLayout(readyPanel, BoxLayout.Y_AXIS));
        scrollReady.setViewportView(readyPanel);

        blockedPanel = new JPanel();
        blockedPanel.setLayout(new BoxLayout(blockedPanel, BoxLayout.Y_AXIS));
        scrollBlocked.setViewportView(blockedPanel);

        suspendedReadyPanel = new JPanel();
        suspendedReadyPanel.setLayout(new BoxLayout(suspendedReadyPanel, BoxLayout.Y_AXIS));
        scrollReadyS.setViewportView(suspendedReadyPanel);

        suspendedBlockedPanel = new JPanel();
        suspendedBlockedPanel.setLayout(new BoxLayout(suspendedBlockedPanel, BoxLayout.Y_AXIS));
        scrollBlockedS.setViewportView(suspendedBlockedPanel);

        terminatedPanel = new JPanel();
        terminatedPanel.setLayout(new BoxLayout(terminatedPanel, BoxLayout.Y_AXIS));
        scrollTerminated.setViewportView(terminatedPanel);
    }

    public void resetClockUI() {
        if (clockTimer != null) {
            clockTimer.stop();
            cycleWatchTime.setText("Ciclo: 0");
        }
    }

    //Validación de Datos para la creación de un proceso
    private void validateAndCreateProcess() {
        // Obtener valores de los campos
        String name = nameField.getText().trim();
        int instructions = (Integer) instructionsSpinner.getValue();
        boolean isCpuBound = cpuBoundRadio.isSelected();
        boolean isIoBound = ioBoundRadio.isSelected();
        ProcessType typeBound;
        int cycles;
        int ioTime;

        // 🔹 Validar nombre
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del proceso no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 🔹 Validar selección de tipo
        if (!isCpuBound && !isIoBound) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar si el proceso es CPU Bound o I/O Bound.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 🔹 Validar rango de instrucciones
        if (instructions < 1 || instructions > 1000) {
            JOptionPane.showMessageDialog(this, "El número de instrucciones debe estar entre 1 y 1000.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 🔹 Asignar tipo y atributos según selección
        if (isCpuBound) {
            typeBound = ProcessType.CPU_BOUND;
            cycles = -1;  // ignorar spinner
            ioTime = -1;  // ignorar spinner
        } else { // IO Bound
            typeBound = ProcessType.IO_BOUND;
            cycles = (Integer) cyclesSpinner.getValue();
            ioTime = (Integer) ioTimeSpinner.getValue();

            // Validar que los valores de IO tengan sentido
            if (cycles < 1 || ioTime < 1) {
                JOptionPane.showMessageDialog(this, "Los valores de ciclos e I/O deben ser mayores que 0 para procesos I/O Bound.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 🔹 Crear el proceso y enviarlo al simulador
        try {
            simulator.createProcess(name, instructions, typeBound, cycles, ioTime);
            JOptionPane.showMessageDialog(this, "Proceso creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar los campos
            resetFields();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear el proceso: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /////// Funciones para mostrar en Pantalla///////////////
    
    //Aca van los otros datos de la cpu
    public void updateCPU(CPU cpu) {
        if (cpu == null) {
            return;
        }
        // Ejemplo: ajusta los nombres a tus labels
        try {
            if (cpu.getCurrentProcess() != null) {
                nameProcessRunning.setText(cpu.getCurrentProcess().getPName());
                typeProcessRunning.setText(cpu.getCurrentProcess().getType().toString());
                modeProcessRunning.setText(simulator.getSo().getCpu().getCurrentMode());
                marProcessRunning.setText(Integer.toString(cpu.getCurrentProcess().getMAR()));
                pcProcessRunning.setText(Integer.toString(cpu.getCurrentProcess().getPC()));
                idProcessRunning.setText(Integer.toString(cpu.getCurrentProcess().getPID()));
                plannerLog.setText(simulator.getSo().getScheduler().getEventLog());

            } else {
                nameProcessRunning.setText("SO");
                typeProcessRunning.setText("...");
                modeProcessRunning.setText(simulator.getSo().getCpu().getCurrentMode());
                marProcessRunning.setText("...");
                pcProcessRunning.setText("...");
                idProcessRunning.setText("...");
                plannerLog.setText(simulator.getSo().getScheduler().getEventLog());
            }

        } catch (Exception ignored) {
        }
    }

    //Actualiza las colas
    public void updateQueues(OperatingSystem so) {
        if (so == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            // Limpia todos los paneles
            newPanel.removeAll();
            readyPanel.removeAll();
            blockedPanel.removeAll();
            suspendedReadyPanel.removeAll();
            suspendedBlockedPanel.removeAll();
            terminatedPanel.removeAll();

            // 🔹 Nuevos
            SimpleList<Process1> list = so.getDma().getNewProcesses();
            SimpleNode<Process1> node = (list == null) ? null : list.GetpFirst();
            while (node != null) {
                PCBPanel pcb = new PCBPanel(node.GetData());
                newPanel.add(pcb);
                node = node.GetNxt();
            }

            // 🔹 Listos
            list = so.getReadyQueue();
            node = (list == null) ? null : list.GetpFirst();
            while (node != null) {
                PCBPanel pcb = new PCBPanel(node.GetData());
                readyPanel.add(pcb);
                node = node.GetNxt();
            }

            // 🔹 Bloqueados
            list = so.getBlockedQueue();
            node = (list == null) ? null : list.GetpFirst();
            while (node != null) {
                PCBPanel pcb = new PCBPanel(node.GetData());
                blockedPanel.add(pcb);
                node = node.GetNxt();
            }

            // 🔹 Suspendidos Listos
            list = so.getDma().getReadySuspendedProcesses();
            node = (list == null) ? null : list.GetpFirst();
            while (node != null) {
                PCBPanel pcb = new PCBPanel(node.GetData());
                suspendedReadyPanel.add(pcb);
                node = node.GetNxt();
            }

            // 🔹 Suspendidos Bloqueados
            list = so.getDma().getBlockedSuspendedProcesses();
            node = (list == null) ? null : list.GetpFirst();
            while (node != null) {
                PCBPanel pcb = new PCBPanel(node.GetData());
                suspendedBlockedPanel.add(pcb);
                node = node.GetNxt();
            }

            // 🔹 Terminados
            list = so.getTerminatedQueue();
            node = (list == null) ? null : list.GetpFirst();
            while (node != null) {
                PCBPanel pcb = new PCBPanel(node.GetData());
                terminatedPanel.add(pcb);
                node = node.GetNxt();
            }

            // 🔹 Refrescar todo
            newPanel.revalidate();
            newPanel.repaint();
            readyPanel.revalidate();
            readyPanel.repaint();
            blockedPanel.revalidate();
            blockedPanel.repaint();
            suspendedReadyPanel.revalidate();
            suspendedReadyPanel.repaint();
            suspendedBlockedPanel.revalidate();
            suspendedBlockedPanel.repaint();
            terminatedPanel.revalidate();
            terminatedPanel.repaint();

            this.revalidate();
            this.repaint();
        });
    }

    //Reinicia todo en pantalla
    // Reinicia todo en pantalla
public void resetView() {
    SwingUtilities.invokeLater(() -> {
        cycleWatchTime.setText("0");
        nameProcessRunning.setText("Idle");

        // 🔹 Limpia los paneles (NO los scrolls)
        newPanel.removeAll();
        readyPanel.removeAll();
        blockedPanel.removeAll();
        suspendedReadyPanel.removeAll();
        suspendedBlockedPanel.removeAll();
        terminatedPanel.removeAll();

        // 🔹 Refresca visualmente
        newPanel.revalidate();
        newPanel.repaint();
        readyPanel.revalidate();
        readyPanel.repaint();
        blockedPanel.revalidate();
        blockedPanel.repaint();
        suspendedReadyPanel.revalidate();
        suspendedReadyPanel.repaint();
        suspendedBlockedPanel.revalidate();
        suspendedBlockedPanel.repaint();
        terminatedPanel.revalidate();
        terminatedPanel.repaint();
    });
}


    private void resetFields() {
        nameField.setText("");
        instructionsSpinner.setValue(1);
        cyclesSpinner.setValue(1);
        ioTimeSpinner.setValue(1);
        cpuBoundRadio.setSelected(false);
        ioBoundRadio.setSelected(false);
    }

    public void initClockTimer(Simulator simulator) {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }

        clockTimer = new javax.swing.Timer(200, e -> {
            long cycles = simulator.getOperatingSystem().getClock().getTotalCyclesElapsed();
            cycleWatchTime.setText(String.valueOf(cycles));
        });
        clockTimer.start();
    }

// Pausar el temporizador visual del reloj
    public void pauseClockUI() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
    }

// Reanudar el temporizador visual del reloj
    public void resumeClockUI() {
        if (clockTimer != null && !clockTimer.isRunning()) {
            clockTimer.start();
        }
    }

    private void loadFromJson() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar configuración desde JSON");

        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {

            long clockDuration = 1000;
            SimpleList<SimpleProcess> processList = new SimpleList<>();

            String line;
            SimpleProcess current = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                //  Duración del reloj
                if (line.startsWith("\"clockDurationMs\"")) {
                    String value = line.split(":")[1].replace(",", "").trim();
                    clockDuration = Long.parseLong(value);
                    simulator.getOperatingSystem().getClock().setClockDuration(clockDuration);

                } //  Detectar inicio de proceso
                else if (line.equals("{")) {
                    current = new SimpleProcess();
                } //  Detectar fin de proceso
                else if (line.equals("},")) {
                    if (current != null) {
                        processList.insertLast(current);
                    }
                    current = null;
                } //  Último proceso
                else if (line.equals("}")) {
                    if (current != null) {
                        processList.insertLast(current);
                    }
                    current = null;
                } //  Atributos del proceso
                else if (current != null) {
                    if (line.contains("\"name\"")) {
                        current.setName(extractJsonValue(line));
                    } else if (line.contains("\"instructions\"")) {
                        current.setInstructions(Integer.parseInt(extractJsonValue(line)));
                    } else if (line.contains("\"type\"")) {
                        current.setType(ProcessType.valueOf(extractJsonValue(line)));
                    } else if (line.contains("\"cyclesForIO\"")) {
                        current.setCyclesForIO(Integer.parseInt(extractJsonValue(line)));
                    } else if (line.contains("\"ioDuration\"")) {
                        current.setIoDuration(Integer.parseInt(extractJsonValue(line)));
                    }
                }
            }

            //  Aplicar configuración cargada
            simulator.getOperatingSystem().getClock().setClockDuration(clockDuration);

            SimpleNode<SimpleProcess> node = processList.GetpFirst();
            while (node != null) {
                SimpleProcess p = node.GetData();
                simulator.createProcess(p.getName(), p.getInstructions(), p.getType(), p.getCyclesForIO(), p.getIoDuration());
                node = node.GetNxt();
            }

            JOptionPane.showMessageDialog(this,
                    "Configuración cargada correctamente desde " + selectedFile.getName(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar archivo JSON: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String extractJsonValue(String line) {
        String[] parts = line.split(":");
        String value = parts[1].replace(",", "").replace("\"", "").trim();
        return value;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        newProcessPanel = new javax.swing.JPanel();
        cpuBoundRadio = new javax.swing.JRadioButton();
        ioBoundRadio = new javax.swing.JRadioButton();
        cyclesSpinner = new javax.swing.JSpinner();
        ioTimeSpinner = new javax.swing.JSpinner();
        instructionsSpinner = new javax.swing.JSpinner();
        nameField = new javax.swing.JTextField();
        submitButton = new javax.swing.JButton();
        label2 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cpuPanel = new javax.swing.JPanel();
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
        jLabel22 = new javax.swing.JLabel();
        scrollReady = new javax.swing.JScrollPane();
        readyPanel = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        scrollBlocked = new javax.swing.JScrollPane();
        blockedPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        scrollReadyS = new javax.swing.JScrollPane();
        suspendedReadyPanel = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        scrollBlockedS = new javax.swing.JScrollPane();
        suspendedBlockedPanel = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        scrollNew = new javax.swing.JScrollPane();
        newPanel = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        scrollTerminated = new javax.swing.JScrollPane();
        terminatedPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cycleWatchTime = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        startSimulation = new javax.swing.JToggleButton();
        uploadSimulation = new javax.swing.JButton();
        resetSimulation = new javax.swing.JButton();
        generate20Process = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        plannerLog = new javax.swing.JTextArea();

        jPanel1.setBackground(new java.awt.Color(13, 84, 141));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        newProcessPanel.setBackground(new java.awt.Color(0, 0, 70));
        newProcessPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        cpuBoundRadio.setText("CPU Bound");

        ioBoundRadio.setText("I/O Bound");

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
        jLabel19.setText("Nuevo Proceso");

        javax.swing.GroupLayout newProcessPanelLayout = new javax.swing.GroupLayout(newProcessPanel);
        newProcessPanel.setLayout(newProcessPanelLayout);
        newProcessPanelLayout.setHorizontalGroup(
            newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newProcessPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newProcessPanelLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(newProcessPanelLayout.createSequentialGroup()
                        .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(newProcessPanelLayout.createSequentialGroup()
                                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(instructionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(newProcessPanelLayout.createSequentialGroup()
                                        .addComponent(cpuBoundRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ioBoundRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(ioTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 16, Short.MAX_VALUE))))
        );
        newProcessPanelLayout.setVerticalGroup(
            newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newProcessPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(6, 6, 6)
                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instructionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newProcessPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cpuBoundRadio)
                    .addComponent(ioBoundRadio))
                .addGap(2, 2, 2)
                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(newProcessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ioTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(submitButton)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jPanel1.add(newProcessPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, 440, 280));

        cpuPanel.setOpaque(false);

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
        typeProcessRunning.setText("...");

        modeProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        modeProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        modeProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        modeProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        modeProcessRunning.setText("...");

        marProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        marProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        marProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        marProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        marProcessRunning.setText("...");

        pcProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        pcProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        pcProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        pcProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pcProcessRunning.setText("...");

        idProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        idProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        idProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        idProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        idProcessRunning.setText("...");

        nameProcessRunning.setBackground(new java.awt.Color(255, 255, 255));
        nameProcessRunning.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        nameProcessRunning.setForeground(new java.awt.Color(255, 255, 255));
        nameProcessRunning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameProcessRunning.setText("...");

        javax.swing.GroupLayout cpuPanelLayout = new javax.swing.GroupLayout(cpuPanel);
        cpuPanel.setLayout(cpuPanelLayout);
        cpuPanelLayout.setHorizontalGroup(
            cpuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cpuPanelLayout.createSequentialGroup()
                .addGroup(cpuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cpuPanelLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(cpuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(cpuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pcProcessRunning)
                            .addComponent(modeProcessRunning)
                            .addComponent(nameProcessRunning)
                            .addComponent(typeProcessRunning)
                            .addComponent(marProcessRunning)
                            .addComponent(idProcessRunning)))
                    .addGroup(cpuPanelLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jLabel7)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        cpuPanelLayout.setVerticalGroup(
            cpuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cpuPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(cpuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cpuPanelLayout.createSequentialGroup()
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
                    .addGroup(cpuPanelLayout.createSequentialGroup()
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

        jPanel1.add(cpuPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 240, 280));

        CPUphoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/_08_SourcesGUI/cpu_texture.png"))); // NOI18N
        jPanel1.add(CPUphoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 240, 280));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Cola Ready");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, -1, -1));

        readyPanel.setLayout(new javax.swing.BoxLayout(readyPanel, javax.swing.BoxLayout.LINE_AXIS));
        scrollReady.setViewportView(readyPanel);

        jPanel1.add(scrollReady, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, 150, 250));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Cola Blocked");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, -1, -1));

        blockedPanel.setLayout(new javax.swing.BoxLayout(blockedPanel, javax.swing.BoxLayout.LINE_AXIS));
        scrollBlocked.setViewportView(blockedPanel);

        jPanel1.add(scrollBlocked, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 130, 290));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Cola Ready S.");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 330, -1, -1));

        suspendedReadyPanel.setLayout(new javax.swing.BoxLayout(suspendedReadyPanel, javax.swing.BoxLayout.LINE_AXIS));
        scrollReadyS.setViewportView(suspendedReadyPanel);

        jPanel1.add(scrollReadyS, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 360, 130, 288));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Cola Blocked S.");
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 330, -1, -1));

        suspendedBlockedPanel.setLayout(new javax.swing.BoxLayout(suspendedBlockedPanel, javax.swing.BoxLayout.LINE_AXIS));
        scrollBlockedS.setViewportView(suspendedBlockedPanel);

        jPanel1.add(scrollBlockedS, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 360, 130, 290));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Cola New");
        jPanel1.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 330, -1, -1));

        newPanel.setLayout(new javax.swing.BoxLayout(newPanel, javax.swing.BoxLayout.LINE_AXIS));
        scrollNew.setViewportView(newPanel);

        jPanel1.add(scrollNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 360, 130, 290));

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(" Cola Terminated");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 330, -1, -1));

        terminatedPanel.setLayout(new javax.swing.BoxLayout(terminatedPanel, javax.swing.BoxLayout.LINE_AXIS));
        scrollTerminated.setViewportView(terminatedPanel);

        jPanel1.add(scrollTerminated, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 360, 130, 290));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Ciclo de Reloj Global");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 490, 240, -1));

        cycleWatchTime.setBackground(new java.awt.Color(255, 255, 255));
        cycleWatchTime.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        cycleWatchTime.setForeground(new java.awt.Color(255, 255, 255));
        cycleWatchTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cycleWatchTime.setText("0");
        jPanel1.add(cycleWatchTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 520, 240, -1));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Log del Planificador");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 560, 240, -1));

        startSimulation.setBackground(new java.awt.Color(0, 0, 70));
        startSimulation.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        startSimulation.setForeground(new java.awt.Color(255, 255, 255));
        startSimulation.setText("Iniciar Simulación");
        startSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startSimulationActionPerformed(evt);
            }
        });
        jPanel1.add(startSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 320, 240, 40));

        uploadSimulation.setBackground(new java.awt.Color(0, 0, 70));
        uploadSimulation.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        uploadSimulation.setForeground(new java.awt.Color(255, 255, 255));
        uploadSimulation.setText("Precargar una simulación");
        uploadSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadSimulationActionPerformed(evt);
            }
        });
        jPanel1.add(uploadSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 450, 240, 30));

        resetSimulation.setBackground(new java.awt.Color(0, 0, 70));
        resetSimulation.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        resetSimulation.setForeground(new java.awt.Color(255, 255, 255));
        resetSimulation.setText("Reiniciar Simulación");
        resetSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetSimulationActionPerformed(evt);
            }
        });
        jPanel1.add(resetSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 370, 240, 30));

        generate20Process.setBackground(new java.awt.Color(0, 0, 70));
        generate20Process.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        generate20Process.setForeground(new java.awt.Color(255, 255, 255));
        generate20Process.setText("Generación automática 20 procesos");
        generate20Process.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generate20ProcessActionPerformed(evt);
            }
        });
        jPanel1.add(generate20Process, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 410, 240, 30));

        jScrollPane1.setBackground(new java.awt.Color(13, 84, 141));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        plannerLog.setEditable(false);
        plannerLog.setBackground(new java.awt.Color(13, 84, 141));
        plannerLog.setColumns(20);
        plannerLog.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        plannerLog.setForeground(new java.awt.Color(255, 255, 255));
        plannerLog.setRows(5);
        plannerLog.setText("...");
        plannerLog.setBorder(null);
        jScrollPane1.setViewportView(plannerLog);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 600, 240, 50));

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

    private void startSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startSimulationActionPerformed
        if (startSimulation.isSelected()) {
            // Cuando el toggle está presionado
            startSimulation.setText("Pausar simulación");
            simulator.toggleSimulation(); // Inicia la simulación
            uploadSimulation.setEnabled(false);
        } else {
            // Cuando se suelta el toggle
            startSimulation.setText("Iniciar simulación");
            simulator.toggleSimulation(); // Pausa la simulación
            uploadSimulation.setEnabled(true);
        }
    }//GEN-LAST:event_startSimulationActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        validateAndCreateProcess();
        // Limpieza de los inputs
        resetFields();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFieldActionPerformed

    private void resetSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSimulationActionPerformed
        if (simulator == null) {
            JOptionPane.showMessageDialog(this, "El simulador no está inicializado.");
            return;
        }

        simulator.resetSimulation();
        startSimulation.setText("Iniciar Simulación");
        startSimulation.setSelected(false);
        resetView();
    }//GEN-LAST:event_resetSimulationActionPerformed

    private void generate20ProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generate20ProcessActionPerformed
        if (simulator == null) {
            JOptionPane.showMessageDialog(this, "El simulador no está inicializado.");
            return;
        }

        simulator.createRandomProcesses(20);
       
        JOptionPane.showMessageDialog(this, "Se generaron 20 procesos aleatorios.");

    }//GEN-LAST:event_generate20ProcessActionPerformed

    private void uploadSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadSimulationActionPerformed
        loadFromJson();
    }//GEN-LAST:event_uploadSimulationActionPerformed

    private void uniformScrollPaneSizes() {
        Dimension size = new Dimension(130, 250); // ajusta ancho y alto a tu gusto

        JScrollPane[] scrolls = {
            scrollNew, scrollReady, scrollBlocked, scrollReadyS, scrollBlockedS, scrollTerminated
        };

        for (JScrollPane scroll : scrolls) {
            scroll.setPreferredSize(size);
            scroll.setMinimumSize(size);
            scroll.setMaximumSize(size);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CPUphoto;
    private javax.swing.JPanel blockedPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cpuBoundRadio;
    private javax.swing.JPanel cpuPanel;
    private javax.swing.JLabel cycleWatchTime;
    private javax.swing.JSpinner cyclesSpinner;
    private javax.swing.JButton generate20Process;
    private javax.swing.JLabel idProcessRunning;
    private javax.swing.JSpinner instructionsSpinner;
    private javax.swing.JRadioButton ioBoundRadio;
    private javax.swing.JSpinner ioTimeSpinner;
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
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Label label2;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private javax.swing.JLabel marProcessRunning;
    private javax.swing.JLabel modeProcessRunning;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameProcessRunning;
    private javax.swing.JPanel newPanel;
    private javax.swing.JPanel newProcessPanel;
    private javax.swing.JLabel pcProcessRunning;
    private javax.swing.JTextArea plannerLog;
    private javax.swing.JPanel readyPanel;
    private javax.swing.JButton resetSimulation;
    private javax.swing.JScrollPane scrollBlocked;
    private javax.swing.JScrollPane scrollBlockedS;
    private javax.swing.JScrollPane scrollNew;
    private javax.swing.JScrollPane scrollReady;
    private javax.swing.JScrollPane scrollReadyS;
    private javax.swing.JScrollPane scrollTerminated;
    private javax.swing.JToggleButton startSimulation;
    private javax.swing.JButton submitButton;
    private javax.swing.JPanel suspendedBlockedPanel;
    private javax.swing.JPanel suspendedReadyPanel;
    private javax.swing.JPanel terminatedPanel;
    private javax.swing.JLabel typeProcessRunning;
    private javax.swing.JButton uploadSimulation;
    // End of variables declaration//GEN-END:variables
}
