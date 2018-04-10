package scraper

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import play.api.libs.json.Json

object Scraper {

  def main(args: Array[String]) = {
    val doc = Jsoup.connect("http://bash.org.pl/latest/?page=1").get
    val post = extract(doc)
    val value = Json.toJson(post)
    println(value)
  }

  def extract(doc: Document): WebPost = {
    val elements = posts(doc)
    val element = elements.first()

    val id = idText(element)
    val point = pointText(element)
    val content = contentText(element)

    WebPost(id, point, content)
  }

  def idText(element: Element): String = element.select(".qid.click").text

  def contentText(element: Element): String = element.select(".quote.post-content.post-body").text

  def pointText(element: Element): String = element.select(".points").text

  def posts(doc: Document): Elements = doc.select(".q.post")

}

