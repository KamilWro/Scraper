import java.io.FileWriter

import com.typesafe.config.ConfigFactory
import play.api.libs.json.Json

object Main extends App {
    if (args.length != 1)
      System.err.println("Expected one argument")
    else
      try {
        val maxPages = args(0).toLong
        val scraper = Scraper()
        val webPosts = scraper.extractPosts(maxPages)
        val jsValue = Json.toJson(webPosts)

        printToConsole(scraper)
        writeToFile(jsValue.toString())
      } catch {
        case e: Exception => e.printStackTrace()
      }

  def writeToFile(jsValue: String): Unit = {
    val defaultConfig = ConfigFactory.parseResources("defaults.conf")
    val fileName = defaultConfig.getString("conf.fileName")
    val file = new FileWriter(fileName)
    file.write(jsValue)
    file.close()
  }

  def printToConsole(scraper: Scraper): Unit = {
    println(s"Total number of pages downloaded: ${scraper.totalPageNumber}")
    println(s"Total number of posts downloaded: ${scraper.totalPostNumber}")
    println(s"Average pages download time: ${scraper.avgPageDownloadTime} ms")
    println(s"Average posts download time: ${scraper.avgPostDownloadTime} ms")
  }
}
