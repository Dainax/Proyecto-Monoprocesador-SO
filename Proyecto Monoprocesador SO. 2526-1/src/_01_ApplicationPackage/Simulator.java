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

}
