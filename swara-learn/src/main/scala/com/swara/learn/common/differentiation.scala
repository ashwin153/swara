package com.swara.learn.common

/**
  * An infinitely differentiable function from ℝ to ℝ. More precisely, this library is intended
  * to model real analytic functions (which are infinitely differentiable) but this distinction is
  * never explicitly enforced and is only really of interest to the mathematically inclined. The
  * purpose of this library is to implement automatic analytic differentiation; by phrasing
  * mathematical functions as compound operations on primitive analytic functions (cos, log, etc.)
  * we can take infinitely many derivatives without invoking expensive numerical methods.
  */
sealed trait Differentiable {

  /**
    * Applies the function and results the result. Variables used in the function must be set before
    * calling this method. Undefined behavior when variables are set to values that are not in the
    * domain of the function.
    *
    * @return Result of evaluating the function.
    */
  def apply(): Double

  /**
    * Returns the partial derivative of the function with-respect-to (wrt) the specified variable.
    *
    * @param wrt Variable to differentiate with-respect-to.
    * @return Partial derivative.
    */
  def d(wrt: Var): Differentiable

  /**
    * Returns the nth partial derivative of the function with-respect-to(wrt) the specified variable,
    * by repeatedly determining the partial derivative the specified number of 'times'.
    *
    * @param wrt Variable to differentiate with-respect-to.
    * @param times Number of times to differentiate; degree.
    * @return Partial derivative.
    */
  def d(wrt: Var, times: Int): Differentiable = {
    var func = this
    (0 until times).foreach(_ => func = func d (wrt))
    func
  }

  /**
    * The sum of two differentiable functions is differentiable. According to the Addition Rule,
    * [f(x) + g(x)] dx = f(x) dx + g(x) dx. Includes optimizations to automatically simplify
    * expressions involving constants.
    *
    * @param that Right-hand-side.
    * @return Differentiable sum of functions.
    */
  def +(that: Differentiable): Differentiable =
    (this, that) match {
      case (Const(0), _) => that
      case (_, Const(0)) => this
      case (Const(a), Const(b)) => Const(a + b)
      case _ => Add(this, that)
    }

  /**
    * The difference of two differentiable functions is differentiable. According to the Subtraction
    * Rule, [f(x) - g(x)] dx = f(x) dx - g(x) dx. Includes optimizations to automatically simplify
    * expressions involving constants.
    *
    * @param that Right-hand-side.
    * @return Differentiable difference of functions.
    */
  def -(that: Differentiable): Differentiable =
    (this, that) match {
      case (Const(0), _) => Const(-1) * that
      case (_, Const(0)) => this
      case (Const(a), Const(b)) => Const(a - b)
      case _ => Sub(this, that)
    }

  /**
    * The product of two differentiable functions is differentiable. According to the Multiplication
    * Rule, [f(x) - g(x)] dx = (f(x) * g(x) dx) + (f(x) dx + g(x)). Includes optimizations to
    * automatically simplify expressions involving constants.
    *
    * @param that Right-hand-side.
    * @return Differentiable product of functions.
    */
  def *(that: Differentiable): Differentiable =
    (this, that) match {
      case (Const(0), _) => Const(0)
      case (_, Const(0)) => Const(0)
      case (Const(1), _) => that
      case (_, Const(1)) => this
      case (Const(a), Const(b)) => Const(a * b)
      case _ => Mul(this, that)
    }

  /**
    * The quotient of two differentiable functions is differentiable. According to the Quotient Rule,
    * [f(x) / g(x)] dx = [f(x) dx * g(x) - f(x) * g(x) dx] / g(x)^2. Includes optimizations to
    * automatically simplify expressions involving constants.
    *
    * @param that Right-hand-side.
    * @return Differentiable quotient of functions.
    */
  def /(that: Differentiable): Differentiable =
    (this, that) match {
      case (Const(0), _) => Const(0)
      case (_, Const(1)) => this
      case (Const(a), Const(b)) => Const(a / b)
      case _ => Div(this, that)
    }

}

/* Nullary Differentiable Functions */
case class Const(value: Double) extends Differentiable {
  override def apply(): Double = value
  override def d(wrt: Var): Differentiable = Const(0)
}

case class Var(var value: Double) extends Differentiable {
  override def apply(): Double = value
  override def d(wrt: Var): Differentiable = if (wrt == this) Const(1) else Const(0)
}

/* Unary Differentiable Functions */
case class Cos(f: Differentiable) extends Differentiable {
  override def apply(): Double = Math.cos(f())
  override def d(wrt: Var): Differentiable = Const(-1) * Sin(f) * f.d(wrt)
}

case class Sin(f: Differentiable) extends Differentiable {
  override def apply(): Double = Math.sin(f())
  override def d(wrt: Var): Differentiable = Cos(f) * f.d(wrt)
}

case class Tan(f: Differentiable) extends Differentiable {
  override def apply(): Double = Math.tan(f())
  override def d(wrt: Var): Differentiable = Secant(f) * Secant(f) * f.d(wrt)
}

case class Secant(f: Differentiable) extends Differentiable {
  override def apply(): Double = 1.0 / Math.cos(f())
  override def d(wrt: Var): Differentiable = Secant(f) * Tan(f) * f.d(wrt)
}

case class Cosecant(f: Differentiable) extends Differentiable {
  override def apply(): Double = 1.0 / Math.sin(f())
  override def d(wrt: Var): Differentiable = Const(-1) * Cosecant(f) * Cotangent(f) * f.d(wrt)
}

case class Cotangent(f: Differentiable) extends Differentiable {
  override def apply(): Double = 1.0 / Math.tan(f())
  override def d(wrt: Var): Differentiable = Const(-1) * Cosecant(f) * Cosecant(f) * f.d(wrt)
}

case class Exp(f: Differentiable) extends Differentiable {
  override def apply(): Double = Math.exp(f())
  override def d(wrt: Var): Differentiable = Exp(f) * f.d(wrt)
}

case class Log(f: Differentiable) extends Differentiable {
  override def apply(): Double = Math.log(f())
  override def d(wrt: Var): Differentiable = Pow(f, -1) * f.d(wrt)
}

case class Pow(f: Differentiable, degree: Double)
    extends Differentiable {
  override def apply(): Double = Math.pow(f(), degree)
  override def d(wrt: Var): Differentiable = Const(degree) * Pow(f, degree - 1) * f.d(wrt)
}

/* Binary Differentiable Functions */
case class Add(f1: Differentiable, f2: Differentiable) extends Differentiable {
  override def apply(): Double = f1() + f2()
  override def d(wrt: Var): Differentiable = f1.d(wrt) + f2.d(wrt)
}

case class Sub(f1: Differentiable, f2: Differentiable) extends Differentiable {
  override def apply(): Double = f1() - f2()
  override def d(wrt: Var): Differentiable = f1.d(wrt) - f2.d(wrt)
}

case class Mul(f1: Differentiable, f2: Differentiable) extends Differentiable {
  override def apply(): Double = f1() * f2()
  override def d(wrt: Var): Differentiable = (f1 * f2.d(wrt)) + (f1.d(wrt) * f2)
}

case class Div(f1: Differentiable, f2: Differentiable) extends Differentiable {
  override def apply(): Double = f1() / f2()
  override def d(wrt: Var): Differentiable = ((f1.d(wrt) * f2) - (f2.d(wrt) * f1)) / Pow(f2, 2)
}
