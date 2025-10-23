/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package _01_ApplicationPackage;

import _02_DataStructures.SimpleList;
import _02_DataStructures.SimpleNode;
import _04_OperatingSystem.OperatingSystem;
import _04_OperatingSystem.Process;
import _04_OperatingSystem.PolicyType;
import _04_OperatingSystem.ProcessType;

/**
 *
 * @author DiegoM
 */
public class Main {
    
    public static void printReadyQueue(SimpleList<Process> queue, String title) {
        System.out.println("\n" + title);
        System.out.println("+-----+--------------+---------+---------+-------+--------+----------+");
        System.out.println("| PID | Nombre   | Total   | Remaining |   Priority    | Wait   | HRRN-R"
                );
        System.out.println("+-----+--------------+---------+---------+-------+--------+----------+");
        
        SimpleNode<Process> current = queue.GetpFirst();
        while (current != null) {
            System.out.println(current.GetData().toString());
            current = current.GetNxt();
        }
        System.out.println("+-----+--------------+---------+---------+-------+--------+----------+");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        OperatingSystem os = new OperatingSystem(PolicyType.FIFO);

        System.out.println("===========================================================================================");
        System.out.println("=== FASE 1: ADMISIÓN DE PROCESOS USANDO SO.newProcess() ===");
        System.out.println("===========================================================================================");

        
        // P(name, totalInstructions, type, cyclesToGenInterruption, cyclesToManInterruption)
        os.newProcess("P1_HPrio", 10, ProcessType.CPU_BOUND, -1, -1);
        Process p1 = (Process) os.getReadyQueue().GetValInIndex(0).GetData();     // Fija la prioridad a 1 (más alta)
        p1.setPPriority(1);   
        
        os.newProcess("P2_IO_Long", 15, ProcessType.IO_BOUND, 5, 2);
        Process p2 = (Process) os.getReadyQueue().GetValInIndex(1).GetData();
        p2.setPPriority(5);
        
        os.newProcess("P3_Shortest", 8, ProcessType.CPU_BOUND, -1, -1);
        Process p3 = (Process) os.getReadyQueue().GetValInIndex(2).GetData();
        p3.setPPriority(3);
        
        os.newProcess("P4_MidPrio", 12, ProcessType.CPU_BOUND, -1, -1);
        Process p4 = (Process) os.getReadyQueue().GetValInIndex(3).GetData();
        p4.setPPriority(3);
        
        os.newProcess("P5_Longest", 20, ProcessType.CPU_BOUND, -1, -1);
        Process p5 = (Process) os.getReadyQueue().GetValInIndex(4).GetData();
        p5.setPPriority(4);
        
        os.newProcess("P6_HPrio", 10, ProcessType.CPU_BOUND, -1, -1);
        Process p6 = (Process) os.getReadyQueue().GetValInIndex(0).GetData();     // Fija la prioridad a 1 (más alta)
        p6.setPPriority(1);
                
        
        os.newProcess("P7_IO_Long", 15, ProcessType.IO_BOUND, 5, 2);
        Process p7 = (Process) os.getReadyQueue().GetValInIndex(1).GetData();
        p7.setPPriority(5);
        
        os.newProcess("P8_Shortest", 8, ProcessType.CPU_BOUND, -1, -1);
        Process p8 = (Process) os.getReadyQueue().GetValInIndex(2).GetData();
        p8.setPPriority(3);
        
        os.newProcess("P9_MidPrio", 12, ProcessType.CPU_BOUND, -1, -1);
        Process p9 = (Process) os.getReadyQueue().GetValInIndex(3).GetData();
        p9.setPPriority(3);
        
        os.newProcess("P10_Longest", 20, ProcessType.CPU_BOUND, -1, -1);
        //Process p10 = (Process) os.getReadyQueue().GetValInIndex(4).GetData();
        //p10.setPPriority(4);
        
        System.out.println("\n\n=======================================================");
        System.out.println("=== FASE 2: PRUEBA DE ORDENAMIENTO POR 6 POLÍTICAS ===");
        System.out.println("=======================================================");

        PolicyType[] policiesToTest = {
            PolicyType.FIFO, 
            PolicyType.ROUND_ROBIN, 
            PolicyType.SPN, 
            PolicyType.SRT, 
            PolicyType.Priority, 
            PolicyType.HRRN
        };

        for (PolicyType policy : policiesToTest) {
            os.getScheduler().setCurrentPolicy(policy);
            Process nextProcess = os.getScheduler().selectNextProcess(); 
            
            printReadyQueue(os.getReadyQueue(), "COLA DE LISTOS ORDENADA POR: " + policy);
            
            if (nextProcess != null) {
                System.out.println(">>> PROCESO SELECCIONADO: " + nextProcess.getPName() + " (PID " + nextProcess.getPID() + ")");
            }
            System.out.println("===========================================================================================");
        }
        
    }


///**
//     * Imprime el contenido de la cola de listos en formato tabular.
//     */
//    public static void printReadyQueue(SimpleList<Process> queue, String title) {
//        System.out.println("\n" + title);
//        System.out.println("+-----+--------------+---------+---------+-------+--------+----------+");
//        System.out.println("| PID | Nombre   | Total   | Remaining |   Priority    | Wait   | HRRN-R"
//                );
//        System.out.println("+-----+--------------+---------+---------+-------+--------+----------+");
//        
//        SimpleNode<Process> current = queue.GetpFirst();
//        while (current != null) {
//            System.out.println(current.GetData().toString());
//            current = current.GetNxt();
//        }
//        System.out.println("+-----+--------------+---------+---------+-------+--------+----------+");
//    }
    
    
// 1. Inicialización de Memoria y Colas
//        MainMemory memory = new MainMemory();
//        SimpleList<Process> readyQueue = new SimpleList<>();
//        
//        System.out.println("===========================================================================================");
//        System.out.println("=== FASE 1: ADMISIÓN Y ASIGNACIÓN DE MEMORIA (Simulación de Scheduler de Largo Plazo) ===");
//        System.out.println("===========================================================================================");
//
//        // --- Creación de Procesos (usando el constructor CORREGIDO) ---
//        // P(Name, Total Inst, Type, Cycles to Gen Exc, Cycles to Man Exc, Base Dir)
//
//        // P1: CPU-Bound, Alta Prioridad (Prio: 1), Corto, no necesita I/O.
//        final int P1_SIZE = 10;
//        int p1_base = memory.isSpaceAvailable(P1_SIZE);
//        Process p1 = new Process("P1_HPrio", P1_SIZE, ProcessType.CPU_BOUND, -1, -1, p1_base);
//        memory.allocate(p1, p1_base, P1_SIZE);
//        p1.setPPriority(1);     // Fija la prioridad a 1 (más alta)
//        p1.setRemainingInstructions(15);
//        System.out.println("Proceso " + p1.getPID() + " admitido en Base: " + p1_base);
//        
//        // P2: I/O-Bound, Larga ejecución, alta espera. (Simulando un proceso reanudado)
//        final int P2_SIZE = 15;
//        int p2_base = memory.isSpaceAvailable(P2_SIZE);
//        Process p2 = new Process("P2_IO_Long", P2_SIZE, ProcessType.IO_BOUND, 5, 2, p2_base);
//        memory.allocate(p2, p2_base, P2_SIZE);
//        p2.setRemainingInstructions(30); // Ha ejecutado 10 ciclos
//        p2.setCyclesWaitingCPU(10);     // Lleva 10 ciclos esperando (infla el HRRN)
//        p2.setPPriority(5);
//        System.out.println("Proceso " + p2.getPID() + " admitido en Base: " + p2_base);
//        
//        // P3: CPU-Bound, Muy corto.
//        final int P3_SIZE = 8;
//        int p3_base = memory.isSpaceAvailable(P3_SIZE);
//        Process p3 = new Process("P3_Shortest", P3_SIZE, ProcessType.CPU_BOUND, -1, -1, p3_base);
//        memory.allocate(p3, p3_base, P3_SIZE);
//        p3.setRemainingInstructions(5);
//        p3.setPPriority(3);
//        System.out.println("Proceso " + p3.getPID() + " admitido en Base: " + p3_base);
//
//        // P4: CPU-Bound, Prioridad media.
//        final int P4_SIZE = 12;
//        int p4_base = memory.isSpaceAvailable(P4_SIZE);
//        Process p4 = new Process("P4_MidPrio", P4_SIZE, ProcessType.CPU_BOUND, -1, -1, p4_base);
//        memory.allocate(p4, p4_base, P4_SIZE);
//        p4.setRemainingInstructions(18);
//        p4.setCyclesWaitingCPU(5);
//        p4.setPPriority(2);
//        System.out.println("Proceso " + p4.getPID() + " admitido en Base: " + p4_base);
//        
//        // P5: CPU-Bound, Más largo (total).
//        final int P5_SIZE = 20;
//        int p5_base = memory.isSpaceAvailable(P5_SIZE);
//        Process p5 = new Process("P5_Longest", P5_SIZE, ProcessType.CPU_BOUND, -1, -1, p5_base);
//        memory.allocate(p5, p5_base, P5_SIZE);
//        p5.setRemainingInstructions(50);
//        p5.setPPriority(4);
//        System.out.println("Proceso " + p5.getPID() + " admitido en Base: " + p5_base);
//        
//        // Inserción en orden de llegada (PID)
//        readyQueue.insertLast(p1);
//        readyQueue.insertLast(p2);
//        readyQueue.insertLast(p3);
//        readyQueue.insertLast(p4);
//        readyQueue.insertLast(p5);
//        
//        // 2. Simulación del Planificador de Corto Plazo
//        OperatingSystem mockOS = new OperatingSystem(readyQueue);
//        Scheduler scheduler = new Scheduler(mockOS, PolicyType.FIFO); 
//
//        System.out.println("\n\n=======================================================");
//        System.out.println("=== FASE 2: PRUEBA DE ORDENAMIENTO POR 6 POLÍTICAS ===");
//        System.out.println("=======================================================");
//
//        PolicyType[] policiesToTest = {
//            PolicyType.FIFO, 
//            PolicyType.ROUND_ROBIN, 
//            PolicyType.SPN, 
//            PolicyType.SRT, 
//            PolicyType.Priority, 
//            PolicyType.HRRN
//        };
//
//        for (PolicyType policy : policiesToTest) {
//            scheduler.setCurrentPolicy(policy);
//            Process nextProcess = scheduler.selectNextProcess(); 
//            
//            printReadyQueue(readyQueue, "COLA DE LISTOS ORDENADA POR: " + policy);
//            
//            if (nextProcess != null) {
//                System.out.println(">>> PROCESO SELECCIONADO: " + nextProcess.getPName() + " (PID " + nextProcess.getPID() + ")");
//            }
//            System.out.println("===========================================================================================");
//        }
//    
        
//        //Prueba de CPU, proceso y MP
//        DMA dma = new DMA();
//        CPU cpu = new CPU(dma);
//        MainMemory memory = new MainMemory();
//        RealTimeClock clock = new RealTimeClock(cpu, dma, 1000); 
//        final int QUANTUM = -1;
//
//        // Simulación del Planificador a Largo Plazo: Admisión ---
//        
//        System.out.println("--- FASE 1: ADMISIÓN Y ASIGNACIÓN DE MEMORIA (Scheduler de Largo Plazo) ---\n");
//        
//        // P1: CPU-Bound (total 10 instr., Size=10)
//        final int P1_SIZE = 28;
//        int p1_base = memory.isSpaceAvailable(P1_SIZE);
//        Process p1 = null;
//
//        if (p1_base != -1) {
//            p1 = new Process("P1_CPU", P1_SIZE, ProcessType.CPU_BOUND, -1, 0, p1_base);
//            memory.allocate(p1, p1_base, P1_SIZE);
//        } else {
//            System.err.println("Error: No hay memoria para P1.");
//        }
//        
//        // P2: I/O-Bound (total 8 instr., Size=8, I/O en ciclo 5)
//        final int P2_SIZE = 101;
//        int p2_base = memory.isSpaceAvailable(P2_SIZE);
//        Process p2 = null;
//        System.out.println(p1.getBaseDirection());
//        
//        if (p2_base != -1) {
//            p2 = new Process("P2_IO", P2_SIZE, ProcessType.IO_BOUND, 5, 2, p2_base);
//            memory.allocate(p2, p2_base, P2_SIZE);
//        } else {
//            System.err.println("Error: No hay memoria para P2.");
//        }
//        
//        if (p1 == null || p2 == null) {
//            System.err.println("Terminando simulación por falta de memoria inicial.");
//            return;
//        }
//        
//        clock.start();
//        cpu.start();
//        dma.start();
//
//        System.out.println("\n--- FASE 2: EJECUCIÓN (Round Robin Quantum=" + QUANTUM + ") ---\n");
//
//        // --- Ciclo 1: Despacho P1 ---
//        System.out.println(">> SO: Despachando P1 (Quantum " + QUANTUM + ")");
//        cpu.setCurrentProcess(p1, QUANTUM);
//        
//        // P1 correrá por y será desalojado por quantum.
//        Thread.sleep(5000); 
//        
//        // --- Ciclo 2: Despacho P2 ---
//        System.out.println("\n>> SO: Despachando P2 (Quantum " + QUANTUM + ")");
//        cpu.setCurrentProcess(p2, QUANTUM);
//
//        // P2 correrá 5 ciclos y será desalojado en el ciclo 5 por I/O.
//        Thread.sleep(8000); // Esperar 8 ticks (8 * 250ms)
//
//        // --- Ciclo 3: Despacho P1 (continúa) ---
//        // P1 le quedan 7 instr. (10 - 3 ejecutadas). Se le da un nuevo quantum.
//        System.out.println("\n>> SO: Despachando P1 nuevamente (continúa).");
//        cpu.setCurrentProcess(p1, QUANTUM);
//        
//        // P1 corre 3 ciclos más (PC=6) y es desalojado por quantum.
//        Thread.sleep(3000); 
//
//        // --- Ciclo 4: Despacho P1 (continúa hasta terminar) ---
//        // P1 le quedan 4 instr. (10 - 6 ejecutadas). Se le da un nuevo quantum.
//        System.out.println("\n>> SO: Despachando P1 (termina en este quantum).");
//        cpu.setCurrentProcess(p1, QUANTUM);
//        
//        // P1 corre 4 ciclos más, finaliza en ciclo 4 de su quantum.
//        Thread.sleep(4000); 

        // --- 5. Detener Simulación ---
//        System.out.println("\n--- DETENIENDO SIMULACIÓN ---\n");
//        clock.stopReloj();
//        cpu.stopCPU();
//        p1.interrupt();
//        p2.interrupt();
        
        
        
        //        Con esto probe el reloj, correlo tambien 
//        CPU cpu = new CPU();
//        RealTimeClock reloj = new RealTimeClock(cpu, 1000); 
//        MainMemory memory = new MainMemory();
//        //cpu.setRemainingCycles(4); // Prueba de timeout
//        int P1_SIZE = 10;
//        int p1_base = 10;
//        Process p1 = new Process("P1_CPU", P1_SIZE, ProcessType.CPU_BOUND, 5, 0, p1_base);
//        System.out.println(p1.getState());
//        cpu.setCurrentProcess(p1, -1);
//        cpu.start();
//        reloj.start();
//        
//        // Dani se espera para poder verlo en la terminal principal 
//        Thread.sleep(5000); 
//
//        // Cambio de velocidad 
//        reloj.setClockDuration(5000); // 5 veces más rápido (100ms)
//        System.out.println("\nMAIN Cambio de velocidad");
//        
//        Thread.sleep(2000); 
//
//        //Detener el reloj y luego la CPU
//        //System.out.println("\nMAIN Deteniendo el sistema...");
//        //reloj.stopReloj();
//        //cpu.stopCPU();
//
//        // Para esperar a que ambos hilos terminen
//        reloj.join();
//        cpu.join();
//
//        System.out.println("\n--- PRUEBA FINALIZADA ---");
//        System.out.println("Instrucciones ejecutadas por la CPU (Ciclos internos): " + cpu.getCycleCounter());
//        System.out.println("Ciclos totales reportados por el Reloj: " + reloj.getTotalCyclesElapsed());

    
    
}
