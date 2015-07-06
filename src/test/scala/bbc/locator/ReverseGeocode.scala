package bbc.locator

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ReverseGeocode extends Simulation {

  val httpProtocol = http
    .baseURL("https://open.stage.bbc.co.uk")
    .acceptHeader("application/xml")

  val longLat = csv("locator/ds_030-points-21000-3dp.csv").circular 

  val scn = scenario("Reverse Geocode")
    .feed(longLat)
    .exec(http("Reverse Geocode")
    .get("/locator/locations?la=${latitude}&longitude=${longitude}")
    .check(status.is(200)))

  setUp(scn.inject(
    rampUsersPerSec(10) to(500) during(10 minutes),
    constantUsersPerSec(500) during(20 minutes)
  ).protocols(httpProtocol))
}
