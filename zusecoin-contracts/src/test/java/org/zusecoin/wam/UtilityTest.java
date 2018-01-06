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
 * Test helper methods of memory class
 */
public class UtilityTest {

	private AbstractMachine memory;

	@Test
	public void addressAsInt() throws Exception {
		byte[] field = new byte[] { 0, 0, 0, 12 };
		memory.set(1, field);
		assertEquals(12, memory.addressAsInt(1));
	}

	@Test
	public void parseFieldAddress() throws Exception {
		byte[] field = new byte[] { 1, 0, 0, 0, 12 };
		memory.set(1, field);
		assertEquals(12, memory.getAddressOfFieldAt(1));
	}

	@Test
	public void readInt() throws Exception {
		byte[] field = new byte[] { 1, 0, 0, 0, 12 };
		assertEquals(12, AbstractMachine.readInt(field, 1));
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}
}
