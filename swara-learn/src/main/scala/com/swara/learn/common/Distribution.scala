
class Distribution[X] {

transition.probability(H_{t+1} | H_{t})
initial.probability(H_0)
Event[X]

transition: Distribution
emission: Distribution

implict def AnyToEvent[X](x: X): Event[X]

define P(A && B)
define P(!A)
define P(A || B)
define P(A | B)


  implicit class Event {

    def !: Event {

    }

    def &&(and: Event): Event = {
      Distribution.this.prob(this) *
    }

    def ||(or: Event): Event = {

    }

    def |(given: Event): Event = {

    }

  }

  val trie = Trie.empty[X, Int]

  val actual(x: X, given: Seq[X] = Seq.empty) = {
    this.trie.put(given :+ x, (s, v) => (s, v) match {
      case (_, None) => Some(1)
      case (_, Some(value)) => Some(value + 1)
     })
  }


  val probability(event: Event[X], given: Seq[X] = Seq.empty): Double = {


    val p = this.trie.get(x :+ given)

    P (a | b) = P (a) / P(a and b)


      probs.get(x).count() / probs.size()
    else



         ar num = Random.nextInt(cur.value.getOrElse(0))

             val iter = cur.children.iterator.dropWhile(child => {
               num -= child.value.getOrElse(0)
               num > 0
             })

             if (iter.hasNext) iter.next.symbol.get else cur.symbol.get
  }

  val random(rand: Random, given: Seq[X] = Seq.empty): X = {

  }

}