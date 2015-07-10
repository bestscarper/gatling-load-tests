package bbc.mediaselector

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MediaSelector extends Simulation {

  before {
    import sys.process._
    "sh bin/mediaselector/pull-data-logs" !
  } 

  val httpProtocol = http
    .header("X-IP-Address", "")

  val open = csv("media-selector/open.csv").circular
  val secure = csv("media-selector/secure.csv").circular

  val scn = scenario("media-selector")
    .feed(open)
    .exec(http("open")
    .get("http://open.stage.cwwtf.bbc.co.uk${openUrl}")
    .check(status.is(200)))
    .feed(secure)
    .exec(http("secure")
    .get("https://ipsecure.stage.bbc.co.uk${secureUrl}")
    .check(status.is(200)))

  setUp(scn.inject(
      rampUsersPerSec(10) to(750) during(20 minutes) 
  ).protocols(httpProtocol))
}
