/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _03_LowLevelAbstractions;

import _04_OperatingSystem.Process;

/**
 *
 * @author DiegoM
 */
public class CPU extends Thread {

    // ---------- Atrs ----------
    // ----- CPU -----
    private int PC;
    private int MAR;
    private volatile boolean isProcessRunning;

    // Contadores de ciclo CPU para planificacion
    private int cycleCounter;
    private int remainingCycles;

    // Proceso que se está ejecutando
    // Si es null, el SO está en control.
    private Process currentProcess;

    // Monitor para la sincronizacion para usar wait() y notify()
    private final Object syncMonitor = new Object();

    // --------------- Metodos ---------------
    /**
     * Constructor
     */
    public CPU() {
        this.PC = 0;
        this.MAR = 0;
        this.isProcessRunning = false;
        this.cycleCounter = 0;
        this.remainingCycles = -1;
        setName("Hilo del CPU");
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
    
    public void stopCPU() {
        this.isProcessRunning = false;
        // Despertar a la CPU para que salga del wait() y termine el hilo.
        synchronized (syncMonitor) {
            syncMonitor.notify();
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        this.isProcessRunning = true;
        
       
        while (isProcessRunning) {
            synchronized (syncMonitor) {
                try {
                    if (currentProcess.getState()==Process.State.NEW){
                        currentProcess.start();
                    }
                    
                    syncMonitor.wait(); // Espera el 'tick' del reloj

                    if (!this.isProcessRunning) { break;} // Si stopCPU fue llamado, sale

                    this.cycleCounter++; // Incrementa el contador global de ciclos de CPU 

                    // Si es null se esta ejecutando el SO
                    if (currentProcess != null) {
                        
                        // Proceso de Usuario
                        currentProcess.executeOneCycle(); // Ejecutar una instrucción del proceso

                        /**
                         * Lógica para planificación Round Robin
                         * Si es menor a 0 (Cuando lo coloquemos en -1)
                         * No tomara en cuenta los ciclos restantes
                         */
                        if (remainingCycles > 0) { // Indica que estamos en RR y queda quantum
                            remainingCycles--;
                        }
                        
                        // Lee el resultado de la ejecucion
                        boolean processWantsToContinue = currentProcess.didExecuteSuccessfully();
                        
                        // Comprobacion de Quantum
                        if (remainingCycles == 0){
                            System.out.println("Quantum de tiempo excedido");
                            // Se acabo el quantum de tiempo del proceso
                            //Llamar al SO para que ejecute RR y coloque al siguiente
                        }
                        
                        // Comprobación de finalización, E/S, 
                        if (processWantsToContinue == false) {
                            this.currentProcess = null;
                            // Llamar al planificador del SO para un cambio de proceso
                            // Aquí es donde el Planificador toma el control (El SO se ejecuta)
                            
                            // Desasignar el proceso de la CPU 
                            // (el Planificador asignará el siguiente en su turno de ejecución)
                            // currentProcess = null;   
                        }
                    }
                    else {
                        System.out.println("No hay proceso");
                        // Se debe ejecutar el sistema operativo     
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    this.isProcessRunning = false;
                }
            }
        }
    }

    // ----- Getters y setters -----
    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getMAR() {
        return MAR;
    }

    public void setMAR(int MAR) {
        this.MAR = MAR;
    }

    public boolean isIsProcessRunning() {
        return isProcessRunning;
    }

    public void setIsProcessRunning(boolean isProcessRunning) {
        this.isProcessRunning = isProcessRunning;
    }

    public int getCycleCounter() {
        return cycleCounter;
    }

    public void setCycleCounter(int cycleCounter) {
        this.cycleCounter = cycleCounter;
    }

    public int getRemainingCycles() {
        return remainingCycles;
    }

    public void setRemainingCycles(int remainingCycles) {
        this.remainingCycles = remainingCycles;
    }

    public void setCurrentProcess(Process process,int quantum) {
        this.currentProcess = process;
        // Al cargar un proceso, actualizamos los registros de la CPU con los del PCB
        if (process != null) {
            this.PC = process.getPC();
            this.MAR = process.getMAR();
            this.remainingCycles = quantum;
        }
    }
    public Process getCurrentProcess() {
        return currentProcess;
    }

}
