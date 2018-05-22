import java.io.IOException

import org.jsoup.nodes.Element
import org.jsoup.parser.Tag
import org.scalatest.{FunSuite, PrivateMethodTester}
import scraper.model.WebPost
import scraper.{ContentExtractors, HtmlExtractor}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class HtmlExtractorTest extends FunSuite with PrivateMethodTester {

  test("extract posts with negative page number should throw Exception") {
    intercept[IOException] {
      HtmlExtractor().extractPosts(-1)
    }
  }

  test("extract posts with positive page number should return non empty list") {
    val webPost = ContentExtractors().processPages(1)
    Await.ready(webPost, Duration.Inf)
    val result = webPost.value.get
    assert(result.get.nonEmpty)
  }

  test("extract post with element containing post should return exactly one WebPost") {
    val element = new Element(Tag.valueOf("div"), "")
    element.append(
      "<div id=\"d4863013\" class=\"q post\">" +
        "<a class=\"qid click\" href=\"/4863013/\">#4863013</a>" +
        "<span class=\" points\">63</span>" +
        "<div class=\"quote post-content post-body\">test</div>" +
        "</div>"
    )

    val extractPost = HtmlExtractor().getClass.getDeclaredMethod("extractPost", classOf[Element])
    extractPost.setAccessible(true)
    val webPost = extractPost.invoke(HtmlExtractor(), element)
    assert(WebPost(4863013, 63, "test") === webPost)
  }

}