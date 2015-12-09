/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.mb.ui.test.log;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.monitor.ApplicationLogsPage;
import org.wso2.mb.integration.common.utils.ui.pages.monitor.MonitorPage;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * The following test case checks accessibility of the system logs page.
 */
public class ViewLogsTestCase extends MBIntegrationUiBaseTest {

    /**
     * Initializes the test case.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * View the system logs page. This page allows to change logs.
     *
     * @throws IOException
     * @throws XPathExpressionException
     */
    @Test()
    public void viewSystemLogsTest() throws IOException, XPathExpressionException {
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        MonitorPage monitorPage = homePage.getMonitorPage();
        ApplicationLogsPage applicationLogsPage = monitorPage.getApplicationLogsPage();

        monitorPage = applicationLogsPage.getMonitorPage();
        monitorPage.getSystemLogsPage();
    }

    /**
     * Shuts down the selenium driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
