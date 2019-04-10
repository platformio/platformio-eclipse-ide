package com.insightmachines.pio.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public final class Helpers {

	private static final int NUM_DOWNLOAD_RETRIES = 3;
	
	private Helpers() {
	}

	private static final Logger LOGGER = Logger.getLogger(Helpers.class.getName());

	public static String basename(String urlString) {
		try {
			return new URL(urlString).getFile();
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, "Failed to extract basename from: " + urlString, e);
			return "";
		}
	}

	public static Path download(String source, Path target) {
		int retries = NUM_DOWNLOAD_RETRIES;
		int contentLength = getContentLength(source);
		if (fileExistsAndSizeMatches(target, contentLength)) {
			return target;
		}

		Exception lastException = null;
		while (retries >= 0) {
			try {
				_download(source, target);
				if (fileExistsAndSizeMatches(target, contentLength)) {
					return target;
				}
			} catch (Exception ex) {
				lastException = ex;
				LOGGER.warning(ex.getMessage());
			}
			retries--;
		}

		throw new RuntimeException("Failed to download file " + source + ". Last error: " + lastException);
	}

//	export async function download(source, target, retries = 3) {
//	  const contentLength = await getContentLength(source);
//
//	  if (fileExistsAndSizeMatches(target, contentLength)) {
//	    return target;
//	  }
//
//	  let lastError = '';
//	  while (retries >= 0) {
//	    try {
//	      await _download(source, target);
//	      if (fileExistsAndSizeMatches(target, contentLength)) {
//	        return target;
//	      }
//	    } catch (err) {
//	      lastError = err;
//	      console.warn(err);
//	    }
//	    retries--;
//	  }
//
//	  throw new Error(`Failed to download file ${source}: ${lastError}`);
//	}

	public static boolean fileExistsAndSizeMatches(Path target, int contentLength) {
		if (Files.exists(target)) {
			try {
				if (contentLength > 0 && contentLength == Files.size(target)) {
					return true;
				}
				Files.delete(target);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Failed to read size of " + target, e);
			}
		}
		return false;
	}

//	function fileExistsAndSizeMatches(target, contentLength) {
//	  if (fs.isFileSync(target)) {
//	    if (contentLength > 0 && contentLength == fs.getSizeSync(target)) {
//	      return true;
//	    }
//	    try {
//	      fs.removeSync(target);
//	    } catch (err) {
//	      console.warn(err);
//	    }
//	  }
//	  return false;
//	}

	private static void _download(String source, Path target) throws ClientProtocolException, IOException {		
		String httpsProxy = System.getenv("HTTPS_PROXY");
		String httpProxy = System.getenv("HTTP_PROXY");		
		String proxy = httpsProxy != null ? httpsProxy.trim() : httpProxy != null ? httpProxy.trim() : "";
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(source);
		
		if ( !proxy.isEmpty() ) {
			RequestConfig requestConfig = RequestConfig.custom()
			        .setProxy( new HttpHost(proxy) )
			        .build();
			httpget.setConfig(requestConfig);
		}
		
		CloseableHttpResponse response = httpClient.execute(httpget);
		try {
		    HttpEntity entity = response.getEntity();
		    if (entity != null) {
		        try ( InputStream inStream = entity.getContent() ) {
		        	Files.copy(inStream, target);
		        }
		    }
		} finally {
		    response.close();
		}
		
	}
	
//	async function _download(source, target) {
//	  let proxy = null;
//	  try {
//	    const apmPath = atom.packages.getApmPath();
//	    proxy = await new Promise((resolve, reject) => {
//	      runCommand(
//	        apmPath,
//	        ['--no-color', 'config', 'get', 'https-proxy'],
//	        (code, stdout) => {
//	          if (code !== 0) {
//	            return reject(null);
//	          }
//	          resolve(stdout.trim() === 'null' ? null : stdout.trim());
//	        }
//	      );
//	    });
//	  } catch (err) {
//	    proxy = (process.env.HTTPS_PROXY && process.env.HTTPS_PROXY.trim()
//	    || process.env.HTTP_PROXY && process.env.HTTP_PROXY.trim());
//	  }
//	  return new Promise((resolve, reject) => {
//	    const file = fs.createWriteStream(target);
//	    const options = {
//	      url: source,
//	    };
//	    if (proxy) {
//	      options.proxy = proxy;
//	    }
//	    request.get(options)
//	      .on('error', err => reject(err))
//	      .pipe(file);
//	    file.on('error', err => reject(err));
//	    file.on('finish', () => resolve(target));
//	  });
//	}

	public static int getContentLength(String url) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("HEAD");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String contentLegthText = connection.getHeaderField("content-length");
				return Integer.parseInt(contentLegthText);
			} else {
				return -1;
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to obtain size of: " + url, e);
			return -1;
		}
	}

//	function getContentLength(url) {
//	  return new Promise(resolve => {
//	    request.head({
//	      url
//	    }, (err, response) => {
//	      if (err || response.statusCode !== 200 || !response.headers.hasOwnProperty('content-length')) {
//	        resolve(-1);
//	      }
//	      resolve(parseInt(response.headers['content-length']));
//	    });
//	  });
//	}

	public static Path extractTarGz(Path source, Path destination) throws IOException {	
		InputStream in = Files.newInputStream(source);
		File out = destination.toFile();
		try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(in))){
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(out, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
        }
		return destination;
	}
	
//	export function extractTarGz(source, destination) {
//	  return new Promise((resolve, reject) => {
//	    fs.createReadStream(source)
//	      .pipe(zlib.createGunzip())
//	      .on('error', err => reject(err))
//	      .pipe(tar.extract({
//	        cwd: destination
//	      }))
//	      .on('error', err => reject(err))
//	      .on('close', () => resolve(destination));
//	  });
//	}
//
//	export function PEPverToSemver(pepver) {
//	  return pepver.replace(/(\.\d+)\.?(dev|a|b|rc|post)/, '$1-$2.');
//	}

}
