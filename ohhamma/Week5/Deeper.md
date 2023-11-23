# [Ch1] Introduction

<aside>
💡 **Why bother with concurrency?**

- can simplify the development of complex systems
- simpler straight-line code
- multiprocessor systems
</aside>

## 1.1 A (very) brief history of concurrency

- **ancient past**
    - `no OS`
    - running only a single program at a time
- **evolution of OS**
    - running individual programs in `processes`
    - interprocess communication (IPC)
        - sockets
        - signal handlers
        - shared memory
        - semaphores
        - files

### Factors that led OS to allow multiple programming

- **Resource utilization**
    - use wait time to let another program run
- **Fairness**
    - equal claims on the machine’s resources
    - share the computer via finer-grained time slicing
- **Convenience**
    - write several & single-task programs

### Development of threads

- finding the right balance of `sequentiality` & `asynchrony`
- lightweight processes
- multiple streams of control flow coexist within a process
- share the memory address space of owning process
- explicit **synchronization** needed for shared data

## 1.2 Benefits of threads

- development & maintenance costs ↓
- performance ↑
- useful in…
    - GUI apps : responsiveness of UI
    - server apps : resource utilization
- simplify JVM : garbage collector

### 1.2.1 Exploiting multiple processors

- single-threaded : idle while waiting (synchronous I/O)
- multithreaded : another thread can still run
    - make progress during blocking I/O

### 1.2.2 Simplicity of modeling

- process with one task
    - simpler
    - error-prone ↓
    - easier to test
- assigning a thread to each type of task
- framework such as servlets
    - do not need to worry about requests being processed
    - simplify component development
    - reduce the learning curve

### 1.2.3 Simplified handling of asynchronous events

- server app
    - each connection allocated to a thread
    - allowed to use synchronous I/O (simpler, less error-prone)
- [Before] low limits of the number of threads
    - multiplexed I/O (select, poll)
    - Java class packages for nonblocking I/O
- [Now] support for larger numbers of threads
    - thread-per-client model

### 1.2.4 More responsive user interfaces

- [Before] GUI apps : used to be single-threaded
    - needed frequent polling
    - main event loop → “freeze” until code finishes
- [Now] modern GUI frameworks
    - event dispatch thread (EDT)
    - called when UI event occurs
    - long-running task → needs to be excuted in a seperate thread

## 1.3 Risk of threads

- developers must be aware of thread-safety issues
- concurrency

### 1.3.1 Safety hazards

- `race` condition
    - threads share same memory space
    - access / modify variables other threads are using
- variables must be properly coordinated
    - in Java : `@ThreadSafe` `synchronized`

### 1.3.2 Liveness hazards

- `liveness failure`
    - activity gets into a state → unable to make forward progress
    - infinite loop → following code never gets executed
    - thread A waiting for thread B to release exclusive resource → waits forever
    - deadlock, starvation, livelock

### 1.3.3 Performance hazards

- `context switches`
    - saving / restoring execution context
    - CPU scheduling threads

## 1.4 Threads are everywhere

- Every Java application uses threads
    - JVM tasks : garbage collection, finalization
    - main thread
    - UI frameworks : manage UI events (AWT, Swing)
    - Timer : execute deferred tasks
    - Component frameworks (servlets, RMI)
- need for thread safety is contagious

# [Ch2] Thread Safety

- protect data from uncontrolled concurrent access
- Java : `synchronized`

### three ways to fix ‘broken program’

- `don’t share` the state variable across threads
- make the state variable `immutable` or
- use `synchronization` whenever accessing the state variable

### designing thread-safe classes in OOP

- encapsulation
- immutability
- clear specification of invariants

## 2.1 What is thread safety?

- `correctness`
    - class conforms to its specification
    - `invariants` : object’s state
    - `postconditions` : effects of its operations
- `thread-safe`
    - continues to behave correctly when accessed from multiple threads
    - encapsulate any needed synchronizatoin

### 2.1.1 Example: a stateless servlet

```java
@ThreadSafe
public class StatelessFactorizer implements Servlet {
	public void service(ServletRequest req, ServletResponse resp) {
		BigInteger i = extractFromRequest(req);
		BigInteger[] factors = factor(i);
		encodeIntoResponse(resp, factors);
	}
}
```

- no fields and references from other classes
- `stateless` objects are thread-safe

## 2.2 Atomicity

- `atomic` : execute as a single, indivisible operation
- `read-modify-write` operation → not atomic!

### 2.2.1 Race condition

- when getting the right answer relies on lucky timing
- `check-then-act`
    - observe something to be true
    - then take action based on that observation
    - observation could become invalid between the time

### 2.2.2 Example: race conditions in lazy initialization

```java
@NotThreadSafe
public class LazyInitRace {
	private ExpensiveObject instance = null;
	
	public ExpensiveObject getInstance() {
		if (instance == null)
			instance = new ExpensiveObject();
		return instance;
	}
}
```

- init an object until it is actually needed
- initialized only once
- can cause serious problems

### 2.2.3 Compound actions

- sequence of operations that must be executed atomically to remain thread-safe
    - check-then-act
    - read-modify-write

```java
@ThreadSafe
public class CountingFactorizer implements Sevlet {
	private final AtomicLong count = new AtomicLong(0);

	public long getCount() { return count.get(); }

	public void service(ServletRequest req, ServletResponse resp) {
		BigInteger i = extractFromRequest(req);
		BigInteger[] factors = factor(i);
		count.incrementAndGet();
		encodeIntoResponse(resp, factors);
	}
}
```

- atomic variable classes (java.util.concurrent.atomic)

## 2.3 Locking

- when we have more than one state
- not `independent`
    - multiple variables participate in an invariant
- to preserve state consistency, update related state variables in a single atomic operation

### 2.3.1 Intrinsic locks

- Java : `synchronized` block
    - lock : reference to an object
    - block of code to be guarded
- intrinsic locks, monitor locks → `mutexes`
    - one thread may own the lock
    - lock automatically acquired / released by the executing thread

### 2.3.2 Reentrancy

- intrinsic locks are `reentrant`
    - thread tries to acquire a lock it already holds → success
    - locks acquired on a per-thread basis (in POSIX, per-invocation)
- acquisition count
    - when acquires lock again → increment
    - when exits synchronized block → decrement
    - when count reaches zero → lock released
- saves from deadlock in situations

## 2.4 Guarding state with locks

- synchronization needs to be used only when **writing** to share variables → NOT TRUE
- **all** accesses to a variable → guarded by **same** lock
- **encapsulate** all mutable state within an object
- when invariant involves more than one variable
    - **all** the variables → guarded by **same** lock
- synchronizing every method → still can have race condition
    - additional locking required

## 2.5 Liveness and performance

- synchronized factoring servlet → poor concurrency
    - queue, handled sequentially
- try to exclude from synchronized blocks
    - which do not affect shared state

```java
@ThreadSafe
public class CachedFactorizer implements Servlet {
	@GuardedBy("this") private BigInteger lastNumber;
	@GuardedBy("this") private BigInteger[] lastFactors;
	@GuardedBy("this") private long hits;
	@GuardedBy("this") private long cacheHits;

	public synchronized long getHits() { return hits; }
	public synchronized double getCacheHitRatio() {
		return (double) cacheHits / (double) hits;
	}

	public void service(ServletRequest req, ServletResponse resp) {
		BigInteger i = extractFromRequest(req);
		BigInteger[] factors = null;
		synchronized (this) {
			++hits;
			if (i.equals(lastNumber)) {
				++cacheHits;
				factors = lastFactors.clone();
			}
		}
		if (factors == null) {
			factors = factor(i);
			synchronized (this) {
				lastNumber = i;
				lastFactors = factors.clone();
			}
		}
		encodeIntoResponse(resp, factors);
	}
}
```

- using different synchronization mechanisms → confusing
- competing design forces → reasonable balance can be found
    - safety
    - simplicity
    - performance
- avoid holding lock for a long time
    - lengthy computations
    - not completed quickly : network, console I/O

# [Ch3] Sharing Objects

- techniques for sharing & publishing objects
    - safely access by multiple threads
- `java.util.concurrent` library classes
- aspects of synchronization
    - atomicity
    - critical sections
    - `memory visibility` : other thread sees the changes made

## 3.1 Visibility

- to ensure visibility of memory writes → synchronization needed
- `reordering` phenomenon
    - no guarantee that operations are performed in order given
- always use the proper synchronization whenever data is shared across threads

### 3.1.1 Stale data

- out-of-date value
- can cause serious safety or liveness failures
    - unexpected exceptions
    - corrupted data structures (linked list)
    - inaccurate computations
    - infinite loops

```java
@ThreadSafe
public class SynchronizedInteger {
	@GuardedBy("this") private int value;

	public synchronized int get() { return value; }
	public synchronized void set(int value) { this.value = value; }
}
```

- synchronizing **both** getter & setter

### 3.1.2 Nonatomic 64-bit operations

- out-of-thin-air safety
    - at least it sees a value placed by some thread
    - not just random value
- 64-bit numeric values not declared volatile
    - two separate 32-bit operations
    - not atomic
- declare `volatile` to use shared mutable long and double

### 3.1.3 Locking and visibility

- without synchronization, there is no visibility guarantee
    - synchronization on the **same** lock needed

### 3.1.4 Volatile variables

- variable is shared & operations should not be reordered
    - compiler & runtime put on notice
- not cached in registers → always return the most recent write
- lighter-weight synchronization mechanism
- memory visibility → just like entering a synchronized block
- but still more fragile & harder to understand
    - use only when simplifying & verifying
- typical use : checking a status flag
    - determine when to exit loop
- can only guarantee `visibility` (not atomicity)
- when to use :
    - writes do not depend on its current value
    (or ensure only single thread updates)
    - variable does not participate with other state variables
    - locking not required when variable is accessed

## 3.2 Publication and escape

- `publishing`
    - making an object available to code outside of current space
    - may require synchronization
- `escaped`
    - object published when it should **not** have been
- any object **reachable** from a published object has also been published

```java
class UnsafeStates {
	private String[] states = new String[] {
		"AK", "AL" ...
	};
	public String[] getStates() { return states; }
}
```

- **alien** method
    - whose behaviors is not fully specified by certain class
    - methods in other class
    - overrideable methods
    - passing object to alien method also considered publishing
- publishing an inner class instance → also implicitly published

### 3.2.1 Safe construction practices

- do not allow **this** reference to escape during construction
- instead, expose a **start** or **initialize** method
- use private constructor & public factory method

## 3.3 Thread confinement

- data only accessed from single thread
- object confined to a thread  → automatically thread-safe
- applications using thread confinement
    - Swing
    - use of pooled JDBC Connection objects

### 3.3.1 Ad-hoc thread confinement

- responsibility falls entirely on the **implementation**
    - fragile
- visual components, data models in GUI apps
- should be used sparingly if possible

### 3.3.2 Stack confinement

- object can only be reached through **local variables**
- primitive local variables
- simpler to maintain, less fragile
- requires assistance : ensure that referent does not escape

### 3.3.3 ThreadLocal

- associate a per-thread value with value-holding object
    - **get** and **set** accessor methods
- prevent sharing in mutable Singletons, global variables
- used when frequently used operation requires a temporary object
- used in implementing app frameworks : J2EE

## 3.4 Immutability

- immutable objects are **always** thread-safe
    - simple & safer
- considered immutable if :
    - its state not modified after construction
    - all its fields are final
    - properly constructed (does not escape)

### 3.4.1 Final fields

- `final` : construction of immutable objects
- guarantee of initialization safety
- documents to maintainers that fields are not expected to change

### 3.4.2 Example: Using volatile to publish immutable objects

- race conditions can be eliminated by using immutable object

```java
@Immutable
class OneValueCache {
	private final BigInteger lastNumber;
	private final BigInteger[] lastFactors;

	public OneValueCache(BigInteger i, BigInteger[] factors) {
    lastNumber = i;
		lastFactors = Arrays.copyOf(factors, factors.length);
	}

	public BigInteger[] getFactors(BigInteger i) {
		if (lastNumber == null || !lastNumber.equals(i))
	    return null;
		else
			return Arrays.copyOf(lastFactors, lastFactors.length);
	}
}
```

- immutable holder object for multiple state variables
- thread-safe without explicit locking

## 3.5 Safe publication

- improper publication → could allow another thread to observe a partially constructed object

### 3.5.1 Improper publication: when good objects go bad

```java
public class Holder {
	private int n;

	public Holder(int n) { this.n = n; }

	public void assertSanity() {
		if (n != n)
			throw new AssertionError("This statement is false.");
	}
}
```

- synchronization not used to make Holder visible to other threads → Holder not properly published
- possible things that can go wrong
    - other threads could see a stale value
        - thus see a null reference
    - other threads could see an up-to-date value
        - but stale values for the state of the Holder

### 3.5.2 Immutable objects and initialization safety

- synchronization needed to guarantee a consistent view of the object’s state
- immutable objects used safely without additional synchronization
    - final fields safely accessed
    - but still required when referring to mutable objects

### 3.5.3 Safe publication idioms

- safely published → entails synchronization by both publishing & consuming thread
- thread-safe library collections
    - Hashtable, synchronizedMap, ConcurrentMap
    - Vector, CopyOnWriteArrayList, CopyOnWriteArraySet
    - BlockingQueue, ConcurrentLinkedQueue
- using a static initializer → executed by JVM at class init time

### 3.5.4 Effectively immutable objects

- objects technically immutable, but whose state will not be modified after publication
    - treated as if they were immutable
    - can be used safely without additional synchronization

### 3.5.5 Mutable objects

- to share mutable objects safely → must be safely published & either be thread-safe or guarded by a lock

### 3.5.6 Sharing objects safely

- documenting how the object can be accessed
    - Thread-confined
    - Shared read-only : immutable, effectively immutable
    - Shared thread-safe
    - Guarded : accessed only with a specific lock