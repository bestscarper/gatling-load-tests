package bbc.lovebutton

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class LoveButton extends Simulation {
  
  val httpProtocol = http
    .baseURL("http://www.stage.bbc.co.uk")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-GB,en;q=0.5")
    .connection("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:35.0) Gecko/20100101 Firefox/35.0")
    .disableCaching

  val scn = scenario("Love Button")
    .exec(http("Love Button Page")
    .get("/modules/love/test/load").check(status.is(200))
    .resources(
      http("love.css").get("http://static.stage.bbci.co.uk/modules/love/0.2.0/style/love.css").check(status.is(200)),
      http("gelicons-social.ttf").get("http://static.stage.bbci.co.uk/gelstyles/0.8.2/fonts/gelicons-social.ttf").check(status.is(200)),
      http("bbclove.js").get("http://static.stage.bbci.co.uk/modules/love/0.2.0/modules/bbclove.js").check(status.is(200))))

  setUp(scn.inject(
    // 4 requests * 100 = 400 RPS
    rampUsersPerSec(10) to(100) during(10 minutes),
    constantUsersPerSec(100) during(10 minutes)
  ).protocols(httpProtocol))
}
