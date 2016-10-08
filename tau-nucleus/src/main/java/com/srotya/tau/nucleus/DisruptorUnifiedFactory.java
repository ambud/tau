/**
 * Copyright 2016 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.tau.nucleus;

import javax.validation.constraints.NotNull;

import com.lmax.disruptor.EventFactory;
import com.srotya.tau.nucleus.processor.AbstractProcessor;
import com.srotya.tau.nucleus.wal.WAL;
import com.srotya.tau.wraith.Event;
import com.srotya.tau.wraith.UnifiedFactory;

/**
 * Unified factory implementation for Tau
 * 
 * @author ambudsharma
 */
public class DisruptorUnifiedFactory extends UnifiedFactory implements EventFactory<Event> {

	@Override
	public Event newInstance() {
		return buildEvent();
	}

	/**
	 * @param walType
	 * @param persistentDirectory
	 * @param transientDirectory
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public WAL newWalInstance(@NotNull Class<? extends WAL> walType,
			@NotNull com.srotya.tau.wraith.EventFactory factory, @NotNull AbstractProcessor caller,
			@NotNull String persistentDirectory, @NotNull String transientDirectory)
			throws InstantiationException, IllegalAccessException {
		WAL wal = walType.newInstance();
		wal.setPersistentDirectory(persistentDirectory);
		wal.setTransientDirectory(transientDirectory);
		wal.setEventFactory(factory);
		wal.setCallingProcessor(caller);
		return wal;
	}

}