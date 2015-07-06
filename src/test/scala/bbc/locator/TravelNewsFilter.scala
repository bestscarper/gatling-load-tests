package bbc.locator

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TravelNewsFilter extends Simulation {

  val httpProtocol = http
    .baseURL("https://api.stage.bbc.co.uk")
    .acceptHeader("application/xml")

  val domesticGeonames = csv("locator/ds_001-geonames_domestic-2304.csv").circular 

  val scn = scenario("Travel News Filter")
    .feed(domesticGeonames)
    .exec(http("travel news filter")
    .get("/locator/locations/${geonameId}/details?df=travelnews")
    .check(status.is(200)))

  setUp(scn.inject(
    rampUsersPerSec(5) to(50) during(5 minutes),
    constantUsersPerSec(50) during(15 minutes)
  ).protocols(httpProtocol))
}
