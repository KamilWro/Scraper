package scraper

import java.io.{FileWriter, IOException}

import play.api.libs.json.Json


object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 1)
      System.err.println("expected one argument")
    else
      try {
        val n = args(0).toInt
        val scraper = Scraper()
        val webPosts = scraper.extractPosts(n)
        val jsValue = Json.toJson(webPosts)
        writeToFile(jsValue.toString())

      } catch {
        case e: NumberFormatException => e.printStackTrace()
        case e: IOException => e.printStackTrace()
        case e: NullPointerException => e.printStackTrace()
      }
  }

  def writeToFile(jsValue: String): Unit = {
    val file = new FileWriter("file.txt")
    file.write(jsValue)
    file.close()
  }
}
