package main;

import actors.Publisher;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnSuccess;
import akka.util.Timeout;
import com.typesafe.config.Config;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import utils.ConfigUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.pattern.Patterns.ask;

public class StartPublisher {


    public static void main(String[] args) {
        String port = args[0];
        final Config config = ConfigUtils.getConfig("publisher", port);

        String clusterName = config.getString("cluster.name");

        // Create Actor System using the configuration
        ActorSystem system = ActorSystem.create(clusterName, config);

        String publisherClientId = String.format("publisher-%s", port);
        String mqttTopic = config.getString("mqtt.topic");
        String brokerAddress = config.getString("mqtt.brokerAddress");

        final ActorRef publisher = system.actorOf(
                Publisher.props(publisherClientId, mqttTopic, brokerAddress), "publisher-actor-name");

        // Schedule message publish to Mqtt topic
        final FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
        final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        final ExecutionContext ec = system.dispatcher();
        final AtomicInteger counter = new AtomicInteger();
        system.scheduler().schedule(interval, interval, () -> ask(publisher,
                new Publisher.SendMqttCommand(String.valueOf(counter.incrementAndGet())),
                timeout).onSuccess(new OnSuccess<Object>() {
            public void onSuccess(Object result) {
                System.out.println(result);
            }
        }, ec), ec);
    }
}
