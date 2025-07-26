package fourFunctionalConcurrent

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

   /*
   the producer-consumer problem
   producer -> [?] -> consumer
   */

   class SimpleContainer {
      private var value: Int = 0
      def isEmpty: Boolean = value == 0
      def set(newValue: Int): Unit = value = newValue
      def get: Int =
         val result = value
         value = 0
         result
   }

   def naiveProdCons(): Unit =
      val container = new SimpleContainer
      val consumer = new Thread(() => {
         println("[consumer] waitting...")
         while(container.isEmpty)
            println("[consumer] actively waitting...")

         println("[consumer] I have consumed " + container.get)
      })

      val producer = new Thread(() => {
         println("[producer] computing...")
         Thread.sleep(500)
         val value = 42
         println("[producer] I have produced, after long work, the vale " + value)
         container.set(value)
      })

      consumer.start()
      producer.start()

   //naiveProdCons()

   // En lugar del while, es mejor y mas seguro usar wait y notify
   // wait en un objeto de monitoreo, suspende el thread hasta que se notifique en notify()

   def smartProdCons(): Unit =
      val container = new SimpleContainer
      val consumer = new Thread(() => {
         println("[consumer] waitting...")
         container.synchronized {
            container.wait()
         }
         // container must have some value
         println("[consumer] I have consumed " + container.get)
      })
      val producer = new Thread(() => {
         println("[producer] Hard at work...")
         Thread.sleep(2000)
         val value = 42

         container.synchronized {
            println("[producer] I am producing " + value)
            container.set(value)
            container.notify()
         }
      })
      consumer.start()
      producer.start()

   //smartProdCons()

   /*
   producer -> [? ? ?] -> consumer
   */

   def prodConsLargeBuffer(): Unit = {
      val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
      val capacity = 3

      val consumer = new Thread(() => {
         val random = new Random()

         while(true)
            buffer.synchronized {
               if (buffer.isEmpty)
                  println("[consumer] buffer empty, waitting...")
                  buffer.wait()

               // there must be at least ONE value in the buffer
               val x = buffer.dequeue()  // quita un valor
               println("[consumer] consumed " + x)

               // hey producer, theres empty space available, are you lazy?
               buffer.notify()
            }
            Thread.sleep(random.nextInt(2000))
      })

      val producer = new Thread(() => {
         val random = new Random
         var i = 0

         while(true)
            buffer.synchronized {
               if(buffer.size == capacity)
                  println("[producer] buffer is full, waiting...")
                  buffer.wait()

               // there must be at least ONE EMPTY SPACE in the buffer
               println("[producer] producing " + i)
               buffer.enqueue(i)

               // Ey consumer, new food for you
               buffer.notify()
               i += 1
            }
            Thread.sleep(random.nextInt(2000))
      })
      consumer.start()
      producer.start()
   }
   //prodConsLargeBuffer()

   // multiple producers and consumers

   class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
      override def run() =
         val random = new Random()

         while (true)
            buffer.synchronized {
               /*
               Producer produce a value, and two consumers are waiting,
               producer notify to ONE consumer, notifies on buffer.
               And the notify inside consumer, notify to the other consumer (inside JVM)
               the other consumer says, ok i have woken up (me he levantado)
               and then there must be at least one element in buffer and
               tries dequeuing
               */
               while (buffer.isEmpty)
                  println(s"[consumer $id] buffer empty, waitting...")
                  buffer.wait()
               // se cambia el if por el while. Debido a que el if dejaria pasar a la
               // siguiente linea donde hace el dequeue

               // there must be at least ONE value in the buffer
               val x = buffer.dequeue() // quita un valor (pero para esta aplicacion está mal)
               println(s"[consumer $id] consumed " + x)

               // hey producer and consumer, theres empty space available, are you lazy?
               buffer.notify()
            }
            Thread.sleep(random.nextInt(2000))

   }

   class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
      override def run() =
         val random = new Random
         var i = 0
         while (true)
            buffer.synchronized {
               while (buffer.size == capacity)
                  println(s"[producer $id] buffer is full, waiting...")
                  buffer.wait()

               // there must be at least ONE EMPTY SPACE in the buffer
               println(s"[producer $id] producing " + i)
               buffer.enqueue(i)

               // Ey consumer, new food for you
               buffer.notify()
               i += 1
            }
            Thread.sleep(random.nextInt(2000))
   }

   def multiPordCons(nConsumers: Int, nProducers: Int) =
      val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
      val capacity = 3
      (1 to nConsumers).map{
         x => new Consumer(x, buffer).start()
      }
      (1 to nProducers).map {
         x => new Producer(x, buffer, capacity).start()
      }






   //val buffer = new mutable.Buffer[Int] {}
   //val consumer = new Consumer()

}
