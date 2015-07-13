package bbc.amq

import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import javax.jms._
import scala.concurrent.duration._
import org.apache.activemq.jndi.ActiveMQInitialContextFactory

class AmqRequestResponse extends Simulation {

  val textMessage = scala.io.Source.fromFile("./src/test/resources/amq/sample-message.xml").mkString 

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
        .textMessage(textMessage)
        .check(simpleCheck(checkBodyTextCorrect)))
  }

  setUp(
    scn.inject(
      rampUsersPerSec(1) to 600 during(5 minutes)
  ).protocols(jmsConfig))

  def checkBodyTextCorrect(m: Message) = {
    m match {
      case tm: TextMessage => tm.getText == textMessage
      case _               => false
    }
  }
}
