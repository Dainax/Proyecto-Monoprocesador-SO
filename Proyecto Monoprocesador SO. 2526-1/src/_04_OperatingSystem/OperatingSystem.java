/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _04_OperatingSystem;

import _02_DataStructures.SimpleList;

/**
 *
 * @author AresR
 */
public class OperatingSystem {
    
    /**
     * El SO usara un hilo
     * 
     * Atributos:
     * Cola de Listo
     * Cola de bloqueados 
     * Cola de listos suspendidos
     * Cola de bloqueado suspendidos
     * Cola de terminados
     * Cola de nuevos 
     * 
     * Tabla de procesos (lista de procesos, el bit de presencia es el registro base del proceso) 
     * 
     * 
     * Modulo de planificación
     * 
     * Metodos:
     * Crear proceso
     * Finalizar un proceso
     * Manejar operacion I/O (Set al DMA)
     * Completar operacion I/O (Terminada la ejecución DMA)
    */
    
    private SimpleList<Process> readyQueue;
   
    //Funcion por cada politica 
    
    //          Funcion crear proceso SO.newProcess
    
    // Los datos van a estar validados 
    
    /**
     * Reviso MP. Si hay espacio guardo el proceso con su dirección base, 
     * si no se coloca la direccion base en -1 y se manda a la lista de nuevos y se actualizan las listas
    */
    
    /**
     * Lo agrego a la lista de procesos
    */
    
    /**
     * Llamo al planificador dependiendo de la politica que este
     * para seleccionar el proceso siguiente
    */
    
    
    //          Funcion gestionar finalización E/S
    
    // Finalizado una operacion E/S del dma
    
    /** 
     * SO debe actualizar el estado del proceso
     * Moverlo a la cola de listo
     * Llamar al planificador 
     */
    
    //          Terminacion de un proceso
    
    /**
     * Cambia el estado
     * Actualiza las listas
     * Hace calculos estadisticos
     */
    
    //          Bloqueo de un proceso

    
    // ----- Metodos -----
    public OperatingSystem(SimpleList<Process> readyQueue) {
        this.readyQueue = readyQueue;
    }

    // Funcion para ver si una politica es expulsiva ()
    // Expulsivas: Prioridades por evaluar procesos desbloqueados, RR por quantum, SRT por evaluar procesos desbloqueados
    // No expulsivas: FIFO, SPN HRRN
    // Getters y Setters
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

