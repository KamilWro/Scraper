# Scraper
Scraper do strony [bash.org.pl](http://bash.org.pl), który ściąga n najnowszych stron i parsuje do jsona w formie:</br>
{
   "id": x,
   "points": y,
   "content": z
},gdzie `x` to id wpisu jako Long, `y` punkty jako Long i `z` treść jako String.
</br></br>
Po zakończeniu crawlowania na konsoli wyświetlane są podstawowe statystyki (liczba pozyskanych wpisów, średni czas pozyskiwania jednego wpisu, średni czas pozyskiwania jednej strony).

### Technologie
- Jsoup
- Typesafe config
- Play Json

### Kompilacja i uruchmienie:
`$ sbt` </br>
`sbt:SimpleScraper> run n`
