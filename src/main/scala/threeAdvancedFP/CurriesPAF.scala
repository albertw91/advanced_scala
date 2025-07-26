package threeAdvancedFP

object CurriesPAF extends App {

   val superAdder: Int => Int => Int =
      x => y => x + y

   val add3 = superAdder(3)
   println(add3(5))
   println(superAdder(3)(5))

   // Method!
   def curriedAdder(x: Int)(y: Int): Int = x + y

   val add4: Int => Int = curriedAdder(4)

   // functions != methods (JVM limitation)
   def inc(x: Int) = x + 1
   List(1,2,3).map(inc)

   // partial functions applications
   val add5 = curriedAdder(5) _
   add5(2)

   // Excercise
   val simpleAddFunction = (x: Int, y: Int) => x + y
   def simpleAddMethod(x: Int, y: Int) = x + y
   def curriedAddMethod(x: Int)(y: Int) = x + y

   // add7: Int => Int = y => 7 + y
   // as many different implementations of add7 using the above
   // be creative!
   println("Ejercicios")
   val add7 = (x: Int) => simpleAddFunction(7, x)  // simplest
   println(add7(5))
   val add7_2 = simpleAddFunction.curried(7)
   println(add7_2(5))
   val add7_6 = simpleAddFunction(7, _: Int)

   val add7_3 = curriedAddMethod(7) // optional _
   println(add7_3(5))

   val add7_5 = simpleAddMethod(7, _: Int)
   println(add7_5(5))

   // underscores are powerfull

   def concatenator(a: String, b: String, c: String): String = a + b + c

   val insertName = concatenator("Hello, I am", _: String, ", how are you?")
   println(insertName("Daniel"))


   val fillInTheblanks = concatenator("Hello, ", _: String, _: String)
   println(fillInTheblanks("Daniel", "Scala is awesome"))

   //Exercises
   /*
   1. Process a list of numbers and return their string representations with different formats
   Use the %4.2f, %8.6f and %14.12f with a curried formatter function
   */
   def curriedFormater(f: String)(n: Double): String =
      f.format(n)

   val numbers: List[Double] = List(1.0, 2.0, 3.1232, math.Pi)

   val formater_numbers = curriedFormater("%4.2f")
   numbers.map(formater_numbers).foreach(println)

   def byName(n: => Int): Int = n + 1
   def byFunction(f: () => Int): Int = f() + 1
   def method: Int = 42
   def parenMethod(): Int = 42

   /*
   callind by name and by function
   - int, method, parenMethod, lambda, PAF
   */

   byName(23)
   byName(method)
   byName(parenMethod())
   //byName(parenMethod) // ok but beware ==> byName(parenMethod())
   // byName(() => 42) no ok
   byName((() => 42)()) // ok. con el ultimo parentesis la estas llamando
   //byName(parenMethod _ ) // not ok

   // byFunction(45) // not ok
   // byFunction(method) // not ok
   byFunction(parenMethod)
   byFunction(() => 42)
   byFunction(parenMethod _) // no es necesario poner el _ para que haga ETA expransion





}
