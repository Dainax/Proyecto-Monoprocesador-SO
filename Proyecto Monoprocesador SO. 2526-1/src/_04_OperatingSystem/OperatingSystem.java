/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _04_OperatingSystem;

import _02_DataStructures.SimpleList;
import _03_LowLevelAbstractions.CPU;
import _03_LowLevelAbstractions.DMA;
import _03_LowLevelAbstractions.MainMemory;
import _04_OperatingSystem.ProcessState;

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

    // Colas del sistema
    private SimpleList<Process> readyQueue;
    private SimpleList<Process> blockedQueue;

    // Para acceder a la cola de nuevos y a las de suspendidos
    // El SO accede al DMA, para simular que estas estan en almacenamiento 
    // a largo plazo
    //Funcion por cada politica 
    //          Funcion crear proceso SO.newProcess
    // Los datos van a estar validados 
    /**
     * Reviso MP. Si hay espacio guardo el proceso con su dirección base, si no
     * se coloca la direccion base en -1 y se manda a la lista de nuevos y se
     * actualizan las listas
     */
    /**
     * Lo agrego a la lista de procesos
     */
    /**
     * Llamo al planificador dependiendo de la politica que este para
     * seleccionar el proceso siguiente
     */
    //          Funcion gestionar finalización E/S
    // Finalizado una operacion E/S del dma
    /**
     * SO debe actualizar el estado del proceso Moverlo a la cola de listo
     * Llamar al planificador
     */
    //          Terminacion de un proceso
    //          Bloqueo de un proceso
//          --------------- Metodos ---------------
    public OperatingSystem(PolicyType currentPolicy) {
        this.cpu = new CPU(this);
        this.dma = new DMA();
        this.mp = new MainMemory(this);
        this.scheduler = new Scheduler(this, currentPolicy);
        this.readyQueue = new SimpleList<Process>();
        this.blockedQueue = new SimpleList<Process>();
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
            //Agregar a la cola de listos 
            this.readyQueue.insertLast(newProcess);
            
            // Coloco el proceso en listo
            newProcess.setState(ProcessState.READY);
            
            // Asignar el espacio en la memoria principal (Actualiza el array memorySlots)
            this.mp.allocate(baseDirection, totalInstructions);
            
            // Notificar al planificador
            this.scheduler.setOrdered(false);
            System.out.println("Proceso " + name + " admitido en la Memoria Principal. Enviando a cola de listos");
        }
    }

    /**
     * Finalizar un proceso Manejar operacion I/O (Set al DMA) Completar
     * operacion I/O (Terminada la ejecución DMA)
     */
    // Funcion para ver si una politica es expulsiva ()
    // Expulsivas: Prioridades por evaluar procesos desbloqueados, RR por quantum, SRT por evaluar procesos desbloqueados
    // No expulsivas: FIFO, SPN HRRN
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

    public void notifyNewProcessArrival(Scheduler scheduler) {
        scheduler.setOrdered(false);
    }

}
