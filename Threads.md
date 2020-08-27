### Threads

- A program can have multiple threads of execution. A thread is like another copy of a process that executed independently.
- Threads share the same address space (code, heap)
- Each thread has a seperate PC {Each different thread may run over different part of program}
- Each thread has seperate stack for independent calls

- Processes have their own address space, need complicated IPC mechanisms to communicate, upon forking extra copies of same code and data in memory.
- Meanwhile two thread in a process, share parts of same address space, global variables can be used for communication, smaller memory footprint. Threads are like seperate processes except they share the same address space
- Threads ensure concurrency and parallelism in order to get better performance from underlying hardware

### Scheduling threads

- OS schedules threads independently much like processes
- The context of threads (PC, registers) saved and restored from TCB (Thread Control Block). Every PCB has one or more linked TCBs
- Threads that are scheduled independently by kernel are kernel threads (like linux pthreads) while user threads are multiplexed into a smaller number of kernel threads by some programming libraries.
- Low overhead of context switching in user threads but multiple user threads cant run in parallel
- When multiple threads act on some shared data weird things may happen due to instruction not being atomic. Leads to race conditions
- Critical Section: Portion of code that can lead to race conditions. To prevent race conditions:
    1. Mutual Exclusion (only one thread to execute critical section at a time)
    2. Need atomicity of critical section

### Locks

```
lock_t mutex; // some global lock in process
lock(&mutex);
// critical section
unlock(&mutex)
```
- All threads accessing a critical section will share the lock. The thread successful in acquiring this lock becomes the owner while other threads wait for it to realease it.
- Goals of building a lock
    1. Mutual Exclusion
    2. Fairness (no thread should starve)
    3. Low overhead acquiring, releasing and waiting for lock (minimal use of resources)
- More on locks
    1. Needed for both user space programs as well as kernel programs
    2. Implementing locks need support from both OS and hardware
    3. Can we build a lock just using interrupts? Disable interrupts on critical section and enable them back when done?
        - DisableInterrupt is a priviledged instruction and a program can misuse it
        - Will not work on multicore systems {since another thread can enter critical section}
        - Mostly works on single processor systems inside OS
- Implementation



### Condition Variable

- Locks allow one type of mutual exclusion between threads - mutual exclusion
- Another common requirement requirement in multi-threaded applications is waiting and signalling. (T2 wants to continue after T1 has finished)
    - Can accomplish this by busy-waiting on some variable but very inefficient
- Due to all this most OSs provide a synchronization primitive called: Condition Variable
- Condition Variable (CV) is just a queue that a thread can put itself when waiting on some condition. (Check a condition if its false and put itself in a queue)
- Another thread that makes the condition true can signal to wake up the sleeping thread
- pThreads provide cv for user programs. OS has similar functionality of wait/signal for kernel threads
- Signal wakes up a thread while signal broadcast wakes up all the waiting threads.

- Without any lock on wait: Race Condition
    - Parent checks condition to be false, decides to sleep but just before sleeping interrupted
    - Child changes the condition, signals but noone is sleeping yet
    - Parent resume, goes to sleep, never wakes up!!
- Lock must be held when calling wait and signal with CV
- Wait function releases the lock before putting thread to sleep, so lock is available for signalling thread. (Never go to sleep with a lock held!)

##### Producer / Consumer problem

- Common pattern: In multi-threaded web server, one thread accepts the requests and puts them in a queue. Worker threads get requests from this queue and process them.
- Setup: One or more producer threads, One or more consumer threads, a shared buffer of bounded size [Always match the wait and signal statement]

```
cont_t empyty, fill;
mutex_t mutex;

void *producer(void *arg) {
    Pthread_mutex_lock(&mutex);
    while (Count == MAX) {
        Pthread_cond_wait(&empty, &mutex);
    }
    put(<item>);
    Pthread_cond_signal(&fill);
    Pthread_mutex_unlock(&mutex);
}

void *consumer(void *arg) {
    Pthread_mutex_lock(&mutex);
    while (Count == 0) {
        Pthread_cond_wait(&fill, &mutex);
    }
    get();
    Pthread_mutex_signal(&empty);
    Pthread_mutex_unlock(&mutex);
}
```

### What is a sempahore?

- Synchronization primitive like condition variables
- Essentially a variable with an underlying counter.
- Two functions on a semaphore variable: 
    1. up/post increments the counter
    2. down/wait decrements the counter
- A semaphore with init value 1 acts as a simple lock.

```
sem_t m;
sem_init(&m, 0, X) // What should X be (X number of permits)

sem_wait(&m);
// critical section
sem_post(&m);
```

- Can use semaphores for ordering / set the order of execution
- Producer / Consumer problem
    1. Need two semaphores for signalling. One to track empty slots and make producer wait if full. One to track full slots and make consumer wait if empty
    2. One semaphore to act as a mutex for buffer.

```
sem_t empty, fill, mutex;

void *producer(void *arg) {
    sem_wait(&empty); // If no empty slots should wait
    sem_wait(&mutex);
    put(item);
    sem_post(&mutex);
    sem_post(&fill);
}

void *consumer(void *arg) {
    sem_wait(&fill); // If nothing in the buffer should wait
    sem_wait(&mutex);
    get();
    sem_post(&mutex);
    sem_post(&empty);
}
```

- What if mutex acquired before not after. Waiting thread sleeps with the mutex and the signalling thread can never wake it up!! 

### Concurrency Bugs

- Writing multi-currency programs tricky
- Bugs are non-deterministic and occur based on execution of order of threads (hard debugging)
- Two types of bugs:
    1. Deadlock - Threads cannot execute any further and wait for each other
    2. Non-deadlock bugs - non deadlock but incorrect results when threads execute

##### Non-deadlock bugs

1. Atomicity bugs: atomicity assumptions made by programmer violated during execution of concurrent threads (Fix using mututal exclusion)
2. Order-violation bugs: Desired order of memory accesses is flipped during concurrent execution (Fix using condition variables)

##### Deadlock bugs

1. Thread waiting for thread2 and thread2 waiting for lock1
2. Need not always occur. Only occurs if executions overlap and context switch from a thread after acquiring only one lock!
3. Conditions for a deadlock
    - Mututal Exclusion: a thread claims exclusive control of a resource
    - Hold-and-wait: thread holds a resource and is waiting for another
    - No preemption: Thread cannot be made to give up a resource
    - Circular wait: there exists a cycle in cycle dependency graph
    - All four conditions must hold for a deadlock to occur
4. Preventing circular wait
    - Always acquire locks in same order
    - Total ordering must be followed (by address maybe!)
5. Preventing hold and wait
    - Acquire all locks at once, say by acquiring a master lock first (coarse-grained)
    - But this may reduce concurrent execution and performance gains!
6. Other solutions
    - Deadlock avoidance: If OS knew which process needs which locks, it can schedule the processes in that deadlock will not occur
        - Bankers algorithm: popular but impractical in real life
    - Detect and recover: reboot system or kill deadlocked processes