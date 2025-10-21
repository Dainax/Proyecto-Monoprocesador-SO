/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _03_LowLevelAbstractions;

import _02_DataStructures.SimpleList;
import _04_OperatingSystem.Process;

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
    private volatile Process currentProcess;

    // Para simular el area de Swap y no hacer una clase disco
    private SimpleList readySuspendedProcess; // Procesos listos suspendidos en el area de Swap
    private SimpleList blockedSuspendedProcess; // Procesos listos suspendidos en el area de Swap

    // Monitor para la sincronizacion para usar wait() y notify()
    private final Object syncMonitor = new Object();

    // --------------- Metodos ---------------
    /**
     * Constructor
     */
    public DMA() {
        this.remainingCycles = -1;
        this.currentProcess = null;
        this.readySuspendedProcess = new SimpleList();
        this.blockedSuspendedProcess = new SimpleList();
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
                            Process terminatedProcess = this.currentProcess;
                            this.currentProcess = null;
                            // Invocar al SO
                        }
                    } else {
                        System.out.println("No hay proceso E/S");
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Estas 6 funciones de readysuspended y blockedSuspended hay que probarlas
    public void addReadySuspendedProcess(Process newRSProcess) {
        this.readySuspendedProcess.insertLast(newRSProcess);
    }

    public void addBlockedSuspendedProcess(Process newRSProcess) {
        this.readySuspendedProcess.insertLast(newRSProcess);
    }

    public void getReadySuspendedProcess(int pidRSProcess) {
        this.readySuspendedProcess.locateData(pidRSProcess);
    }

    public void getBlockedSuspendedProcess(int pidBSProcess) {
        this.blockedSuspendedProcess.locateData(pidBSProcess);
    }

    public void delReadySuspendedProcess(int pidRSProcess) {
        this.readySuspendedProcess.delNodewithVal(pidRSProcess);
    }

    public void delBlockedSuspendedProcess(int pidBSProcess) {
        this.blockedSuspendedProcess.delNodewithVal(pidBSProcess);
    }

    // ------ Getters y Setters ------
    public int getRemainingCycles() {
        return remainingCycles;
    }

    public void setRemainingCycles(int remainingCycles) {
        this.remainingCycles = remainingCycles;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(Process process) {
        this.currentProcess = process;
        this.remainingCycles = process.getCyclesToManageException();
    }
}
