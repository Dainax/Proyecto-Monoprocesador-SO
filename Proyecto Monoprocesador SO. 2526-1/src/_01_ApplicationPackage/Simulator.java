package _01_ApplicationPackage;

import _03_LowLevelAbstractions.CPU;
import java.io.File;
import java.util.List;
import _04_OperatingSystem.OperatingSystem;
import _04_OperatingSystem.PolicyType;
import static _04_OperatingSystem.PolicyType.Priority;
import _04_OperatingSystem.Process1;
import _04_OperatingSystem.ProcessType;
import _07_GUI.SimulationPanel;
import java.io.FileReader;
import javax.swing.SwingUtilities;
import com.google.gson.Gson;
import _02_DataStructures.SimpleList;
import _02_DataStructures.SimpleNode;


/**
 *
 * @author Danaz
 */
/**
 * Simulador: controlador central que conecta la vista (SimulationPanel) con el
 * modelo (OperatingSystem, CPU, RealTimeClock).
 */
public class Simulator {

    private OperatingSystem so;
    private SimulationPanel simulationPanel;
    private boolean running;

    // Constructor principal
    public Simulator(SimulationPanel panel, PolicyType policy, long cycleDuration) {
        this.simulationPanel = panel;
        this.so = new OperatingSystem(policy, cycleDuration);
        simulationPanel.initClockTimer(this);
        this.so.getClock().setOnTickListener(() -> {
            SwingUtilities.invokeLater(() -> {
                simulationPanel.updateCPU(so.getCpu());
                simulationPanel.updateQueues(so);
            });
        });
    }

    public OperatingSystem getSo() {
        return so;
    }

    // -----------------------------
    // Control de simulación
    // -----------------------------
    /**
     * Inicia la simulación
     */
    public void startSimulation() {
        if (!running) {
            running = true;
            so.startOS();  // aquí se inicia TODO (reloj + cpu + scheduler)
        }
    }

    /**
     * Pausa la simulación
     */
    ///HAY QUE HACER LA FUNCIÓN DE PAUSA EN SO
    public void pauseSimulation() {
        so.stopOS();
        running = false;
    }

    public void toggleSimulation() {
        if (running) {
            running = false;
            so.stopOS();
            simulationPanel.pauseClockUI();
            
        } else {
            running = true;
            so.startOS();
            simulationPanel.resumeClockUI();
        }
    }

    ///HAY QUE HACER LA FUNCIÓN DE RESET EN SO
    /**
     * Reinicia todo el sistema y la interfaz
     */
    public void resetSimulation() {
        so.reset();
        running = false;
        simulationPanel.resetView(); // método opcional para limpiar colas y CPU
    }

    public void createProcess(String name, int totalInstructions, ProcessType type, int cyclesToGenerateInterruption, int cyclesToManageInterruption) {
        so.newProcess(name, totalInstructions, type, cyclesToGenerateInterruption, cyclesToManageInterruption);
        simulationPanel.updateQueues(so);
        System.out.println("Llamando a updateQueues() después de crear proceso");
    }

    /**
     * Carga una configuración inicial y procesos desde un archivo JSON
     */
    public void loadFromJSON(File file) {
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            SimulationData data = gson.fromJson(reader, SimulationData.class);

            // Actualiza la configuración del OS
            so = new OperatingSystem(Priority, data.getCycleDuration());

            // Carga procesos iniciales
            List<Process1> processes = data.getProcesses();
            for (Process1 p : processes) {
                so.newProcess(p.getPName(), p.getTotalInstructions(), p.getType(), p.getCyclesToGenerateException(), p.getCyclesToManageException());
            }
            updateUI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza visualmente la interfaz según el estado actual del sistema
     */
    private void updateUI() {
        if (simulationPanel != null) {
            simulationPanel.updateCPU(so.getCpu());
            simulationPanel.updateQueues(so);
        }
    }

    public void createRandomProcesses(int n) {
        so.createRandomProcesses(n);
        scheduleUIUpdate();
    }

    // Envía actualización de UI en el EDT
    private void scheduleUIUpdate() {
        SwingUtilities.invokeLater(() -> {
            try {
                CPU cpu = so.getCpu(); // según tu comentario: so.getCpu().getCycleCounter()
                // Actualizar CPU
                simulationPanel.updateCPU(cpu);

                // Actualizar colas: asumimos que so tiene un método que devuelve Map<String, List<Proces
                simulationPanel.updateQueues(so);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Getters útiles
    public OperatingSystem getOperatingSystem() {
        return so;
    }

    public boolean isRunning() {
        return running;
    }

    public void setSimulationPanel(SimulationPanel panel) {
        this.simulationPanel = panel;
    }

    public SimulationPanel getSimulationPanel() {
        return this.simulationPanel;
    }

    // Clase auxiliar para deserializar JSON
    private static class SimulationData {

        private long cycleDuration;
        private List<Process1> processes;

        public long getCycleDuration() {
            return cycleDuration;
        }

        public List<Process1> getProcesses() {
            return processes;
        }
    }
        public int getCompletedProcessesCount() {
    return this.getOperatingSystem().getTerminatedQueue().GetSize(); // Número de procesos terminados
}
        public long getElapsedTime() {
    return this.getOperatingSystem().getClock().getTotalCyclesElapsed(); // o tiempo en milisegundos
}
public double calculateThroughput() {
    int completed = getCompletedProcessesCount();
    long elapsed = getElapsedTime(); // tiempo en ticks

    if (elapsed == 0) return 0;
    return (double) completed / elapsed;
}
    public double getCPUProductivePercentage () {
        
        int productivecycles = this.getOperatingSystem().getCpu().getProductiveCycles();
        long cyclecounter = this.getOperatingSystem().getClock().getTotalCyclesElapsed();
        
        if (cyclecounter == 0) return -1;
        return ((double) productivecycles / cyclecounter) * 100.0;

    }
    public double getAverageWaitingTime (){
        
        int finishedProcess = this.getOperatingSystem().getTerminatedQueue().GetSize();
        int totalWaitingTime = this.getOperatingSystem().getTotalWaitingTime();

        if (finishedProcess == 0) return 0;
        return (double) totalWaitingTime / finishedProcess ;
    }
    
    public double getTotalFairness () {
       SimpleList<Process1> terminated = this.getOperatingSystem().getTerminatedQueue();
       if (terminated.isEmpty()) return 1.0;
       
       double sum = 1;
       
       SimpleNode<Process1> node = terminated.GetpFirst();
       while (node != null){
           Process1 p = node.GetData();
           sum += p.calculateSlowdown();
           node = node.GetNxt();
       }
       
       return sum / terminated.GetSize();
    }        
}
