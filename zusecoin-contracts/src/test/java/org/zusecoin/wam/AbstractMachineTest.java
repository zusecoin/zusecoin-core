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

public class AbstractMachineTest {

	private AbstractMachine am;

	@Test
	public void pushUnboundVar() throws Exception {
		byte[] var = am.pushUnboundVar();
		assertEquals(TermFactory.REF_TAG, var[0]);
		assertEquals(am.heap, AbstractMachine.getAddressOfFieldAt(var));
		assertArrayEquals(var, am.get(am.heap));
	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		am = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
	}

	@Test
	public void setValue() throws Exception {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(200);
		AbstractMachine memory = builder.build();

		// memory.setValue(1);
	}
}
