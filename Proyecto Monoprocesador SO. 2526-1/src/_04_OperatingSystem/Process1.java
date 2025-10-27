/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _04_OperatingSystem;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author DiegoM
 */
public class Process1 extends Thread {

    // ---------- Atributos ----------
    // 
    private int PID;
    private int PC;
    private int MAR;
    private String name;
    private int totalInstructions;
    private int executedInstructions;
    private int remainingInstructions;
    private ProcessType type;
    private ProcessState state;
    private int cyclesToGenerateException; // Para generar IO si es IO_Bound
    private int cyclesToManageException;   // Para satisfacer interrupcion
    private boolean ExceptionManaged;
    private int baseDirection;

    /**
     * ----- Planificación ----- FCFS:First-Come-First-Served o FIFO se usara el
     * id del proceso Para SPN shortest process next se usara el que tenga el
     * menor totalInstruction Para SRT shortest remaining time se usara el que
     * tenga el menor remainingInstruction
     */
    private int priority; // Prioridad del 1 al 5 aleatoria
    private int cyclesWaitingCPU;
    private double responseRate; //Tasa de respuesta al proceso para la politica HRRN (ver formula)
    private int arriveCycle; // Ciclo de llegada a la cola de listos
    private int finishCycle; // Ciclo de terminacion del proceso

    /**
     * Para comunicar el resultado de la ejecución a la CPU true: El proceso
     * ejecutó una instrucción y desea seguir (no terminó, no pidió E/S). false:
     * El proceso terminó o solicitó una excepción/E/S.
     */
    private volatile boolean executedSuccessfully;

    // Para sincronización
    private final Object processMonitor = new Object();
    private volatile boolean keepRunning = true;

    // Contador atómico para asegurar que los ID son únicos incluso si se crean en hilos distintos
    private static final AtomicInteger PID_COUNTER = new AtomicInteger(1);

    private static final Random RANDOM = new Random();

    // ---------- Metodos ----------
    /**
     * Constructor de la clase
     *
     * @param name Nombre del proceso
     * @param totalInstructions Numero total de instrucciones
     * @param type Tipo del proceso, si es IO_BOUND tiene numero de ciclos para
     * generar y manejar interrupcion
     * @param cyclesToGenerateInterruption
     * @param cyclesToManageInterruption
     * @param baseDirection Direccion de la memoria principal donde inicia el
     * proceso (Usar metodos de la clase MP)
     */
    public Process1(String name, int totalInstructions, ProcessType type, int cyclesToGenerateInterruption, int cyclesToManageInterruption, int baseDirection) {
        this.PID = PID_COUNTER.getAndIncrement();
        if (baseDirection==-1) {
            this.PC = baseDirection;
        } else {
            this.PC = baseDirection + 1;
        }
        this.MAR = baseDirection;
        this.name = name;
        this.totalInstructions = totalInstructions;
        this.executedInstructions = 0;
        this.remainingInstructions = totalInstructions;
        this.type = type;
        this.state = ProcessState.NEW;
        this.cyclesToGenerateException = cyclesToGenerateInterruption;
        this.cyclesToManageException = cyclesToManageInterruption;
        this.ExceptionManaged = false;
        this.priority = RANDOM.nextInt(5) + 1;
        this.cyclesWaitingCPU = 0;
        this.responseRate = 0;
        this.baseDirection = baseDirection;
        this.executedSuccessfully = true;
        this.arriveCycle = -1;
        this.finishCycle = -1;
    }

    // Metodo para despertar al hilo por un ciclo
    public void executeOneCycle() {
        synchronized (processMonitor) {
            processMonitor.notify();
        }
    }

    // Método que la CPU consulta después de despertar al proceso
    public boolean didExecuteSuccessfully() {
        return executedSuccessfully;
    }

    /**
     * Método que simula la ejecución de una instrucción
     */
    @Override
    public void run() {
        // Bucle de vida del proceso (Hilo de usuario)
        while (keepRunning) {
            synchronized (processMonitor) {
                try {
                    processMonitor.wait(); // Esperar la señal de la CPU (que permite empezar la ejecución)

                    if (!keepRunning) {
                        break;
                    }

                    // Simula la ejecucion de una instruccion
                    if (this.executedInstructions < this.totalInstructions) {
                        this.PC = this.PC + 1; // PC y MAR aumentan en 1 por ciclo 
                        this.MAR = this.MAR + 1;
                        this.executedInstructions = this.executedInstructions + 1;
                        this.remainingInstructions = this.remainingInstructions - 1;

                        System.out.println("[" + this.name + "] Ejecutando: PC=" + this.PC + "/" + this.totalInstructions + ". MAR=" + this.MAR);
                        this.executedSuccessfully = true;

                        /**
                         * Logica de I/O bound Cuando el proceso solicite una
                         * operacion E/S el CPU debera ver que este indico que
                         * no se ejecuto exitosamente pero no ha terminado
                         */
                        if (this.executedInstructions == this.cyclesToGenerateException) {
                            System.out.println("Generando E/S");
                            this.executedSuccessfully = false; // llama al SO a traves de la CPU para que maneje la operacion E/S
                        }
                    } else {
                        // El proceso ha completado todas sus instrucciones
                        System.out.println("Proceso completado");
                        this.executedSuccessfully = false;
                        break;
                        // El SO debera llevarlo a finalizado
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    keepRunning = false;
                }
            }
        }
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

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

    public String getPName() {
        return name;
    }

    public void setPName(String name) {
        this.name = name;
    }

    public int getTotalInstructions() {
        return totalInstructions;
    }

    public void setTotalInstructions(int totalInstructions) {
        this.totalInstructions = totalInstructions;
    }

    public int getExecutedInstructions() {
        return executedInstructions;
    }

    public int getRemainingInstructions() {
        return remainingInstructions;
    }

    public void setRemainingInstructions(int remainingInstructions) {
        this.remainingInstructions = remainingInstructions;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }

    public ProcessState getPState() {
        return state;
    }

    public void setPState(ProcessState state) {
        this.state = state;
    }

    public int getCyclesToGenerateException() {
        return cyclesToGenerateException;
    }

    public void setCyclesToGenerateException(int cyclesToGenerateException) {
        this.cyclesToGenerateException = cyclesToGenerateException;
    }

    public int getCyclesToManageException() {
        return cyclesToManageException;
    }

    public void setCyclesToManageException(int cyclesToManageException) {
        this.cyclesToManageException = cyclesToManageException;
    }

    public boolean isExceptionManaged() {
        return ExceptionManaged;
    }

    public void setExceptionManaged(boolean ExceptionManaged) {
        this.ExceptionManaged = ExceptionManaged;
    }

    public int getBaseDirection() {
        return baseDirection;
    }

    public void setBaseDirection(int baseDirection) {
        this.baseDirection = baseDirection;
    }

    public int getLimitDirection() {
        return this.getBaseDirection() + this.getTotalInstructions();
    }

    public int getPPriority() {
        return priority;
    }

    public void setPPriority(int priority) {
        this.priority = priority;
    }

    public int getCyclesWaitingCPU() {
        return cyclesWaitingCPU;
    }

    public void setCyclesWaitingCPU(int cyclesWaitingCPU) {
        this.cyclesWaitingCPU = cyclesWaitingCPU;
    }

    public double getResponseRate() {
        return responseRate;
    }

    public void setResponseRate(double responseRate) {
        this.responseRate = responseRate;
    }

    public boolean isExecutedSuccessfully() {
        return executedSuccessfully;
    }

    public void setExecutedSuccessfully(boolean executedSuccessfully) {
        this.executedSuccessfully = executedSuccessfully;
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public boolean equals(int pid) {
        return this.PID == pid;
    }

    public String toString() {
        // Calcular el Response Rate solo para la impresión si aún no se ha hecho
        double currentRR = this.responseRate;
        if (this.remainingInstructions > 0) {
            currentRR = 1.0 + ((double) this.cyclesWaitingCPU / (double) this.remainingInstructions);
        }

        return String.format(
                "P%s | PID=%d | I_Total=%d | I_Restantes=%d | Prio=%d | Wait=%d | RR=%.2f",
                name, PID, totalInstructions, remainingInstructions, priority, cyclesWaitingCPU, currentRR
        );
    }

    public int getArriveCycle() {
        return arriveCycle;
    }

    public void setArriveCycle(int arriveCycle) {
        this.arriveCycle = arriveCycle;
    }

    public int getFinishCycle() {
        return finishCycle;
    }

    public void setFinishCycle(int finishCycle) {
        this.finishCycle = finishCycle;
    }

    public double calculateSlowdown() {
        long totalTime = this.getFinishCycle() - this.getArriveCycle();
        int serviceTime = this.getTotalInstructions();

        return (double) totalTime / serviceTime;

    }

}
