package scraper

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable.ListBuffer

class Scraper {
  def extractPosts(n: Int): Seq[WebPost] = {
    var webPosts = ListBuffer[WebPost]()
    var length = 0
    var pageNumber = 0

    while (length < n) {
      pageNumber += 1
      val document = getDocument(pageNumber)
      var posts = getPosts(document)

      var it = posts.listIterator()
      while (it.hasNext) {
        webPosts += extract(it.next)
        length += 1
      }

    }
    webPosts
  }

  private def extract(element: Element): WebPost = {
    val id = idText(element).substring(1).toLong
    val point = pointText(element).toLong
    val content = contentText(element)

    WebPost(id, point, content)
  }

  private def idText(element: Element): String = element.select(".qid.click").text()

  private def contentText(element: Element): String = element.select(".quote.post-content.post-body").text

  private def pointText(element: Element): String = element.select(".points").text

  private def getPosts(doc: Document): Elements = doc.select(".q.post")

  private def getDocument(pageNumber: Int): Document = Jsoup.connect("http://bash.org.pl/latest/?page=" + pageNumber).get

}

object Scraper {
  def apply() = new Scraper()
}
