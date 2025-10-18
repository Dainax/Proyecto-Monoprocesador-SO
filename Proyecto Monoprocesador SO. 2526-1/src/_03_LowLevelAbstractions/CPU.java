/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _03_LowLevelAbstractions;

/**
 *
 * @author DiegoM
 */
public class CPU extends Thread{
    
    // ---------- Atrs ----------
    // ----- CPU -----
    private int PC;
    private int MAR;
    private volatile boolean isProcessRunning;
    
    // Contadores de ciclo CPU para planificacion
    private int cycleCounter;
    private int remainingCycles;
    
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
     * 
     */
    public void receiveTick() {
        synchronized (syncMonitor) {
            syncMonitor.notify();
        }
    }

    /**
     * Detiene el hilo de CPU y lo saca de la espera (wait).
     */
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
    public void run()
    {
        this.isProcessRunning = true;
        
        while (isProcessRunning) {
            synchronized (syncMonitor) {
                try {
                    syncMonitor.wait();
                    // La politica no es RR
                    if (remainingCycles!=-1) {
                        break;
                    } 
                    // La politica es RR
                    else {
                        
                    }
                }
                catch (InterruptedException e) {
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
    
}

