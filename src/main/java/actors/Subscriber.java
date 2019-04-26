package actors;

import akka.Done;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.alpakka.mqtt.MqttConnectionSettings;
import akka.stream.alpakka.mqtt.MqttMessage;
import akka.stream.alpakka.mqtt.MqttQoS;
import akka.stream.alpakka.mqtt.MqttSubscriptions;
import akka.stream.alpakka.mqtt.javadsl.MqttSource;
import akka.stream.javadsl.Source;
import message.Message;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import utils.MqttPayloadConverter;

import java.util.concurrent.CompletionStage;

public class Subscriber extends AbstractActor {
    private final String subscriberClientId;
    private final String mqttTopic;
    private final String brokerAddress;
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props props(String subscriberClientId, String mqttTopic, String brokerAddress) {
        return Props.create(Subscriber.class, subscriberClientId, mqttTopic, brokerAddress);
    }

    public Subscriber(String subscriberClientId, String mqttTopic, String brokerAddress) {
        this.subscriberClientId = subscriberClientId;
        this.mqttTopic = mqttTopic;
        this.brokerAddress = brokerAddress;

        subscribe();
    }


    private void subscribe() {
        MqttConnectionSettings connectionSettings = MqttConnectionSettings.create(
                brokerAddress,
                subscriberClientId,
                new MemoryPersistence()
        ).withCleanSession(true);
        ActorMaterializer materializer = ActorMaterializer.create(getContext());
        MqttSubscriptions subscriptions =
                MqttSubscriptions.create(mqttTopic, MqttQoS.atMostOnce());

        int bufferSize = 10;
        Source<MqttMessage, CompletionStage<Done>> mqttSource =
                MqttSource.atMostOnce(
                        connectionSettings, subscriptions, bufferSize);

        CompletionStage<Done> doneCompletionStage = mqttSource
                .map(MqttPayloadConverter::getPayloadFromMqttMessage)
                .runForeach(this::handleMessage, materializer);

        doneCompletionStage.thenAccept(d -> System.out.println("Stage Done"));
    }


    private void handleMessage(Message m) {
        log.info("Message received : " + m.getContent());
    }

    @Override
    public AbstractActor.Receive createReceive() {
        // Do nothing
        return receiveBuilder()
                .build();
    }

}
