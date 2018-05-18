package scraper

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import scraper.model.WebPost

import scala.collection.JavaConverters._

class HtmlExtractor {

  def extractPosts(pageNumber: Long): Seq[WebPost] = {
    val document = getDocument(pageNumber)
    val posts = getPosts(document)
    posts.map(post => extractPost(post))
  }

  private def extractPost(post: Element): WebPost = {
    val id = getIdText(post).substring(1).toLong
    val point = getPointText(post).toLong
    val content = getContentText(post)

    WebPost(id, point, content)
  }

  private def getIdText(post: Element): String = post.select(".qid.click").text()

  private def getContentText(post: Element): String = post.select(".quote.post-content.post-body").text

  private def getPointText(post: Element): String = post.select(".points").text

  private def getPosts(document: Document): List[Element] = document.select(".q.post").asScala.toList

  private def getDocument(pageNumber: Long): Document = Jsoup.connect("http://bash.org.pl/latest/?page=" + pageNumber).get
}

object HtmlExtractor {
  def apply() = new HtmlExtractor()
}