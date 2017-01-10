package com.swara.learn.neural
package layers

import com.swara.learn.neural.math._

// x1, x2, x3, x4, x5
// h0, h1, h2, h3, h4
// backpropagate errors backward

class Recurrent(
  forget: FeedForward,
  select: FeedForward,
  output: FeedForward,
  update: FeedForward,
  private[this] var memory: Vector,
  private[this] var result: Vector
) extends Layer[Vector, Vector] {

  override def apply(batch: Seq[Vector]): Result[Seq[Vector], Seq[Vector]] = {
    // Construct arrays to hold the results of intermediate computations; arrays are one-indexed.
    // Because the batch is zero-indexed, we will shift all its indexes over by one. Names of the
    // variables come directly from: http://arunmallya.github.io/writeups/nn/lstm/index.html.
    val x: Array[Vector] = Array.ofDim(batch.size + 1)
    val f: Array[Vector] = Array.ofDim(batch.size + 1)
    val i: Array[Vector] = Array.ofDim(batch.size + 1)
    val o: Array[Vector] = Array.ofDim(batch.size + 1)
    val a: Array[Vector] = Array.ofDim(batch.size + 1)
    val c: Array[Vector] = Array.ofDim(batch.size + 1)
    val h: Array[Vector] = Array.ofDim(batch.size + 1)

    c(0) = this.memory
    h(0) = this.result

    // Calculate the forward result for all vectors in the batch. For each input vector, update the
    // memory from a combination of recalled memory and selected updates; then, determine the result
    // by outputting parts of the memory.
    (1 to batch.size).foreach { t =>
      x(t) = Vector.vertcat(batch(t - 1), this.result)
      f(t) = this.forget.forward(x(t))
      i(t) = this.select.forward(x(t))
      o(t) = this.output.forward(x(t))
      a(t) = this.update.forward(x(t))
      c(t) = (f(t) :* c(t-1)) + (i(t) :* a(t))
      h(t) = Tanh(c(t)) :* o(t)
    }

    this.memory = c.last
    this.result = h.last

    Result(h.toList.drop(1), { errors: Seq[Vector] =>
      // Construct arrays to hold the intermediate gradient vectors and weight update matrices; this
      // allows us to properly apply the weight updates after all errors have been backpropagated.
      val dC: Array[Vector] = Array.ofDim(batch.size + 1)
      val dH: Array[Vector] = Array.ofDim(batch.size + 1)
      val dF: Array[Matrix] = Array.ofDim(batch.size + 1)
      val dS: Array[Matrix] = Array.ofDim(batch.size + 1)
      val dO: Array[Matrix] = Array.ofDim(batch.size + 1)
      val dU: Array[Matrix] = Array.ofDim(batch.size + 1)

      dC(dC.length - 1) = Vector.zeros(this.memory.length)
      dH(dH.length - 1) = errors.last

      // Backpropagate errors through all timesteps, and return the error with respect to inputs at
      // each timestep.
      val propagate = (batch.size to 1 by -1).map { t =>
        dC(t) += dH(t) :* o(t) :* Tanh.gradient(c(t))
        val (dFdI, dFdW) = this.forget.backward(x(t), dC(t) :* c(t-1))
        val (dSdI, dSdW) = this.select.backward(x(t), dC(t) :* a(t))
        val (dOdI, dOdW) = this.output.backward(x(t), dH :* Tanh(c(t)))
        val (dUdI, dUdW) = this.update.backward(x(t), dC(t) :* i(t))
        val dI = dFdI + dSdI + dOdI + dUdI

        dF(t) = dFdW
        dS(t) = dSdW
        dO(t) = dOdW
        dU(t) = dUdW
        dC(t-1) = dC(t) :* f(t)
        dH(t-1) = dI(this.memory.length until dI.length)

        dI(0 until this.memory.length)
      }

      // Update the weights of the gates.
      this.forget.weights -= dF.sum
      this.select.weights -= dS.sum
      this.output.weights -= dO.sum
      this.update.weights -= dU.sum

      // Backpropagate errors.
      propagate
    })
  }
}

object Recurrent {

  case class Gate {

    def forward(x: Vector): Vector = {
      require(x.length == this.weights.cols - 1, "Invalid input dimensions.")
      val biased = Vector.vertcat(x, Vector.ones(1))
      this.activation(this.weights * biased)
    }

    def backward(x: Vector, error: Vector): (Vector, Matrix) = {
      require(x.length == this.weights.cols - 1, "Invalid input dimensions.")
      require(error.length == this.weights.rows, "Invalid error dimensions.")

      val gradient =
      val input = Vector.vertcat(x, Vector.ones(1)).asDenseMatrix
      val total = gradient :* error
      val propagate = this.weights.t * total
      val update = total.asDenseMatrix.t * input
      (propagate, update)
    }
  }
}
//    val x = mutable.ArrayBuffer.empty[Vector]   // Inputs
//    val f = mutable.ArrayBuffer.empty[Vector]   // Forgotten
//    val s = mutable.ArrayBuffer.empty[Vector]   // Selected
//    val o = mutable.ArrayBuffer.empty[Vector]   // Outputted
//    val u = mutable.ArrayBuffer.empty[Vector]   // Updated
//    val m = mutable.ArrayBuffer(this.memory)    // Memory
//    val r = mutable.ArrayBuffer.empty[Vector]   // Results
//
//    inputs.foreach { input =>
//      x += Vector.vertcat(input, this.result)
//      f += this.forget.forward(x.last)
//      s += this.select.forward(x.last)
//      o += this.select.forward(x.last)
//      u += this.update.forward(x.last)
//
//      this.memory = (f.last :* this.memory) + (s.last :* u.last)
//      this.result = Tanh(this.memory) :* o.last
//
//      m += this.memory
//      r += this.result
//    }
//
//    Result(r, { errors =>
//      var dF =
//    })

//  }

//    val forward = inputs.map { input =>
//      val x = Vector.vertcat(input, this.result)
//      val f = this.forget.forward(x)
//      val s = this.select.forward(x)
//      val o = this.output.forward(x)
//      val u = this.update.forward(x)
//      val p = this.memory.copy
//
//      this.memory = (f :* this.memory) + (s :* u)
//      this.result = Tanh(this.memory) :* o
//
//      (x, f, s, o, u, p, this.memory, this.result)
//    }
//
//    Result(forward.map(_._6), { errors: Seq[Vector] =>
//      var dFdW = Matrix.zeros(this.forget.weights.rows, this.forget.weights.cols)
//      var dSdW = Matrix.zeros(this.forget.weights.rows, this.forget.weights.cols)
//      var dOdW = Matrix.zeros(this.forget.weights.rows, this.forget.weights.cols)
//      var dUdW = Matrix.zeros(this.forget.weights.rows, this.forget.weights.cols)
//      var dH: Vector = Vector.zeros(this.memory.length)
//      var dM: Vector = Vector.zeros(this.memory.length)
//
//      forward.zip(errors).reverseIterator.map { case ((x, f, s, o, u, p, m, _), e) =>
//        val dMt = dM + (dH :* o :* Tanh.gradient(m))
//        val (dFdIt, dFdWt) = this.forget.backward(x, dMt :* p)
//        val (dSdIt, dSdWt) = this.select.backward(x, dMt :* u)
//        val (dOdIt, dOdWt) = this.output.backward(x, dH :* Tanh(this.memory))
//        val (dUdIt, dUdWt) = this.update.backward(x, dMt :* s)
//
//        dFdW += dFdWt
//        dSdW += dSdWt
//        dOdW += dOdWt
//        dUdW += dUdWt
//
//        val total = dFdIt + dSdIt + dOdIt + dUdIt
//        dM = dMt :* f
//        dH = total.slice(x.length, total.length)
//        total.slice(0, x.length)
//      }
//    })
//  }
//}











//      // Return the result and the backpropagation function; the back propagation function takes the
//      // error of the this layer with respect to its output and memory, and returns the error of the
//      // previous layer with respect to its input and memory.
//      (this.result, { (errIn: Vector, errOut: Vector, errMem: Vector) =>
//        val dM = errMem + (errOut :* outputted :* Tanh.gradient(this.memory))
//        val (dFdI, dFdW) = this.forget.backward(x, dM :* previous)
//        val (dSdI, dSdW) = this.select.backward(x, dM :* updated)
//        val (dOdI, dOdW) = this.output.backward(x, errOut :* Tanh(this.memory))
//        val (dUdI, dUdW) = this.update.backward(x, dM :* selected)
//
//        (dFdI + dSdI + dOdI + dUdI, dM :* forgotten
//        //        this.biases  -= gradient
////        this.weights -= gradient.asDenseMatrix.t * x.asDenseMatrix
//      })

//      val x = Vector.vertcat(input, this.result)
//      val c = this.memory
//      val f = sigmoid(this.forget * x)
//      val s = sigmoid(this.select * x)
//      val o = sigmoid(this.output * x)
//      val u = tanh(this.update * x)
//
//      this.memory = (f :* this.memory) + (s :* u)
//      this.result = o :* tanh(this.memory)
//
//      (this.result, { (dI: Vector, dH: Vector, dC: Vector) =>
//        val dM = dC + (dH :* o :* (1 - (tanh(this.memory) :^ 2)))
//        val dF = dM :* c :* f :* (1 - f)
//        val dS = dM :* u :* s :* (1 - s)
//        val dO = dH :* tanh(this.memory) :* o :* (1 - o)
//        val dU = dM :* s :* (1 - (u :^ 2))
//
//        // Vector.vertcat (dI, dH, bias)
//        //
//        (this.forget.t * dF) + (this.select * dS) + (this.output :* dO + this.update :* dU)
//
//      })

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
//  }
//}

//object Recurrent {
//
//  /**
//   *
//   * @param inputs
//   * @param outputs
//   * @param init
//   * @return
//   */
//  def apply(
//    inputs: Int,
//    outputs: Int
//  )(
//    init: Rand[Double] = Rand.uniform.map(x => (x * 2 - 1) / numerics.sqrt(inputs))
//  ): Recurrent = new Recurrent(
//    Matrix.rand(outputs, inputs + outputs + 1, init),
//    Matrix.rand(outputs, inputs + outputs + 1, init),
//    Matrix.rand(outputs, inputs + outputs + 1, init),
//    Matrix.rand(outputs, inputs + outputs + 1, init),
//    Vector.zeros(outputs),
//    Vector.zeros(outputs)
//  )
//
//}


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
//
//  }

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
//}
//
//object Recurrent {
//
//  /**
//   *
//   * @param in
//   * @param out
//   * @return
//   */
//  def apply(in: Int, out: Int): Recurrent = new Recurrent(
//    FeedForward.sigmoid(in + out, out),
//    FeedForward.sigmoid(in + out, out),
//    FeedForward.sigmoid(in + out, out),
//    FeedForward.tanh(in + out, out),
//    Vector.zeros(out),
//    Vector.zeros(out)
//  )
//
//}
