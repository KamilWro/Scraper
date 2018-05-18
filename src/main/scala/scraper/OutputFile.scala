package scraper

import java.io.FileWriter

class OutputFile(fileName: String) {

  def write(data: String): Unit =
    using(new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }

  private def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
    try f(param) finally param.close()
}

object OutputFile {
  def apply(fileName: String) = new OutputFile(fileName)
}
