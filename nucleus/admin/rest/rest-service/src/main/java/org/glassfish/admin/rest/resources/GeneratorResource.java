/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.admin.rest.resources;

import com.sun.enterprise.config.serverbeans.Domain;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.glassfish.admin.rest.RestLogging;
import org.glassfish.admin.rest.adapter.LocatorBridge;


import org.glassfish.admin.rest.generator.ResourcesGenerator;
import org.glassfish.admin.rest.generator.TextResourcesGenerator;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.config.ConfigModel;
import org.jvnet.hk2.config.Dom;
import org.jvnet.hk2.config.DomDocument;

/**
 * @author Ludovic Champenois ludo@dev.java.net
 * @author Rajeshwar Patil
 */
@Path("/generator/")
public class GeneratorResource {

    private static final String DEFAULT_OUTPUT_DIR = System.getProperty("user.home") +
            "/tmp/glassfish";
    @Context
    protected ServiceLocator habitat;

    @GET
    @Produces({"text/plain"})
    public String get(@QueryParam("outputDir")String outputDir) {
        if(outputDir == null) {
            outputDir = DEFAULT_OUTPUT_DIR;
        }
        String retVal = "Code Generation done at : " + outputDir;

        try {
            LocatorBridge locatorBridge = habitat.getService(LocatorBridge.class);
            Dom dom = Dom.unwrap(locatorBridge.getRemoteLocator().<Domain>getService(Domain.class));
            DomDocument document = dom.document;
            ConfigModel rootModel = dom.document.getRoot().model;

            ResourcesGenerator resourcesGenerator = new TextResourcesGenerator(outputDir, habitat);
            resourcesGenerator.generateSingle(rootModel, document);
            resourcesGenerator.endGeneration();
        } catch (Exception ex) {
            RestLogging.restLogger.log(Level.SEVERE, null, ex);
            retVal = "Exception encountered during generation process: " + ex.toString() + "\nPlease look at server.log for more information.";
        }
        return retVal;
    }
}
