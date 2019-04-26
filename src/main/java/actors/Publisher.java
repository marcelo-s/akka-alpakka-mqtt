package actors;

import akka.Done;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.alpakka.mqtt.MqttConnectionSettings;
import akka.stream.alpakka.mqtt.MqttMessage;
import akka.stream.alpakka.mqtt.MqttQoS;
import akka.stream.alpakka.mqtt.javadsl.MqttSink;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import message.Message;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import utils.MqttPayloadConverter;

import java.util.Collections;
import java.util.concurrent.CompletionStage;

public class Publisher extends AbstractActor {

    private final String subscriberClientId;
    private final String mqttTopic;
    private final String brokerAddress;
    private MqttConnectionSettings connectionSettings;
    private ActorMaterializer materializer;

    public static class SendMqttCommand {

        private String number;

        public SendMqttCommand(String number) {
            this.number = number;
        }
    }

    public static Props props(String subscriberClientId, String mqttTopic, String brokerAddress) {
        return Props.create(Publisher.class, subscriberClientId, mqttTopic, brokerAddress);
    }


    public Publisher(String subscriberClientId, String mqttTopic, String brokerAddress) {
        this.subscriberClientId = subscriberClientId;
        this.mqttTopic = mqttTopic;
        this.brokerAddress = brokerAddress;

        setupMqtt();
    }

    private void setupMqtt() {
        connectionSettings =
                MqttConnectionSettings.create(
                        brokerAddress,
                        subscriberClientId,
                        new MemoryPersistence()
                ).withCleanSession(true);
        materializer = ActorMaterializer.create(getContext());
    }

    private void publish(MqttMessage mqttMessage) {
        Sink<MqttMessage, CompletionStage<Done>> mqttSink =
                MqttSink.create(connectionSettings, MqttQoS.atLeastOnce());
        Source.from(Collections.singletonList(mqttMessage)).runWith(mqttSink, materializer);
    }

    private MqttMessage createMqttMessage(SendMqttCommand sendMqttCommand) {
        System.out.println("Sending : " + sendMqttCommand.number);
        String content = String.format("Message number : %s", Integer.parseInt(sendMqttCommand.number));
        Message message = new Message(content);
        ByteString payload = MqttPayloadConverter.createPayload(message);
        return MqttMessage.create(mqttTopic, payload)
                .withQos(MqttQoS.atLeastOnce())
                .withRetained(true);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SendMqttCommand.class, this::handleSendMqttCommand)
                .build();
    }

    private void handleSendMqttCommand(SendMqttCommand sendMqttCommand) {
        MqttMessage mqttMessage = createMqttMessage(sendMqttCommand);
        publish(mqttMessage);
    }
}
