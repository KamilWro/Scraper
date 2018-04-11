package scraper

import java.io.IOException

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

class Scraper {
  private var postsNumber: Long = 0
  private var pageNumber: Long = 0
  private var sumPagesDownloadTime: Long = 0
  private var sumPostsDownloadTime: Long = 0

  def extractPosts(maxPages: Long): Seq[WebPost] = {
    clear()
    val webPosts = ListBuffer[WebPost]()

    try {
      while (pageNumber <= maxPages) {
        pageNumber += 1
        val document = getDocument(pageNumber)
        val posts = getPosts(document)
        posts.foreach(post => {
          webPosts += extract(post)
          postsNumber += 1
        })
      }
    }
    catch {
      case e: IOException => System.err.println("Page number " + pageNumber + " is not available");
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

  private def getPosts(document: Document): List[Element] = {
    val millisActualTime = System.currentTimeMillis
    val posts = document.select(".q.post")
    sumPostsDownloadTime += System.currentTimeMillis - millisActualTime

    posts.asScala.toList
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

  def totalPageNumber: Long = pageNumber - 1

}

object Scraper {
  def apply() = new Scraper()
}
