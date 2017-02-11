package com.swara.music

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class MusicElementTest extends FunSuite {

  test("Fragment duration is correctly calculated.") {
    import Key._

    val channels = Map(0 -> Phrase(0, Set(Voice(Seq(Chord(Set.empty, Length(1, 4)))))))
    val half     = Tempo(Length(2, 4), 0.5)
    val regular  = Tempo(Length(3, 4), 1.0)
    val double   = Tempo(Length(4, 4), 2.0)

    assert(Fragment(CMajor, regular, Map.empty).duration === (0 minutes))
    assert(Fragment(CMajor, half, channels).duration === (2 minutes))
    assert(Fragment(CMajor, regular, channels).duration === (1 minutes))
    assert(Fragment(CMajor, double, channels).duration === (30 seconds))
  }

  test("Key scale returns correct note pitches.") {
    import Key._
    import Pitch._

    assert(CMajor.scale == Seq(C, D, E, F, G, A, B))
    assert(AMinor.scale == Seq(A, B, C, D, E, F, G))
    assert(EMinor.scale == Seq(E, Fx, G, A, B, C, D))
    assert(FMajor.scale == Seq(F, G, A, Bb, C, D, E))
  }

  test("Pitch is only distinguishable by frequency.") {
    import Pitch._

    assert(Pitch(Gx, 3) === Pitch(Ab, 3))
    assert(Pitch(E,  2) === Pitch(Fb, 2))
  }

  test("Pitch is correctly ordered by frequency.") {
    import Pitch._

    assert(Pitch(C, 4) < Pitch(G, 5))
    assert(Pitch(G, 3) < Pitch(C, 4))
    assert(Pitch(C, 4) < Pitch(G, 4))
  }

  test("Pitch frequencies are correctly calculated.") {
    import Pitch._

    assert(Pitch(E, 3).frequency === Pitch(Fb, 3).frequency)
    assert(Pitch(A, 5).frequency === 440)
    assert(Pitch(C, 5).frequency === 261.625 +- 0.001)
    assert(Pitch(E, 4).frequency === 164.813 +- 0.001)
    assert(Pitch(C, 6).frequency === 523.251 +- 0.001)
  }

}
