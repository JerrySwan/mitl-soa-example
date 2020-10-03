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

object Client { 
  
  private implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /////////////////////////////////

//  def mkAccept[State,Env](serviceURL: String, timeout: Duration) = new Accept[State,Env] {
//
//    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
//    private val remote: AcceptRPC[State,Env] = client.createAPI[API]
//    
//    override def apply(incumbent: State, incoming: State, env: Env): Pair[State, Env] = {
//      type API = AcceptRPC[State,Env]
//      val result = Await.result( remote(incumbent, incoming, env), timeout ) // FIXME
//      Pair.of(result._1,result._2)
//    }  
//  }

  /////////////////////////////////

  def mkPerturb(serviceURL: String, timeout: Duration): Perturb[String,String] = new Perturb[String,String] {

    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
    private val remote: PerturbRPC = client.createAPI[PerturbRPC]
    
    override def apply(incumbent: String, env: String): Pair[String, String] = { 
      val result = Await.result( remote(incumbent, env), timeout )
      Pair.of(result._1,result._2)
    }  
  }

  /////////////////////////////////
  
  private def jsonSender(serviceURL:String): String => Future[Option[String]] = 
    (json: String) => Future { 
      val response = http(POST(serviceURL).contentType(APPLICATION_JSON).entity(json))
      if( response.status == OK ) Some( response.entityAsString ) else None
  }

  private val jsonSerializer = UpickleJSONSerializer()
}

// End ///////////////////////////////////////////////////////////////
