package bbc.trafficmanager

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TrafficManager extends Simulation {

  val httpProtocol = http.baseURL("https://open.stage.bbc.co.uk/")

  val https2kb = scenario("https2kb")
    .exec(http("https2kb")
      .get("loadtest/2kb")
      .check(status.is(200)))

  setUp(https2kb.inject(
    rampUsersPerSec(1) to(2000) during(5 minutes)
  ).protocols(httpProtocol))
}
