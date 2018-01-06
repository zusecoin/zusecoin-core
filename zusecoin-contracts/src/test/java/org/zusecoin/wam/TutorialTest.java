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
import static org.zusecoin.wam.TermFactory.functor;
import static org.zusecoin.wam.TermFactory.structure;
import static org.zusecoin.wam.TermFactory.variable;

import org.junit.Test;

import com.igormaznitsa.prologparser.PrologParser;
import com.igormaznitsa.prologparser.terms.AbstractPrologTerm;
import com.igormaznitsa.prologparser.terms.PrologStructure;

public class TutorialTest {

	/**
	 * Verify that the effect of executing the sequence of instructions shown in
	 * Figure 2.3 (starting with H = 0) does indeed yield a correct heap
	 * representation for the term p(Z, h(Z, W), f(W)) — the one shown earlier as
	 * Figure 2.1, in fact.
	 * 
	 * See PDF/13 or book/9
	 * 
	 * @throws Exception
	 */
	@Test
	public void exercise21() throws Exception {
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		AbstractMachine memory = builder.heap(20).codeArea(1).register(10).stack(20).pdl(1).trail(1).build();
		memory.instructionLabel = 17;
		int startHeap = memory.heap;

		int X1 = 0;
		int X2 = 1;
		int X3 = 2;
		int X4 = 3;
		int X5 = 4;

		memory.putStructure("h/2", X3);

		memory.setVariable(X2);
		memory.setVariable(X5);
		memory.putStructure("f/1", X4);
		memory.setValue(X5);
		memory.putStructure("p/3", X1);
		memory.setValue(X2);
		memory.setValue(X3);
		memory.setValue(X4);

		memory.printMachine();
		memory.printMemory("ex 2.1");

		// assert values from Fig 2.1
		assertArrayEquals(functor("h/2"), memory.get(startHeap));
		assertArrayEquals(variable(12), memory.get(startHeap + 1));
		assertArrayEquals(variable(13), memory.get(startHeap + 2));
		assertArrayEquals(functor("f/1"), memory.get(startHeap + 3));
		assertArrayEquals(variable(13), memory.get(startHeap + 4));
		assertArrayEquals(functor("p/3"), memory.get(startHeap + 5));
		assertArrayEquals(variable(12), memory.get(startHeap + 6));
		assertArrayEquals(structure(11), memory.get(startHeap + 7));
		assertArrayEquals(structure(14), memory.get(startHeap + 8));
	}

	/**
	 * Give heap representations for the terms f(X, g(X, a)) and f(b, Y). Let a1 and
	 * a2 be their respective heap addresses, and let ax and ay be the heap
	 * addresses corresponding to variables X and Y, respectively. Trace the effects
	 * of executing unify(a1, a2), verifying that it terminates with the eventual
	 * dereferenced bindings from ax and ay corresponding to X = b and Y = g(b, a).
	 */
	@Test
	public void exercise22() throws Exception {// page 19
		AbstractMachine.AbstractMachineBuilder builder = new AbstractMachine.AbstractMachineBuilder(11);
		AbstractMachine machine = builder.heap(20).codeArea(1).register(10).stack(20).pdl(10).trail(10).build();
		machine.instructionLabel = 17;
		int startHeap = machine.heap;
		int X1 = 0;
		int X2 = 1;
		int X3 = 2;
		int X4 = 3;
		int X5 = 4;
		int X6 = 5;

		/**
		 * 
		 * f = X1 X = X2 g = X3 a = X4 b = X5 Y = X6
		 */
		// f(X, g(X, a)) and f(b, Y)
		machine.printMachine();
		machine.getTracer().startTrace();
		machine.putStructure("g/2", X3);
		machine.setVariable(X2);
		machine.setVariable(X4);

		machine.putStructure("f/2", X1);
		machine.setValue(X2);
		machine.setValue(X3);

		machine.putStructure("f/2", X1);
		machine.setVariable(X5);
		machine.setVariable(X6);

		machine.getTracer().printTrace();

		// memory.unify(0, 2);
	}

	@Test
	public void testA() {
		final PrologParser parser = new PrologParser(null);
		try {
			final PrologStructure structure = (PrologStructure) parser.nextSentence("world(X, g(Y)).");
			AbstractPrologTerm t;

			// structure.getArity()
			System.out.println(structure.getElement(0).getText() + ' ' + structure.getElement(1).getText());
		} catch (Exception unexpected) {
			throw new RuntimeException(unexpected);
		}
	}
}
