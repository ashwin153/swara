package com.swara.learn.neural.layers

import breeze.linalg._
import breeze.optimize.DiffFunction
import com.swara.learn.neural.Layer

/**
 * A feed-forward, fully-connected layer. A forward layer consists of a weight matrix, W, in which
 * W(i, j) indicates the weight of the ith neuron for the jth input, a bias vector, B, in which B(k)
 * indicates the bias of the kth neuron for all inputs, and a monotonically increasing, bounded,
 * differentiable activation function, A. The forward layer is essentially a linear combination
 * machine; for some input vector x, the output of the forward layer is A(Wx + B).
 *
 * @param weights
 * @param biases
 * @param activation
 */
class ForwardLayer(weights: Matrix, biases: Vector, activation: DiffFunction[Double]) {

  require(this.weights.rows == this.biases.length)

  def forward(input: Vector): Vector =
    (this.weights * input + this.biases).map(activation)

  def backward(examples: Seq[(Vector, Vector, Vector)]): Seq[Vector] = {
    // Calculate the gradients for each neuron, weight updates, and propagated error.
    val (gradient, updates, propagated) = examples.map { case (input, _, error) =>
      val gradient = (this.weights * input + this.biases).map(activation.gradientAt) :* error
      val updates = gradient.asDenseMatrix * input.toDenseMatrix.t
      val propagated = this.weights.t * gradient
      (gradient, updates, propagated)
    }.unzip3

    // Update biases and weights using the calculated neuron gradients and weight updates.
    this.biases -= gradient
    this.weights -= updates

    // Propagate the calculated error to the previous layer.
    propagated

//    val (dX, dW, external) = seq.map { case ((in, out), error) ⇒
//      val dFdX = act.diffAtY(out)
//      /*
//       * Chain Rule : dG/dX_ij = tr[ ( dG/dF ).t * dF/dX_ij ].
//       *
//       * Note 1. X, dG/dF, dF/dX_ij are row vectors. Therefore tr(.) can be omitted.
//       *
//       * Thus, dG/dX = [ (dG/dF).t * dF/dX ].t, because [...] is 1 × fanOut matrix.
//       * Therefore dG/dX = dF/dX * dG/dF, because dF/dX is symmetric in our case.
//       */
//      val dGdX: DataVec = dFdX * error
//
//      /*
//       * Chain Rule : dG/dW_ij = tr[ ( dG/dX ).t * dX/dW_ij ].
//       *
//       * dX/dW_ij is a fan-Out dimension column vector with all zero but (i, 1) = X_j.
//       * Thus, tr(.) can be omitted, and dG/dW_ij = (dX/dW_ij).t * dG/dX
//       * Then {j-th column of dG/dW} = X_j * dG/dX = dG/dX * X_j.
//       *
//       * Therefore dG/dW = dG/dX * X.t
//       */
//      val dGdW: Matrix = dGdX * in.t
//
//      /*
//       * Chain Rule : dG/dx_ij = tr[ ( dG/dX ).t * dX/dx_ij ].
//       *
//       * X is column vector. Thus j is always 1, so dX/dx_i is a W_?i.
//       * Hence dG/dx_i = tr[ (dG/dX).t * dX/dx_ij ] = (W_?i).t * dG/dX.
//       *
//       * Thus dG/dx = W.t * dG/dX
//       */
//      val dGdx: DataVec = weight.value.t * dGdX
//
//      (dGdX, dGdW, dGdx)
//    }.unzip3
//
//    (external, ParSeq(bias -= dX, weight -= dW))
  }
//  def backward(seq: Seq[(Vec, Vec, Vec)]): Seq[Vec] = {
//
//  }

//    private final UnivariateDifferentiableFunction activation;
//    private final Stack<RealVector> history;
//    private RealMatrix weights;
//
//    public ForwardLayer(int inputs, int outputs, UnivariateDifferentiableFunction activation) {
//        super(inputs, outputs);
//        this.activation = activation;
//        this.history = new Stack<>();
//        this.weights = new Array2DRowRealMatrix(outputs, inputs + 1);
//    }
//
//    @Override
//    public RealVector forward(RealVector input) {
//        // Append a bias weight to the input, and calculate the linear combination.
//        final RealVector netj = this.history.push(this.weights.operate(input.append(1.0)));
//        return this.history.push(netj.map(this.activation));
//    }
//
//    @Override
//    public RealVector backward(RealVector error) {
//        final RealVector output = this.history.pop();
//        final RealVector input  = this.history.pop();
//
//        // Calculate the gradient of the activation function with respect to the last input to the
//        // activation (netj in wikipedia description).
//        final RealVector gradient = error.ebeMultiply(input.map(i -> this.activation
//            .value(new DerivativeStructure(1, 1, 0, i))
//            .getPartialDerivative(1)
//        ));
//
//        // Calculate the error that is propagated to the previous layer.
//        final RealVector next = this.weights.preMultiply(gradient);
//        this.weights = this.weights.add(gradient.outerProduct(output));
//        return next;
//    }

}
