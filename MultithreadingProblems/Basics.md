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

### Throughput vs Latency

- **Throughput:** Rate of doing work or how much work gets done per unit of time. Like in instagram how many photos mobile is able to download per unit of time
- **Latency:** Time required to complete a task or produce a result(response time). Time required to download images from internet is latency

- Given hundreds of file containing integer, give sum of all files. In a single process system, it will be done one by one synchronously. But in a concurrent system, we can spawn 100s of threads for each one file and combine their results.
    - In this example, throughput is number of files processed per minute while latency is total time taken to completely process all the files. (In this example, throughput goes up while latency goes down!)
    - Generally, throughput and latency have inverse relationship!

### Critical Sections & Race Conditions

When multiple threads of same program start executing same portion of code special care must be taken due to race conditions.

- Critical Section: Any piece of code that can be executed concurrently and exposes any shared data or resources.
- Race Conditions: When thread runs through critical sections without thread synchronisation. They race through critical section to read/write shared resources and depending on the order in which race is finished the program output changes.
- Like if a thread checks for a state (test-then-act) and another thread changes that state which results in incorrect actio by the said thread. 

[Broken-Synchronisation-Example]()
[Fixed-Synchronisation-Example]()

### Deadlocks, Liveness & Reentrant Locks

While trying to avoid race conditions and guarding critical sections, logical follies can occur
- Deadlock: When two or more threads arent able to make any progress as resource required by first thread is occupied by second and the one by second thread acquired by second.
- Liveness: Ability of program to execute in a timely manner
- Live-lock: When two threads react in response to actions by other thread without making any real progress. [Two person moving left-right to give each other the way resulting in blocking each other]
- Starvation: A thread can never get CPU time or access to shared resources as other threads not let it acquire resources.

Deadlock example:
```
void increment() {
    acquire mutex_a
    acquire mutex_b
    // Work done here
    release mutex_b
    release mutex_a
}

void decrement() {
    acquire mutex_b
    acquire mutex_a
    // work done here
    release mutex_a
    release mutex_b
}
```

- Above code can easily result in a deadlock sometimes
```
T1 enters increment and acquires mutex_a
T1 gets switched out
T2 enters decrement and acquries mutex_b
T2 gets switched out
Now both threads are in a deadlock
```
[Deadlock-Example]()

- Re-entrant lock: Allow for relocking or reentring of a synchronisation lock. [Reentrant-lock-Example]()
    - Any object if locked in succession will result in a deadlock. 

### Mutex vs Semaphore

- Mutex: Used to guard shared data structures or any primitive types so that only single thread can access a resource or critical section. Strictly limited to serializing access to competing threads.
- Semaphore: Used to limit access to a collection of resources. (limited permits to give out. Once permits over, process blocked till permite returned!). It can also be used for signalling amongst threads (to cooperatively work towards completing a task!)
- Difference between mutex and binary semaphore
    - In case of mutex, same thread that acquired it must release it otherwise undefined behavior. While in case of binary sempahore (any thread can return the permit) different threads can call acquire and release on threads
    - Leads to concept of ownership. In mutex, a thread owns it till it releases it while a semaphore has no concept of ownership!
- Semaphores can used for signalling amongst threads, like in case of producer/consumer problem where a producer signal consumer thread by increasing semaphore count to indicate something has been produced.
- Semaphore also solves the issue of missed signals.
[Semaphore car rental like while mutex lone jet on a runway!]

### Mutex vs Monitor

Moniter is exposed as a concurrency construct by some languages and used for locking and signalling. Moniters are language level constructs as opposed to mutex, semaphores. 

Problem solved by moniters:
- In a producer/consumer problem, when a thread needs a predicate to be true so it can proceed forward, like consumer can consume only when there is something to be consumed. What can consumer do? One option is to continuously wait for the predicate to be set true

```
// PsuedoCode
void busyWait() {
    acquire mutex
    while (predicate == false) {
        release mutex // To give other threads to acquire and set predicate to true
        acquire mutex
    }
    do work
    release mutex
} // Works but wastes a lot of CPU cycles
```

##### Condition Variables

- Essentially cv solves the problem we just encountered, we want to test for a predicate with a lock so no other thread can change it during the test but if its false then need to wait for it to change.
- Condition variable exposes wait() and signal() methods. Upon calling wait(), thread is placed in a wait queue(set) and releases mutex. Another thread can now acquire the mutex and maybe changes the predicate upon which it will signal the waiting threads by using signal().
- Upon signal, the woken up signal goes in ready state and placed in a ready queue (scheduler will schedule it!). Also, it can only run after signalling thread has released the mutex.

```
// CV psuedo code
void consumer() {
    acquire mutex
    while (predicate == false) {
        condVar.wait() // thread gets placed in wait queue
    }
    // do work
    release mutex
}

void producer() {
    acquire mutex
    // do work
    set predicate = true
    condVar.signal() // waiting thread gets put into ready queue
    release mutex
}
```
- Cant we use if instead of while in consumer function? 
    - What if a different thread got scheduled first and changed the predicate value, before signalled thread got the chance to execute => Need to check the predicate again!
    - Use of loop necessitated by design choices of moniters
    - On POSIX systems, spurious or fake wakeups are possible without a signal.

- **Moniter** is made up of a mutex and one or more condition variables. It is an entity having an entry set and a waiting set. In java, each object is a moniter and implicitly has a lock and is a condition veriable too. Moniters allow threads to exercise mutual exclusion as well as cooperation by allowing them to wait and signal on conditions.

### Java's Monitor & Hoare vs Mesa Monitors

- Java's moniter: Every object is a cv with a hidden lock. Exposes wait(), notify() methods.
    - Before wait(), notify() need to lock the hidden mutex. Done by using synchronised keyword otherwise results in ```IllegalMonitorStateException```
    - Can only be called on an object once the calling thread becomes the owner of the moniter.
    - Ownership can be achieved via:
        1. Method that thread is executing has synchronised signature
        2. Block that thread is executing is synchronised on which wait, notify will be called.
        3. In case of class, static method which is synchronised

```
class BadSynchronization {

    public static void main(String args[]) throws InterruptedException {
        Object dummyObject = new Object();

        // Attempting to call wait() on the object
        // outside of a synchronized block.
        dummyObject.wait();
    }
}
```

```
class BadSynchronization {

    public static void main(String args[]) {
        Object dummyObject = new Object();
        Object lock = new Object();

        synchronized (lock) {
            lock.notify();

            // Attempting to call notify() on the object
            // in synchronized block of another object
            dummyObject.notify();
        }
    }
}
```

### Hoare vs Mesa Monitors

As discussed, the need for while loop while testing for predicate arises due to design choices of moniter.

1. Mesa moniters: After signalling, another thread can be scheduled to change the value of predicate before woken up thread runs. Need to check predicate again!
2. Hoare moniters: Signalling thread yields the moniter to woken up thread, which enters the moniter while the first one sits out! Here an if clause will suffice as no other thread gets a chance to change the predicate since no other thread gets to enter the monitor.

Java, in particular, subscribes to Mesa monitor semantics and the developer is always expected to check for condition/predicate in a while loop. Mesa moniters are more efficient than hoare moniters.

### Semaphore vs Monitor

1. Interchangeable. Moniters take care of atomically acquiring locks whereas onus is on developer.
2. Semaphores are lightweight when compared to moniters which are bloated. but when Semaphore and mutex pair as an alternative to a moniters, its easy to make mistakes!
3. Java moniters enforce correct locking by throwing IllegalMonitorState exception object when cv methods invoked without acquiring locks. => Incorrect lock or no lock acquired
4. A semaphore can allow several threads access to a given resource or critical section while in moniters only a single thread can own it.

### Amdahls law

