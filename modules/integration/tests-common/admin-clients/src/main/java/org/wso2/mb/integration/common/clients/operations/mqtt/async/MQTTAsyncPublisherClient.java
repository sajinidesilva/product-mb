/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.mb.integration.common.clients.operations.mqtt.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.mb.integration.common.clients.QualityOfService;
import org.wso2.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.mb.integration.common.clients.operations.mqtt.callback.CallbackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous MQTT publisher client.
 */
public class MQTTAsyncPublisherClient extends AndesMQTTAsyncClient {

    // The payload to send as the message content
    private byte[] messagePayLoad;

    // The number of messages to send
    private int noOfMessages;

    private static final Log log = LogFactory.getLog(MQTTAsyncPublisherClient.class);

    /**
     * Initialize publishing to mqtt.
     *
     * @param configuration MQTT configurations
     * @param clientID      Unique mqtt client Id
     * @param topic         Topic to publish to
     * @param qos           Quality of service
     * @param payload       Data to send
     * @param noOfMessages  Number of message to send
     * @throws MqttException
     */
    public MQTTAsyncPublisherClient(MQTTClientConnectionConfiguration configuration, String clientID, String topic,
                                    QualityOfService qos, byte[] payload, int noOfMessages) throws MqttException {
        super(configuration, clientID, topic, qos, new CallbackHandler());
        messagePayLoad = payload;
        this.noOfMessages = noOfMessages;
    }

    /**
     * Start the publisher to publish to mqtt.
     */
    @Override
    public void run() {
        try {
            publish(messagePayLoad, noOfMessages);
        } catch (MqttException e) {
            log.error("Error publishing messages to " + getTopic() + " from " + getMqttClientID(), e);
        }
    }

    /**
     * Return subscription status as false since this is the publishing client.
     *
     * @return False
     */
    @Override
    public boolean isSubscribed() {
        return false;
    }

    /**
     * Return empty as this is the publisher client and does not accept any messages.
     *
     * @return empty message list
     */
    @Override
    public List<MqttMessage> getReceivedMessages() {
        return new ArrayList<MqttMessage>(0);
    }
}
