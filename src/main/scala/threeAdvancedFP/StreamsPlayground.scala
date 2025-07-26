package threeAdvancedFP


import scala.annotation.tailrec
import scala.collection.immutable.List

abstract class MyStream[+A] {
   def isEmpty: Boolean
   def head: A
   def tail: MyStream[A]
   def #::[B >: A](elem: B): MyStream[B] // append operator
   def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B]
   def foreach(f: A => Unit): Unit
   def map[B](f: A => B): MyStream[B]
   def flatMap[B](f: A => MyStream[B]): MyStream[B]
   def filter(f: A => Boolean): MyStream[A]
   def take(n: Int): MyStream[A]
   def takeAsList(n: Int): List[A] = take(n).toList()

   /*
   (1,2,3).toList()
   (2,3).toList(1 :: Nil)
   (3).toList(2 :: 1 :: Nil)
   ().toList(3 :: 2 :: 1 :: Nil)
   3 :: 2 :: 1 :: Nil
   */
   @tailrec
   final def toList[B >: A](acc: List[B] = Nil): List[B] =
      if (isEmpty) acc
      else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
   def isEmpty: Boolean = true
   def head: Nothing = throw new NoSuchElementException()
   def tail: MyStream[Nothing] = throw new NoSuchElementException()
   def #::[B >: Nothing](elem: B): MyStream[B] = // append operator
      new Cons(elem, this)
   def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] =
      anotherStream
   def foreach(f: Nothing => Unit): Unit = ()
   def map[B](f: Nothing => B): MyStream[B] = this
   def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
   def filter(f: Nothing => Boolean): MyStream[Nothing] = this
   def take(n: Int): MyStream[Nothing] = this
}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
   def isEmpty: Boolean = false

   override val head: A = hd
   override lazy val tail: MyStream[A] = tl

   def #::[B >: A](elem: B): MyStream[B] = // append operator
      new Cons(elem, this)

   def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] =
      new Cons(head, tail ++ anotherStream)

   def foreach(f: A => Unit): Unit =
      f(head)
      tail.foreach(f)
   def map[B](f: A => B): MyStream[B] =
      new Cons(f(head), tail.map(f))
   def flatMap[B](f: A => MyStream[B]): MyStream[B] =
      f(head) ++ tail.flatMap(f)

   def filter(f: A => Boolean): MyStream[A] =
      if (f(head)) new Cons(head, tail.filter(f))
      else
         tail.filter(f)

   def take(n: Int): MyStream[A] =
      if (n <= 0) then EmptyStream
      else if (n == 1) then
         new Cons(head, EmptyStream)
      else
         new Cons(head, tail.take(n - 1))
}


object MyStream {
   def from[A](start: A)(generator: A => A): MyStream[A] =
      new Cons(start, MyStream.from(generator(start))(generator))

}

object StreamsPlayground extends App {
   val naturals = MyStream.from(1)(_ + 1)
   println(naturals.head)
   println(naturals.tail.head)
   println(naturals.tail.tail.head)

   val startFrom0 = 0 #:: naturals
   println(startFrom0.head)

   startFrom0.take(10000).foreach(println)
   println(startFrom0.map(_ * 2).take(100).toList())

   println(startFrom0.flatMap(
      x => new Cons(x, new Cons(x + 1, EmptyStream))
   ).take(10).toList())

   // Exercises on streams
   /*
   1. Stream of fibonacci numbers
   2. Stream of prime numbers with Eratosthenes sieve

   */


   def fibo(first: Int, second: Int): MyStream[Int] =
      new Cons(first, fibo(second, first + second ))

   fibo(1, 1).take(10).foreach(println)

   /*
   filter out all numbers divisible by 2, then by 3, then by 5
   */
   println("eratosthenes")
   def eratosthenes(stream: MyStream[Int]): MyStream[Int] =
      if (stream.isEmpty) then stream
      else
         new Cons(stream.head, eratosthenes(stream.tail.filter(_ % stream.head != 0)))

   eratosthenes(MyStream.from(2)(_ + 1)).take(10).foreach(println)
}
