/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _04_OperatingSystem;

import _02_DataStructures.SimpleList;
import _02_DataStructures.SimpleNode;
import _04_OperatingSystem.Process1;

/**
 *
 * @author AresR
 */
public class Scheduler {

    //          ----- Atributos -----
    private PolicyType currentPolicy;
    private final OperatingSystem osReference;
    // Indica si la cola de listos ya está ordenada
    private boolean isOrdered;

    // Para RR
    private final int quantum = 5;

    //          --------------- Metodos ---------------
    /**
     * Constructor
     *
     * @param osReference Referencia al objeto SO para acceder a sus atributos
     * @param currentPolicy Politica escogida para iniciar el sistema
     */
    public Scheduler(OperatingSystem osReference, PolicyType currentPolicy) {
        this.osReference = osReference;
        this.currentPolicy = currentPolicy;
        this.isOrdered = false;
    }

    // ---------- Planificacion a corto plazo ----------
    /**
     * Seleccionar nuevo proceso de la cola de listos para ponerlo en ejecución
     *
     * @return proceso a darle el control del CPU
     */
    public Process1 selectNextProcess() {
        // Obtener la Cola de Listos del SO
        SimpleList<Process1> readyProcesses = osReference.getReadyQueue(); // Asume que el OS tiene este getter

        if (readyProcesses == null || readyProcesses.isEmpty()) {
            return null;
        }

        // Si la lista no está ordenada, la ordenamos 
        if (!this.isOrdered) {
            sortReadyQueue();
            this.isOrdered = true;
        }

        // Tomo el proceso. La cola debe estar ordenada segun el algoritmo de ordenamiento
        Process1 nextProcess = (Process1) readyProcesses.GetpFirst().GetData();

        // Cambio su estado y lo elimino de la cola
        nextProcess.setPState(ProcessState.RUNNING);
        this.osReference.getReadyQueue().delNodewithVal(nextProcess);

        System.out.println("Seleccionado el proceso " + nextProcess.getPID() + " para ejecutarse");
        return nextProcess;
    }

    /**
     * Metodo para ordenar la cola que llama al metodo de la politica segun sea
     * el caso
     */
    public void sortReadyQueue() {
        SimpleList<Process1> readyProcesses = osReference.getReadyQueue();
        if (readyProcesses.GetSize() <= 1) {
            this.isOrdered = true;
            return;
        }

        switch (currentPolicy) {
            case Priority:
                sortPriority();
                break;

            case FIFO:
                sortFIFO();
                break;

            case ROUND_ROBIN:
                sortRoundRobin();
                break;

            case SPN:
                sortSPN();
                break;

            case SRT:
                sortSRT();
                break;

            case HRRN:
                sortHRRN();
                break;
        }
        System.out.println("Cola de listos reordenada con política " + currentPolicy);
    }

    private void sortPriority() {
        // Ordena por prioridad siendo la mas importante la menor (1)
        ordenateWithBubbleSort(PolicyType.Priority);
    }

    private void sortFIFO() {
        // Toma en cuenta el PID del proceso ya que este es unico 
        // y esta en orden de creacion
        ordenateWithBubbleSort(PolicyType.FIFO);
    }

    private void sortRoundRobin() {
        // Ordena por PID tambien ya que no es RR Virtual, es decir solo se le da un quantum a cada proceso
        // siguiendo cualquier orden mientras se le de el mismo tiempo a cada proceso
        ordenateWithBubbleSort(PolicyType.ROUND_ROBIN);
    }

    private void sortSPN() {
        // SPN shortest process next se usara el mas corto, es decir que tenga 
        // el menor totalInstruction
        ordenateWithBubbleSort(PolicyType.SPN);
    }

    private void sortSRT() {
        // Para SRT shortest remaining time se usara el que tenga el menor 
        // remainingInstruction
        ordenateWithBubbleSort(PolicyType.SRT);
    }

    private void sortHRRN() {
        ordenateWithBubbleSort(PolicyType.HRRN);
    }

    /**
     * Metodo que centraliza el ordenar la lista
     */
    private void ordenateWithBubbleSort(PolicyType policy) {
        // Obtengo la lista del SO
        SimpleList<Process1> readyProcesses = osReference.getReadyQueue();
        int size = readyProcesses.GetSize();

        // Hago un arreglo temporal
        Process1[] processArray = new Process1[size];

        SimpleNode<Process1> current = readyProcesses.GetpFirst();
        int index = 0;
        while (current != null) {
            processArray[index] = current.GetData();
            current = current.GetNxt();
            index++;
        }

        // Ordeno el arreglo temporal
        bubbleSort(processArray, policy);

        // Vuelvo a llenar la cola de listos
        readyProcesses.SetpFirst(null);
        readyProcesses.SetpLast(null);
        for (Process1 p : processArray) {
            readyProcesses.insertLast(p);
        }
    }

    /**
     * Ordena un array de procesos usando Bubble Sort usando dos metodos
     */
    private void bubbleSort(Process1[] processes, PolicyType policy) {
        int n = processes.length;
        // Pasadas a todo el arreglo
        for (int i = 0; i < n - 1; i++) {
            // Iteracion por cada elemento 
            for (int j = 0; j < n - i - 1; j++) {
                Process1 a = processes[j];
                Process1 b = processes[j + 1];

                // Si b es mejor que a (es decir, a no está en el orden correcto respecto a b) 
                // los intercambiamos.
                if (!isABetterThanB(a, b, policy)) {
                    Process1 temp = a;
                    processes[j] = b;
                    processes[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Determina si el Proceso 'a' debe ir antes que el Proceso 'b' para
     * organizar la lista
     */
    private boolean isABetterThanB(Process1 a, Process1 b, PolicyType policy) {
        double valA = getComparisonValue(a, policy);
        double valB = getComparisonValue(b, policy);

        if (policy == PolicyType.HRRN) {
            if (valA != valB) { // HRRN: Buscamos el valor maximo.
                return valA > valB;
            }
        } else {
            if (valA != valB) { // Priority, SPN, SRT, FIFO, RR: Buscamos el valor MÍNIMO.
                return valA < valB;
            }
        }
        return a.getPID() < b.getPID(); // Desempate por PID si tienen el mismo valor
    }

    /**
     * Devuelve el valor numérico clave para la comparación según la política.
     */
    private double getComparisonValue(Process1 p, PolicyType policy) {
        switch (policy) {
            case Priority:
                return p.getPPriority();
            case SPN:
                return p.getTotalInstructions();
            case SRT:
                return p.getRemainingInstructions();
            case FIFO:
            case ROUND_ROBIN:
                return p.getPID();
            case HRRN:
                return p.getResponseRate();
            default:
                return Double.MAX_VALUE;
        }
    }

    /**
     * Actualiza el tiempo de espera y recalcula la Tasa de Respuesta (HRRN).
     */
    public void updateHRRNMetrics(SimpleList<Process1> list) {
        SimpleNode current = list.GetpFirst();
        while (current != null) {
            Process1 p = (Process1) current.GetData();

            // Aumentar el tiempo de espera (necesario para SRT/HRRN)
            p.setCyclesWaitingCPU(p.getCyclesWaitingCPU() + 1);

            // Calcular el Response Ratio (solo para HRRN)
            if (p.getRemainingInstructions() > 0) {
                double waitTime = (double) p.getCyclesWaitingCPU();
                double serviceTime = (double) p.getRemainingInstructions();
                double ratio = 1.0 + (waitTime / serviceTime);
                p.setResponseRate(ratio);
            }

            current = current.GetNxt();
        }
    }

    // ---------- Planificacion a mediano plazo ----------
    public Process1 selectProcessToSuspend() {

        SimpleList<Process1> blockedProcesses = this.osReference.getBlockedQueue();

        // Si hay procesos bloqueados empezamos por ellos
        if (!blockedProcesses.isEmpty()) {

            Process1 bestBlockedCandidate = null;

            int minPriorityBlocked = 1;
            long maxManageCyclesBlocked = -1; // Usamos un tipo más grande para seguridad

            // Busco el de menor prioridad y en empate mayor tiempo I/O
            for (int i = 0; i < blockedProcesses.GetSize(); i++) {
                
                // Revisar que SimpleList.GetValInIndex(i) funciona
                
                Process1 p = (Process1) blockedProcesses.GetValInIndex(i).GetData();

                if (p == null) {
                    continue;
                }

                // Suspender al bloqueado de menor prioridad (número más alto) 
                if (p.getPPriority() > minPriorityBlocked) {
                    minPriorityBlocked = p.getPPriority();
                    maxManageCyclesBlocked = p.getCyclesToManageException();
                    bestBlockedCandidate = p;
                } // y si hay empate seleccionar mayor tiempo de manejo de E/S
                else if (p.getPPriority() == minPriorityBlocked) {
                    if (p.getCyclesToManageException() > maxManageCyclesBlocked) {
                        maxManageCyclesBlocked = p.getCyclesToManageException();
                        bestBlockedCandidate = p;
                    }
                }
            }

            return bestBlockedCandidate;
        }

        SimpleList<Process1> readyProcesses = this.osReference.getReadyQueue();

        // Si no hay procesos bloqueados pero si listos de mucho menor prioridad que el primer proceso en la cola de nuevos
        // Suspender al proceso listo de menor prioridad, si hay empate suspender al mas largo (totalInstruction mayor)
        if (!readyProcesses.isEmpty() && !this.osReference.getDma().getNewProcesses().isEmpty()) {
            
            Process1 bestReadyCandidate = null;
            
            Process1 firstNew = (Process1) this.osReference.getDma().getNewProcesses().GetpFirst().GetData();
            int minPriorityReady = 1;
            int maxSizeReady = -1;

            // Condición compleja: "Listos de mucho menor prioridad que el primer proceso en la cola de nuevos"
            // Definimos "mucho menor" como una prioridad numéricamente más alta (peor)
            for (int i = 0; i < readyProcesses.GetSize(); i++) {
                Process1 p = (Process1) readyProcesses.GetValInIndex(i).GetData();

                if (p == null) {
                    continue;
                }

                // Verificar que la prioridad del proceso READY sea PEOR (mayor número) que la del primer NEW
                if (p.getPPriority() > firstNew.getPPriority()) {

                    // Suspender al proceso listo de menor prioridad (número más alto)
                    if (p.getPPriority() > minPriorityReady) { // Nota: Usamos > para encontrar el peor (mayor número)
                        minPriorityReady = p.getPPriority();
                        maxSizeReady = p.getTotalInstructions();
                        bestReadyCandidate = p;
                    } // Empate: suspender al más largo (totalInstruction mayor)
                    else if (p.getPPriority() == minPriorityReady) {
                        if (p.getTotalInstructions() > maxSizeReady) {
                            maxSizeReady = p.getTotalInstructions();
                            bestReadyCandidate = p;
                        }
                    }
                }
            }

            return bestReadyCandidate;
        }

        return null; // Si no hay procesos que cumplan los criterios de suspensión
    }

    public void manageSwapping() {

        final int multiprogramingLimit = 10;

        // Lógica de swap out 
        Process1 processToSuspend = null;

        SimpleList<Process1> readyProcesses = this.osReference.getReadyQueue();
        SimpleList<Process1> blockedProcesses = this.osReference.getBlockedQueue();

        // Reviso si la cola de listos esta vacia, si lo esta debo sacar un proceso de bloqueado a bloqueado suspendido
        if (readyProcesses.isEmpty() && !blockedProcesses.isEmpty()) {
            // Suspender el Bloqueado de menor prioridad
            processToSuspend = selectProcessToSuspend();
        } // Si la politica es RR el rendimiento se degrada al tener muchos procesos
        // Ademas si se tienen muchos procesos CPU no se esta usando el DMA
        // Por lo que si la politica es Round Robin y no se tienen procesos bloqueados pero si muchos listos se saca a un proceso que espera el CPU
        else if (this.getCurrentPolicy() == PolicyType.ROUND_ROBIN
                && blockedProcesses.isEmpty()
                && readyProcesses.GetSize() > multiprogramingLimit) {
            // Suspender el Listo de menor prioridad
            processToSuspend = selectProcessToSuspend();
        }

        // Ejecutar el SWAP OUT si se encontró un candidato
        if (processToSuspend != null) {

            // El proceso debe estar en listo o bloqueado para ser Suspendido
            if (processToSuspend.getPState() == ProcessState.BLOCKED) {
                
                blockedProcesses.delNodewithVal(processToSuspend);
                processToSuspend.setPState(ProcessState.BLOCKED_SUSPENDED);
                this.osReference.getMp().freeSpace(processToSuspend.getBaseDirection(), processToSuspend.getTotalInstructions()); //Se debe liberar la memoria del proceso
                
                this.osReference.getDma().getBlockedSuspendedProcesses().insertLast(processToSuspend);
                System.out.println("PID " + processToSuspend.getPID() + " movido a bloqueados suspendidos.");

            } else if (processToSuspend.getPState() == ProcessState.READY) {
                
                readyProcesses.delNodewithVal(processToSuspend);
                processToSuspend.setPState(ProcessState.READY_SUSPENDED);
                this.osReference.getMp().freeSpace(processToSuspend.getBaseDirection(), processToSuspend.getTotalInstructions());
                
                this.osReference.getDma().getReadySuspendedProcesses().insertLast(processToSuspend);
                System.out.println("SO (MTS): SWAP OUT. PID " + processToSuspend.getPID() + " movido a READY_SUSPENDED.");
            }
        }

        // Lógica de swap in. Prioridad: Traer procesos que terminaron su I/O y están Listos/Suspendidos
        SimpleList<Process1> readySuspendedQueue = this.osReference.getDma().getReadySuspendedProcesses();

        if (!this.osReference.getDma().getReadySuspendedProcesses().isEmpty()) {
            Process1 pToSwapIn = (Process1) readySuspendedQueue.GetpFirst().GetData(); // Usando FIFO

            // Verificar si hay espacio en la Memoria Principal
            int baseDirection = this.osReference.getMp().isSpaceAvailable(pToSwapIn.getTotalInstructions());

            if (baseDirection != -1) {
                readySuspendedQueue.delNodewithVal(pToSwapIn);
                pToSwapIn.setPState(ProcessState.READY);
                readyProcesses.insertLast(pToSwapIn);
                this.osReference.getMp().allocate(baseDirection, pToSwapIn.getTotalInstructions()); //️ Asignar espacio en la memoria
                pToSwapIn.setBaseDirection(baseDirection);
                System.out.println("SO (MTS): SWAP IN (PID " + pToSwapIn.getPID() + ") de READY_SUSPENDED a READY.");

            }
        }
    }

    // ---------- Planificacion a largo plazo ----------
    /**
     * Admision de procesos de nuevo al sistema
     *
     * @return
     */
    public void manageAdmission() {
        // Admite un proceso si hay 25% de espacio en MP y el sistema no esta full de procesos
        if (this.osReference.getMp().admiteNewProcess()) {

            System.out.println("Planificador a largo plazo");
            // Utilizara simplemente el FIFO
            Process1 newProcessToMP = (Process1) this.osReference.getDma().getNewProcesses().GetpFirst().GetData();

            // Si hay espacio suficiente en memoria principal
            int baseDirection = this.osReference.getMp().isSpaceAvailable(newProcessToMP.getTotalInstructions());

            // Si no hay espacio en memoria
            if (baseDirection == -1) {
                System.out.println("No hay espacio contiguo suficiente (" + newProcessToMP.getTotalInstructions() + " unidades) en la Memoria Principal para planificarlo desde el Largo plazo.");
                // Si no hay espacio no se hace nada

                // Considerar la cola de listo suspendidos. Por ahora solo listo por simplicidad 
                // Si hay espacio
            } else {
                newProcessToMP.setBaseDirection(baseDirection);
                // Coloco el proceso en listo
                newProcessToMP.setPState(ProcessState.READY);

                // Muevo el proceso de la cola de nuevo a la cola de listos
                this.osReference.getReadyQueue().insertLast(newProcessToMP);
                this.osReference.getDma().getNewProcesses().delNodewithVal(newProcessToMP);

                // Asignar el espacio en la memoria principal (Actualiza el array memorySlots)
                this.osReference.getMp().allocate(baseDirection, newProcessToMP.getTotalInstructions());

                // Notificar al planificador
                this.setIsOrdered(false);
                System.out.println("Proceso " + newProcessToMP.getPName() + " admitido en la Memoria Principal. Enviando a cola de listos");
            }
        }
    }

    // Getters y Setters
    public PolicyType getCurrentPolicy() {
        return currentPolicy;
    }

    public void setCurrentPolicy(PolicyType newPolicy) {
        this.currentPolicy = newPolicy;

        this.isOrdered = false; // Para que vuelva a ordenar la cola por el cambio de politica

        System.out.println("Política cambiada a " + newPolicy);
    }

    public int getQuantum() {
        return quantum;
    }

    public void setIsOrdered(boolean isOrdered) {
        this.isOrdered = isOrdered;
    }
}
