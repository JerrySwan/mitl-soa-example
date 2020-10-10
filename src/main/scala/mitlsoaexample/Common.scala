package mitlsoaexample

import scala.concurrent._

///////////////////////////////////

trait AcceptRPC {
  def apply(incumbent: State, incoming: State, env: Env): Future[(State, Env)]
}

///////////////////////////////////

trait CreateRPC {
  def apply(env: Env): Future[Pair[Entity, Env]]
}

///////////////////////////////////

trait EvaluateRPC {
	def apply(x: Entity, e: Env ): Future[(Value, Env)]
}

///////////////////////////////////

trait IsFinishedRPC {
	def apply(incumbent: State, env: Env): Future[(Boolean,Env)]
}

///////////////////////////////////

trait LocalityRPC {
  def apply(incumbent: State, env: Env): Future[(List[State],Env)]
}

///////////////////////////////////

trait PerturbRPC {
  def apply(incumbent: State, env: Env): Future[(State, Env)]
}

///////////////////////////////////

trait PreferRPC {
  def apply(left: State, right: State, env: Env): Future[(State, Env)]
}

// End ///////////////////////////////////////////////////////////////
