package scraper

import com.typesafe.scalalogging.LazyLogging
import scraper.model.WebPost

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class ContentExtractors extends LazyLogging {

  private implicit class FutureOps[T](val self: Future[T]) {
    def measureResponseTime(): Future[Long] = {
      val startTime = System.currentTimeMillis
      self.map(_ => System.currentTimeMillis - startTime)
    }
  }

  def processPages(pagesCount: Long): Future[List[Seq[WebPost]]] = {
    val processedPages = List.range(1, pagesCount + 1).map(pageNumber => processPage(pageNumber))
    val triedProcessedPages = Future.sequence(processedPages.map(futureToFutureTry))
    Statistics().log(triedProcessedPages)
    mapToWebPosts(triedProcessedPages)
  }

  private def processPage(pageNumber: Long): Future[(Seq[WebPost], Long)] = {
    val webPosts = Future {HtmlExtractor().extractPosts(pageNumber)}
    val responseTime = webPosts.measureResponseTime()
    webPosts.zip(responseTime)
  }

  private def futureToFutureTry[T](future: Future[T]): Future[Try[T]] =
    future.map(Success(_)).recover { case x => Failure(x) }

  def mapToWebPosts(triedProcessedPages: Future[List[Try[(Seq[WebPost], Long)]]]): Future[List[Seq[WebPost]]] =
    triedProcessedPages.map(_.filter(_.isSuccess).map { case Success(value) => value._1 })

  def mapToResponseTimes(triedProcessedPages: Future[List[Try[(Seq[WebPost], Long)]]]): Future[List[Long]] =
    triedProcessedPages.map(_.filter(_.isSuccess).map { case Success(value) => value._2 })

}

object ContentExtractors {
  def apply() = new ContentExtractors()
}