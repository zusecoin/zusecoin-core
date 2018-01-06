/*******************************************************************************
 * ZuseCoin licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.zusecoin.wam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test trail instruction
 */
public class TrailTest {

	private AbstractMachine memory;

	/**
	 * Address is between heap and previousChoicePoint. Address greater than heap
	 * backtrack.
	 */
	@Test
	public void addWhenInbounds() {
		memory.heap = 14;
		memory.heapBacktrack = 15;
		memory.latestChoicePoint = 17;
		memory.trail(16);

		int tr = AbstractMachine.readInt(memory.get(memory.trail - 1), 0);
		assertNotNull(tr);
		assertEquals(16, tr);
	}

	@Test
	public void addWhenTrailLessThanHB() {
		memory.heapBacktrack = 15;
		memory.trail(14);

		int tr = AbstractMachine.readInt(memory.get(memory.trail - 1), 0);
		assertNotNull(tr);
		assertEquals(14, tr);
	}

	@Test
	public void incrementTrail() {
		int trail = memory.trail;
		memory.heap = 14;
		memory.heapBacktrack = 15;
		memory.latestChoicePoint = 17;
		memory.trail(16);

		assertEquals(trail + 1, memory.trail);
	}

	@Test
	public void noAddWhenTrailGreaterThanHB() {
		memory.heapBacktrack = 15;
		memory.trail(16);
		assertNull(memory.get(memory.trail - 1));
	}

	/**
	 * Don't increment trail if trail index greater than heap backtrack
	 */
	@Test
	public void noIncrementTrail() {
		int trail = memory.trail;
		memory.heapBacktrack = 15;
		memory.trail(16);
		assertEquals(trail, memory.trail);
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}
}
