package scraper

import play.api.libs.json.Json

object WebPost {
  implicit val webPostFormat = Json.writes[WebPost]
}

case class WebPost(id: Long, points: Long, content: String)