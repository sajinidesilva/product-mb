/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.andes.services.beans;

import org.wso2.andes.server.util.CompositeDataHelper;
import org.wso2.carbon.andes.services.exceptions.SubscriptionManagerException;
import org.wso2.carbon.andes.services.types.Subscription;
import org.wso2.carbon.andes.services.utils.SubscriptionManagementConstants;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

/**
 * The following class contains the MBeans invoking services related to subscription resources.
 */
public class SubscriptionManagementBeans {
    private static final Object lock = new Object();
    private static SubscriptionManagementBeans instance = new SubscriptionManagementBeans();

    /**
     * Gets the active subscription managing instance.
     *
     * @return A subscription managing instance.
     */
    public static SubscriptionManagementBeans getInstance() {
        if (null == instance) { // avoid sync penalty if we can
            synchronized (lock) { // declare a private static Object to use for mutex
                if (null == instance) {  // have to do this inside the sync
                    instance = new SubscriptionManagementBeans();
                }
            }
        }
        return instance;
    }

    /**
     * Gets subscriptions belonging to a specific protocol type and destination type. The subscriptions can be filtered
     * by subscription name, destination name and whether they are active or not.
     *
     * @param protocolType     The protocol type matching for the subscription. Example : amqp, mqtt.
     * @param destinationType  The destination type matching for the subscription. Example : queue, topic,
     *                         durable_topic.
     * @param subscriptionName The name of the subscription. If "*", all subscriptions are included. Else subscriptions
     *                         that <strong>contains</strong> the value are included.
     * @param destinationName  The name of the destination name. If "*", all destinations are included. Else
     *                         destinations that <strong>equals</strong> the value are included.
     * @param active           Filtering the subscriptions that are active or inactive.
     * @param offset           The starting index to return.
     * @param limit            The number of subscriptions to return.
     * @return A list of {@link Subscription}s.
     * @throws SubscriptionManagerException
     */
    public List<Subscription> getSubscriptions(String protocolType, String destinationType, String subscriptionName,
                                               String destinationName, String active, int offset, int limit)
                                                throws SubscriptionManagerException {
        ArrayList<Subscription> subscriptionDetailsList = new ArrayList<>();
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName =
                    new ObjectName("org.wso2.andes:type=SubscriptionManagementInformation," +
                                   "name=SubscriptionManagementInformation");

            Object[] parameters = new Object[]{protocolType, destinationType, subscriptionName, destinationName, active,
                                               offset, limit};
            String[] signature = new String[]{String.class.getName(), String.class.getName(), String.class.getName(),
                                              String.class.getName(), String.class.getName(),
                                              int.class.getName(), int.class.getName()};

            Object result = mBeanServer.invoke(objectName,
                    SubscriptionManagementConstants.SUBSCRIPTIONS_MBEAN_ATTRIBUTE,
                    parameters, signature);

            if (null != result) {
                CompositeData[] messageDataList = (CompositeData[]) result;
                for (CompositeData messageData : messageDataList) {
                    Subscription destination = getSubscriptionInfo(messageData);
                    subscriptionDetailsList.add(destination);
                }
            }
            return subscriptionDetailsList;

        } catch (MalformedObjectNameException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            throw new SubscriptionManagerException("Error while invoking mBean operations to get " +
                                                   "subscription list", e);
        }
    }

    /**
     * Close/unsubscribe subscriptions forcefully belonging to a specific protocol type, destination type.
     *
     * @param protocol         The protocol type matching for the subscription. Example : amqp, mqtt.
     * @param subscriptionType The subscription type matching for the subscription. Example : queue, topic,
     *                         durable_topic.
     * @param destinationName  The name of the destination to close/unsubscribe. If "*", all destinations are included.
     *                         Else destinations that <strong>contains</strong> the value are included.
     * @throws SubscriptionManagerException
     */
    public void closeSubscriptions(String protocol, String subscriptionType, String destinationName)
                                                                                throws SubscriptionManagerException {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName = new ObjectName("org.wso2.andes:type=SubscriptionManagementInformation," +
                                                   "name=SubscriptionManagementInformation");

            Object[] parameters = new Object[]{protocol, subscriptionType, destinationName};
            String[] signature = new String[]{String.class.getName(), String.class.getName(), String.class.getName()};

            mBeanServer.invoke(objectName,
                    SubscriptionManagementConstants.SUBSCRIPTIONS_CLOSE_MBEAN_ATTRIBUTE,
                    parameters, signature);

            //could catch all these exceptions in one block if we use Java 7
        } catch (MalformedObjectNameException | ReflectionException | MBeanException | InstanceNotFoundException e) {
            throw new SubscriptionManagerException("Cannot access mBean operations to get " +
                                                   "subscription list", e);
        }
    }

    /**
     * Close/Remove/Unsubscribe subscriptions forcefully belonging to a specific protocol type, destination type.
     *
     * @param protocol         The protocol type matching for the subscription. Example : amqp, mqtt.
     * @param subscriptionType The subscription type matching for the subscription. Example : queue, topic,
     *                         durable_topic.
     * @param destinationName  The name of the destination to close/unsubscribe. If "*", all destinations are included.
     *                         Else destinations that <strong>equals</strong> the value are included.
     * @throws SubscriptionManagerException
     */
    public void closeSubscription(String protocol, String subscriptionType, String destinationName)
            throws SubscriptionManagerException {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName = new ObjectName("org.wso2.andes:type=SubscriptionManagementInformation," +
                                                   "name=SubscriptionManagementInformation");

            Object[] parameters = new Object[]{protocol, subscriptionType, destinationName};
            String[] signature = new String[]{String.class.getName(), String.class.getName(), String.class.getName()};

            mBeanServer.invoke(objectName,
                    SubscriptionManagementConstants.SUBSCRIPTION_CLOSE_MBEAN_ATTRIBUTE,
                    parameters, signature);

            //could catch all these exceptions in one block if we use Java 7
        } catch (MalformedObjectNameException | ReflectionException | MBeanException | InstanceNotFoundException e) {
            throw new SubscriptionManagerException("Cannot access mBean operations to get " +
                                                   "subscription list", e);
        }
    }

    /**
     * Converts a {@link CompositeData} to a {@link Subscription}. The {@link CompositeData} should match the {@link
     * Subscription} in attribute wise.
     *
     * @param compositeSubscription The composite data object.
     * @return A {@link Subscription}.
     */
    private Subscription getSubscriptionInfo(CompositeData compositeSubscription) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionIdentifier((String) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.SUBSCRIPTION_ID));
        subscription.setSubscribedQueueOrTopicName((String) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.DESTINATION_NAME));
        subscription.setSubscriberQueueBoundExchange((String) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.TARGET_QUEUE_BOUND_EXCHANGE_NAME));
        subscription.setSubscriberQueueName((String) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.TARGET_QUEUE));
        subscription.setDurable((Boolean) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.IS_DURABLE));
        subscription.setActive((Boolean) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.HAS_EXTERNAL_SUBSCRIPTIONS));
        subscription.setNumberOfMessagesRemainingForSubscriber((Integer) compositeSubscription.get
                (CompositeDataHelper.SubscriptionCompositeDataHelper.PENDING_MESSAGE_COUNT));
        subscription.setSubscriberNodeAddress((String) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.SUBSCRIBED_NODE));
        subscription.setProtocolType((String) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.PROTOCOL_TYPE));
        subscription.setDestinationType((String) compositeSubscription.get(CompositeDataHelper
                .SubscriptionCompositeDataHelper.DESTINATION_TYPE));

        return subscription;
    }
}
