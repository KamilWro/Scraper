import org.scalatest.FunSuite
import scraper.ContentExtractors

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Success

class ContentExtractorsTest extends FunSuite {

  test("process pages with negative pages count should return empty list") {
    val webPost = ContentExtractors().processPages(-1)
    Await.ready(webPost, Duration.Inf)
    assert(webPost.value.contains(Success(List.empty)))
  }

  test("process pages with positive pages count should return non empty list") {
    val webPost = ContentExtractors().processPages(1)
    Await.ready(webPost, Duration.Inf)
    val result = webPost.value.get
    assert(result.get.nonEmpty)
  }

}
