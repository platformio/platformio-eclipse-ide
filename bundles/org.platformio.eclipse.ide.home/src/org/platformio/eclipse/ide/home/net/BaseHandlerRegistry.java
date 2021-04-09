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
package org.platformio.eclipse.ide.home.net;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component
public class BaseHandlerRegistry implements HandlerRegistry {

	private final Map<String, IDECommandHandler> handlers = new HashMap<>();

	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	public void bind(IDECommandHandler handler) {
		handlers.put(handler.method(), handler);
	}

	public void unbind(IDECommandHandler handler) {
		handlers.remove(handler.method());
	}

	@Override
	public IDECommandHandler get(String method) {
		return handlers.get(method);
	}

	@Override
	public boolean contains(String method) {
		return handlers.containsKey(method);
	}

}
