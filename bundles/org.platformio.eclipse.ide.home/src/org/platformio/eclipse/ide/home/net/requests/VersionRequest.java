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
package org.platformio.eclipse.ide.home.net.requests;

import org.platformio.eclipse.ide.home.net.Handler;
import org.platformio.eclipse.ide.home.net.Request;

public final class VersionRequest implements Request {

	private final Handler handler;

	public VersionRequest(Handler handler) {
		this.handler = handler;
	}

	@Override
	public String method() {
		return "core.version"; //$NON-NLS-1$
	}

	@Override
	public Handler handler() {
		return handler;
	}

}
