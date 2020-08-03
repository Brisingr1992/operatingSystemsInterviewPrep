### Introduction

Lets say we are updating code in IDE on a single processor machine and we save the file. This causes the bytes to be written on the underlying disk. Cpu can either wait for this write operation to finish OR it can run another thread/process till the write completes. This makes other processes responsive and give the illusion of multi-tasking. Each thread is given a slice of time on CPU and gets switched out if it has to wait OR uses its time-slice on Cpu. With multi-core machines common nowadays we can make use of this extensive parallism.

##### Benefits of threads

1. Higher throughput (Though throughput gains can be stolen by overhead of context switching)
2. Illusion of multi-tasking.
3. Effiecient utilization of resources. Thread creation light-weight in comparision to spawning a process. (Like handling a new request on servers)

##### Performance gains via multi-threading

Check out: Single vs multi-threaded process gains for a sum of 0 to MAX_VALUE. [Example1](\programs\threadsExample1)

##### Problems with threads

1. Hard to find bugs
2. Higher cost of code maintainence (code is harder to reason about)
3. Increased utilisation of system resources (Extra memory, context switches, Cpu book-keeping)
4. Programs may experience slowdown (Coordination/lock-contention uses execution time and resources)

### Program vs Process vs Thread

1. Program: Set of instructions and its associated data that resides on the disk and loaded by OS kernel by creating a new process (loading to main mem, allocation code mem, stack, heap, creating entry in Process table, etc...)
2. Process: Program in execution. Consists of instructins, stack, heap etc. along with resources like cpu, memory, address space, disk, network I/O acquired at runtime. A program can have many copies of it running but process is one instance of it.
3. Thread: Smallest unit of execution. A process can have mulitple threads running as a part of it. Usually, some state of process is global while some state private to threads. Special care while accessing and modifying this global state by multiple threads.

Incase if multi-processor systems, they usually require some hardware support to schedule muliple process in parallel. Processes dont share any resources amongst them whereas threads can share resources allocated to that process, including address space. Process can do inter-process communication if they wanna talk.

Incorrect synchronisation can lead to varying results and hence accessing/modifying global state should be done with great care.

```
int counter  = 0;

void incrementCounter() {
    counter++;
}
```

- ```counter = counter + 1``` made up of 3 lines in assembly and due to scheduling, scenarios emerge when a thread can be scheduled in between and the leads to incorrect results.
- Without proper guarding of mutable data structures access to them causes hard to find bugs.
- Also, we cant make any assumptions about the order in which threads are scheduled.

[UnsafeThreads-Example](\programs\threadsExample2)

### Concurrency vs Parallelism

- Often confused with the ability of system to run multiple distinct programs at same time. Related but mean different things.
- Serial Execution: Run till completion one by one without interruption. Juggler juggling one ball at a time.

##### Concurrency

- Concurrent program: A program that can be split into parts, which can run out of order without affecting final outcome.
- Concurrent System: A system capable of running several different programs or multiple units of same program in overlapping time intervals. This execution may or may not happen simultaneously (depends on number of cores)
- Main goal is to maximize throughput and minimize latency. Achieve this when programs running on system require frequent network or disk I/O.
- A single processor concurrent system is not parallel. Two programs in such a system may be in progress but dont execute simultaneously.
- Juggler who can juggle several balls but at one time it has one ball in hand.

##### Parallelism

- Parallel system: A system that can execute multiple programs at once or multiple threads of same program simultaneously. Aided by hardware in form of multi-cores or as computing clusters.
- A problem has to be in concurrent in nature to run it parallely.
- In parallel systems, emphasis is on increasing throughput and optimising usage of hardware resources (extract as much speedup as possible like matrix multiplication)
- Multiple jugglers juggling one or more balls simultaneously.

##### Summary

- A concurrent system need not be parallel but a parallel system is indeed concurrent.
- Concurrency is about dealing with lots of things at once while prallelism is about doing lots of things at once.
- Also, concurrency can be defined as property of program or system while parallelism is runtime behavior of executing multiple tasks.
- Concurrency: Customers waiting in two queues to buy from counter
- Parallelism: Customers waiting in two queues to buy from two counters

### Cooperative Multitasking vs Preemptive Multitasking

A system can achieve concurrency by employing these two models
1. **Preemptive Multitasking**
    - OS interrupts process to allow another process to run on CPU.
    - Scheduler decide how long to run the process for and when. If a malicious process is to run infintely hurts only itself (as upon descheduling doesn't know when it will run again)
2. **Cooperative Multitasking**
    - Involves well-behaved programs to give up control back to scheduler so others can run. (OS kernel)
    - OS scheduler has no say how long a process/thread runs for. If a process is blocked or finished its time slot or logically blocked it gives up control.
    - As a result, malicious programs can bring system to halt and harm it.
    - Participating processes and threads need to cooperate to make this work

### Synchronous vs Asynchronous

1. **Synchronous**
    - Blocking, line by line serial execution. Blocks at the end of method call.
2. **Asynchronous**
    - Non-blocking. Runs seperately from the main application thread and notifies the calling thread of its completion/failure. (future/promise)
    - Doesnt wait for task to complete, moves on to the next task.
    - Promise is the representation of in-process computation. Main thread can query for its status and retrive result once complete OR pass a callback to run upon completion.
    - Excellent choice for application that do extensive I/O or spend most of the time waiting.
    - In non-threaded environments, it provides an alternative to threads for concurrency and fall under the cooperative multitasking model.

### I/O Bound vs CPU Bound

A process require many resources like cpu time, memory, networking, disk etc. to execute. It can require heavier use of one or more resources. Like a program which loads GBs of data from storage to main memory v/s a program that writes GBs to disk.

1. **CPU Bound**
    - Compute-Intensive programs whose execution requires very high utilization of CPU are cpu bound. Primarily depend on improving CPU speed to decrease program completion time.
    - With scaling up, these programs may potentially run faster but there is a limit to increasing cpu speed. Alternative, is to use more cpu's either via introducing multi-cores or a computing cluster both of which require the program to be concurrent or being able to be divided into smaller problems.
    - If we go multi-threaded way, the concurrency can reduce execution time but also introduces overhead of thread creation, context switching.
2. **I/O Bound**
    - Spend most of the time waiting for input/output operations (main memory, disk or network) to complete while cpu remains idle.
    - Even though physical distances are tiny, thousands of cpu cycles may go to waste and hence lower cpu utilization

Both of these types (and memory-bound) can benefit from concurrency. For cpu bound, can introduce more CPUs and structure program to spawn multiple threads. For I/O bound, make threads give up cpu control if waiting for IO and another thread can run and utilize cpu cycles.
- Javascript (Single threade)
- Java (fully multi-threaded)
- Python (Only single thread in running state GIL limitation)
