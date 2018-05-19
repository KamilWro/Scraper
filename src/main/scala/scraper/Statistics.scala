package scraper

import com.typesafe.scalalogging.LazyLogging
import scraper.model.WebPost

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}

class Statistics extends LazyLogging {

  private val contentExtractors = ContentExtractors()

  def log(triedProcessedPages: Future[List[Try[(Seq[WebPost], Long)]]]): Unit = {
    triedProcessedPages.map(_.filter(_.isFailure).foreach { case Failure(e) => logger.error(e.getMessage) })
    logAboutPages(triedProcessedPages)
    logAboutPosts(triedProcessedPages)
  }

  private def logAboutPages(triedProcessedPages: Future[List[Try[(Seq[WebPost], Long)]]]): Unit =
    contentExtractors.mapToResponseTimes(triedProcessedPages).foreach(times => {
      logger.info(s"Total number of pages downloaded: ${times.length}")
      if (times.nonEmpty) logger.info(s"Average pages download time: ${times.sum / times.length}ms")
    })

  private def logAboutPosts(triedProcessedPages: Future[List[Try[(Seq[WebPost], Long)]]]): Unit = {
    val responseTimes = contentExtractors.mapToResponseTimes(triedProcessedPages)
    contentExtractors.mapToWebPosts(triedProcessedPages).zip(responseTimes).foreach { case (posts, times) =>
      logger.info(s"Total number of posts downloaded: ${posts.flatten.length}")
      if (posts.flatten.nonEmpty) logger.info(s"Average posts download time: ${times.sum / posts.flatten.length}ms")
    }
  }

}

object Statistics {
  def apply() = new Statistics()
}