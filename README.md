# Scraper

#### Opis:
Scraper do strony [bash.org.pl](http://bash.org.pl), który ściąga `n` najnowszych stron i parsuje do jsona w formie:  
{
   "id": x,
   "points": y,
   "content": z
}, gdzie `x` - id wpisu (typu `Long`), `y` - zdobyte punkty (typu `Long `), `z` - treść wpisu (typu `String`).
  
  
Po zakończeniu crawlowania na konsoli wyświetlane są podstawowe statystyki (liczba pozyskanych wpisów, średni czas pozyskiwania jednego wpisu, średni czas pozyskiwania jednej strony).

#### Technologie
- JSoup
- Typesafe config
- Play Json
- Scala-logging
- ScalaTest
- Future
- Akka

#### Kompilacja i uruchmienie:
`$ sbt`  
`sbt:Scraper> run n`
