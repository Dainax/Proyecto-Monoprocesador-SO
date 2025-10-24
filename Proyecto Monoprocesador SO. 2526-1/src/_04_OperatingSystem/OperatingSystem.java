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
    private SimpleList<Process> readyQueue;
    private SimpleList<Process> blockedQueue;
    private SimpleList<Process> terminatedQueue;

    // Para sincronización
    private final Object osMonitor = new Object();
    private volatile boolean isRunning = true;

    // Para acceder a la cola de nuevos y a las de suspendidos
    // El SO accede al DMA, para simular que estas estan en almacenamiento 
    // a largo plazo
    //          --------------- Metodos ---------------
    public OperatingSystem(PolicyType currentPolicy, long duration) {
        this.cpu = new CPU(this);
        this.dma = new DMA(this);
        this.mp = new MainMemory(this);
        this.scheduler = new Scheduler(this, currentPolicy);
        this.readyQueue = new SimpleList<Process>();
        this.blockedQueue = new SimpleList<Process>();
        this.terminatedQueue = new SimpleList<Process>();
        this.clock = new RealTimeClock(this.cpu, this.dma, duration);
    }

    // Método para que otros hilos (como la CPU) notifiquen al SO
    public void notifyOS() {
        synchronized (osMonitor) {
            osMonitor.notify();
        }
    }

    public void startOS() {

        this.start();
        this.cpu.start();
        this.clock.start();
    }

    public void stopOS() {
        this.isRunning = false;
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

                    // Simula que se esta ejecutando el proceso del sistema
                    if (readyQueue.isEmpty() || cpu.getCurrentProcess() != null) {
                        // Solo espera si no hay trabajo pendiente para planificar
                        osMonitor.wait();
                    }

                    if (!this.isRunning) {
                        break;
                    }

                    // Lógica de planificación de Corto Plazo
                    // Intentar planificar si la CPU está libre y hay procesos listos
                    if (cpu.getCurrentProcess() == null && !readyQueue.isEmpty()) {
                        this.dispatchProcess();
                    }

                    // A medio y a largo
                    // Incluir aca la logica revision de interrupciones
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    this.isRunning = false;
                }
            }
        }
        System.out.println("SO: Hilo del Sistema Operativo detenido.");
    }

    public void newProcess(String name, int totalInstructions, ProcessType type, int cyclesToGenerateInterruption, int cyclesToManageInterruption) {

        // Verifico y obtengo la dirección base usando las instrucciones totales como tamaño
        int baseDirection = this.mp.isSpaceAvailable(totalInstructions);

        Process newProcess;

        // Si no hay espacio en memoria
        if (baseDirection == -1) {
            System.out.println("No hay espacio contiguo suficiente (" + totalInstructions + " unidades) en la Memoria Principal para el proceso " + name + ". Proceso no admitido en el sistema. Enviando a cola de nuevos");
            newProcess = new Process(
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
            newProcess = new Process(
                    name,
                    totalInstructions,
                    type,
                    cyclesToGenerateInterruption,
                    cyclesToManageInterruption,
                    baseDirection
            );
            // Coloco el proceso en listo
            newProcess.setState(ProcessState.READY);

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
        Process nextProcess = scheduler.selectNextProcess();

        if (nextProcess != null) {
            // Setea el quantum segun la politica
            int quantum = (this.scheduler.getCurrentPolicy() == PolicyType.ROUND_ROBIN) ? scheduler.getQuantum() : -1;

            cpu.setCurrentProcess(nextProcess, quantum);
            nextProcess.setState(ProcessState.RUNNING); // Nuevo estado

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
    public void handlePreemption(Process preemptedProcess) {

        preemptedProcess.setState(ProcessState.READY);
        this.getReadyQueue().insertLast(preemptedProcess);

        System.out.println("SO: Desalojo de PID " + preemptedProcess.getPID() + ". Movido a READY.");
        notifyOS(); // Despierta el hilo del SO para que llame a dispatchProcess
    }

    public void manageIORequest(Process processToSet) {
        processToSet.setState(ProcessState.BLOCKED); // Cambio el estado

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

    public void manageIOInterruptionByDMA(Process terminatedIOProcess) {
        terminatedIOProcess.setExceptionManaged(true); // Indico que se manejo la E/S al proceso
        this.getBlockedQueue().delNodewithVal(terminatedIOProcess); // Quito de la cola de bloqueados
        this.getReadyQueue().insertLast(terminatedIOProcess); // Lo agrego a la cola de listos

        // Si no hay mas procesos bloqueados el DMA queda libre
        if (this.blockedQueue.isEmpty()) {
            this.getDma().setCurrentProcess(null); // Libero al DMA
            this.getDma().setBusy(false);
        } else {
            // Si hay alguien en la cola de bloqueado del sistema operativo lo agarro
            Process nextProcessForIO = (Process) this.getBlockedQueue().GetpFirst().GetData();
            this.getDma().setCurrentProcess(nextProcessForIO); // Envio al DMA al proceso
            this.getDma().receiveTick(); // Le indico al DMA que continue
        }

        //-----------        
        // COLOCAR LA SEGUNDA POLITICA
        //-----------
        if (this.scheduler.getCurrentPolicy() == PolicyType.ROUND_ROBIN || this.scheduler.getCurrentPolicy() == PolicyType.ROUND_ROBIN) {

            // Llamar al Dispatcher
        }

// Finalizado una operacion E/S del dma
        /**
         * SO debe actualizar el estado del proceso Moverlo a la cola de listo
         * Llamar al planificador
         */
        // Completar operacion I/O (Terminada la ejecución DMA)
    }

    public void terminateProcess() {
        //          Terminacion de un proceso
        System.out.println("CPU: Proceso terminado.");
        Process terminatedProcess = this.getCpu().getCurrentProcess(); // Cambio el estado
        terminatedProcess.setState(ProcessState.TERMINATED);
        this.getTerminatedQueue().insertLast(terminatedProcess); //Mando el proceso a listos
        this.getCpu().setCurrentProcess(null, -1);// Libera CPU

    }

    // Funcion para ver si una politica es expulsiva ()
    // Expulsivas: RR por quantum, SRT por evaluar procesos desbloqueados
    // No expulsivas: FIFO, SPN HRRN, Prioridades por evaluar procesos desbloqueados
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

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public SimpleList<Process> getBlockedQueue() {
        return blockedQueue;
    }

    public void setBlockedQueue(SimpleList<Process> blockedQueue) {
        this.blockedQueue = blockedQueue;
    }

    public SimpleList<Process> getReadyQueue() {
        return readyQueue;
    }

    public void setReadyQueue(SimpleList<Process> readyQueue) {
        this.readyQueue = readyQueue;
    }

    public SimpleList<Process> getTerminatedQueue() {
        return terminatedQueue;
    }

    public void setTerminatedQueue(SimpleList<Process> terminatedQueue) {
        this.terminatedQueue = terminatedQueue;
    }

    public void notifyNewProcessArrival(Scheduler scheduler) {
        scheduler.setIsOrdered(false);
    }

}
