/*******************************************************************************
 * Copyright (c) 2021 ArSysOp and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Nikifor Fedorov (ArSysOp) - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.installer.net;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public final class RemoteResource {

	private final String url;

	public RemoteResource(String url) {
		this.url = url;
	}

	public TarGzPackage download(Path target) throws IOException {
		String httpsProxy = System.getenv("HTTPS_PROXY"); //$NON-NLS-1$
		String httpProxy = System.getenv("HTTP_PROXY"); //$NON-NLS-1$
		String proxy = httpsProxy != null ? httpsProxy.trim() : httpProxy != null ? httpProxy.trim() : ""; //$NON-NLS-1$

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);

		if (!proxy.isEmpty()) {
			RequestConfig requestConfig = RequestConfig.custom().setProxy(new HttpHost(proxy)).build();
			httpget.setConfig(requestConfig);
		}

		CloseableHttpResponse response = httpClient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try (InputStream inStream = entity.getContent()) {
					Files.copy(inStream, target);
				}
			}
		} finally {
			response.close();
			httpClient.close();
		}
		return new TarGzPackage(target);
	}

}
