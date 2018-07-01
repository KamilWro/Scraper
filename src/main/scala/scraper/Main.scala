package scraper

import scraper.model.WebPost
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    if (args.length != 1)
      logger.error("Expected one argument")
    else
      Try(args(0).toLong > 0) match {
        case Success(true) => processWeb(args(0).toLong)
        case Success(false) => logger.error("Number greater than zero was expected")
        case Failure(e) => logger.error("Integer was expected")
      }
  }

  private def processWeb(pagesCount: Long) = {
    val actorSystem = ActorSystem("ActorSystem")
    val contentExtractors = actorSystem.actorOf(ContentExtractors.props)
    extractPostsAndWriteToFile(pagesCount, contentExtractors).onComplete(_ => actorSystem.terminate())
    Await.ready(actorSystem.whenTerminated, Duration.Inf)
  }

  private def extractPostsAndWriteToFile(pagesCount: Long, contentExtractors: ActorRef) = {
    implicit val timeout = Timeout(5 seconds)
    val response = contentExtractors ? ContentExtractors.ProcessPages(pagesCount)
    val webPosts = response.flatMap { case fut: Future[List[Seq[WebPost]]] => fut }
    webPosts.map(value => writeToFile(Json.toJson(value.flatten).toString))
  }

  private def writeToFile(data: String) = {
    val fileName = ConfigFactory.parseResources("defaults.conf").getString("conf.fileName")
    Try(OutputFile(fileName).write(data)) match {
      case Success(_) => logger.info("The downloaded posts have been correctly saved to file.")
      case Failure(e) => logger.error(e.getMessage)
    }
  }
}
