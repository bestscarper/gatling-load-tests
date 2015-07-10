package bbc.mediaselector

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MediaSelectorOpen extends Simulation {

  val httpProtocol = http
    .header("X-IP-Address", "") // https://api.stage.bbc.co.uk/mediaselector/kipps?ip=194.159.80.39

  val open = csv("media-selector/open.csv").circular

  val scn = scenario("media-selector")
    .feed(open)
    .exec(http("open")
    .get("http://open.stage.cwwtf.bbc.co.uk${openUrl}")
    .check(status.is(200)))

  setUp(scn.inject(
      rampUsersPerSec(10) to(750) during(20 minutes) 
  ).protocols(httpProtocol))
}
