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

object Server {
 
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /////////////////////////////////
 
  def mkPerturb(impl: Perturb[String,String]): PerturbRPC = new PerturbRPC { 
    override def apply(incumbent: State, env: Env): Future[(State, Env)] = Future {
      val result = impl(incumbent,env)
      (result._1,result._2) 
    }
  }
  
  /////////////////////////////////
  
  def respond(request: Request, perturb: Perturb[String,String], timeout: Duration): Response = {  

    val jsonSerializer = UpickleJSONSerializer()
    
     val jsonRPCServer = JSONRPCServer(jsonSerializer)
     jsonRPCServer.bindAPI[PerturbRPC](Server.mkPerturb(perturb))
        
    val futureResponse = jsonRPCServer.receive(request.entityAsString).map {
      case Some(responseJSON: String) => 
        io.shaka.http.Response.respond(responseJSON).contentType(APPLICATION_JSON)
      case None => 
        io.shaka.http.Response.respond(s"""{"error":"bad request"}""").status(BAD_REQUEST)
    } 
    Await.result( futureResponse, timeout )
  }
}

// End ///////////////////////////////////////////////////////////////
