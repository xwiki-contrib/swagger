/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.swagger.script;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.swagger.internal.Proxy;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

/**
 * Simple scripting service for accessing the @org.xwiki.contrib.swagger.internal.Proxy.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("swagger")
@Singleton
@Unstable
public class SwaggerScriptService implements ScriptService
{
    @Inject
    private Proxy proxy;

    /**
     * Makes the request to retrieve the OpenAPI specification and returns it.
     *
     * @param url address from where to retrieve the OpenAPI specification
     * @param accessToken authentication token for accessing the data
     * @param username bitbucket username for accessing the resource.
     * @param password bitbucket password for accessing the resouce
     * @return OpenAPI specification as a string
     * @throws IOException
     * @throws URISyntaxException
     */
    public String executeProxy(String url, String accessToken, String password, String username)
        throws IOException, URISyntaxException
    {
        return proxy.request(url, accessToken, password, username);
    }
}
