### Operating Systems
- What is an operating system?
A middleware that sits between user programs and hardware. Manages hardware like cpu, memory, I/O devices (disk, network card, mouse, keyboard etc.)

- Von neumann model?
Load the instructions from disk to main memory, load data required by instructions into registers, decode and execute instructions, save the result to memory

- How a C program works?
    1. Compiler translates the high level programs into an executable
    2. Executable contains asssembly level instruction and data of program all numbered with addresses
    3. Hardware implements an instruction set archtechture and cpu has a few registers like PC, op, mem address etc.
    4. CPU also has a data and instruction cache for faster access
    5. Upon starting the executable, a process is created by OS with code, stack, heap and will be run on CPU!!

- What does OS do?
    1. Manages program memory by virtualizing it
        - Loads the program exec from disk to memory
        - Each process is given an virtual address space and given the illusion that it has whole memory to itself!
        - Actually, code, stack, heap may be place anywhere in physical memory and OS will translate virtual address to physical addresses
    2. Manages CPU by providing process abstraction
        - Initialises PC and other registers to begin execution
        - Creates and manages processes. Each process thinks it has the entire cpu to itself (vitualizes cpu)
        - Timeshares processes on CPU
        - Enables coordination amongst processes
    3. Manages external devices
        - Like read/wrtie files from disk.
        - Contacts and performs actions on behalf of user to disk, network card and other external drivers
        - Device drivers (piece of OS kernel code) know how to talk to devices in their hardware languages. 
            - Issues instructions to devices (eg. fetch this file!)
            - Responds to interrupt events by devices (eg. file found!, user has pressed a key!)
        - Persistant data organised as filesystem on disk.

- Design goals of OS
    1. Convenience by abstracting hardware resources for user programs
    2. Efficiency usage of cpu, memory etc
    3. Isolation between multiple process (for security)

- What is the difference between procedure call and system call?
    1. On system call, the cpu executes it at a higher priviledge level while procedure call runs in user space

### Process Abstraction

- When an executable is run, the OS creates a process. Also, timeshares multiple process on cpu. OS scheduler determines which process to run of all the active processes.
    - Policy: dictates which process will be run
    - Mechanism: dictates how to context switch b/w processes

- What constitutes a process or the state of a process?
    1. Unique PID
    2. Memory Image (residing in RAM)
        - Code and data (static)
        - stack and heap (dynamic)
    3. CPU context (PC, current operands, stack pointer)
    4. File descriptors
        - Pointers to open files and devices
        - STDIN, STDOUT, STDERR

- How does OS create a process?
    1. Allocates memory and creates memory image
    2. Open basic files and stores their descriptors (stdin, out, err)
    3. Initializes cpu registers like load first instruction address in PC

- States of process (a few)
    1. Running (currently executing)
    2. Ready (waiting to be scheduled)
    3. Blocked (suspended but not ready. Blocked on some event like a read from disk. Upon completion the disk will generate an interrupt)
    4. New (being created, yet to run)
    5. Dead (terminated)

- OS maintains a list of all active processes so what does a single PCB contains?
    1. Process ID
    2. Process State
    3. CPU context (for resuming after context switch)
    4. Pointers to other related processes (like parent)
    5. Pointers to memory locations
    6. Pointers to open files

### Process API

- OS provides a set of system calls
    1. System call is a function call to OS code that runs at higher priviledge level
    2. Sensitive operations like access to hardware allowed only at kernel mode
    3. Blocking calls like read a file cause the process to be descheduled
- Should we rewrite programs for each OS?
    1. No, as POSIX API provides a standard set of system calls that an OS must implement.
    2. programs written to POSIX API can run on any posix complaint OS ensuring portability
    3. Language libraries usually hide the details of systems calls by wrapping them in a library
- Process related system calls (unix)
    1. fork() creates a new child process
        - All processes created by forking from a parent
        - Init process is the ancestor of all parents
    2. exec() makes a process execute given executable
    3. wait() causes parent to block till child terminates
    4. exit() terminates a process
- What happens during fork?
    1. Memory image of parent is copied into new process (child)
    2. New entry (PCB) is added to OS process list and scheduled like rest
    3. Parent and child start execution just after fork (with child pid returned to parent)
    4. Parent and child execute, modify data independently in their own memory space

```
int rc = fork();
if (rc == 0) {
// child process
} else if (rc > 0) {
// parent process with pid of the child
}
```

- Process Termination
    1. calls exit()
    2. misbehaving -> terminated by OS
    3. Terminated processes are not removed from list immediately
    4. wait() blocks till the child terminates (non-blocking ways also exist)
    5. If a parent terminates before child, child is adopted by init
- What happens during exec?
    1. After fork, child run the same executable as parent
    2. Using exec, another executable can be loaded into childs memory image
    3. Number of variants exist for exec function
- How does a shell work?
    1. Init process created just after initilization of hardware
    2. Init process spawns a shell
    3. Shell reads user command, forks a new process, execs the command executable and waits for it to finish.
    4. Shell can also rewrite file descriptors etc..
- Mechanism of Process Execution
    How an OS run a process, handle its system calls, context switch it?

    1. Process Execution
        - OS allocated the memory and creates memory image
        - OS points the registers to correct values
        - After this, process executes directly in cpu
    2. Function Call Mechanism
        - First cpu context is pushed onto stack and stack pointer is updated
        - Upon return, OS will pop the stack and restore cpu context and return the value
    3. How is a system call different?
        1. - Multiple priviledge levels (user, kernel)
           - kernel doesnt trust user stack (uses a seperate kernel stack)
           - kernel doesnt trust user provided addresses to jump to. So sets up an Intterupt Descriptor Table(IDT) at boot time which has the addresses of kernel functions to run for system calls etc..
        2. Mechanism of system calls (Trap instruction)
            - When a system call is made, a special trap instruction is run (user doesnt know)
            - Moves CPU to higher priviledge level
            - switch to kernel stack
            - Save context (old pc, registers) on kernel stack
            - Look up address in IDT and jump to trap handler function in OS
            - Upon return-from-trap context restored from kernel, reduce priviledge level, restore pc and jump to user code after trap
        3. Trap executed on system call (OS services), program services, Interrupts. Mechanism is same: Save context on kernel stack and switch to OS address in IDT.
        4. But how to identify entries in IDT? System calls/Interrupts store a number in CPU register before calling trap to identify which entry to use
        5. Upon return do we always return to same process? No, another process can be scheduled OR maybe process exited or maybe there was an error or a segfault! Maybe process made a blocking call like read. Here a context switch performed with another process.

### OS scheduler

Consists of two parts: Policy (which process to run) and mechanism (to switch that process)

1. Non-preemptive(cooperative) schedulers: switch only when a process exited, terminated or yields.
2. Preemptive schedulers: Gives interrupts periodically even if a process can run. After servicing interrupts, checks whether we need to schedule another process or this process has run too long!!
3. Mechanism of Context Switch
    - Save context of process A on kernel stack (via trap or by an interrupt)
    - switch sp to kernel stack of B
    - restore context from kernel stack of B. Reduce priviledge level and let it run
4. Scheduling policy
    - On context switch, which process from the list of ready process to run
    - Scheduler decides the cpu bursts of the process (time on cpu) and schedules them on cpu
    - Schedular goals:
        - Tries to maximise cpu utilization time
        - minimize turnaround time (from arrival to completion) fastness
        - minimize average response time (from arrival to first scheduling) Interactivity
        - fairness (processes must be treated equally)
        - minimize overhead (run process long enough to amortize the cost of context swtich)
5. First-in-first-Out(FIFO)
    - 3 processes arrive, schedule them in FIFO fashion till completion
    - Problems?
        1. Convoy effect: stuck behind a long process.
        2. Turnaround time tends to be high
6. Shortest Job First(SJF)
    - When all processes arrive together, optimal.
    - Non pre-emptive so short jobs can still get stuck behind long ones!
7. Shortest-time-to-Completion First
    - shortest remaining time first
    - preemptive scheduler (check on arrival which is shorter)
    - prempts running task if time left is > than that of arrived process
8. Round Robin (RR)
    - Every process executes for a fixed time slice (slice should be big enough to amortize cost of context switch ~1ms)
    - Preemptive. Good for response time and fairness
    - Bad for turnaround time
9. Schedulers in real systems (MLFQ)
    - Many queues in order of priority
    - Process from highest priorityQueue scheduled first
    - Within same pq, any algorithm like round-robin can be used
    - priority of process decays with the age

### Inter-Process Communication

Processes dont share memory but may want to cooperate to finish a task (IPC mechanisms allow for this!)

1. Shared Memory
    - Can get access to same region of memory via shmget() sys call
    - By providing the same key, two processes can get access to same segment of memory
    - Can read/write to it to communicate
    - Need special care to coordinate? which process should write/read where? Not writing at same place
2. Signals
    - A certain set of signals supported by OS (like kill signal). Some signals can be user defined
    - Signals can be sent by OS or another process (like ctrl-c)
    - Signal handler - every process has a default code to execute each signal
    - Some signal handler can be over-ridden to do other things
3. Sockets
    - Can be used by two processes on same machine or on other machine
        - TCP/UDP sockets across all machines
        - Unix sockets across all machines
    - Communicating with sockets
        - Process open sockets and connect to each other
        - Messages written into socket delivered to process and OS transfers data across socket buffers
4. Pipes (half duplex communication)
    - Return two file descriptors (read and write handle)
    - half-duplex data written in one fd can be read through another
    - Regular pipes: both fd are in same process. Parent and child share fd after fork. parent uses one end while child uses another
    - Names pipes: two endpoints can be in different processes. Data buffered in OS buffers between read and write
5. Message Queues (mailbox abstraction)
    - Process can open a mailbox at a specified location
    - processes can send/recieve messages from mailbox
    - OS buffers messages b/w read and write
6. Blocking vs NonBlocking Communication
    - Some IPC actions can block (reading from empty socket/pipe/message queue or writing to full socket/pipe/message queue)
    - The system calls to read/write have versions that can return with an error code instead of blocking!!