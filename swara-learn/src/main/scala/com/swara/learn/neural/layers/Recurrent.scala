package com.swara.learn.neural
package layers

import breeze.numerics._

class Recurrent(
  forget: Matrix,
  select: Matrix,
  output: Matrix,
  update: Matrix,
  private[this] var memory: Vector,
  private[this] var result: Vector
) extends Layer[Vector, Vector] {

  override def apply(inputs: Seq[Vector]): Result[Seq[Vector], Seq[Vector]] = {
    inputs.map { input 


    val W = Matrix(
      tanh()
    )
  }

}

///**
// * A recurrent neural network. Recurrent neural networks are implemented using the famous long-short
// * term memory architecture (LSTM). An LSTM is capable of learning long-term relationships by
// * selectively remembering aspects of its memory. This selective memory is facilitated by a series
// * of gates: forget, select, output, and update.
// *
// * @param forget Forgets elements of the memory.
// * @param select Selects which elements of the memory receive updates.
// * @param output Determines which elements of the memory to output.
// * @param update Updates the values of elements of the memory.
// * @param memory Internal layer memory.
// * @param result Previous output of the layer.
// */
//class Recurrent(
//  forget: FeedForward,
//  select: FeedForward,
//  output: FeedForward,
//  update: FeedForward,
//  private[this] var memory: Vector,
//  private[this] var result: Vector
//) extends Layer[Vector, Vector] {
//
//  override def apply(inputs: Seq[Vector]): Result[Seq[Vector], Seq[Vector]] = {
//    // Incrementally build the backward function; this backward function takes the errors with
//    // respect to the input, result, and memory and returns the errors with respect to the input,
//    // result, and memory for the previous timestep. (dI, dH, dM) => (dI, dH, dM)
//
//
//
//    inputs.map { input =>
//      val x = Vector.vertcat(input, this.result)
//      val c = this.memory
//      val f = this.forget(x)
//      val s = this.select(x)
//      val u = this.update(x)
//      val o = this.output(x)
//
//      this.memory = (f.forward :* this.memory) + (s.forward :* u.forward)
//      this.result = o.forward :* tanh(this.memory)
//
//      // WE NEED TO SUM ALL WEIGHT UPDATES. RIGHT NOW THEY ARE HAPPENING EACH ITERATION.
//
//      (this.result, { (dI: Vector, dH: Vector, dC: Vector) =>
//        val dM = dC + (dH :* o.forward :* (1 - (tanh(this.memory) :^ 2)))   // Error Memory
//        val dF = f.backward(dM :* c)                                        // Error Forget
//        val dU = u.backward(dM :* s.forward)                                // Error Update
//        val dS = s.backward(dM :* u.forward)                                // Error Select
//        val dO = o.backward(dH :* tanh(this.memory))                        // Error Output
//        val dP = dF + dU + dS + dO                                          // Error Previous
//        (dI + dP.slice(0, input.length), dP.slice(input.length, dP.length), dM :* f.forward)
//      })
//    }
//
//
//  }
//
//    // Incrementally build the backward function. The backward function (errI, errH, errC) => (errI, errH, errC)
//    // takes some errors with respect to the input, result, and memory and returns the errors with
//    // respect to the input, result, and memory for the previous timestep.
//    val outcome = inputs.map { input =>
//      // Calculate and save all intermediate computations.
//      val x = Vector.vertcat(input, this.result)
//      val c = this.memory
//      val f = this.forget(x)
//      val i = this.select(x)
//      val a = this.update(x)
//      val o = this.output(x)
//
//      // Selectively update the memory and determine the result.
//      this.memory = (f.forward :* this.memory) + (a.forward :* i.forward)
//      this.result = o.forward :* tanh(this.memory)
//
//
//    { (deltaH: Vector, deltaC: Vector) =>
//
//      val gradO = deltaH :* tanh(this.memory)
//      val gradC = deltaC + (deltaH :* o.forward :* (1 - (tanh(this.memory) :^ 2)))
//      val gradI = gradC :* a
//      val gradF = gradC :* c
//      val gradA = gradC :* i
//
//      val propagate = a.backward(gradA) + f.backward(gradF) + i.backward(gradI) + o.backward(gradO)
//      val deltaCToPassOn = gradC :* f
//      val deltaHToPassOn = propagate.slice(input.length, propagate.length)
//      val deltaInputToPassOn = propagate.slice(0, input.length)
//
//    }
//
//
//      (c, this.memory, f, i, a, o, this.result)
//    }
//
//    Result(outcome.map(_._6), { errors: Seq[Vector] =>
//      var memoryErr = 0
//
//      outcome.zip(errors).reverseIterator.map { case (outcome, err) =>
//          outcome._5.backward(err * tanh(outcome.))
//
//      }
//    })
//  }

//  override def apply(inputs: Seq[Vector]): Result[Vector, Vector] = {
//    val (c, f, i, a, o, h) = inputs.map { x =>
//      // Input is a concatenation of previous output and new input.
//      val in: Vector = Vector.vertcat(x, this.result)
//
//      // Perform intermediate calculations.
//      val c: Vector = memory
//      val f: Result[Vector, Vector] = this.forget(in)
//      val i: Result[Vector, Vector] = this.select(in)
//      val a: Result[Vector, Vector] = this.update(in)
//      val o: Result[Vector, Vector] = this.output(in)
//
//      // Update state as a linear combination of recalled and updated state.
//      this.memory = (f.forward :* this.memory) :+ (a.forward :* i.forward)
//      this.result = o.forward :* tanh(this.memory)
//
//      (c, f, i, a, o, result)
//    }.unzip
//
//    Result(h, { errors: Seq[Vector] =>
//
//      errors
//    })

//    inputs.foreach { x =>
//
//
//      // Update the state as a linear combination of recalled state and detected updates.
//      val f: Result[Vector, Vector] = this.forget(in)
//      val i: Result[Vector, Vector] = this.input(in)
//      val d: Result[Vector, Vector] = this.detect(in)
//      val o: Result[Vector, Vector] = this.output(in)
//
//      val recall = f.forward :* this.state
//      val update = d.forward :* i.forward
//      this.state = recall :+ update
//
//      // Selectively output normalized elements of the state.
//      this.previous = o.forward :* tanh(this.state)
//      this.previous
//    }

  }

  //  def forward(x: Vector): Vector = {
//    // Input is a concatenation of previous output and new input.
//    val concat = Vector.vertcat(this.memory, x)
//
//    // Update the state as a linear combination of recalled state and detected updates.
//    val recall = this.forget.forward(concat) :* this.state
//    val update = this.detect.forward(concat) :* this.input.forward(concat)
//    this.state = recall :+ update
//
//    // Selectively output normalized elements of its state.
//    this.memory = this.output.forward(concat) :* tanh(this.state)
//    this.memory
//  }
//
//  def backward(examples: Seq[(Vector, Vector)]): Seq[Vector] = {
//    // Output Layer Error (o_t) = error_t :* tanh(state_t)
//    // State Error (c_t) += error_t :* output_t :* (1 - tanh^2(state_t))
//    // Input Layer Error (i_t) = c_t :* detect_t
//    // Forget Layer Error (f_t) = c_t :* state_(t-1)
//    // Detect Layer Error (d_t) = c_t :* input_t
//    // Previous State Error (c_(t-1)) = c_t :* forget_t
//  }
}

object Recurrent {

  /**
   *
   * @param in
   * @param out
   * @return
   */
  def apply(in: Int, out: Int): Recurrent = new Recurrent(
    FeedForward.sigmoid(in + out, out),
    FeedForward.sigmoid(in + out, out),
    FeedForward.sigmoid(in + out, out),
    FeedForward.tanh(in + out, out),
    Vector.zeros(out),
    Vector.zeros(out)
  )

}
