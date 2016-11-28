package com.swara.learn.neural

import breeze.linalg.Tensor

trait Layer[I, O] {

  def forward(input: I): O

  def backward(seq: Seq[(I, O, O)]): Seq[I]

}