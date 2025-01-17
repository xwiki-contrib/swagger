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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.swagger.internal.Proxy;
import org.xwiki.script.service.ScriptService;

/**
 * Simple scripting service for accessing the proxy.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("swagger")
@Singleton
public class SwaggerScriptService implements ScriptService
{
    @Inject
    private Proxy proxy;

    /**
     * Makes the request to retrieve the OpenAPI specification and returns it.
     *
     * @param  params map with all the parameters needed for the requests.
     * @return OpenAPI specification as a string
     * @throws IOException
     * @throws URISyntaxException
     */
    public String executeProxy(Map<String, String> params)
        throws IOException, URISyntaxException
    {
        return proxy.request(params);
    }
}
