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
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component
public class DefaultHandlerRegistry implements HandlerRegistry {

	private final Map<String, IDECommand> handlers = new HashMap<>();

	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	public void bind(IDECommand handler) {
		System.out.println("bound: " + handler.method()); //$NON-NLS-1$
		handlers.put(handler.method(), handler);
	}

	public void unbind(IDECommand handler) {
		handlers.remove(handler.method());
	}

	@Override
	public Optional<IDECommand> get(String method) {
		if (handlers.containsKey(method)) {
			return Optional.of(handlers.get(method));
		}
		return Optional.empty();
	}

}
