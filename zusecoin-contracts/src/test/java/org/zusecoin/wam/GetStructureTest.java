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
import static org.junit.Assert.assertTrue;

import org.junit.Before;

public class GetStructureTest {

	private AbstractMachine memory;

	// @Test
	public void bindAddressIfRefPointsToUnboundVar() throws Exception {
		int heap = memory.heap;
		memory.instructionLabel = 17;

		byte[] functorField = new byte[] { 100 };
		memory.set(20, functorField);

		memory.set(0, TermFactory.variable(12));
		memory.set(12, TermFactory.variable(12));

		// memory.getStructure(20, 0);
		// copy 12 to 11
		assertArrayEquals(memory.get(heap), memory.get(12));
	}

	/**
	 * If the register points to an unbound variable, push a structure onto heap and
	 * then copy the specified functor field into the next (heap + 1) position
	 */
	// @Test
	public void copyFunctorToHeapIfRefPointsToUnboundVar() throws Exception {
		int heap = memory.heap;
		memory.instructionLabel = 17;

		byte[] functorField = new byte[] { 100 };
		memory.set(20, functorField);

		memory.set(0, TermFactory.variable(12));
		memory.set(12, TermFactory.variable(12));

		// memory.getStructure(20, 0);

		byte[] field = memory.get(heap + 1);
		assertNotNull(field);
		assertArrayEquals(functorField, field);
	}

	/**
	 * In this test, the field address at register 0 is different than the address
	 * specified in the getStructure method, so the method returns a failure.
	 */
	// @Test
	public void failIfFunctorAddressNotEqualToFieldAddress() throws Exception {
		// memory.putStructure("f", 1, 0);
		// assertFalse(memory.getStructure(memory.heap + 1, 0));
	}

	/**
	 * Test next subterm should point to current heap pointer
	 */
	// @Test
	public void nextSubtermEqualsHeapAddress() throws Exception {
		memory.writeMode();
		// memory.putStructure("f", 1, 0);
		// memory.getStructure(memory.heap - 1, 0);
		assertEquals(memory.heap, memory.nextSubterm);
	}

	/**
	 * Test structure pushed to heap if the structure is unbound
	 */
	// @Test
	public void pushToHeapIfRefPointsToUnboundVar() throws Exception {
		int heap = memory.heap;

		memory.set(0, TermFactory.variable(12));
		memory.set(12, TermFactory.variable(12));
		// memory.getStructure(12, 0);

		byte[] field = memory.get(heap);
		assertNotNull(field);
		assertEquals(TermFactory.STRUCTURE_TAG, field[0]);
		assertEquals(heap + 1, memory.getAddressOfFieldAt(heap));
	}

	// @Test
	public void refPointsToSelfRefReadMode() throws Exception {
		memory.readMode();
		memory.set(0, TermFactory.variable(12));
		memory.set(12, TermFactory.variable(12));
		// memory.getStructure(12, 0);
		assertTrue(memory.mode.equals(Mode.WRITE));
	}

	/**
	 * If we specify register index that is not in the register area, throw
	 * exception
	 */
	// @Test(expected = IllegalStateException.class)
	public void registerOutOfBounds() throws Exception {
		memory.register = 10;
		// memory.getStructure(12, 11);
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}

	// @Test(expected = ExecutionException.class)
	public void structureThatDoesNotExist() throws Exception {
		// memory.getStructure(10, 0);
	}

	/**
	 * Test
	 */
	// @Test
	public void switchesToReadMode() throws Exception {
		memory.writeMode();
		memory.putStructure("f/1", 0);
		// memory.getStructure(memory.heap - 1, 0);
		assertTrue(memory.mode.equals(Mode.READ));
	}

	/**
	 * Test for write mode if the structure is unbound
	 */
	// @Test
	public void writeModeIfRefPointsToUnboundVar() throws Exception {
		memory.readMode();
		memory.set(0, TermFactory.variable(12));
		memory.set(12, TermFactory.variable(12));
		// memory.getStructure(12, 0);
		assertTrue(memory.mode.equals(Mode.WRITE));
	}
}
