package bbc.newsprovider

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.config.HttpProtocolBuilder
import io.gatling.core.structure.ScenarioBuilder

trait HttpScenario {
  val scn : ScenarioBuilder 
  val httpProtocol : HttpProtocolBuilder
}

trait Environment {
  val baseURL : String
}

trait VM extends Environment {
  val baseURL = "http://10.10.32.46"
}

trait Test extends Environment {
  val baseURL = "http://open.test.bbc.co.uk"
}

trait Stage extends Environment {
  val baseURL = "http://open.stage.bbc.co.uk"
}

trait Live extends Environment {
  val baseURL = "http://open.live.bbc.co.uk"
}

trait SmokeTest {
  self : Simulation with HttpScenario =>
    setUp(
      scn.inject( atOnceUsers(50) )
    ).protocols(httpProtocol)
}

trait LoadTest {
   self : Simulation with HttpScenario =>
     setUp(
        scn.inject(
          // typical usage
          constantUsersPerSec(9) during(2 minutes),
          nothingFor(30 seconds),
          // aspirational peak usage
          constantUsersPerSec(100) during(5 minutes),
          nothingFor(30 seconds),
          // find breaking point
          rampUsersPerSec(100) to(500) during(5 minutes)
	)
     ).protocols(httpProtocol)
}


 
