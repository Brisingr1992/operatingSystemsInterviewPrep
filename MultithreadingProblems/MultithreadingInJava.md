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

