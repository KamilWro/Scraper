package scraper

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer

object Scraper {
  var webPosts = ListBuffer[WebPost]()
  var length = 0
  var pageNumber = 0

  def main(args: Array[String]) = {
    val values = extractPosts(10)
    println(Json.toJson(values))
  }

  def extractPosts(n: Int): Seq[WebPost] = {
    while (length < n) {
      pageNumber += 1
      val document = getDocument()
      var posts = getPosts(document)

      var it = posts.listIterator()
      while (it.hasNext) {
        webPosts += extract(it.next)
        length += 1
      }

    }
    webPosts
  }

  def extract(element: Element): WebPost = {
    val id = idText(element)
    val point = pointText(element)
    val content = contentText(element)

    WebPost(id, point, content)
  }

  def idText(element: Element): String = element.select(".qid.click").text

  def contentText(element: Element): String = element.select(".quote.post-content.post-body").text

  def pointText(element: Element): String = element.select(".points").text

  def getPosts(doc: Document): Elements = doc.select(".q.post")

  def getDocument(): Document = Jsoup.connect("http://bash.org.pl/latest/?page=" + pageNumber).get

}

