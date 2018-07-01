package scraper

import scraper.model.WebPost

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

object Mapper {

  def mapToWebPosts(triedProcessedPages: Future[List[Try[(Seq[WebPost], Long)]]]): Future[List[Seq[WebPost]]] =
    triedProcessedPages.map(_.filter(_.isSuccess).map { case Success(value) => value._1 })

  def mapToResponseTimes(triedProcessedPages: Future[List[Try[(Seq[WebPost], Long)]]]): Future[List[Long]] =
    triedProcessedPages.map(_.filter(_.isSuccess).map { case Success(value) => value._2 })

  def mapToFutureTry[T](future: Future[T]): Future[Try[T]] =
    future.map(Success(_)).recover { case x => Failure(x) }

}
