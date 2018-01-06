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

import org.junit.Before;
import org.junit.Test;

public class PrologTest {

	private AbstractMachine machine;

	@Test
	public void setAndUnifyVariable() throws Exception {
		machine.setVariable(0);
		machine.printMemory("set");
		machine.unifyVariable(0);

		machine.printMemory("set");

	}

	@Before
	public void setup() {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		machine = builder.heap(20).codeArea(100).register(10).stack(20).pdl(1).trail(1).build();
	}
}
