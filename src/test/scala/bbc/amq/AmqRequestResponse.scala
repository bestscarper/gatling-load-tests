package bbc.amq

import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import javax.jms._
import scala.concurrent.duration._
import org.apache.activemq.jndi.ActiveMQInitialContextFactory

class AmqRequestResponse extends Simulation {

  val jmsConfig = jms
    .connectionFactoryName("ConnectionFactory")
    .url("tcp://192.168.59.103:61616")
    .contextFactory(classOf[ActiveMQInitialContextFactory].getName)
    .listenerCount(1)
    .matchByCorrelationID

  val scn = scenario("AmqBenchmark").repeat(1) {
    exec(
      jms("request reply").reqreply
        .queue("loadTestQueue")
        .replyQueue("loadTestQueue")
        .textMessage("hello from gatling jms dsl")
        .check(simpleCheck(checkBodyTextCorrect)))
  }

  setUp(
    scn.inject(
      atOnceUsers(1)
  ).protocols(jmsConfig))

  def checkBodyTextCorrect(m: Message) = {
    m match {
      case tm: TextMessage => tm.getText == "hello from gatling jms dsl"
      case _               => false
    }
  }
}
