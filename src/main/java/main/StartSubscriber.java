package main;

import actors.Subscriber;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import utils.ConfigUtils;

public class StartSubscriber {
    public static void main(String[] args) {
        String port = args[0];
        final Config config = ConfigUtils.getConfig("frontend", port);

        String clusterName = config.getString("cluster.name");

        // Create Actor System using the configuration
        ActorSystem system = ActorSystem.create(clusterName, config);

        String subscriberClientId = String.format("subscriber-%s", port);
        String mqttTopic = config.getString("mqtt.topic");
        String brokerAddress = config.getString("mqtt.brokerAddress");

        system.actorOf(Subscriber.props(subscriberClientId, mqttTopic, brokerAddress), "subscriber-actor-name");
    }
}
