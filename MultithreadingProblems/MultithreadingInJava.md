### Thread Safety and Synchronized

- Thread safe: A class and its public API thread safe if multiple threads can consume these without causing race conditions or state corruption in class.
- Synchronized keyword: Used to restrict access to critical sections one thread at a time. Each object has a moniter and when a thread gets hold of its moniter, it has exclusive access to all the methods marked as synchronised. All other threads will block on these methods while a thread has a lock on these
    1. For static methods, moniter will be a class object as opposed to an instance.
    2. If an uncaught exception occurs in synchronised block, moniter is still released.
    3. Synchronised blocks can be re-entered.

```
public synchronized setName(...) {
    name = ...
}

// Equivalent
public setName(...) {
    synchronized(this) {
        name = ...
    }
}
```

- In case of synchronised keyword, java forces us to implicitly acquire and release the moniter lock for object within same method. Same thread will acquire and release the moniter in the same method. In case of semaphore, we can acquire/release in different methods or by different threads.
- If we synchronize on an object and reassign (change) it somewhere in code, it will be met with illegal state exception.
- Marking all the methods on a class synchronized will reduce throughput and if we do that for lets say getters on the same moniter they will be also be locked. Better to design properly and synchronize them on different lock.

### Wait & Notify

- Wait: Exposed on each java object. Upon executing it, the thread is placed in wait queue. The calling thread must be inside a synchronized block of code synchonizing on the same object calling wait() otherwise IllegalMoniterState exception
- Notify: Awakens one of the thread in associated wait queue of moniter. Awakened thread will not be scheduled for execution immediately and will compete with other active threads trying to synchronize on same object.
- NotifyAll(): Wakes up all the waiting threads on objects moniter.

### Interrupting Threads

- When a thread is on wait/sleep, one way to wake it up is interrupt method. It will wake up and immediately through Interrupted Exception.
- Invoking an interrupt method only sets a flag that is polled periodically by sleep or wait to know the current thread has been interrupted and the interruptedException should be thrown.
[InterruptExample]()

### Volatile Keyword

- Normally JVM will decide when to write to main memory even if threads read a stale value. Also, a thread may keep copy of a variable in cache and manipulate it rather than writing to main memory.
- If a variable is declared volatile, then whenever a thread writes or reads to volatile variable they happen in main memory. Also, all the variables visible to writing threads also get written-out to main memory alongside volatile one. Similar for reading threads too.
- Comes into play due to multiple levels of memory in hardware architecture required for performance enhancements. If just a single thread reading/writing on volatile then just using volatile is enough but if multiple threads doing this then synchronized would be required.

### Reentrant Locks & Condition Variables

##### Reentrant Locks
- Java's equivalent to traditional lock is rentrant lock (comes with additional methods). Similar to implicit moniter lock using synchronised methods or block.
- With the reentrant lock can lock and unlock it in differnt methods but with same threads! Like when a thread attempts to unlock a pthread mutex!!

##### Condition Variables
- Each java object exposes wait, notify, notifyAll which can be used to wait and signal.
- Condiition is essentially factoring out these three methods into separate methods so that there can be multiple wait-sets per object. 
- As a reentrant lock replaces synchronized blocks or methods, a condition replaces the object monitor methods. Also, exposes an API to create new condition variables

```
Lock lock = new ReentrantLock();
Condition c = lock.newCondition();
```
- In synchronised, we had one wait-set per object but with this we can have multiple cv's associated with each object.
- java.util.concurrent provides several classes for concurrency problems like ConcurrentHashMap

### Semaphore

- Exposes two methods - release() and acquire() for signalling amongst thread. But important thing is permits acquired should be equal to permits returned.

[SemaphoreExample]()

### Missed Signals

- A missed signal happens when a signal is sent by a thread before the other thread starts waiting on a condition. Missed signals are caused by using the wrong concurrency constructs. 

```
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Demonstration {

    public static void main(String args[]) throws InterruptedException {
        MissedSignalExample.example();
    }
}

class MissedSignalExample {

    public static void example() throws InterruptedException {

        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();

        Thread signaller = new Thread(new Runnable() {

            public void run() {
                lock.lock();
                condition.signal();
                System.out.println("Sent signal");
                lock.unlock();
            }
        });

        Thread waiter = new Thread(new Runnable() {

            public void run() {

                lock.lock();

                try {
                    condition.await(); // wrong way to use await method. Use with while loop and associated boolean condition!!
                    System.out.println("Received signal");
                } catch (InterruptedException ie) {
                    // handle interruption
                }

                lock.unlock();

            }
        });

        signaller.start();
        signaller.join();

        waiter.start();
        waiter.join();

        System.out.println("Program Exiting.");
    }
}
```

### Spurious Wakeup

- Means a thread is woken up even though no signal has been received. Like on POSIX based operating systems when a process is signalled, all its waiting threads wake up.
- That's why we should always guard by a while. Java documentation
```
* A thread can also wake up without being notified, interrupted, or
* timing out, a so-called <i>spurious wakeup</i>.  While this will rarely
* occur in practice, applications must guard against it by testing for
* the condition that should have caused the thread to be awakened and
* continuing to wait if the condition is not satisfied.  In other words,
* waits should always occur in loops, like this one:
* 
*     synchronized (obj) {
*         while (condition does not hold)
*             obj.wait(timeout);
*         ... // Perform action appropriate to condition
*     }
*
```

### Miscellaneous Topics

- No order to a thread acquiring access to locks. A thread may acquire lock more frequently than other locks. In java, locks can be turned into fair locks by passing in a fair parameter at cost of lower throughput and slower compared to unfair locks! [Lock Fairness]
- If an applications uses threads for short lived tasks and creates threads for tasks and then destroys it. This incurs a performance penalty. Programming frameworks solve this by creating a pool of threads, handed out to execute task and then returned to pool without destroying it!
- Java offers thread pools via its Executor Framework.