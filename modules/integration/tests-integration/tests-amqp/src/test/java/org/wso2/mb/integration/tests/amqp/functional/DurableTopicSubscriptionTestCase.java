package org.wso2.mb.integration.tests.amqp.functional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.mb.integration.common.clients.operations.topic.BasicTopicSubscriber;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * This class holds set of test cases to verify if durable topic
 * subscriptions happen according to spec.
 */
public class DurableTopicSubscriptionTestCase {

    private static Log log = LogFactory.getLog(DurableTopicSubscriptionTestCase.class);
    private String host = "127.0.0.1";
    private String port = "5672";
    private String userName = "admin";
    private String password = "admin";
    private long intervalBetSubscription = 100;

    @Test(groups = {"wso2.mb", "topic"})
    public void basicSubscriptionTest() throws JMSException, NamingException {

        /**
         * create with sub id= x topic=y. disconnect and try to connect again
         */
        BasicTopicSubscriber sub1 = null;
        try {
            String topic = "myTopic1";
            String subID = "wso2";
            sub1 = new BasicTopicSubscriber(host,port,userName,password,topic);
            sub1.subscribe(topic,true,subID);
            sleepForInterval(intervalBetSubscription);
            sub1.close();
            sleepForInterval(intervalBetSubscription);
            sub1 = new BasicTopicSubscriber(host,port,userName,password,topic);
            sub1.subscribe(topic,true,subID);
            sleepForInterval(intervalBetSubscription);
        } finally {
            if(null != sub1) {
                sub1.close();
            }
        }

        /**
         * create with sub id= x topic=y. kill subscription and try to connect again
         */
        //Cannot automate

    }

    @Test(groups = {"wso2.mb", "topic"})
    public void multipleSubsWithSameIdTest() throws JMSException, NamingException {
        /**
         * create with sub id= x topic=y. try another subscription with same params.
         * should rejects the subscription
         */
        String topic = "myTopic2";
        String subID = "sriLanka";
        BasicTopicSubscriber sub1 = null;
        BasicTopicSubscriber sub2 = null;
        boolean multipleSubsNotAllowed = true;
        try {
            sub1 = new BasicTopicSubscriber(host, port, userName, password, topic);
            sub1.subscribe(topic, true, subID);
            sleepForInterval(intervalBetSubscription);
            try {
                sub2 = new BasicTopicSubscriber(host, port, userName, password, topic);
                sub2.subscribe(topic, true, subID);
                sleepForInterval(intervalBetSubscription);
            } catch (JMSException e) {
                if (e.getMessage().contains("as it already has an existing exclusive consumer")) {
                    log.error("Error while subscribing. This is expected.", e);
                    multipleSubsNotAllowed = false;
                } else {
                    log.error("Error while subscribing", e);
                    throw new JMSException("Error while subscribing");
                }
            }
            Assert.assertFalse(multipleSubsNotAllowed, "Multiple subscriptions allowed for same client ID.");
        } finally {
            if (null != sub1) {
                sub1.close();
            }
        }
    }

    @Test(groups = {"wso2.mb", "topic"})
    public void multipleSubsWithDifferentIdTest() throws JMSException, NamingException {
        /**
         * create with sub id= x topic=y. try another with sub id=z topic=y. Allowed
         */
        String topic = "myTopic3";
        String subID1 = "test1";
        String subID2 = "test2";
        BasicTopicSubscriber sub1 = new BasicTopicSubscriber(host,port,userName,password,topic);
        sub1.subscribe(topic,true,subID1);
        sleepForInterval(intervalBetSubscription);
        BasicTopicSubscriber sub2 = new BasicTopicSubscriber(host,port,userName,password,topic);
        sub2.subscribe(topic,true,subID2);
        sleepForInterval(intervalBetSubscription);

        /**
         * above multiple subscribers closed
         */
        sub1.close();
        sub2.close();
    }

    @Test(groups = {"wso2.mb", "topic"})
    public void multipleSubsToDifferentTopicsWithSameSubIdTest() throws JMSException, NamingException {
        /**
         *  create with sub id= x topic=y.
         *  close it.
         *  Then try with sub id= x topic=z.
         *  Should reject the subscription
         */
        String topic1 = "myTopic4";
        String topic2 = "myTopic5";
        String subID1 = "test3";
        boolean subscriptionAllowedForDifferentTopic = true;
        BasicTopicSubscriber sub1 = new BasicTopicSubscriber(host,port,userName,password,topic1);
        sub1.subscribe(topic1,true,subID1);
        sleepForInterval(intervalBetSubscription);
        sub1.close();
        sleepForInterval(intervalBetSubscription);
        try {
            BasicTopicSubscriber sub2 = new BasicTopicSubscriber(host,port,userName,password,topic2);
            sub2.subscribe(topic2,true,subID1);
            sleepForInterval(intervalBetSubscription);
        } catch (JMSException e) {
            if(e.getMessage().contains("An Exclusive Bindings already exists for different topic. Not permitted")) {
                log.error("Error while subscribing. This is expected.",e);
                subscriptionAllowedForDifferentTopic = false;
            } else {
                log.error("Error while subscribing." , e);
                throw new JMSException("Error while subscribing");
            }
        }
        Assert.assertFalse(subscriptionAllowedForDifferentTopic, "Subscriptions to a different topic" +
                                      " was allowed by same client Id without un-subscribing");
    }

    @Test(groups = {"wso2.mb", "topic"})
    public void durableTopicWithNormalTopicTest() throws JMSException, NamingException {
        /**
         * create with sub id= x topic=y.
         * Create a normal topic subscription topic=y
         */
        String topic = "myTopic5";
        String subID = "test5";

        BasicTopicSubscriber sub1 = null;
        BasicTopicSubscriber sub2 = null;
        try {
            sub1 = new BasicTopicSubscriber(host,port,userName,password,topic);
            sub1.subscribe(topic,true,subID);
            sleepForInterval(intervalBetSubscription);

            sub2 = new BasicTopicSubscriber(host,port,userName,password,topic);
            sub2.subscribe(topic,false,"");
            sleepForInterval(intervalBetSubscription);
        } finally {
            if (null != sub1) {
                sub1.close();
            }
            if (null != sub2) {
                sub2.close();
            }
        }
    }

    @Test(groups = {"wso2.mb", "topic"})
    public void subscribeUnSuscribeAndSubscribeAgainTest() throws JMSException, NamingException {
        /**
         * create with sub id= x topic=y.
         * Unsubscribe.
         * Now try sub id= x topic=y
         */
        String topic = "myTopic7";
        String subID = "test7";

        BasicTopicSubscriber sub1 = null;
        BasicTopicSubscriber sub2 = null;

        try {
            sub1 = new BasicTopicSubscriber(host,port,userName,password,topic);
            sub1.subscribe(topic,true,subID);
            sleepForInterval(intervalBetSubscription);

            sub1.unsubscribe(subID);
            sleepForInterval(intervalBetSubscription);

            sub2 = new BasicTopicSubscriber(host,port,userName,password,topic);
            sub2.subscribe(topic,true,subID);
            sleepForInterval(intervalBetSubscription);

        } finally {
            if(null != sub2) {
                sub2.close();
            }
        }

    }

    @Test(groups = {"wso2.mb", "topic"})
    public void multipleSubsWithDiffIDsToSameTopicTest() throws JMSException, NamingException {
        /**
         * create with sub id= x topic=y.
         * Unsubscribe.
         * Now try sub id= z topic=y
         */
        String topic = "multiSubTopic";
        String subID1 = "new1";
        String subID2 = "new2";
        String subID3 = "new3";
        String subID4 = "new4";

        BasicTopicSubscriber sub1 = null;
        BasicTopicSubscriber sub2 = null;
        BasicTopicSubscriber sub3 = null;
        BasicTopicSubscriber sub4 = null;

        try {
            sub1 = new BasicTopicSubscriber(host, port, userName, password, topic);
            sub1.subscribe(topic, true, subID1);
            sleepForInterval(intervalBetSubscription);

            sub2 = new BasicTopicSubscriber(host, port, userName, password, topic);
            sub2.subscribe(topic, true, subID2);
            sleepForInterval(intervalBetSubscription);

            sub3 = new BasicTopicSubscriber(host, port, userName, password, topic);
            sub3.subscribe(topic, true, subID3);
            sleepForInterval(intervalBetSubscription);

            sub4 = new BasicTopicSubscriber(host, port, userName, password, topic);
            sub4.subscribe(topic, true, subID4);
            sleepForInterval(intervalBetSubscription);

        } finally {
            if (null != sub1) {
                sub1.close();
            }
            if (null != sub2) {
                sub2.close();
            }
            if (null != sub3) {
                sub3.close();
            }
            if (null != sub4) {
                sub4.close();
            }
        }
    }

    @Test(groups = {"wso2.mb", "topic"})
    public void subscribeUnsubscribeAndTryDifferentTopicTest()
            throws JMSException, NamingException {
        /**
         * create with sub id= x topic=y.
         * Unsubscribe.
         * Now try sub id= x topic=z
         */

        String topic1 = "myTopic8";
        String topic2 = "myTopic9";
        String subID = "test8";

        BasicTopicSubscriber sub1 = new BasicTopicSubscriber(host, port, userName, password, topic1);
        sub1.subscribe(topic1, true,subID);
        sleepForInterval(intervalBetSubscription);

        sub1.unsubscribe(subID);
        sleepForInterval(intervalBetSubscription);

        BasicTopicSubscriber sub2 = new BasicTopicSubscriber(host, port, userName, password, topic2);
        sub2.subscribe(topic2, true,subID);
        sleepForInterval(intervalBetSubscription);

        sub2.close();

    }

    private void sleepForInterval(long timeToSleep) {
        try {
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            //ignore
        }
    }
}
