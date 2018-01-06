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

import org.junit.Before;
import org.junit.Test;

public class SetTest {

	private AbstractMachine memory;

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}

	@Test
	public void setValue() throws Exception {
		memory.set(1, TermFactory.variable(1));
		memory.setValue(1);

		assertArrayEquals(TermFactory.variable(1), memory.get(memory.heap - 1));
	}

	/**
	 * Copies specified value to the current position on the heap
	 */
	@Test
	public void setValueIncrementsHeap() throws Exception {
		int heap = memory.heap;
		memory.set(1, TermFactory.variable(1));
		memory.setValue(1);

		assertEquals(heap + 1, memory.heap);
	}

	@Test
	public void setVariableHeapValue() throws Exception {
		memory.heap = 10;
		memory.setVariable(20);

		assertArrayEquals(TermFactory.variable(10), memory.get(20));
	}

	@Test
	public void setVariableIncrementsHeap() throws Exception {
		memory.heap = 10;
		memory.setVariable(20);

		assertEquals(11, memory.heap);
	}

	@Test
	public void setVariablePushUnbound() throws Exception {
		memory.heap = 10;
		memory.setVariable(20);

		assertArrayEquals(TermFactory.variable(10), memory.get(10));
	}
}
