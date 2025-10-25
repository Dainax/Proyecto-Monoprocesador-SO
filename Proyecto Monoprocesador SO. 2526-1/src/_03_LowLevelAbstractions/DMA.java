/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _03_LowLevelAbstractions;

import _02_DataStructures.SimpleList;
import _04_OperatingSystem.OperatingSystem;
import _04_OperatingSystem.Process1;

/**
 *
 * @author AresR
 */
public class DMA extends Thread {

    // ----- Atributos ----- 
    // Contador para la gestion de E/S
    private int remainingCycles;
    private volatile boolean isRunning;

    // Proceso al que se le esta manejando su E/S
    // Si es null, no se esta gestionando la E/S de ningun proceso
    private volatile Process1 currentProcess;

    // Cola de nuevos
    private SimpleList newProcesses;

    // Cola de suspendidos. Se simula el area de Swap y no hacer una clase disco
    private SimpleList readySuspendedProcesses; // Procesos listos suspendidos en el area de Swap
    private SimpleList blockedSuspendedProcesses; // Procesos listos suspendidos en el area de Swap

    private volatile boolean busy; // Para saber si esta ocupado o no

    // Monitor para la sincronizacion para usar wait() y notify()
    private final Object syncMonitor = new Object();

    // Para poder referenciar los metodos del SO
    private final OperatingSystem osReference;

    // --------------- Metodos ---------------
    /**
     * Constructor
     */
    public DMA(OperatingSystem osReference) {
        this.remainingCycles = -1;
        this.currentProcess = null;
        this.newProcesses = new SimpleList();
        this.readySuspendedProcesses = new SimpleList();
        this.blockedSuspendedProcesses = new SimpleList();
        this.busy = false;
        this.osReference = osReference;
    }

    /**
     * Hace que el DMA reanude su operacion
     */
    public void playDMA() {
        this.isRunning = true;
        this.receiveTick();
    }

    /**
     * Detiene la ejecucion del DMA
     */
    public void stopDMA() {
        this.isRunning = false;
        // Despertar a la CPU para que salga del wait() y termine el hilo.
        synchronized (syncMonitor) {
            syncMonitor.notify();
        }
    }

    // ----- Sincronización -----
    /**
     * Detiene el hilo de CPU y lo saca de la espera (wait).
     */
    public void receiveTick() {
        synchronized (syncMonitor) {
            syncMonitor.notify();
        }
    }

    /**
     * Metodo principal para el funcionamiento del DMA
     */
    @Override
    public void run() {

        this.isRunning = true;
        while (this.isRunning) {
            synchronized (syncMonitor) {
                try {
                    syncMonitor.wait();

                    if (this.currentProcess != null) {
                        System.out.println("Ejecutando DMA");

                        this.remainingCycles--;

                        if (this.remainingCycles == 0) {
                            System.out.println("Proceso E/S terminado");

                            // Para darselo al Sistema operativo                            
                            // Invocar al sistema operativo para terminar el proceso
                            // Para darselo al Sistema operativo
                            Process1 terminatedProcess = this.currentProcess;
                            // Invocar al sistema operativo para terminar E/S
                            this.osReference.manageIOInterruptionByDMA(terminatedProcess);

                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void addNewProcess(Process1 newProcess) {
        this.newProcesses.insertLast(newProcess);
    }

    // Estas 6 funciones de readysuspended y blockedSuspended hay que probarlas
    public void addReadySuspendedProcess(Process1 newRSProcess) {
        this.readySuspendedProcesses.insertLast(newRSProcess);
    }

    public void addBlockedSuspendedProcess(Process1 newRSProcess) {
        this.readySuspendedProcesses.insertLast(newRSProcess);
    }

    public void getReadySuspendedProcess(int pidRSProcess) {
        this.readySuspendedProcesses.locateData(pidRSProcess);
    }

    public void getBlockedSuspendedProcess(int pidBSProcess) {
        this.blockedSuspendedProcesses.locateData(pidBSProcess);
    }

    public void delReadySuspendedProcess(int pidRSProcess) {
        this.readySuspendedProcesses.delNodewithVal(pidRSProcess);
    }

    public void delBlockedSuspendedProcess(int pidBSProcess) {
        this.blockedSuspendedProcesses.delNodewithVal(pidBSProcess);
    }

    // ------ Getters y Setters ------
    public int getRemainingCycles() {
        return remainingCycles;
    }

    public void setRemainingCycles(int remainingCycles) {
        this.remainingCycles = remainingCycles;
    }

    public Process1 getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(Process1 process) {
        this.currentProcess = process;
        if (process != null) {
            this.remainingCycles = process.getCyclesToManageException();
            this.busy = true;
        }
    }

    public SimpleList getNewProcesses() {
        return newProcesses;
    }

    public SimpleList getReadySuspendedProcesses() {
        return readySuspendedProcesses;
    }

    public SimpleList getBlockedSuspendedProcesses() {
        return blockedSuspendedProcesses;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
