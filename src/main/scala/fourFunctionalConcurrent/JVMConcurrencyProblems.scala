package fourFunctionalConcurrent

object JVMConcurrencyProblems {
   def runInParallel(): Unit = {
      var x = 0 // será modificado por algunos threads
      val thread1 = new Thread(() => {
         x = 1
      })
      val thread2 = new Thread(() => {
         x = 2
      })
      thread1.start()
      thread2.start() // puede que esta se ejecute primero
      // y que por lo tanto x termine siendo 1
      println(x) // Race condition: es ocacionada por el uso de var
      // se soluciona usando syncrhonization
   }

   case class BankAccount(var amount: Int)
   def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
      bankAccount.amount -= price
      /*
      tres pasos involucrados en el metodo: read old value, compute, write new value
      */
   }
   def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
      bankAccount.synchronized({
         // Con synchronized, no se permite que los threads ejecuten
         // secciones criticas al
         // mismo tiempo
         bankAccount.amount -= price
      })
   }

   /*
   Example de race condition
   thread1(shoes)
      -reads amount 5000
      - compute result 50000 - 3000 = 47000
   thread2(iphone)
      - reads amount 50000
      - compute result 50000 - 4000 = 46000
   thread1 (shoes)
      - write amount 47000
   thread2 (iphone)
      - write amount 46000
   se debe a que son tres pasos los que se efectuan en cada operación
   donde puede que el segundo thread esté leyendo 50000 como monto y no el
   primer residuo
   */
   def demoBankingProblem(): Unit = {
      (1 to 10000).foreach { x =>
         val account = new BankAccount(50000)
         val thread1 = new Thread(() => buy(account, "shoes", 3000))
         val thread2 = new Thread(() => buy(account, "iphone", 4000))
         thread1.start()
         thread2.start()
         thread1.join()
         thread2.join()
         if (account.amount != 43000) println(s"$x Aha I have just broken the bank")
         // el resultado deberia de ser igual a 43000 pero
         // en el ejemplo, este if sucede en dos ocaciones de diez mil
         // debido a que los threads tratan de sobre escribir la clase
         // y no están devolviendo correctamente el resultado

      }
   }

   /*
   Exercises
   1. Create "inception threads"
      thread 1
      -> thread 2
         -> thread 3
            ...
   each thread prints "hello from thread $i"
   print all messages in reverse order

   2. whats the max/min value of x
   3. "sleep fallacy"
   */

   // respuesta
   // los imprime de forma inversa debido a que primero hace la recursividad
   def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = {
      new Thread(() => {
         if (i < maxThreads)
            val newThread = inceptionThreads(maxThreads, i + 1)
            newThread.start()
            newThread.join()
         println(s"Hello from thread $i")

      })
   }

   /*
   apuntes de la respuesta
   max value = 100 - each thread increases x by 1
   min value = 1
   all threads read x = 0 at the same time
   all threads (in parallel) compute 0 + 1 = 1
   all threads try to write x = 1
   */

   def minMaxX(): Unit = {
      var x = 0
      val threads = (1 to 100).map(_ => new Thread(() => x += 1))
      threads.foreach(_.start())
   }

   /*
   almost always, message = "Scala is awesome
   is it guaranteed? No
   Obnoxious situacion (possible)

   main thread:
      message = "Scala Sucks"
      awesomeThread.start()
      sleep(1001) - yileds execution (produce ejecucion)
   awesome thread:
      sleep(1000) - yields execution
   OS gives the CPU to some important thread, takes > 2 s
   OS gives the CPU back to the main thread
   main thread:
      println(message) // scala sucks"
   aweome thread:
      message = "Scala is awesome"

   */


   def demoSleepFalacy(): Unit = {
      var message = ""
      val awesomeThread = new Thread(() => {
         Thread.sleep(1000)
         message = "Scala is awesome"
      })
      message = "Scala sucks"
      awesomeThread.start() // puede que el CPU tarde mas de 2 segundos en
      // devolver el resultado del thread
      // como se tarda mas de dos segundos, entonces imprime primero scala sucks
      // antes de que termine el thread
      // la solucion es hacer join
      Thread.sleep(1001)
      awesomeThread.join()
      println(message)
   }


   def main(args: Array[String]): Unit = {
      demoBankingProblem()
      demoSleepFalacy()

      // ejericicios
      println("ejercicios")
      inceptionThreads(5, 1).start()
      demoSleepFalacy()

   }

}
