/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _04_OperatingSystem;

import _02_DataStructures.SimpleList;
import _03_LowLevelAbstractions.CPU;
import _03_LowLevelAbstractions.DMA;
import _03_LowLevelAbstractions.MainMemory;
import _03_LowLevelAbstractions.RealTimeClock;
import java.io.File;
import java.util.Random;

/**
 *
 * @author AresR
 */
public class OperatingSystem extends Thread {

    //          ----- Atributos -----
    // Componentes del sistema
    private CPU cpu;
    private DMA dma;
    private MainMemory mp;
    private Scheduler scheduler;
    private RealTimeClock clock;

    // Colas del sistema
    private SimpleList<Process1> readyQueue;
    private SimpleList<Process1> blockedQueue;
    private SimpleList<Process1> terminatedQueue;

    // Para sincronización
    private final Object osMonitor = new Object();
    private volatile boolean isRunning = true;

    // Para crear procesos random
    private static final Random RANDOM = new Random();

    // Para acceder a la cola de nuevos y a las de suspendidos
    // El SO accede al DMA, para simular que estas estan en almacenamiento 
    // a largo plazo
    // Funcion para ver si una politica es expulsiva ()
    // Expulsivas: RR por quantum, SRT por evaluar procesos desbloqueados
    // No expulsivas: FIFO, SPN HRRN, Prioridades por evaluar procesos desbloqueados
    
    // Apoyos para las estadisticas
    int totalWaitingTime;
    
    //          --------------- Metodos ---------------
    public OperatingSystem(PolicyType currentPolicy, long duration) {
        this.cpu = new CPU(this);
        this.dma = new DMA(this);
        this.mp = new MainMemory(this);
        this.scheduler = new Scheduler(this, currentPolicy);
        this.readyQueue = new SimpleList<Process1>();
        this.blockedQueue = new SimpleList<Process1>();
        this.terminatedQueue = new SimpleList<Process1>();
        this.clock = new RealTimeClock(this.cpu, this.dma, duration);
        this.totalWaitingTime = 0;
        
    }

    // Método para que otros hilos (como la CPU) notifiquen al SO
    public void notifyOS() {
        synchronized (osMonitor) {
            osMonitor.notify();
        }
    }

    public void startOS() {
        if (this.getState() == Thread.State.NEW) {
            this.start();
            this.cpu.start();
            this.dma.start();
            this.clock.start();
        }

        this.isRunning = true;
        this.cpu.playCPU();
        this.dma.playDMA();
        this.clock.playClock();

    }

    public void stopOS() {
        this.isRunning = false;
        this.cpu.stopCPU();
        this.dma.stopDMA();
        this.clock.stopClock();
        // Despertar al SO para que salga del wait()
        synchronized (osMonitor) {
            osMonitor.notify();
        }
    }

    @Override
    public void run() {
        System.out.println("SO: Hilo de gestión del Sistema Operativo iniciado.");
        while (isRunning) {
            // El SO se sincroniza en su propio monitor
            synchronized (osMonitor) {
                try {

                    if (!this.getScheduler().isIsOrdered()) {
                        this.scheduler.sortReadyQueue();
                    }

                    // Simula que se esta ejecutando el proceso del sistema
                    if (readyQueue.isEmpty() || cpu.getCurrentProcess() != null) {
                        // Solo espera si no hay trabajo pendiente para planificar
                        osMonitor.wait();
                    }

                    if (!this.isRunning) {
                        break;
                    }

                    // Planificación de Corto Plazo
                    // Intentar planificar si la CPU está libre y hay procesos listos
                    if (cpu.getCurrentProcess() == null && !readyQueue.isEmpty()) {
                        this.dispatchProcess();
                    }

                    // Planificacion a medio
                    this.getScheduler().manageSwapping();

                    //Planificacion a largo plazo
                    this.getScheduler().manageAdmission();

                    // Incluir aca la logica revision de interrupciones
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    this.isRunning = false;
                }
            }
        }
        System.out.println("SO: Hilo del Sistema Operativo detenido.");
    }

    public void reset() {
    }

    public void createRandomProcesses(int n) {
        if (n <= 0) {
            return;
        }

        for (int i = 0; i < n; i++) {

            // Instrucciones entre 1 y 128 
            int instructions = RANDOM.nextInt(60) + 1;

            // Tipo aleatorio
            ProcessType type = RANDOM.nextBoolean() ? ProcessType.CPU_BOUND : ProcessType.IO_BOUND;

            String name;
            // Nombre 
            if (type == ProcessType.CPU_BOUND) {
                name = "P-" + i + ".Inst:" + instructions;
            } else {
                name = "IO-" + i + ".Inst:" + instructions;
            }

            int cyclesToGenerateInterruption = -1;
            int cyclesToManageInterruption = 0;

            if (type == ProcessType.IO_BOUND) {
                // Elegir un ciclo dentro del rango de instrucciones en el que se genere la E/S
                // Si solo hay 1 instrucción, se generará en la instrucción 1
                if (instructions <= 1) {
                    cyclesToGenerateInterruption = 1;
                } else {
                    cyclesToGenerateInterruption = RANDOM.nextInt(10) + 1;
                }
                // Tiempo de servicio de E/S (DMA) 
                cyclesToManageInterruption = RANDOM.nextInt(5);
            } else {
                // CPU bound: no generación de E/S
                cyclesToGenerateInterruption = -1;
                cyclesToManageInterruption = 0;
            }

            // Creo el proceso
            this.newProcess(name, instructions, type, cyclesToGenerateInterruption, cyclesToManageInterruption);
        }
    }

    public void loadConfigFromJSON(File f) {
    }

    public void newProcess(String name, int totalInstructions, ProcessType type, int cyclesToGenerateInterruption, int cyclesToManageInterruption) {

        // Verifico y obtengo la dirección base usando las instrucciones totales como tamaño
        int baseDirection = this.mp.isSpaceAvailable(totalInstructions);

        Process1 newProcess;

        // Si no hay espacio en memoria
        if (baseDirection == -1) {
            System.out.println("No hay espacio contiguo suficiente (" + totalInstructions + " unidades) en la Memoria Principal para el proceso " + name + ". Proceso no admitido en el sistema. Enviando a cola de nuevos");
            newProcess = new Process1(
                    name,
                    totalInstructions,
                    type,
                    cyclesToGenerateInterruption,
                    cyclesToManageInterruption,
                    -1
            );
            //Agregar a la cola de nuevos del dma
            this.dma.addNewProcess(newProcess);

            // Si hay espacio
        } else {
            //Crear el objeto Process con la dirección base encontrada
            newProcess = new Process1(
                    name,
                    totalInstructions,
                    type,
                    cyclesToGenerateInterruption,
                    cyclesToManageInterruption,
                    baseDirection
            );
            // Coloco el proceso en listo
            newProcess.setPState(ProcessState.READY);

            //Agregar a la cola de listos 
            this.readyQueue.insertLast(newProcess);

            // Asignar el espacio en la memoria principal (Actualiza el array memorySlots)
            this.mp.allocate(baseDirection, totalInstructions);

            // Notificar al planificador
            this.scheduler.setIsOrdered(false);
            System.out.println("Proceso " + name + " admitido en la Memoria Principal. Enviando a cola de listos");
        }
    }

    public void dispatchProcess() {

        // Escoger uno nuevo con el planificador
        Process1 nextProcess = scheduler.selectNextProcess();

        if (nextProcess != null) {
            // Setea el quantum segun la politica
            int quantum = (this.scheduler.getCurrentPolicy() == PolicyType.ROUND_ROBIN) ? scheduler.getQuantum() : -1;

            this.cpu.setCurrentProcess(nextProcess, quantum);
            nextProcess.setPState(ProcessState.RUNNING); // Nuevo estado

            System.out.println("SO: Despachando proceso PID " + nextProcess.getPID() + ".");
        } else {
            System.out.println("SO: Despachando el proceso del sistema.");
        }
    }

    /**
     * Maneja un desalojo por Quantum en RR.
     *
     * @param preemptedProcess El proceso a desalojar.
     */
    public void handlePreemption(Process1 preemptedProcess) {

        preemptedProcess.setPState(ProcessState.READY);
        this.getReadyQueue().insertLast(preemptedProcess);
        this.getCpu().setCurrentProcess(null, -1);
        System.out.println("SO: Desalojo de PID " + preemptedProcess.getPID() + ". Movido a READY.");
        notifyOS(); // Despierta el hilo del SO para que llame a dispatchProcess
    }

    public void manageIORequest() {
        Process1 processToSet = this.getCpu().getCurrentProcess();
        processToSet.setPState(ProcessState.BLOCKED); // Cambio el estado

        // Si el DMA esta desocupado le seteo el proceso
        if (this.getDma().isBusy() == false) {
            this.blockedQueue.insertLast(processToSet); // Lo añado a la cola de bloqueados del SO
            this.getDma().setCurrentProcess(this.cpu.getCurrentProcess()); // Envio al DMA al proceso
            this.getDma().receiveTick();
            this.getCpu().setCurrentProcess(null, -1);
        } // Si el DMA esta ocupado bloqueo y encolo el proceso
        else {
            this.blockedQueue.insertLast(processToSet); // Encolo
            this.getCpu().setCurrentProcess(null, -1); // Libero al CPU
        }
    }

    public void manageIOInterruptionByDMA(Process1 terminatedIOProcess) {
        terminatedIOProcess.setExceptionManaged(true); // Indico que se manejo la E/S al proceso

        // Si esta en la cola de bloqueados
        if (terminatedIOProcess.getPState() == ProcessState.BLOCKED) {
            this.getBlockedQueue().delNodewithVal(terminatedIOProcess); // Quito de la cola de bloqueados
            terminatedIOProcess.setPState(ProcessState.READY); // Cambio su estado
            this.getReadyQueue().insertLast(terminatedIOProcess); // Lo agrego a la cola de listos

        } // Esta en la cola de bloqueados suspendidos
        else {
            this.getDma().delBlockedSuspendedProcess(terminatedIOProcess.getPID()); // Lo quito de la cola de bloqueados suspendidos
            terminatedIOProcess.setPState(ProcessState.READY_SUSPENDED); // Cambio su estado
            this.getDma().addReadySuspendedProcess(terminatedIOProcess); // Lo agrego a la cola de listos suspendidos
        }

        // Si no hay mas procesos bloqueados el DMA queda libre
        if (this.blockedQueue.isEmpty()) {
            this.getDma().setCurrentProcess(null); // Libero al DMA
            this.getDma().setBusy(false);
        } else {
            // Si hay alguien en la cola de bloqueado del sistema operativo lo agarro
            Process1 nextProcessForIO = (Process1) this.getBlockedQueue().GetpFirst().GetData();
            this.getDma().setCurrentProcess(nextProcessForIO); // Envio al DMA al proceso
            this.getDma().receiveTick(); // Le indico al DMA que continue
        }

        if (this.scheduler.getCurrentPolicy() == PolicyType.ROUND_ROBIN || this.scheduler.getCurrentPolicy() == PolicyType.SRT) {
            handlePreemption(this.getCpu().getCurrentProcess());
        }
    }

    public void terminateProcess() {
        //          Terminacion de un proceso
        System.out.println("CPU: Proceso terminado.");
        Process1 terminatedProcess = this.getCpu().getCurrentProcess(); // Cambio el estado
        terminatedProcess.setPState(ProcessState.TERMINATED);
        terminatedProcess.setMAR(-1);
        terminatedProcess.setFinishCycle(this.cpu.getCycleCounter());
        this.totalWaitingTime = this.totalWaitingTime + terminatedProcess.getCyclesWaitingCPU();
        this.getTerminatedQueue().insertLast(terminatedProcess); //Mando el proceso a listos
        this.getCpu().setCurrentProcess(null, -1);// Libera CPU

    }

    // Getters y Setters
    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public DMA getDma() {
        return dma;
    }

    public void setDma(DMA dma) {
        this.dma = dma;
    }

    public MainMemory getMp() {
        return mp;
    }

    public void setMp(MainMemory mp) {
        this.mp = mp;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public SimpleList<Process1> getBlockedQueue() {
        return blockedQueue;
    }

    public void setBlockedQueue(SimpleList<Process1> blockedQueue) {
        this.blockedQueue = blockedQueue;
    }

    public SimpleList<Process1> getReadyQueue() {
        return readyQueue;
    }

    public void setReadyQueue(SimpleList<Process1> readyQueue) {
        this.readyQueue = readyQueue;
    }

    public SimpleList<Process1> getTerminatedQueue() {
        return terminatedQueue;
    }

    public void setTerminatedQueue(SimpleList<Process1> terminatedQueue) {
        this.terminatedQueue = terminatedQueue;
    }

    public void notifyNewProcessArrival(Scheduler scheduler) {
        scheduler.setIsOrdered(false);
    }

    public RealTimeClock getClock() {
        return clock;
    }

    /**
     * Apaga completamente los subhilos del SO: CPU, DMA y Reloj. Usar cuando se
     * quiera destruir la instancia antes de crear otra nueva (reset completo).
     */
    public void shutdownOS() {
        // Marcar para que el hilo del SO termine
        this.isRunning = false;

        // Parar CPU y DMA (estos métodos ya despiertan los hilos para que salgan)
        try {
            this.cpu.stopCPU();
        } catch (Exception ignored) {
        }
        try {
            this.dma.stopDMA();
        } catch (Exception ignored) {
        }

        // Detener permanentemente el reloj
        try {
            this.clock.shutdownClock();
        } catch (Exception ignored) {
        }

        // Despertar al SO para que salga del wait() y termine
        synchronized (osMonitor) {
            osMonitor.notify();
        }
    }

    public int getTotalWaitingTime() {
        return totalWaitingTime;
    }
}
