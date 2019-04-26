package utils;

import akka.stream.alpakka.mqtt.MqttMessage;
import akka.util.ByteString;
import message.Message;

import java.io.*;

public class MqttPayloadConverter {

    public static ByteString createPayload(Message message) {
        byte[] byteArray = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            ;
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);
            byteArray = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ByteString.fromArray(byteArray);
    }

    public static Message getPayloadFromMqttMessage(MqttMessage mqttMessage) {
        ObjectInputStream objectInputStream = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mqttMessage.payload().toArray());
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object o = objectInputStream.readObject();
            if (o instanceof Message) {
                return (Message) o;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayInputStream.close();
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
