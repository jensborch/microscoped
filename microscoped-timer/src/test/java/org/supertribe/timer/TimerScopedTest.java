/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.supertribe.timer;

import org.apache.cxf.jaxrs.client.WebClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tomitribe.microscoped.core.ScopeContext;
import org.tomitribe.microscoped.timer.TimerScopedExtension;

import javax.enterprise.inject.spi.Extension;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Arquillian will start the container, deploy all @Deployment bundles, then run all the @Test methods.
 *
 * A strong value-add for Arquillian is that the test is abstracted from the server.
 * It is possible to rerun the same test against multiple adapters or server configurations.
 *
 * A second value-add is it is possible to build WebArchives that are slim and trim and therefore
 * isolate the functionality being tested.  This also makes it easier to swap out one implementation
 * of a class for another allowing for easy mocking.
 *
 */
@RunWith(Arquillian.class)
public class TimerScopedTest extends Assert {

    /**
     * ShrinkWrap is used to create a war file on the fly.
     *
     * The API is quite expressive and can build any possible
     * flavor of war file.  It can quite easily return a rebuilt
     * war file as well.
     *
     * More than one @Deployment method is allowed.
     */
    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class)
                .addPackage(ScopeContext.class.getPackage())
                .addPackage(TimerScopedExtension.class.getPackage())
                .addPackage(ColorService.class.getPackage())
                .addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml"), "classes/META-INF/beans.xml")
                .addAsWebInfResource(new StringAsset(TimerScopedExtension.class.getName()),
                        "classes/META-INF/services/" + Extension.class.getName()
                );
    }

    /**
     * This URL will contain the following URL data
     *
     *  - http://<host>:<port>/<webapp>/
     *
     * This allows the test itself to be agnostic of server information or even
     * the name of the webapp
     *
     */
    @ArquillianResource
    private URL webappUrl;


    @Test
    public void test() throws Exception {

        Thread.sleep(TimeUnit.SECONDS.toMillis(7));

        final WebClient webClient = WebClient.create(webappUrl.toURI());
        webClient.accept(MediaType.APPLICATION_JSON);

        final List<String> lines = webClient.path("color").get(Log.class).getLines();

        assertTrue(lines.contains("red, 1"));
        assertTrue(lines.contains("red, 2"));
        assertTrue(lines.contains("red, 3"));
        assertTrue(lines.contains("red, 4"));
        assertTrue(lines.contains("green, 1"));
        assertTrue(lines.contains("green, 2"));
        assertTrue(lines.contains("green, 3"));
        assertTrue(lines.contains("green, 4"));
        assertTrue(lines.contains("blue, 1"));
        assertTrue(lines.contains("blue, 2"));
        assertTrue(lines.contains("blue, 3"));
        assertTrue(lines.contains("blue, 4"));
        assertTrue(lines.contains("orange, 1"));
        assertTrue(lines.contains("orange, 2"));
        assertTrue(lines.contains("orange, 3"));
        assertTrue(lines.contains("orange, 4"));
    }
}
