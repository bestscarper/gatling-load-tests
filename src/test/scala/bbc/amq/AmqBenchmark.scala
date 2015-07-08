package bbc.amq

import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import javax.jms._
import scala.concurrent.duration._
import org.apache.activemq.jndi.ActiveMQInitialContextFactory

class AmqBenchmark extends Simulation {

  val jmsConfig = jms
    .connectionFactoryName("ConnectionFactory")
    .url("tcp://queue.back.int.local:61003")
    .contextFactory(classOf[ActiveMQInitialContextFactory].getName)
    .listenerCount(1)
    .matchByCorrelationID

  val scn = scenario("JMS DSL test").repeat(1) {
    exec(
      jms("req reply testing").reqreply
        .queue("loadTestQueue")
        .replyQueue("responseQueue")
        .textMessage("hello from gatling jms dsl")
        .property("test_header", "test_value")
        .check(simpleCheck(checkBodyTextCorrect)))
  }

  setUp(
    scn.inject(
      atOnceUsers(1)
  ).protocols(jmsConfig))

  def checkBodyTextCorrect(m: Message) = {
    m match {
      case tm: TextMessage => tm.getText == "HELLO FROM GATLING JMS DSL"
      case _               => false
    }
  }
}
