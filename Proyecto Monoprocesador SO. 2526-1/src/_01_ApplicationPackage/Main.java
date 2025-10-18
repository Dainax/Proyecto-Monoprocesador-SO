/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package _01_ApplicationPackage;

import _03_LowLevelAbstractions.CPU;
import _03_LowLevelAbstractions.RealTimeClock;

/**
 *
 * @author DiegoM
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        //        Con esto probe el reloj, correlo tambien 
    
        CPU cpu = new CPU();
        //cpu.setRemainingCycles(4); // Prueba de timeout
        
        RealTimeClock reloj = new RealTimeClock(cpu, 1000); 
        
        cpu.start();
        reloj.start();
        
        // Dani se espera para poder verlo en la terminal principal 
        Thread.sleep(5000); 

        // Cambio de velocidad 
        reloj.setClockDuration(5000); // 5 veces más rápido (100ms)
        System.out.println("\nMAIN Cambio de velocidad");
        
        Thread.sleep(2000); 

        //Detener el reloj y luego la CPU
        //System.out.println("\nMAIN Deteniendo el sistema...");
        //reloj.stopReloj();
        //cpu.stopCPU();

        // Para esperar a que ambos hilos terminen
        reloj.join();
        cpu.join();

        System.out.println("\n--- PRUEBA FINALIZADA ---");
        System.out.println("Instrucciones ejecutadas por la CPU (Ciclos internos): " + cpu.getCycleCounter());
        System.out.println("Ciclos totales reportados por el Reloj: " + reloj.getTotalCyclesElapsed());

    }
    
}
