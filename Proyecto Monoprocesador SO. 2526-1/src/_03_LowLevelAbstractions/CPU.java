/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _03_LowLevelAbstractions;

import _04_OperatingSystem.Process;
import _04_OperatingSystem.ProcessType;

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

    private DMA dma;

    // --------------- Metodos ---------------
    /**
     * Constructor
     */
    public CPU(DMA dma) { // Le paso el DMA solo para probar, el SO le dara el proceso al DMA
        this.PC = 0;
        this.MAR = 0;
        this.isProcessRunning = false;
        this.cycleCounter = 0;
        this.remainingCycles = -1;
        setName("Hilo del CPU");
        this.dma = dma;
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
     * Metodo principal para ejecutar un proceso en CPU
     */
    @Override
    public void run() {
        this.isProcessRunning = true;

        while (isProcessRunning) {
            synchronized (syncMonitor) {
                try {
                    if (currentProcess != null && currentProcess.getState() == Process.State.NEW) {
                        currentProcess.start();
                        System.out.println("Iniciando proceso:" + currentProcess.getPID());
                    }

                    syncMonitor.wait(); // Espera el 'tick' del reloj

                    if (!this.isProcessRunning) {
                        break;
                    } // Si stopCPU fue llamado, sale

                    this.PC++; // Incrementa el contador global de ciclos de CPU 
                    this.MAR++; // Incrementa el contador global de ciclos de CPU 
                    this.cycleCounter++; // Incrementa el contador global de ciclos de CPU 

                    // Si es null se esta ejecutando el SO
                    if (currentProcess != null) {

                        // Proceso de Usuario
                        currentProcess.executeOneCycle(); // Ejecutar una instrucción del proceso

                        /**
                         * Lógica para planificación Round Robin Si es menor a 0
                         * (Cuando lo coloquemos en -1) No tomara en cuenta los
                         * ciclos restantes
                         */
                        if (remainingCycles > 0) { // Indica que estamos en RR y queda quantum
                            remainingCycles--;
                        }

                        // Lee el resultado de la ejecucion
                        boolean processWantsToContinue = currentProcess.didExecuteSuccessfully();

                        // Comprobacion de Quantum
                        if (remainingCycles == 0) {
                            System.out.println("Quantum de tiempo excedido");
                            // Se acabo el quantum de tiempo del proceso
                            //Llamar al SO para que ejecute RR y coloque al siguiente
                        }

                        // Comprobación de E/S y terminacion
                        if (processWantsToContinue == false && currentProcess.getType() == ProcessType.IO_BOUND) {
                            //  Si el proceso no ha terminado sus instrucciones pero "no quiere continuar"
                            // es que necesita una operacion E/S
                            if (currentProcess.getPC() != currentProcess.getTotalInstructions()) {

                                this.dma.setCurrentProcess(currentProcess); // Solo para probar
                                this.dma.receiveTick();
                                this.currentProcess = null;
                                // Llamar al planificador del SO para un cambio de proceso
                                // Aquí es donde el Planificador toma el control (El SO se ejecuta)
                                // (el Planificador asignará el siguiente en su turno de ejecución)
                            }
                            else{ // En caso contrario, significa que termino sus instrucciones
                                
                            }
                        } else if (processWantsToContinue == false) {
                            // Considerar colocar una variable proceso terminado para que la revise el SO
                            
                            // El proceso ha sido completado, se debe llevar a finalizado
                            // Llamar al planificador del SO para un cambio de proceso
                        }
                    } else {
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

    public void setCurrentProcess(Process process, int quantum) {
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
