package scraper

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import scraper.model.WebPost

import scala.collection.JavaConverters._

class HtmlExtractor {

  def extractPosts(pageNumber: Long): Seq[WebPost] = {
    val document = getDocument(pageNumber)
    val posts = selectPosts(document)
    posts.map(post => extractPost(post))
  }

  private def extractPost(post: Element): WebPost = {
    val id = selectIdText(post).drop(1).toLong
    val point = selectPointText(post).toLong
    val content = selectContentText(post)

    WebPost(id, point, content)
  }

  private def selectIdText(post: Element): String = post.select(".qid.click").text

  private def selectContentText(post: Element): String = post.select(".quote.post-content.post-body").text

  private def selectPointText(post: Element): String = post.select(".points").text

  private def selectPosts(document: Document): List[Element] = document.select(".q.post").asScala.toList

  private def getDocument(pageNumber: Long): Document = Jsoup.connect(s"http://bash.org.pl/latest/?page=$pageNumber").get
}

object HtmlExtractor {
  def apply() = new HtmlExtractor()
}