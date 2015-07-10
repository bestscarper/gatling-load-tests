package bbc.mediaselector

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MediaSelector2379 extends Simulation {

  val httpProtocol = http
    .header("X-IP-Address", "")
  // https://api.stage.bbc.co.uk/mediaselector/kipps?ip=194.159.80.39

  val open = csv("media-selector/open.csv").circular

  val countStageServers = 4
  val countProdServers = 16
  val prodPeakRate = 1600
  val prodFailureRate = 4000
  val stagePeakRate = prodPeakRate * countStageServers / countProdServers
  val stageFailureRate = prodFailureRate * countStageServers / countProdServers

  val scn = scenario("media-selector")
    .feed(open)
    .exec(http("open")
    .get("http://open.stage.cwwtf.bbc.co.uk${openUrl}")
    .check(status.is(200)))

  setUp(scn.inject(
      rampUsersPerSec(11) to(stagePeakRate)                 during(10 minutes) randomized,
      constantUsersPerSec(stagePeakRate)                    during(20 minutes) randomized,
      rampUsersPerSec(stagePeakRate) to(stageFailureRate)   during(10 minutes) randomized,
      constantUsersPerSec(stageFailureRate)                 during(20 minutes) randomized,
      rampUsersPerSec(stageFailureRate) to(stagePeakRate)   during(10 minutes) randomized,
      constantUsersPerSec(stagePeakRate)                    during(10 minutes) randomized
  ).protocols(httpProtocol))
}
