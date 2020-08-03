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