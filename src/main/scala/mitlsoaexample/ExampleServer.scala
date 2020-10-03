package mitlsoaexample

///////////////////////////////////

import io.github.shogowada.scala.jsonrpc._
import io.github.shogowada.scala.jsonrpc.server._
import io.github.shogowada.scala.jsonrpc.serializers._
import io.github.shogowada.scala.jsonrpc.serializers.UpickleJSONSerializer

import scala.concurrent._
import scala.concurrent.duration._
import scala.util._

import io.shaka.http._
import io.shaka.http.HttpServer
import io.shaka.http.Response.respond
import io.shaka.http.RequestMatching._
import io.shaka.http.HttpHeader.{ACCEPT, CONTENT_LENGTH, USER_AGENT}
import io.shaka.http.Request.{GET, HEAD, POST}
import io.shaka.http.Status._
import io.shaka.http.ContentType.APPLICATION_JSON

import org.mitlware.immutable._
import org.mitlware.util.Pair

///////////////////////////////////

object ExampleServer {
 
  // invoke the Choco CSP solver as if it is a perturbation operator ...
  
  def chocoSolverPerturb(timeout: Duration) = new Perturb[String,String] {

    override def apply(incumbent: String, env: String): Pair[String, String] = {
      
      val dir = System.getProperty( "user.dir" ) + "/resources/bin"
      val xcspFile = new java.io.File( s"${dir}/xcsp.xml".replace( '\\', '/' ) )
      new java.io.PrintWriter( xcspFile ) { write(env); close }
      
      runChocoSolver(xcspFile, timeout.toSeconds.toInt) match { 
        case None =>           Pair.of(incumbent,env)
        case Some(incoming) => Pair.of(incoming,env)
      }
    }    
  } 
  
  /////////////////////////////////
  
  def main(args: Array[String]): Unit = {

    val port = 8080
    val timeout: Duration = 30 seconds
    
    ///////////////////////////////
    
    val httpServer = HttpServer(port).handler {
      case request@POST("/apply/") => Server.respond(request, chocoSolverPerturb( timeout ), timeout)  
      case _ => io.shaka.http.Response.respond("""{"error":"not found"}""").status(NOT_FOUND)
    }

    ///////////////////////////////
    
    httpServer.start()
  }
}

// End ///////////////////////////////////////////////////////////////
