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
package org.platformio.eclipse.ide.home.net.json;

import com.google.gson.JsonElement;

public final class RawResult {

	private final long id;
	private final JsonElement result;

	public RawResult(JsonElement result, long id) {
		this.id = id;
		this.result = result;
	}

	public long id() {
		return id;
	}

	public JsonElement result() {
		return result;
	}
}
