/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package _01_ApplicationPackage;

import _03_LowLevelAbstractions.CPU;
import _03_LowLevelAbstractions.DMA;
import _03_LowLevelAbstractions.MainMemory;
import _03_LowLevelAbstractions.RealTimeClock;
import _04_OperatingSystem.Process;
import _04_OperatingSystem.ProcessType;

/**
 *
 * @author DiegoM
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        
        //Prueba de CPU, proceso y MP
        DMA dma = new DMA();
        CPU cpu = new CPU(dma);
        MainMemory memory = new MainMemory();
        RealTimeClock clock = new RealTimeClock(cpu, dma, 1000); 
        final int QUANTUM = -1;

        // --- 2. Simulación del Planificador a Largo Plazo: Admisión ---
        
        System.out.println("--- FASE 1: ADMISIÓN Y ASIGNACIÓN DE MEMORIA (Scheduler de Largo Plazo) ---\n");
        
        // P1: CPU-Bound (total 10 instr., Size=10)
        final int P1_SIZE = 10;
        int p1_base = memory.isSpaceAvailable(P1_SIZE);
        Process p1 = null;

        if (p1_base != -1) {
            p1 = new Process("P1_CPU", P1_SIZE, ProcessType.CPU_BOUND, -1, 0, p1_base);
            memory.allocate(p1, p1_base, P1_SIZE);
        } else {
            System.err.println("Error: No hay memoria para P1.");
        }
        
        // P2: I/O-Bound (total 8 instr., Size=8, I/O en ciclo 5)
        final int P2_SIZE = 8;
        int p2_base = memory.isSpaceAvailable(P2_SIZE);
        Process p2 = null;
        
        if (p2_base != -1) {
            p2 = new Process("P2_IO", P2_SIZE, ProcessType.IO_BOUND, 5, 2, p2_base);
            memory.allocate(p2, p2_base, P2_SIZE);
        } else {
            System.err.println("Error: No hay memoria para P2.");
        }
        
        if (p1 == null || p2 == null) {
            System.err.println("Terminando simulación por falta de memoria inicial.");
            return;
        }
        
        clock.start();
        cpu.start();
        dma.start();

        System.out.println("\n--- FASE 2: EJECUCIÓN (Round Robin Quantum=" + QUANTUM + ") ---\n");

        // --- Ciclo 1: Despacho P1 ---
        System.out.println(">> SO: Despachando P1 (Quantum " + QUANTUM + ")");
        cpu.setCurrentProcess(p1, QUANTUM);
        
        // P1 correrá por y será desalojado por quantum.
        Thread.sleep(5000); 
        
        // --- Ciclo 2: Despacho P2 ---
        System.out.println("\n>> SO: Despachando P2 (Quantum " + QUANTUM + ")");
        cpu.setCurrentProcess(p2, QUANTUM);

        // P2 correrá 5 ciclos y será desalojado en el ciclo 5 por I/O.
        Thread.sleep(8000); // Esperar 8 ticks (8 * 250ms)

        // --- Ciclo 3: Despacho P1 (continúa) ---
        // P1 le quedan 7 instr. (10 - 3 ejecutadas). Se le da un nuevo quantum.
        System.out.println("\n>> SO: Despachando P1 nuevamente (continúa).");
        cpu.setCurrentProcess(p1, QUANTUM);
        
        // P1 corre 3 ciclos más (PC=6) y es desalojado por quantum.
        Thread.sleep(3000); 

        // --- Ciclo 4: Despacho P1 (continúa hasta terminar) ---
        // P1 le quedan 4 instr. (10 - 6 ejecutadas). Se le da un nuevo quantum.
        System.out.println("\n>> SO: Despachando P1 (termina en este quantum).");
        cpu.setCurrentProcess(p1, QUANTUM);
        
        // P1 corre 4 ciclos más, finaliza en ciclo 4 de su quantum.
        Thread.sleep(4000); 

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
    
}
