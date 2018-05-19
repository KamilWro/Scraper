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
    logStatistics(triedWebPosts)
    filterFutureSequence(triedWebPosts)
  }

  private def processPage(pageNumber: Long): Future[(Seq[WebPost], Long)] = {
    val webPosts = Future {
      HtmlExtractor().extractPosts(pageNumber)
    }
    val responseTime = webPosts.measureResponseTime()
    webPosts.zip(responseTime)
  }

  private def futureToFutureTry[T](future: Future[T]): Future[Try[T]] =
    future.map(Success(_)).recover { case x => Failure(x) }

  private def logStatistics(triedWebPosts: Future[List[Try[(Seq[WebPost], Long)]]]) = {
    val responseTimes = triedWebPosts.map(_.filter(_.isSuccess).map { case Success(value) => value._2 })
    responseTimes.map(times => {
      logger.info(s"Total number of pages downloaded: ${times.length}")
      logger.info(s"Average pages download time: ${times.sum / times.length}ms")
    })

    val webPosts = filterFutureSequence(triedWebPosts)
    webPosts.zip(responseTimes).map { case (posts, times) =>
      logger.info(s"Total number of posts downloaded: ${posts.flatten.length}")
      logger.info(s"Average posts download time: ${times.sum / posts.flatten.length}ms")
    }
  }

  private def filterFutureSequence(triedWebPosts: Future[List[Try[(Seq[WebPost], Long)]]]): Future[List[Seq[WebPost]]] =
    triedWebPosts.map(_.filter(_.isSuccess).map { case Success(value) => value._1 })
}

object ContentExtractors {
  def apply() = new ContentExtractors()
}