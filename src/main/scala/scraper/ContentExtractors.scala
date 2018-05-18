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
    val webPosts = List.range(0, pagesCount + 1).map(pageNumber => processPage(pageNumber))
    val triedWebPosts = Future.sequence(webPosts.map(futureToFutureTry))
    filterFutureSequence(triedWebPosts)
  }

  def filterFutureSequence(triedWebPosts: Future[List[Try[Seq[WebPost]]]]): Future[List[Seq[WebPost]]] = {
    triedWebPosts.map(_.filter(_.isSuccess).map { case Success(value) => value })
  }

  private def processPage(pageNumber: Long): Future[Seq[WebPost]] = Future {
    HtmlExtractor().extractPosts(pageNumber)
  }

  private def futureToFutureTry[T](future: Future[T]): Future[Try[T]] =
    future.map(Success(_)).recover { case x => Failure(x) }
}

object ContentExtractors {
  def apply() = new ContentExtractors()
}
