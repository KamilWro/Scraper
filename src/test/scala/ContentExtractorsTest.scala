import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfter, FunSuite}
import scraper.ContentExtractors
import akka.pattern.ask
import scraper.model.WebPost

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

class ContentExtractorsTest extends FunSuite with BeforeAndAfter{

  implicit val timeout = Timeout(5 seconds)
  var actorSystem:ActorSystem = _
  var contentExtractors: ActorRef = _

  before {
    actorSystem = ActorSystem("ActorSystem")
    contentExtractors = actorSystem.actorOf(ContentExtractors.props)
  }

  after {
    actorSystem.terminate()
  }

  test("process pages with negative pages count should return empty list") {
    val response = contentExtractors ? ContentExtractors.ProcessPages(-1)
    val webPost = response.flatMap { case fut: Future[List[Seq[WebPost]]] => fut }
    Await.ready(webPost, Duration.Inf)

    assert(webPost.value.get == Success(List.empty))
  }

  test("process pages with positive pages count should return non empty list") {
    val response = contentExtractors ? ContentExtractors.ProcessPages(1)
    val webPost = response.flatMap { case fut: Future[List[Seq[WebPost]]] => fut }
    Await.ready(webPost, Duration.Inf)

    val result = webPost.value.get
    assert(result.get.nonEmpty)
  }


}
