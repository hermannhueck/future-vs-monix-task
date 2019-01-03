# Future vs. Monix Task

This repo contains code & slides for my talk on this topic.
<br/>
<br/>

*scala.concurrent.Future* is familiar to nearly all Scala devs.

This presentation first talks about referential transparency and the IO Monad in general.
(Monix *Task* is an impl of the IO Monad.)

Then it compares *Future* Monix 3.x *Task* with their Pros and Cons.

Interop with *Future*: As Scala's *Future* is used in many environments and libraries, we look at
the conversion from *Task* to *Future* and - vice versa - from *Future* to *Task*.

I will also take a look at *Task* evaluation, cancelation and memoization as well as tail recursive loops
and asynchronous boundaries.

The presentation will include a comparative discussion on *ExecutionContext* (required for *Future*)
and *Scheduler* (required for *Task*, but only to run it).

Often recurring on the valuable Monix *Task* doumentation at https://monix.io/docs/3x/eval/task.html
the presentation can also be seen as an introduction to Monix *Task*.
<br/>
<br/>

## Resources

- Monix 3.x Documentation<br/>
  https://monix.io/docs/3x/

- Monix 3.x API Documentation<br/>
  https://monix.io/api/3.0/
