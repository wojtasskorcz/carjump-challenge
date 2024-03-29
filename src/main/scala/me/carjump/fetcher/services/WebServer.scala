package me.carjump.fetcher.services

import akka.pattern.ask
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import me.carjump.fetcher.AppProps
import me.carjump.fetcher.actors.CacheActor.GetElement

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._

class WebServer(cache: ActorRef)(implicit system: ActorSystem) {
  private implicit val materializer = ActorMaterializer()
  private implicit val askTimeout = Timeout(5.seconds)

  private val route =
    get {
      path(IntNumber) { index =>
        val future = (cache ? GetElement(index)).mapTo[Option[String]]
        onComplete(future) {
          case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          case Success(elementOpt) => elementOpt match {
            case Some(element) => complete(element)
            case None => complete(StatusCodes.NotFound)
          }
        }
      }
    }

  def run(): Future[ServerBinding] = Http().bindAndHandle(route, AppProps.httpHost, AppProps.httpPort)

}
