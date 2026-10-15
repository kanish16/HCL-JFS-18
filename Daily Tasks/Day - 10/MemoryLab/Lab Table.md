# Day 10: JVM Memory Internals & Memory Management

## MemoryLab Experimental Results

* MEMORY LAB EXPERIMENTAL RESULTS:
* -----------------------------------------------------------------------------------------------------------------
* Experiment               | Flags / Config         | Result / Output           | Technical Behavior
* -------------------------+------------------------+---------------------------+----------------------------------
* Stack Overflow Depth     | -Xss256k               | 3,722 calls               | Rapid stack frame exhaustion
* Integer Cache (127)      | Default                | == true, .equals() true   | Cached in Integer.IntegerCache
* Integer Cache (128)      | Default                | == false, .equals() true  | Separate heap allocations
* String += Concatenation  | -Xmx64m -Xlog:gc       | 337.24 ms                 | Heavy heap allocation churn
* StringBuilder.append()   | -Xmx64m -Xlog:gc       | 2.88 ms (117x faster)     | In-place array resize
* GC Activity Logging      | -Xlog:gc               | 33 Young GCs, Full GCs    | G1 Evacuation pauses visible
* OutOfMemoryError         | -Xmx64m                | Caught, freed to 1 MB     | Clean exit code 0
* -----------------------------------------------------------------------------------------------------------------
