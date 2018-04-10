package scraper

import java.io.{FileWriter, IOException}

import com.typesafe.config.ConfigFactory
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
        
        printToConsole(scraper)
        writeToFile(jsValue.toString())
      } catch {
        case e: NumberFormatException => e.printStackTrace()
        case e: IOException => e.printStackTrace()
        case e: NullPointerException => e.printStackTrace()
      }
  }

  def writeToFile(jsValue: String): Unit = {
    val defaultConfig = ConfigFactory.parseResources("defaults.conf")
    val nameFile = defaultConfig.getString("conf.nameFile")
    val file = new FileWriter(nameFile)
    file.write(jsValue)
    file.close()
  }

  def printToConsole(scraper: Scraper): Unit = {
    println("Total number of pages downloaded:" + scraper.totalPageNumber())
    println("Total number of posts downloaded:" + scraper.totalPostNumber())
    println("Average pages download time:" + scraper.avgPageDownloadTime() + "ms")
    println("Average posts download time:" + scraper.avgPostDownloadTime() + "ms")
  }
}
