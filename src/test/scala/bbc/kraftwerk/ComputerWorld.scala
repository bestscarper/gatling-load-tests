package bbc.kraftwerk

import scala.concurrent.duration._

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ComputerWorld extends Simulation {

  val httpProtocol = http
    .baseURL("http://computer-database.herokuapp.com")
    .acceptHeader("""text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")
    .acceptEncodingHeader("""gzip, deflate""")
    .acceptLanguageHeader("""en-gb,en;q=0.5""")
    .contentTypeHeader("application/x-www-form-urlencoded")
    .userAgentHeader("""Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0""")

  val computers = http("computers") 
    .get("/computers")
    .check(
      status.is(200),
      regex("""\d+ computers found"""),
      css("#add", "href").saveAs("addComputer"))
    
  val addNewComputer = http("addNewComputer")
    .get("${addComputer}")
    .check(substring("Add a computer"))

  val addNewComputerPost = http("addNewComputerPost") 
    .post("/computers")
    .formParam("name", "${homeComputer}") 
    .formParam("introduced", "2015-10-10") 
    .formParam("discontinued", "2017-10-10") 
    .formParam("company", "") 
    .check(substring("${homeComputer}"))
  
  val computerDatabaseScn = scenario("computerDatabaseScn") 
    .exec(_.set("homeComputer", s"homeComputer_${ThreadLocalRandom.current.nextInt(Int.MaxValue)}"))
    .exec(computers)
    .exec(addNewComputer)
    .exec(addNewComputerPost)
    
  setUp(computerDatabaseScn.inject(
    constantUsersPerSec(2) during(1 minutes)
  ).protocols(httpProtocol)) 
}
