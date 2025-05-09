### Question 1a : What output do you get from the program? Why?
The program prints `Calling run()`, `Running in: main`, `Calling start()`, and `Running in: Thread-2`—in that order—because the first `run()` executes in the main thread while `start()` spawns a new thread that then runs its own `run()`.

---

### Question 1b: What’s the difference between calling `start()` and `run()`?

* `run()` is just a normal method; calling it runs the code **inside the caller’s thread**.
* `start()` asks the JVM to create a **new thread** and then runs `run()` there, allowing the two threads to run concurrently.

---

### Question 2a : What output do you get from the program? Why?

You always see `Main thread ends.` and, if the scheduler lets it run first, a few `Daemon thread running...` lines, since the JVM kills the daemon thread as soon as the main (non-daemon) thread finishes.

---

### Question 2b: What happens if you remove `thread.setDaemon(true)`?

The worker becomes a **regular** thread.  
The JVM will now wait for its `run()` method to finish all 20 loop iterations, so the message `Daemon thread running...` prints twenty times before the program exits.

---

### Question 2c: What are some real-life uses for daemon threads?

* The JVM’s garbage-collector / finalizer threads  
* Background log flushers or metrics collectors  
* Idle-timeout timers or heartbeat pingers that shouldn’t block JVM shutdown

---

### Question 3a: What output do you get from the program?

Thread is running using a ...!


(Exactly that one line, printed by the new thread.)

---

### Question 3b: What is the `() -> { ... }` syntax called?

A **lambda expression** (introduced in Java 8).

---

### Question 3c: How is this code different from extending `Thread` or implementing `Runnable`?

* **Lambda** – the shortest, in-place way to create a one-off `Runnable`; no extra class file.
* **Implementing `Runnable`** – keeps class inheritance free, good when you want to reuse the task logic or store state.
* **Extending `Thread`** – ties the task directly to a thread object and prevents extending another class (Java’s single inheritance); generally less flexible and used less often.



