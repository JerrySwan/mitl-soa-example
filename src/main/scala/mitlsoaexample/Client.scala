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
  
  def mkAccept(serviceURL: String, timeout: Duration) = new Accept[State,Env] {

    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
    private val remote: AcceptRPC = client.createAPI[AcceptRPC]
    
    override def apply(incumbent: State, incoming: State, env: Env): Pair[State, Env] = {
      val result = Await.result( remote(incumbent, incoming, env), timeout )
      Pair.of(result._1,result._2)
    }  
  }

  /////////////////////////////////
  
  def mkCreate(serviceURL: String, timeout: Duration) = new Create[Entity,Env] {
    
    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
    private val remote: CreateRPC = client.createAPI[CreateRPC]
    
    override def apply(env: Env): Pair[Entity, Env] = {
      val result = Await.result( remote(env), timeout )
      Pair.of(result._1,result._2)
    }
  }

  /////////////////////////////////
  
  def mkEvaluate(serviceURL: String, timeout: Duration) = new Evaluate[Entity,Value,Env] {
    
    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
    private val remote: EvaluateRPC = client.createAPI[EvaluateRPC]
    
    override def apply(x: Entity, env: Env): Pair[Value, Env] = {
      val result = Await.result( remote(x,env), timeout )
      Pair.of(result._1,result._2)
    }
  }

  /////////////////////////////////

  def mkIsFinished(serviceURL: String, timeout: Duration) = new IsFinished[State,Env] {
    
    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
    private val remote: IsFinishedRPC = client.createAPI[IsFinishedRPC]
    
    override def apply(x: State, env: Env): Pair[java.lang.Boolean, Env] = {
      val result = Await.result( remote(x,env), timeout )
      Pair.of(result._1,result._2)
    }
  }
  
  /////////////////////////////////
  
  def mkLocality(serviceURL: String, timeout: Duration) = new Locality[State,Env] {
    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
    private val remote: LocalityRPC = client.createAPI[LocalityRPC]
    
    override def apply(x: State, env: Env): Pair[java.util.List[State], Env] = {
      import scala.collection.JavaConversions._      
      val result = Await.result( remote(x,env), timeout )
      Pair.of(result._1,result._2)
    }
  }
  
  /////////////////////////////////

  def mkPerturb(serviceURL: String, timeout: Duration) = new Perturb[State,Env] {

    private val client = JSONRPCClient(jsonSerializer, jsonSender(serviceURL))
    private val remote: PerturbRPC = client.createAPI[PerturbRPC]
    
    override def apply(incumbent: State, env: Env): Pair[State, Env] = { 
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
