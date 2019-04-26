# Akka Alpakka MQTT Publish/Subscribe 

Simple Akka cluster using Alpakka to publish/subscribe MQTT messages to a topic using a MQTT broker.

### Requirements

Gradle version 5+

Java version 1.8+


### Installing/Running

Clone the project

Open a terminal go to the location of the cloned project

First create the publisher

```
gradle run --args="2550"
```

Then create the subscriber
```
gradle run --args="2560"
```

The publisher publishes a message every 2 seconds.

The subscriber receives the message from the broker.

The MQTT broker used is:

    tcp://test.mosquitto.org:1883

### Configuration

The "application.conf" file contains all the details of the configuration.
Other details such as the frequency of publishing can be changed on the scheduler defined on the StartPublisher.java file.

### Serializing

The Message.java, which is the message published, is serialized in the MqttPayloadConverter.java 
using ObjectOutputStream and ObjectInputStream.