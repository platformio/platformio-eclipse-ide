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
package org.platformio.eclipse.ide.installer.json;

public final class Distribution {

	private final String download_url;
	private final String[] system;

	public Distribution(String[] system, String download_url) {
		this.download_url = download_url;
		this.system = system;
	}

	public String url() {
		return download_url;
	}

	public String[] system() {
		return system;
	}

}
