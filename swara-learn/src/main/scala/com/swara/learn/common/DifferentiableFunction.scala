package com.swara.learn.common

/**
 * An infinitely differentiable function from ℝ to ℝ. More precisely, this library is intended
 * to model real analytic functions (which are infinitely differentiable) but this distinction is
 * never explicitly enforced and is only really of interest to the mathematically inclined. The
 * purpose of this library is to implement automatic analytic differentiation; by phrasing
 * mathematical functions as compound operations on primitive analytic functions (cos, log, etc.)
 * we can take infinitely many derivatives without invoking expensive numerical methods.
 */
sealed trait DifferentiableFunction {

  def apply(): Double
  def deriv(wrt: Var): DifferentiableFunction
  def +(that: DifferentiableFunction) = Add(this, that)
  def -(that: DifferentiableFunction) = Sub(this, that)
  def *(that: DifferentiableFunction) = Mul(this, that)
  def /(that: DifferentiableFunction) = Div(this, that)

}

/* Nullary Differentiable Functions */
case class Const(value: Double) extends DifferentiableFunction {
  override def apply(): Double = value
  override def deriv(wrt: Var) = Const(0)
}

case class Var(var value: Double) extends DifferentiableFunction {
  override def apply(): Double = value
  override def deriv(wrt: Var) : DifferentiableFunction = if (wrt == this) Const(1) else Const(0)
}

/* Unary Differentiable Functions */
case class Cos(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = Math.cos(f())
  override def deriv(wrt: Var) = Const(-1) * Sin(f) * f.deriv(wrt)
}

case class Sin(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = Math.sin(f())
  override def deriv(wrt: Var) = Cos(f) * f.deriv(wrt)
}

case class Tan(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = Math.tan(f())
  override def deriv(wrt: Var) = Secant(f) * Secant(f) * f.deriv(wrt)
}

case class Secant(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = 1.0 / Math.cos(f())
  override def deriv(wrt: Var) = Secant(f) * Tan(f) * f.deriv(wrt)
}

case class Cosecant(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = 1.0 / Math.sin(f())
  override def deriv(wrt: Var) = Const(-1) * Cosecant(f) * Cotangent(f) * f.deriv(wrt)
}

case class Cotangent(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = 1.0 / Math.tan(f())
  override def deriv(wrt: Var) = Const(-1) * Cosecant(f) * Cosecant(f) * f.deriv(wrt)
}

case class Exp(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = Math.exp(f())
  override def deriv(wrt: Var) = Exp(f) * f.deriv(wrt)
}

case class Log(f: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = Math.log(f())
  override def deriv(wrt: Var) = Pow(f, -1) * f.deriv(wrt)
}

case class Pow(f: DifferentiableFunction, degree: Double) extends DifferentiableFunction {
  override def apply() = Math.pow(f(), degree)
  override def deriv(wrt: Var) = Const(degree) * Pow(f, degree - 1) * f.deriv(wrt)
}

/* Binary Differentiable Functions */
case class Add(f1: DifferentiableFunction, f2: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = f1() + f2()
  override def deriv(wrt: Var) = Add(f1.deriv(wrt), f2.deriv(wrt))
}

case class Sub(f1: DifferentiableFunction, f2: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = f1() - f2()
  override def deriv(wrt: Var) = Sub(f1.deriv(wrt), f2.deriv(wrt))
}

case class Mul(f1: DifferentiableFunction, f2: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = f1() * f2()
  override def deriv(wrt: Var) = Add(Mul(f1, f2.deriv(wrt)), Mul(f1.deriv(wrt), f2))
}

case class Div(f1: DifferentiableFunction, f2: DifferentiableFunction) extends DifferentiableFunction {
  override def apply() = f1() / f2()
  override def deriv(wrt: Var) = Div(Sub(Mul(f1.deriv(wrt), f2), Mul(f2.deriv(wrt), f1)), Mul(f2, f2))
}