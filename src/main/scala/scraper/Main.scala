package scraper

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

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
    val webPosts = ContentExtractors().processPages(pagesCount)
    val writer = webPosts.map(value => writeToFile(Json.toJson(value.flatten).toString()))
    Await.ready(writer, Duration.Inf)
  }

  private def writeToFile(data: String) = {
    val fileName = ConfigFactory.parseResources("defaults.conf").getString("conf.fileName")
    Try(OutputFile(fileName).write(data)) match {
      case Success(_) => logger.info("The downloaded posts have been correctly saved to file.")
      case Failure(e) => logger.error(e.getMessage)
    }
  }
}
