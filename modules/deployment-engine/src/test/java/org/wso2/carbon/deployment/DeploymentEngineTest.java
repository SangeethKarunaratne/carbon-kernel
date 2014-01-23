/*
 *  Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.deployment;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.deployment.deployers.CustomDeployer;
import org.wso2.carbon.deployment.exception.CarbonDeploymentException;
import org.wso2.carbon.deployment.exception.DeployerRegistrationException;
import org.wso2.carbon.deployment.exception.DeploymentEngineException;

import java.io.File;
import java.util.ArrayList;

public class DeploymentEngineTest extends BaseTest {
    private final static String CARBON_REPO = "carbon-repo";
    private final static String DEPLOYER_REPO = "carbon-repo" + File.separator + "text-files";
    private DeploymentEngine deploymentEngine;
    private CustomDeployer customDeployer;
    private ArrayList artifactsList = new ArrayList();

    /**
     * @param testName
     */
    public DeploymentEngineTest(String testName) {
        super(testName);

    }

    @BeforeTest
    public void setup() throws CarbonDeploymentException {
        customDeployer = new CustomDeployer();
        Artifact artifact = new Artifact(new File(getTestResourceFile(DEPLOYER_REPO).getAbsolutePath()
                                         + File.separator + "sample1.txt"));
        artifact.setType(new ArtifactType("txt"));
        artifactsList.add(artifact);
    }

    @Test
    public void testCarbonDeploymentEngine() throws DeploymentEngineException {
        deploymentEngine = new DeploymentEngine(getTestResourceFile(CARBON_REPO).getAbsolutePath());
        deploymentEngine.start();
    }

    @Test(dependsOnMethods = {"testCarbonDeploymentEngine"})
    public void testAddDeployer() throws DeployerRegistrationException {
        deploymentEngine.registerDeployer(customDeployer);
        Assert.assertNotNull(deploymentEngine.getDeployer(customDeployer.getArtifactType()));
    }

    @Test(dependsOnMethods = {"testAddDeployer"})
    public void testDeployArtifacts() {
        deploymentEngine.deployArtifacts(artifactsList);
        Assert.assertTrue(CustomDeployer.sample1Deployed);
    }

    @Test(dependsOnMethods = {"testDeployArtifacts"})
    public void testUpdateArtifacts() {
        deploymentEngine.updateArtifacts(artifactsList);
        Assert.assertTrue(CustomDeployer.sample1Updated);
    }

    @Test(dependsOnMethods = {"testUpdateArtifacts"})
    public void testUndeployArtifacts() {
        deploymentEngine.undeployArtifacts(artifactsList);
        Assert.assertFalse(CustomDeployer.sample1Deployed);
    }

    @Test(dependsOnMethods = {"testUndeployArtifacts"})
    public void testRemoveDeployer() throws DeployerRegistrationException {
        deploymentEngine.unRegisterDeployer(customDeployer);
        Assert.assertNull(deploymentEngine.getDeployer(customDeployer.getArtifactType()));
    }

}
