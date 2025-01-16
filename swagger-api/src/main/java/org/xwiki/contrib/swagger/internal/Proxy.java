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
package org.xwiki.contrib.swagger.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * Retrieves the OpenAPI specification from URL.
 *
 * @version $Id$
 */
@Component(roles = Proxy.class)
@Singleton
public class Proxy
{
    /**
     * Failsafe to make sure that we return a valid object even if the request fails.
     */
    private static final String DEFAULT_OBJECT = "{}";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Inject
    private Logger logger;

    @Inject
    private HttpClientBuilderFactory httpClientBuilderFactory;

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
    public String request(String url, String accessToken, String username, String password)
        throws IOException, URISyntaxException
    {
        if (url == null || url.isEmpty()) {
            return DEFAULT_OBJECT;
        }
        // We are having 4 cases: random url, github, gitlab, bitbucket
        URL basicURL = new URL(url);
        if (basicURL.getHost().contains("github")) {
            return handleGithub(basicURL, accessToken);
        } else if (basicURL.getHost().contains("gitlab")) {
            return handleGitlab(basicURL, accessToken);
        } else if (basicURL.getHost().contains("bitbucket")) {
            return handleBitbucket(basicURL, username, password);
        } else {
            return handleBasicURL(basicURL);
        }
    }

    private String handleBasicURL(URL basicURL) throws IOException, URISyntaxException
    {
        try (CloseableHttpClient client = httpClientBuilderFactory.create()) {
            return executeRequest(client, new HttpGet(basicURL.toURI()));
        }
    }

    private String handleGithub(URL basicURL, String accessToken) throws IOException, URISyntaxException
    {
        try (CloseableHttpClient client = httpClientBuilderFactory.create()) {
            HttpGet get = new HttpGet(basicURL.toURI());

            // Add headers if accessToken is provided
            if (accessToken != null && !accessToken.isEmpty()) {
                get.addHeader(AUTHORIZATION_HEADER, String.format("Bearer %s", accessToken));
            }
            get.addHeader("Accept", "application/vnd.github.v3.raw");

            return executeRequest(client, get);
        }
    }

    private String executeRequest(CloseableHttpClient client, HttpGet get) throws IOException
    {
        try (CloseableHttpResponse response = client.execute(get)) {
            // Return the response body if status code is 200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                int statusCode = response.getStatusLine().getStatusCode();
                logger.error("Request failed with status code: [{}]", statusCode);
            }
        } catch (IOException e) {
            // Log any exception that occurs during the request execution
            logger.error("Exception occurred while executing the request: [{}]", e.getMessage(), e);
        }

        // Failsafe: return an empty JSON object if status code is not 200
        return DEFAULT_OBJECT;
    }

    private String handleBitbucket(URL basicURL, String username, String password)
        throws IOException, URISyntaxException
    {
        try (CloseableHttpClient client = httpClientBuilderFactory.create()) {
            HttpGet get = new HttpGet(basicURL.toURI());
            if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                String preEncode = String.format("%s:%s", username, password);
                get.addHeader(AUTHORIZATION_HEADER, String.format("Basic %s",
                    Base64.getEncoder().encodeToString(preEncode.getBytes())));
            }
            return executeRequest(client, get);
        }
    }

    private String handleGitlab(URL basicURL, String accessToken) throws URISyntaxException, IOException
    {
        try (CloseableHttpClient client = httpClientBuilderFactory.create()) {
            if (accessToken != null && !accessToken.isEmpty()) {
                // Gitlab uses the token in the qParams
                URIBuilder uriBuilder =  new URIBuilder(basicURL.toURI());
                uriBuilder.addParameter("private_token", accessToken);
                return executeRequest(client, new HttpGet(uriBuilder.build()));
            }
            return executeRequest(client, new HttpGet(basicURL.toURI()));
        }
    }
}
