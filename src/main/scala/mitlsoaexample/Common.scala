package mitlsoaexample

import scala.concurrent._

///////////////////////////////////

trait AcceptRPC[State,Env] {
  def apply(incumbent: State, incomine: State, env: Env): Future[(State, Env)]
}

///////////////////////////////////

trait PerturbRPC {
  
  type State = String
  type Env = String
  
  def apply(incumbent: State, env: Env): Future[(State, Env)]
}

///////////////////////////////////

// End ///////////////////////////////////////////////////////////////
