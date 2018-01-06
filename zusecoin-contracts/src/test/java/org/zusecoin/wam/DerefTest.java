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

import org.junit.Before;
import org.junit.Test;

/**
 * Test deref instruction
 */
public class DerefTest {

	private AbstractMachine memory;

	@Test(expected = ExecutionException.class)
	public void addressTooHigh() {
		memory.deref(1000);
	}

	@Test
	public void deref() {
		memory.set(0, TermFactory.variable(1));
		memory.set(1, TermFactory.structure(5));
		assertEquals(1, memory.deref(0));
	}

	/**
	 * Two refs create cycle
	 */
	@Test(expected = StackOverflowError.class)
	public void derefCycle() {
		memory.set(0, TermFactory.variable(1));
		memory.set(1, TermFactory.variable(0));

		memory.deref(0);
	}

	/**
	 * Ref points to address that doesn't exist
	 */
	@Test(expected = ExecutionException.class)
	public void derefDangling() {
		memory.set(0, TermFactory.variable(1));
		memory.set(1, TermFactory.variable(2));

		memory.deref(0);
	}

	@Test(expected = ExecutionException.class)
	public void emptyField() {
		memory.deref(1);
	}

	/**
	 * Only deref a term, otherwise just return the address
	 */
	@Test
	public void noRef() {
		memory.set(1, TermFactory.structure(5));
		assertEquals(1, memory.deref(1));
	}

	@Test
	public void selfRef() {
		memory.set(1, TermFactory.variable(1));
		assertEquals(1, memory.deref(1));
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}
}
