package scraper

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

class Scraper {
  private var postsNumber: Long = 0
  private var pageNumber: Long = 0
  private var sumPagesDownloadTime: Long = 0
  private var sumPostsDownloadTime: Long = 0

  def extractPosts(n: Long): Seq[WebPost] = {
    clear()
    var webPosts = ListBuffer[WebPost]()

    while (postsNumber < n) {
      pageNumber += 1
      val document = getDocument(pageNumber)
      var posts = getPosts(document).asScala.toList
      for (post <- posts if postsNumber < n) {
        webPosts += extract(post)
        postsNumber += 1
      }
    }

    webPosts
  }

  private def clear(): Unit = {
    postsNumber = 0
    pageNumber = 0
    sumPagesDownloadTime = 0
    sumPostsDownloadTime = 0
  }

  private def extract(post: Element): WebPost = {
    val id = getIdText(post).substring(1).toLong
    val point = getPointText(post).toLong
    val content = getContentText(post)

    WebPost(id, point, content)
  }

  private def getIdText(post: Element): String = post.select(".qid.click").text()

  private def getContentText(post: Element): String = post.select(".quote.post-content.post-body").text

  private def getPointText(post: Element): String = post.select(".points").text

  private def getPosts(document: Document): Elements = {
    val millisActualTime = System.currentTimeMillis
    val posts = document.select(".q.post")
    sumPostsDownloadTime += System.currentTimeMillis - millisActualTime

    posts
  }

  private def getDocument(pageNumber: Long): Document = {
    val millisActualTime = System.currentTimeMillis
    val document = Jsoup.connect("http://bash.org.pl/latest/?page=" + pageNumber).get
    sumPagesDownloadTime += System.currentTimeMillis - millisActualTime

    document
  }

  def avgPageDownloadTime: Double = if (pageNumber == 0) 0d else sumPagesDownloadTime / pageNumber

  def avgPostDownloadTime: Double = if (postsNumber == 0) 0d else sumPostsDownloadTime / postsNumber

  def totalPostNumber: Long = postsNumber

  def totalPageNumber: Long = pageNumber

}

object Scraper {
  def apply() = new Scraper()
}
