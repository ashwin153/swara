package com.swara.learn.neural

/**
 *
 * @param result
 * @param state
 * @tparam R
 * @tparam S
 */
case class Output[R, S](result: R, state: S)
