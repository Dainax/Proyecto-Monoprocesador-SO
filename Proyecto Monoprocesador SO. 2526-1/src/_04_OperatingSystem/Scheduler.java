/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _04_OperatingSystem;

import _02_DataStructures.SimpleList;
import _02_DataStructures.SimpleNode;
import _04_OperatingSystem.Process;

/**
 *
 * @author AresR
 */
public class Scheduler {

    //          ----- Atributos -----
    private PolicyType currentPolicy;
    private final OperatingSystem osReference;
    // Indica si la cola de listos ya está ordenada
    private boolean ordered;

    // Para RR
    private final int quantum = 5;

    //          --------------- Metodos ---------------
    /**
     * Constructor
     *
     * @param osReference Referencia al objeto SO para acceder a sus atributos
     * @param currentPolicy Politica escogida para iniciar el sistema
     */
    public Scheduler(OperatingSystem osReference, PolicyType currentPolicy) {
        this.osReference = osReference;
        this.currentPolicy = currentPolicy;
        this.ordered = false;
    }

    // ---------- Planificacion a corto plazo ----------
    
    /**
     * Seleccionar nuevo proceso de la cola de listos para ponerlo en ejecución
     *
     * @return proceso a darle el control del CPU
     */
    public Process selectNextProcess() {
        // Obtener la Cola de Listos del SO
        SimpleList<Process> readyProcesses = osReference.getReadyQueue(); // Asume que el OS tiene este getter

        if (readyProcesses == null || readyProcesses.isEmpty()) {
            return null;
        }

        // Si la lista no está ordenada, la ordenamos 
        if (!this.ordered) {
            sortReadyQueue();
            this.ordered = true;
        }

        // Tomo el proceso. La cola debe estar ordenada segun el algoritmo de ordenamiento
        Process nextProcess = (Process) readyProcesses.GetpFirst().GetData();
        
        // Cambio su estado y lo elimino de la cola
        nextProcess.setState(ProcessState.RUNNING);
        this.osReference.getReadyQueue().delNodewithVal(nextProcess);
        
        System.out.println("Seleccionado el proceso "+nextProcess.getPID()+" para ejecutarse");
        return nextProcess;
    }

    /**
     * Metodo para ordenar la cola que llama al metodo de la politica segun sea el caso
     */
    public void sortReadyQueue() {
        SimpleList<Process> readyProcesses = osReference.getReadyQueue();
        if (readyProcesses.GetSize() <= 1) {
            this.ordered = true;
            return;
        }
        
        switch (currentPolicy) {
            case Priority: sortPriority(); break;
            
            case FIFO: sortFIFO(); break;
            
            case ROUND_ROBIN: sortRoundRobin(); break;
            
            case SPN: sortSPN(); break;
            
            case SRT: sortSRT(); break;
            
            case HRRN: sortHRRN(); break;
        }
        System.out.println("Cola de listos reordenada con política " + currentPolicy);
    }
    
    private void sortPriority() {
        // Ordena por prioridad siendo la mas importante la menor (1)
        ordenateWithBubbleSort(PolicyType.Priority);
    }

    private void sortFIFO() {
        // Toma en cuenta el PID del proceso ya que este es unico 
        // y esta en orden de creacion
        ordenateWithBubbleSort(PolicyType.FIFO);
    }

    private void sortRoundRobin() {
        // Ordena por PID tambien ya que no es RR Virtual, es decir solo se le da un quantum a cada proceso
        // siguiendo cualquier orden mientras se le de el mismo tiempo a cada proceso
        ordenateWithBubbleSort(PolicyType.ROUND_ROBIN);
    }

    private void sortSPN() {
        // SPN shortest process next se usara el mas corto, es decir que tenga 
        // el menor totalInstruction
        ordenateWithBubbleSort(PolicyType.SPN);
    }

    private void sortSRT() {
        // Para SRT shortest remaining time se usara el que tenga el menor 
        // remainingInstruction
        ordenateWithBubbleSort(PolicyType.SRT);
    }

    private void sortHRRN() {
        ordenateWithBubbleSort(PolicyType.HRRN);
    }

    
    /**
     * Metodo que centraliza el ordenar la lista
     */
    private void ordenateWithBubbleSort(PolicyType policy) {
        // Obtengo la lista del SO
        SimpleList<Process> readyProcesses = osReference.getReadyQueue();
        int size = readyProcesses.GetSize();

        // Hago un arreglo temporal
        Process[] processArray = new Process[size];
        
        SimpleNode<Process> current = readyProcesses.GetpFirst();
        int index = 0;
        while (current != null) {
            processArray[index] = current.GetData();
            current = current.GetNxt();
            index++;
        }

        // Ordeno el arreglo temporal
        bubbleSort(processArray, policy);

        // Vuelvo a llenar la cola de listos
        readyProcesses.SetpFirst(null);
        readyProcesses.SetpLast(null);
        for (Process p : processArray) {
            readyProcesses.insertLast(p);
        }
    }

    /**
     * Ordena un array de procesos usando Bubble Sort usando dos metodos 
     */
    private void bubbleSort(Process[] processes, PolicyType policy) {
        int n = processes.length;
        // Pasadas a todo el arreglo
        for (int i = 0; i < n - 1; i++) {
            // Iteracion por cada elemento 
            for (int j = 0; j < n - i - 1; j++) {
                Process a = processes[j];
                Process b = processes[j + 1];

                // Si b es mejor que a (es decir, a no está en el orden correcto respecto a b) 
                // los intercambiamos.
                if (!isABetterThanB(a, b, policy)) {
                    Process temp = a;
                    processes[j] = b;
                    processes[j + 1] = temp;
                }
            }
        }
    }
    
    /**
     * Determina si el Proceso 'a' debe ir antes que el Proceso 'b' para
     * organizar la lista
     */
    private boolean isABetterThanB(Process a, Process b, PolicyType policy) {
        double valA = getComparisonValue(a, policy);
        double valB = getComparisonValue(b, policy);

        if (policy == PolicyType.HRRN) {
            if (valA != valB) { // HRRN: Buscamos el valor maximo.
                return valA > valB;
            }
        } else {
            if (valA != valB) { // Priority, SPN, SRT, FIFO, RR: Buscamos el valor MÍNIMO.
                return valA < valB;
            }
        }
        return a.getPID() < b.getPID(); // Desempate por PID si tienen el mismo valor
    }

    /**
     * Devuelve el valor numérico clave para la comparación según la política.
     */
    private double getComparisonValue(Process p, PolicyType policy) {
        switch (policy) {
            case Priority:
                return p.getPPriority();
            case SPN:
                return p.getTotalInstructions();
            case SRT:
                return p.getRemainingInstructions();
            case FIFO:
            case ROUND_ROBIN:
                return p.getPID();
            case HRRN:
                return p.getResponseRate();
            default:
                return Double.MAX_VALUE;
        }
    }

    /**
     * Actualiza el tiempo de espera y recalcula la Tasa de Respuesta (HRRN).
     */
    public void updateHRRNMetrics(SimpleList<Process> list) {
        SimpleNode current = list.GetpFirst();
        while (current != null) {
            Process p = (Process) current.GetData();

            // Aumentar el tiempo de espera (necesario para SRT/HRRN)
            p.setCyclesWaitingCPU(p.getCyclesWaitingCPU() + 1);

            // Calcular el Response Ratio (solo para HRRN)
            if (p.getRemainingInstructions() > 0) {
                double waitTime = (double) p.getCyclesWaitingCPU();
                double serviceTime = (double) p.getRemainingInstructions();
                double ratio = 1.0 + (waitTime / serviceTime);
                p.setResponseRate(ratio);
            }

            current = current.GetNxt();
        }
    }
    
    // ---------- Planificacion a mediano plazo ----------
    
    
    // ---------- Planificacion a largo plazo ----------
    // Admision de procesos de nuevo al sistema

    // Getters y Setters
    public PolicyType getCurrentPolicy() {
        return currentPolicy;
    }

    public void setCurrentPolicy(PolicyType newPolicy) {
        this.currentPolicy = newPolicy;
        
        this.ordered = false; // Para que vuelva a ordenar la cola por el cambio de politica
        
        System.out.println("Política cambiada a " + newPolicy);
    }

    public int getQuantum() {
        return quantum;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }
}
