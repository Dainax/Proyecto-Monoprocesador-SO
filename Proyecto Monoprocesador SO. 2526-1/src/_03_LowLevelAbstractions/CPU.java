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
                    
                    // Espera el 'tick' del reloj
                    syncMonitor.wait();
                    
                    // Si stopCPU fue llamado, sale
                    if (!this.isProcessRunning) {
                        break;
                    }
                    
                    // Incrementa el contador global de ciclos de CPU 
                    this.cycleCounter++;
                    
                    // Si es null se esta ejecutando el CPU
                    if (currentProcess != null) {
                        // Proceso de Usuario
                        
                        // Ejecutar una instrucción del proceso
                        
                        //boolean stillRunning = currentProcess.executeInstruction();
                        
                        
                        
                        // Lógica para planificación preemptiva (Round Robin)
                        if (remainingCycles > 0) { // Indica que estamos en RR y queda quantum
                            remainingCycles--;
                        }

                        // Comprobación de finalización, E/S, o Quantum
//                        if (!stillRunning || remainingCycles == 0) {
//                            
//                            // Llamar al planificador del SO para un cambio de contexto
//                            // Aquí es donde el Planificador toma el control (El SO se ejecuta)
//                            // scheduler.handlePreemption(currentProcess, remainingCycles == 0); 
//                            
//                            // Desasignar el proceso de la CPU 
//                            // (el Planificador asignará el siguiente en su turno de ejecución)
//                            // currentProcess = null; 
//                        }
                        
                    } else {
                        // El Planificador/Sistema Operativo toma el control
                        
                        // El SO debe encargarse de la gestión:
                        // - Crear nuevos procesos (a largo plazo). [cite: 33]
                        // - Mover procesos de Listo a Listo/Bloqueado Suspendido. [cite: 32]
                        // - Ejecutar el Planificador (Scheduler) a corto plazo. [cite: 37]
                        
                        // **Llamar al planificador del SO para que seleccione un proceso**
                        // Process nextProcess = scheduler.selectNextProcess();
                        // if (nextProcess != null) {
                        //    setCurrentProcess(nextProcess);
                        //    // Si es RR, reiniciar el quantum
                        //    if (scheduler.isRoundRobin()) {
                        //        remainingCycles = scheduler.getQuantum();
                        //    }
                        // }
                        
                        // NOTA: Es crucial que tu clase 'Scheduler' o 'OperatingSystem'
                        // tenga su propia lógica para simular su tiempo de ejecución.
                        // Podrías usar un 'flag' o un 'currentSOOperation' para indicar
                        // que el SO está trabajando durante este ciclo.
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
    
//    public void setCurrentProcess(Process process) {
//        this.currentProcess = process;
//        // Al cargar un proceso, actualizamos los registros de la CPU con los del PCB
//        if (process != null) {
//            this.PC = process.get;
//            this.MAR = process.getMemoryAddressRegister();
//        }
//    }
    
    public Process getCurrentProcess() {
        return currentProcess;
    }
    
}

