package bbc.prism

import scala.concurrent.duration._
 
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Realtime {

  val serviceFeeder = Array(
    Map("service" -> "bbc_radio_one"),
    Map("service" -> "bbc_radio_two"),
    Map("service" -> "bbc_radio_three"),
    Map("service" -> "bbc_6music")).random

  val dateRangeFeeder = Array(
    Map("dateRange" -> "start_date=2015-06-01T09:00:00Z&end_date=2015-06-01T11:00:00Z"),
    Map("dateRange" -> "start_date=2015-04-25T02:00:00Z&end_date=2015-04-25T03:30:00Z"),
    Map("dateRange" -> "start_date=2015-06-02T12:00:00Z&end_date=2015-06-02T15:00:00Z"),
    Map("dateRange" -> "start_date=2015-06-02T15:00:00Z&end_date=2015-06-02T16:45:00Z"),
    Map("dateRange" -> "start_date=2015-06-02T18:00:00Z&end_date=2015-06-02T21:00:00Z"),
    Map("dateRange" -> "start_date=2015-04-19T03:00:00Z&end_date=2015-04-19T04:00:00Z"),
    Map("dateRange" -> "start_date=2015-04-22T04:00:00Z&end_date=2015-04-22T05:00:00Z")).random

  val serviceRangeFeeder = Array(
    Map("serviceRange" -> "service=bbc_radio_one&service=bbc_radio_two"),
    Map("serviceRange" -> "service=bbc_radio_one&service=bbc_radio_two&bbc_radio_three"),
    Map("serviceRange" -> "service=bbc_radio_one&service=bbc_radio_two&bbc_radio_three&bbc_radio_four"),
    Map("serviceRange" -> "service=bbc_radio_one&service=bbc_radio_two&bbc_radio_three&bbc_radio_four&bbc_6music"),
    Map("serviceRange" -> "service=bbc_radio_one&service=bbc_radio_two&bbc_radio_three&bbc_radio_four&bbc_6music&bbc_1xtra")).random

}

class Realtime extends Simulation {

  val httpProtocol = http.baseURL("https://prism-realtime.test.api.bbci.co.uk/")

  import Realtime._
  val tracksByService = scenario("tracksByService")
    .feed(serviceFeeder)
    .feed(dateRangeFeeder)
  
    .exec(http("byService")
      .get("services/${service}/messages.json?${dateRange}")
      .check(status.is(200)))

  val tracksForServices = scenario("tracksForServices") 
    .feed(serviceRangeFeeder)
    .feed(dateRangeFeeder)
 
    .exec(http("forService")
      .get("services/messages.json?${serviceRange}&${dateRange}")
      .check(status.is(200)))

  setUp(
    tracksByService.inject(
      rampUsersPerSec(1) to(14) during(10 minutes),
      constantUsersPerSec(14) during(10 minutes)),

    tracksForServices.inject(
      rampUsersPerSec(1) to(6) during(10 minutes),
      constantUsersPerSec(6) during(10 minutes))
  ).protocols(httpProtocol)
}
