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
package com.srotya.tau.linea.ft;

import com.srotya.tau.linea.network.Router;
import com.srotya.tau.wraith.Constants;
import com.srotya.tau.wraith.Event;
import com.srotya.tau.wraith.EventFactory;

/**
 * @author ambud
 */
public class Collector {

	public static final String FIELD_ACK_EVENT = "_ack";
	private EventFactory factory;
	private Router router;

	public Collector(EventFactory factory, Router router) {
		this.factory = factory;
		this.router = router;
	}

	public void ack(Event event) {
		for (Long sourceEventId : event.getSourceIds()) {
			ack((String) event.getHeaders().get(Constants.FIELD_SPOUT_NAME), event.getOriginEventId(), sourceEventId,
					event.getEventId());
		}
	}

	protected void ack(String spoutName, Long originalEventId, Long sourceEventId, Long currentEventId) {
		Event event = factory.buildEvent();
		event.getHeaders().put(FIELD_ACK_EVENT, true); // for debug purposes
		event.getHeaders().put(Constants.FIELD_AGGREGATION_KEY, originalEventId);
		event.getHeaders().put(Constants.FIELD_AGGREGATION_TYPE, sourceEventId);
		event.getHeaders().put(Constants.FIELD_AGGREGATION_VALUE, currentEventId);
		event.getHeaders().put(Constants.FIELD_SPOUT_NAME, spoutName);
		router.routeEvent(Acker.ACKER_BOLT_NAME, event);
	}

	public void spoutEmit(String spoutName, String nextProcessorId, Event event) {
		event.getHeaders().put(Constants.FIELD_SPOUT_NAME, spoutName);
		event.setOriginEventId(event.getEventId());
		emit(nextProcessorId, event, event);
	}

	public void emit(String nextProcessorId, Event outputEvent) {
		router.routeEvent(nextProcessorId, outputEvent);
	}

	public void emit(String nextProcessorId, Event outputEvent, Event anchorEvent) {
		outputEvent.getSourceIds().add(anchorEvent.getEventId());
		outputEvent.setOriginEventId(anchorEvent.getOriginEventId());
		outputEvent.getHeaders().put(Constants.FIELD_SPOUT_NAME,
				(String) anchorEvent.getHeaders().get(Constants.FIELD_SPOUT_NAME));
		ack((String) anchorEvent.getHeaders().get(Constants.FIELD_SPOUT_NAME), anchorEvent.getOriginEventId(),
				anchorEvent.getEventId(), outputEvent.getEventId());
		router.routeEvent(nextProcessorId, outputEvent);
	}

	public void emit(String nextProcessorId, Event outputEvent, Event... anchorEvents) {
		for (Event anchorEvent : anchorEvents) {
			outputEvent.getSourceIds().add(anchorEvent.getEventId());
			outputEvent.setOriginEventId(anchorEvent.getOriginEventId());
			outputEvent.getHeaders().put(Constants.FIELD_SPOUT_NAME,
					(String) anchorEvent.getHeaders().get(Constants.FIELD_SPOUT_NAME));
			ack((String) anchorEvent.getHeaders().get(Constants.FIELD_SPOUT_NAME), anchorEvent.getOriginEventId(),
					anchorEvent.getEventId(), outputEvent.getEventId());
		}
		router.routeEvent(nextProcessorId, outputEvent);
	}

	public EventFactory getFactory() {
		return factory;
	}

}
