# Future vs. Monix Task

This repo contains code & slides for my talk on this topic.
<br/>
<br/>

*scala.concurrent.Future* is familiar to most Scala devs.

This presentation first talks about referential transparency and the IO Monad.

Then it compares Future with monix.eval.Task (Monix 3.x)
with their Pros and Cons.

Interop with *Future*: As Scala's *Future* is used in many environments and libraries, we look at
the conversion from *Future* to *Task* and - vice versa - from *Task* to *Future*.

I will also take a look at *Task* evaluation, cancelation and memoization as well as tail recursive loops
and asynchronous boundaries.

The presentation will include a discussion on *ExecutionContext* (required for *Future*) and *Scheduler*
(required *Task*, but only to run it)

Often recurring on the valuable Monix *Task* doumentation at https://monix.io/docs/3x/eval/task.html
the presentation can also be seen as an introduction to Monix *Task*.
<br/>
<br/>

## Resources

- Code and Slides of this Talk:<br/>
  https://github.com/hermannhueck/future-vs-monix-task

- Monix 3.x Documentation<br/>
  https://monix.io/docs/3x/

- Monix 3.x API Documentation<br/>
  https://monix.io/api/3.0/
