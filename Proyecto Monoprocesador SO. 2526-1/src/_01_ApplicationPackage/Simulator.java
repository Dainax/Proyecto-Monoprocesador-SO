package _01_ApplicationPackage;

import _03_LowLevelAbstractions.CPU;
import java.io.File;
import java.util.List;
import _04_OperatingSystem.OperatingSystem;
import _04_OperatingSystem.PolicyType;
import static _04_OperatingSystem.PolicyType.Priority;
import _04_OperatingSystem.Process1;
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
        
        this.so.getClock().setOnTickListener(() -> {
        SwingUtilities.invokeLater(() -> {
            long ciclos = so.getCpu().getCycleCounter();
            simulationPanel.updateClock(ciclos);
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
        }}
    

    /**
     * Pausa la simulación
     */
    
    ///HAY QUE HACER LA FUNCIÓN DE PAUSA EN SO
    public void pauseSimulation() {
        so.stopOS();
        running = false;
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

    public void createProcess(Process1 p) {
        so.newProcess(p.getName(), p.getTotalInstructions(), p.getType(), p.getCyclesToGenerateException(), p.getCyclesToManageException());
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
                so.newProcess(p.getName(), p.getTotalInstructions(), p.getType(), p.getCyclesToGenerateException(), p.getCyclesToManageException());
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
            simulationPanel.updateClock(so.getCpu().getCycleCounter());
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

                // actualizar contador de ciclos sacándolo del CPU (como dijiste)
                long ciclos = cpu.getCycleCounter();
                simulationPanel.updateClock(ciclos);
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
