import play.api.libs.json.{Json, OWrites}

object WebPost {
  implicit val webPostFormat: OWrites[WebPost] = Json.writes[WebPost]
}

case class WebPost(id: Long, points: Long, content: String)