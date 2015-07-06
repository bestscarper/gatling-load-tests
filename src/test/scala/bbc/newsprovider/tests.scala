package bbc.newsprovider

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RedButtonSmokeTestOnVM extends Simulation with VM with NewsProviderScenario with SmokeTest

class RedButtonSmokeTestOnTest extends Simulation with Test with NewsProviderScenario with SmokeTest 

class RedButtonSmokeTestOnStage extends Simulation with Stage with NewsProviderScenario with SmokeTest 

class RedButtonSmokeTestOnLive extends Simulation with Live with NewsProviderScenario with SmokeTest 

class RedButtonLoadTestOnStage extends Simulation with Stage with NewsProviderScenario with LoadTest 

 
