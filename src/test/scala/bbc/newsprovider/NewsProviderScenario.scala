package bbc.newsprovider

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

trait NewsProviderScenario extends HttpScenario {
    self : Environment =>

    val httpProtocol = http.baseURL(s"${baseURL}/redbutton/newsprovider")
                           .acceptHeader("application/json")

    val random = new scala.util.Random()

    def pickRandomEntry(key : String, session : Session) = {
        val list = session(key).as[List[String]]
        list(random.nextInt(list.size))
    }

    val textIndexChain =
        exec(
            http("${app} ${category} index")
            .get("${indexUrl}")
            .check(regex("""(/story\.json\?assetUri=.+?)"""")
            .findAll
            .exists
            .saveAs("stories"))
        )
        .exec( session => {
              val StoryIdMatch = ".*assetUri=([^&]+)&?.*".r
              val storyUrl = pickRandomEntry("stories", session)
              val StoryIdMatch(storyId) = storyUrl
              session.set("storyUrl", storyUrl)
                     .set("storyId", storyId)
        })
        .exec(
            http("${app} Text story ${storyId}")
            .get("${storyUrl}")
        )

    val videoIndexChain =
        exec(
            http("${app} ${category} index")
            .get("${indexUrl}")
        )

    val journey =
        exec(
            http("${app} categories")
            .get("${categoriesUrl}")
            .check(regex("""(http://.+?/collection.json)""")
            .findAll
            .saveAs("indexUrls"))
        )
        .exec( session => {
            val randomIndexUrl = pickRandomEntry("indexUrls", session)
            val CategoryMatch = ".*/category/([a-z_]+?)/.*".r
            val CategoryMatch(category) = randomIndexUrl
            session.set("indexUrl", randomIndexUrl)
                   .set("category", category)
        })
        .doIfEqualsOrElse("${category}", "videos") {
            videoIndexChain
        } {
            textIndexChain
        }

    val redbuttonJourney =
        exec( session => {
            session.set("app", "RB+")
                   .set("categoriesUrl", "/categories.json?platform=html")
        })
        .exec(
            // rb+ always requests default index on startup
            http("${app} default index")
            .get("/default.json?platform=html")
        )
        .exec(
            journey
        )

    val newsOnTalDomesticJourney =
        exec( session => {
            session.set("app", "NoT")
                   .set("categoriesUrl", "/newsontal/edition/domestic/resolution/720/categories.json")
        })
        .exec(
            // newsontal always requests video index on startup
            http("${app} video index")
            .get("/newsontal/edition/domestic/category/videos/resolution/720/collection.json")
        )
        .exec(
            journey
        )

    val newsOnTalInternationalJourney =
        exec( session => {
            session.set("app", "NoT")
                   .set("categoriesUrl", "/newsontal/edition/international/resolution/720/categories.json")
        })
        .exec(
            // newsontal always requests video index on startup
            http("${app} video index")
            .get("/newsontal/edition/international/category/videos/resolution/720/collection.json")
        )
        .exec(
            journey
        )


    val scn = scenario("News Provider Scenario")
        .exitBlockOnFail {
            randomSwitch(
                10.0 -> redbuttonJourney,
                45.0 -> newsOnTalDomesticJourney,
                45.0 -> newsOnTalInternationalJourney
            )
        }
}
 
