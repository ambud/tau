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
package com.srotya.tau.wraith.conditions.logical;

import java.util.List;

import com.srotya.tau.wraith.conditions.Condition;

/**
 * Od condition is a {@link ComplexCondition} such that any of them have to be true. 
 * This is a short circuit Or i.e. same as || to help improve performance.
 * 
 * @author ambud_sharma
 *
 */
public class OrCondition extends ComplexCondition {

	private static final long serialVersionUID = 1677724624900681868L;

	public OrCondition(List<Condition> conditions) {
		super(conditions);
	}

	@Override
	public boolean operator(boolean c1, boolean c2) {
		return c1 || c2;
	}

	@Override
	public boolean shortCircuit(boolean c1, boolean c2) {
		return c1 || c2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "|| (OR)";
	}

}
