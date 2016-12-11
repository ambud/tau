/**
 * Copyright 2016 Symantec Corporation.
 * 
 * Licensed under the Apache License, Version 2.0 (the “License”); 
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
 */
package com.srotya.tau.dengine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.srotya.tau.wraith.Event;

/**
 * Storm event implementation.
 * 
 * @author ambud_sharma
 */
public class DEngineEvent implements Event {

	private static final long serialVersionUID = 1L;
	private Long eventId;
	private Map<String, Object> headers;
	private byte[] body;

	DEngineEvent() {
		headers = new ConcurrentHashMap<>(Constants.AVG_EVENT_FIELD_COUNT);
	}

	DEngineEvent(Map<String, Object> headers) {
		this.headers = headers;
	}

	@Override
	public Map<String, Object> getHeaders() {
		return headers;
	}

	@Override
	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "Hendrix [headers=" + headers.toString() + ", body=" + Arrays.toString(body) + "]";
	}

	@Override
	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public static Map<String, Object> getMapInstance() {
		return new ConcurrentHashMap<>(Constants.AVG_EVENT_FIELD_COUNT);
	}

	@Override
	public Long getEventId() {
		return eventId;
	}

	@Override
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	@Override
	public List<Long> getSourceIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSourceIds(List<Long> sourceIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getOriginEventId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOriginEventId(Long eventId) {
		// TODO Auto-generated method stub
		
	}

}
