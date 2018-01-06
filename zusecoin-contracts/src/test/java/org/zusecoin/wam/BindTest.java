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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test bind instruction
 */
public class BindTest {

	private AbstractMachine memory;

	/**
	 * If we bind two refs, then copy lower address to higher address
	 */
	@Test
	public void bindUnboundVars() throws Exception {
		byte[] var1 = memory.pushUnboundVar();
		int a1 = AbstractMachine.getAddressOfFieldAt(var1);

		memory.heap++;

		byte[] var2 = memory.pushUnboundVar();
		int a2 = AbstractMachine.getAddressOfFieldAt(var2);

		memory.bind(a1, a2);
		assertArrayEquals(var1, memory.get(a2));
	}

	/**
	 * If we bind two refs, then higher address should be copied into the trail.
	 */
	@Test
	public void bindUnboundVarsWithTrail() throws Exception {
		memory.heapBacktrack = 15;

		byte[] var1 = memory.pushUnboundVar();
		int a1 = AbstractMachine.getAddressOfFieldAt(var1);

		memory.heap++;

		byte[] var2 = memory.pushUnboundVar();
		int a2 = AbstractMachine.getAddressOfFieldAt(var2);

		memory.bind(a1, a2);

		int tr = AbstractMachine.readInt(memory.get(memory.trail - 1), 0);
		assertNotNull(tr);
		assertEquals(a2, tr);
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}
}
