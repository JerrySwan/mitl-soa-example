package mitlsoaexample

import io.github.shogowada.scala.jsonrpc._
import io.github.shogowada.scala.jsonrpc.client._
import io.github.shogowada.scala.jsonrpc.serializers._ 
import io.github.shogowada.scala.jsonrpc.serializers.UpickleJSONSerializer
import io.github.shogowada.scala.jsonrpc.Types.JSONSender

import io.shaka.http.Http.http
import io.shaka.http.Status.{NOT_FOUND, OK}
import io.shaka.http.Request.{GET, POST}
import io.shaka.http.ContentType.APPLICATION_JSON

import scala.concurrent._
import scala.concurrent.duration._

import scala.util.{Success,Failure}

import org.mitlware.immutable._
import org.mitlware.util.Pair

///////////////////////////////////

object ExampleClient { 
  
  // Examples 14 and 15 from https://arxiv.org/pdf/2009.00514.pdf
  
  val xcspProblem = <instance format = "XCSP3" type ="CSP">
		<variables >
		<array id="x" size ="4"> 1..3 </array>
		</variables>
		<constraints>
		<intension> eq( add(x[0], x[1]), x[2]) </intension>
		</constraints>
	</instance >

  val xcspSolution = <instantiation>
		<list> x[0] x[1] x[2]</list>
		<values>1 1 3</values>
	</instantiation>

  /////////////////////////////////
  
  def main(args: Array[String]): Unit = {
    
    val serviceURL = "http://localhost:8080/apply/"
    val perturb = Client.mkPerturb( serviceURL, timeout = 30 seconds )
  
    println( s"solution before remote invocation:\n${xcspSolution}" ) 
    val perturbed = perturb( xcspSolution.toString, xcspProblem.toString )
    println( s"solution after remote invocation:\n${perturbed._1}" ) 
    
    println("All done.")
  }
}

// End ///////////////////////////////////////////////////////////////
