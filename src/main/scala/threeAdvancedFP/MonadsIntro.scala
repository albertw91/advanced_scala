package threeAdvancedFP

object MonadsIntro extends App {

   println(List(1,2,3,4,5).map(x => List(x * 2)))

   // identidades de las monads
   /*
   left identity


   List(x).flatMap(f) == f(x) ++ Nil.flatMap(f)
   = f(x)

   right identity
   list.flatMap(x =Z List(x)) = list

   Associativity
   [a, b, c].flatMap(f).flatMap(g) =
   (f(a) ++ f(b) ++ f(c)).flatMap(g) =
   f(a).flatMap(g) ++ f(b).flatMap(g) ++ f(c).flatMap(g) =
   [a, b, c].flatMap(f(_).flatMap(g)) =
   [a, b, c].flatMap(x => f(x).flatMap(g))
    */

   trait Attempt[+A] {
      def flatMap[B](f: A => Attempt[B]): Attempt[B]
   }
   object Attempt {
      def apply[A](a: => A): Attempt[A] =
         try
            Success(a)
         catch
            case e: Throwable => Fail(e)
   }
   case class Success[+A](value: A) extends Attempt[A] {
      def flatMap[B](f: A => Attempt[B]): Attempt[B] =
         try
            f(value)
         catch
            case e: Throwable => Fail(e)

   }
   case class Fail(e: Throwable) extends Attempt[Nothing] {
      def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
   }

   /*
   left identity
   unit.flatMap(f) = f(x)
   Attempt(x).flatMap(f) = f(x)
   Success(x).flatMap(f) = f(x)

   right identity

   attempt.flatMap(unit) = attempt
   Success(x).flatMap(x => Attempt(x)) = Success(x)
   Fail.flatMap(x => Attempt(x)) = Fail(e)

   Associativity

   attempt.flatMap(f).flatMap(g) === attempt.flatMap(x => f(x).flatMap(g))
   Fail(e).flatMap(f).flatMap(g) === Fail(e)
   Fail(e).flatMap(x => f(x).flatMap(g)) === Fail(e)

   Success(v).flatMap(f).flatMap(g) === f(v).flatMap(g) or Fail(e)
   Success(v).flatMap(x => f(x).flatMap(g)) === f(v).flatMap(g) or Fail(e)
   */

   val x = 2
   def func(x: Int): Int = x * 2
   println(Attempt(x))
   println(Attempt(throw new RuntimeException("My own monad, yes!")))

   /*
   Exercise
   1) implement a Lazy[T] monad = computation which will only be executed when its needed
   unit/apply
   flatMap
   2) Monads = unit + flatMap
   mondas = unit + map + flatten

   Monad[T] {
   def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implement)
   def map[B](f: T => B): Monad[B] = ???
   def flatten(m: Monad[Monad[T]]): Monad[T] = ???
   (have List in mind)
   }
   */

   println("Monads exercise")
   class Lazy[+A](value: => A) {
      // call by need
      private lazy val internalvalue = value
      def use: A = value
      def flatMap[B](f: A => Lazy[B]): Lazy[B] = f(internalvalue)
   }

   object Lazy{
      def apply[A](value: => A): Lazy[A] = new Lazy(value) // unit
   }
   // aqui no imprime nada
   val lazyInstance: Lazy[Int] = Lazy {
      println("Today i dont feel like doing anything")
      42
   }
   println("print de instancias")
   println(lazyInstance)  // threeAdvancedFP.MonadsIntro$Lazy@71c7db30
   // imprime el print y el use
   println(lazyInstance.use)

   // aqui si imprime un print
   val flatMappedInstance = lazyInstance.flatMap({
      x => Lazy{
         10 * x
      }
   })
   // aqui imprime el segundo print
   val flatMappedInstance2 = lazyInstance.flatMap({
      x =>
         Lazy {
            10 * x
         }
   })

   /*
   sin el private lazy val
   print de instancias
   threeAdvancedFP.MonadsIntro$Lazy@71c7db30
   Today i dont feel like doing anything
   42
   Today i dont feel like doing anything
   Today i dont feel like doing anything // con el private este se omite
   // debido a que lo recicla del anterior.
   */

   /*
   left-identity
   unit.flatMap(f) = f(v)
   lazy(v).flatMap(f) = f(v)

   right-identity
   l.flatMap(unit) = l
   Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

   associativity: l.flatMap(f).flatMap(g) = f(v).flatMap(g)
   Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
   Lazy(v).flatMap(x => Lazy(x).flatMap(g)) = f(v).flatMap(g)

   */




}
