package scraper

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable.ListBuffer

class Scraper {
  private var postsNumber: Long = 0
  private var pageNumber: Long = 0
  private var sumPagesDownloadTime: Long = 0
  private var sumPostsDownloadTime: Long = 0

  def extractPosts(n: Int): Seq[WebPost] = {
    clear()
    var webPosts = ListBuffer[WebPost]()

    while (postsNumber < n) {
      pageNumber += 1
      val document = getDocument(pageNumber)
      var posts = getPosts(document)

      var it = posts.listIterator()
      while (it.hasNext && postsNumber < n) {
        webPosts += extract(it.next)
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
    val id = idText(post).substring(1).toLong
    val point = pointText(post).toLong
    val content = contentText(post)

    WebPost(id, point, content)
  }

  private def idText(post: Element): String = post.select(".qid.click").text()

  private def contentText(post: Element): String = post.select(".quote.post-content.post-body").text

  private def pointText(post: Element): String = post.select(".points").text

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

  def avgPageDownloadTime(): Double = if (pageNumber == 0) 0d else sumPagesDownloadTime / pageNumber

  def avgPostDownloadTime(): Double = if (postsNumber == 0) 0d else sumPostsDownloadTime / postsNumber

  def totalPostNumber(): Long = postsNumber

  def totalPageNumber(): Long = pageNumber

}

object Scraper {
  def apply() = new Scraper()
}
