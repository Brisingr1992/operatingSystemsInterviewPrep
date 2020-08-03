### Introduction

Lets say we are updating code in IDE on a single processor machine and we save the file. This causes the bytes to be written on the underlying disk. Cpu can either wait for this write operation to finish OR it can run another thread/process till the write completes. This makes other processes responsive and give the illusion of multi-tasking. Each thread is given a slice of time on CPU and gets switched out if it has to wait OR uses its time-slice on Cpu. With multi-core machines common nowadays we can make use of this extensive parallism.

##### Benefits of threads

1. Higher throughput (Though throughput gains can be stolen by overhead of context switching)
2. Illusion of multi-tasking.
3. Effiecient utilization of resources. Thread creation light-weight in comparision to spawning a process. (Like handling a new request on servers)

##### Performance gains via multi-threading

Check out: Single vs multi-threaded process gains for a sum of 0 to MAX_VALUE.

##### Problems with threads

1. Hard to find bugs
2. Higher cost of code maintainence (code is harder to reason about)
3. Increased utilisation of system resources (Extra memory, context switches, Cpu book-keeping)
4. Programs may experience slowdown (Coordination/lock-contention uses execution time and resources)
