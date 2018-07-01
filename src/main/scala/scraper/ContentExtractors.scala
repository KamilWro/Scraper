package scraper

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ContentExtractors extends Actor with LazyLogging {

  private implicit class FutureOps[T](val self: Future[T]) {
    def measureResponseTime(): Future[Long] = {
      val startTime = System.currentTimeMillis
      self.map(_ => System.currentTimeMillis - startTime)
    }
  }

  override def receive: Receive = {
    case ContentExtractors.ProcessPages(pagesCount) => sender ! processPages(pagesCount)
  }

  private def processPages(pagesCount: Long) = {
    val processedPages = List.range(1, pagesCount + 1).map(pageNumber => processPage(pageNumber))
    val triedProcessedPages = Future.sequence(processedPages.map(Mapper.mapToFutureTry))
    Statistics().log(triedProcessedPages)
    Mapper.mapToWebPosts(triedProcessedPages)
  }

  private def processPage(pageNumber: Long) = {
    val webPosts = Future {HtmlExtractor().extractPosts(pageNumber)}
    val responseTime = webPosts.measureResponseTime()
    webPosts.zip(responseTime)
  }
}

object ContentExtractors {
  def props: Props = Props[ContentExtractors]

  case class ProcessPages(pagesCount: Long)

}