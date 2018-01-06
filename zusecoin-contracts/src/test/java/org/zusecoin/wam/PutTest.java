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
 * Test Put instructions
 */
public class PutTest {

	private AbstractMachine memory;

	@Test
	public void putConstant() throws Exception {
		memory.putConstant("parent", 0);
		assertArrayEquals(TermFactory.constant("parent"), memory.get(0));
	}

	/**
	 * Heap counter should increment when putting a structure
	 */
	@Test
	public void putStructureIncrement() throws Exception {
		int currentHeap = memory.heap;
		memory.putStructure("f/1", 0);
		assertEquals(currentHeap + 1, memory.heap);
	}

	/**
	 * Test structure field is correct
	 * 
	 * @throws Exception
	 */
	@Test
	public void putStructureTestHeap() throws Exception {
		memory.putStructure("f/1", 0);
		byte[] field = memory.get(memory.heap - 1);
		assertNotNull(field);

		// assertArrayEquals("f/1".getBytes(), Arrays.copyOfRange(field, 1,
		// field.length));
	}

	/**
	 * Test that the register points to the heap
	 */
	@Test
	public void putStructureTestRegister() throws Exception {
		memory.putStructure("f/1", 0);
		byte[] field = memory.get(0);
		assertNotNull(field);
		assertEquals(TermFactory.STRUCTURE_TAG, field[0]);

		int address = AbstractMachine.readInt(field, 1);
		assertEquals(memory.heap - 1, address);
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}
}
