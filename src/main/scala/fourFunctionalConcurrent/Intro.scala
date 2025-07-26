package fourFunctionalConcurrent

import java.util.concurrent.Executors

object Intro extends App {

   /*
   interface Runnable {
      public void run()
   }
   */

   val runnable = new Runnable {
      override def run(): Unit = println("Running in parallel")
   }

   val aThread = new Thread(runnable)

   aThread.start() // gives the signal to the jvm to start a jvm thread
   // create a JVM thread => OS thread
   runnable.run() // doesnt do anything in parallel!
   aThread.join()

   val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
   val threadBye = new Thread(() => (1 to 5).foreach(_ => println("bye")))

   threadHello.start()
   threadBye.start()

   // different runs produce different results!
   // executors
   val pool = Executors.newFixedThreadPool(10)
   pool.execute(() => println("something in the thread pool"))

   pool.execute(() => {
      Thread.sleep(1000)
      println("done after 1 second")
   })
   pool.execute(() => {
      Thread.sleep(1000)
      println("almost done")
      Thread.sleep(1000)
      println("done after 2 seconds")
   })
   //pool.shutdown() // no mas ejecuciones serán enviadas al pool
   //pool.execute(() => println("")) // esto tronaria

   //pool.shutdownNow() // lo detiene desde antes del codigo anterior


}
