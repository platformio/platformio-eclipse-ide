/*******************************************************************************
 * Copyright (c) 2021 PlatformIO and ArSysOp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
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
