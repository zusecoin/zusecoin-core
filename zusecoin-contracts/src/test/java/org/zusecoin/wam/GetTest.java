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

import org.junit.Before;
import org.junit.Test;

/**
 * Test get instructions
 */
public class GetTest {

	private AbstractMachine memory;

	/**
	 * Copy from register to another area of memory
	 */
	@Test
	public void getVariable() throws Exception {
		byte[] field = new byte[] { TermFactory.CONSTANT_TAG, 102 };
		memory.set(0, field);
		memory.getVariable(10, 0);

		assertArrayEquals(memory.get(10), field);
	}

	/**
	 * Copy from register to another area of memory
	 */
	@Test
	public void getVariableEmptyOk() throws Exception {
		memory.getVariable(10, 0);
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}
}
