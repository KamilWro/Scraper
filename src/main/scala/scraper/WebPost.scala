package scraper

import play.api.libs.json.Json

object WebPost {
  implicit val webPostFormat = Json.writes[WebPost]
}

case class WebPost(id: String, points: String, content: String)