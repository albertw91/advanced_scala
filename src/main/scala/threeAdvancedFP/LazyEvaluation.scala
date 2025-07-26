package threeAdvancedFP

import scala.annotation.tailrec

object LazyEvaluation extends App {

   lazy val truena = throw new RuntimeException()

   lazy val x = {
      println("hello x")
      42
   }
   val y = {
      println("hello y")
      50
   }

   println(x) // aqui si imprime el hello
   println(x) // aqui ya no

   println(y) // en cuanto se define imprime el y
   println(y)

   def sideEffectCondition = {
      println("boo")
      true
   }


   def simpleCondition = false

   println("evaluacion logica con lazy, false y true no evalua el lazy")
   lazy val lazyCondition = sideEffectCondition
   println(if(simpleCondition && lazyCondition) "true" else "false")
   println("segunda evaluacion con lazy")
   println(if(sideEffectCondition && lazyCondition) "true" else "false")


   println("evaluacion logica sin lazy, false y true si evalua side effect")
   val normalCondition = sideEffectCondition
   println(if (simpleCondition && normalCondition) "true" else "false")

   println("in conjuntion with call by name")
   def byNameMethod(n: => Int): Int = n + n + n + 1
   def byValueMethod(n: Int): Int = n + n + n + 1

   def retrieveMagicValue: Int = {
      println("waiting")
      Thread.sleep(1000)
      10
   }
   println("Print by name evalua muchas veces")
   byNameMethod(retrieveMagicValue)
   println("by value, evalua una vez")
   byValueMethod(retrieveMagicValue)

   println("No hacer la evaluación n veces. Usa lazy val")

   def byNamelazyMethod(n: => Int): Int = {
      lazy val t = n // only evaluete once
      t + t + t + 1
   }

   println("evaluar con lazy val")
   val a = byNamelazyMethod(retrieveMagicValue)
   println(a)

   println("filtering with lazy vals")

   def lessThan30(i: Int): Boolean = {
      println(s"less than 30?")
      i < 30
   }
   def greaterThan20(i: Int): Boolean = {
      println(s"greater than 20?")
      i > 20
   }

   val numbers = List(1, 25, 40, 5, 23)
   val lt30 = numbers.filter(lessThan30)
   val gt20 = lt30.filter(greaterThan20)

   println(gt20)

   println("In lazy mode using withFilter")
   val lt30Lazy = numbers.withFilter(lessThan30)
   val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
   println(gt20Lazy)
   gt20Lazy.foreach(println) // los va devolviendo mientras los evalua

   println("for comprehensions use lazy val")
   val comprehensionFor = for {
      a <- List(1,2,3,4) if(a % 2 == 0)

   } yield a + 1
   println(comprehensionFor)
   val mapsComprehension = List(1,2,3,4).withFilter(x => x % 2 == 0).map(b => b + 1)
   println(mapsComprehension)

   // streams
   /*
   Implement a lazily evaluated, singly linked stream of elements
   naturals = MyStream.from(1)(x =< x + 1) = stream of natural numbers
   (potentially infinite)
   naturals.take(100)
   naturals.foreach(println)
   naturals.map(_ * 2)

   */



}
