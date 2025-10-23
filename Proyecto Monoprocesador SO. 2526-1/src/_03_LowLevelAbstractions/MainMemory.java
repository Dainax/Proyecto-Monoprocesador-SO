/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _03_LowLevelAbstractions;

import _02_DataStructures.SimpleNode;
import _02_DataStructures.SimpleList;
import _04_OperatingSystem.OperatingSystem;
import _04_OperatingSystem.Process;

/**
 *
 * @author AresR
 */
public class MainMemory {

    private final int MEMORY_SIZE = 128; // 128 para almacenar mas, 20 o 64 para probar suspendidos
    private boolean[] memorySlots; // true = ocupado, false = libre
    
    // Para poder referenciar los metodos del SO
    private final OperatingSystem osReference;

    public MainMemory(OperatingSystem osReference) {
        this.memorySlots = new boolean[MEMORY_SIZE];
        this.osReference = osReference;
        
    }

    /**
     * Verifica si hay espacio libre contiguo para un proceso nuevo. Devuelve la base
     * libre o -1 si no hay espacio suficiente
     *
     * @param size Tamaño continuo a reviar
     * @return Indice de la base si consigue espacio, -1 si no lo consigue
     */
    public int isSpaceAvailable(int size) {
        for (int base = 0; base <= MEMORY_SIZE - size; base++) {
            int newLimit = base + size - 1;
            boolean available = true;
            if (size > MEMORY_SIZE){
                    return -1;
                }
            for (SimpleNode<Process> node = this.osReference.getReadyQueue().GetpFirst(); node != null; node = node.GetNxt()) {
                Process p = node.GetData();
                int pBase = p.getBaseDirection();
                int pLimit = p.getLimitDirection();
                if (!(newLimit < pBase || base > pLimit)) {
                    available = false;
                    break;
                }      
            }
            if (available) {
                return base; // Espacio encontrado
            }
        }
        return -1; // No se encontró espacio suficiente
    }

    /**
     * Marca espacio ocupado para un proceso (base-límite)
     *
     * @param process
     * @param base
     * @param size
     * @return true si completa exitosamente, false si no
     */
    public boolean allocate(int base, int size) {
        if (base + size > MEMORY_SIZE) {
            return false;
        }
        for (int i = base; i < base + size; i++) {
            if (memorySlots[i]) {
                return false; // ya ocupado
            }
        }
        for (int i = base; i < base + size; i++) {
            memorySlots[i] = true;
        }
        return true;
    }

    /**
     * Libera espacio de un proceso indicando su base y tamaño
     *
     * @param base
     * @param size
     */
    public void freeSpace(int base, int size) {
        for (int i = base; i < base + size; i++) {
            memorySlots[i] = false;
        }
    }
}

