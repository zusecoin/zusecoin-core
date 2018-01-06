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
package org.zusecoin.prolog.parser;

import org.junit.Test;
import org.zusecoin.contracts.PrologCompiler;
import org.zusecoin.wam.instructions.Op;

import com.igormaznitsa.prologparser.PrologParser;
import com.igormaznitsa.prologparser.terms.PrologStructure;
import com.igormaznitsa.prologparser.terms.PrologTermType;

public class Test2 {

	@Test
	public void a() throws Exception {
		PrologParser parser = new PrologParser(null);
		// "p(X,Y) :- q(X,Z)."
		PrologStructure rule = (PrologStructure) parser.nextSentence("p(f(X),h(Y,f(a)),Y).");
		if (PrologTermType.ATOM.equals(rule.getType())) {
			System.out.println(rule);
		} else if (PrologTermType.VAR.equals(rule.getType())) {
			System.out.println(rule);

		} else if (PrologTermType.STRUCT.equals(rule.getType())) {
			System.out.println(rule);
		}
		// PrologStructure and = (PrologStructure) rule.getElement(1);
		// System.out.println(rule.getElement(0).getText()+'
		// '+and.getElement(0).getText()+and.getElement(1).getText());

	}

	@Test
	public void b() throws Exception {
		PrologCompiler compiler = new PrologCompiler();
		compiler.program("p(f(X),h(Y,f(a)),Y).");
		for (Op op : compiler.instructions) {
			System.out.println(op);
		}
		System.out.println(compiler.map);
	}

	@Test
	public void lessThan() throws Exception {
		PrologCompiler compiler = new PrologCompiler();
		compiler.program("p(X,Y) :- X<Y.");
	}

	@Test
	public void multiple() throws Exception {
		PrologCompiler compiler = new PrologCompiler();
		compiler.program("(1*2 + 3*4).");
	}

	@Test
	public void query() throws Exception {
		PrologCompiler compiler = new PrologCompiler();
		compiler.query("p(Z, h(Z,W), f(W)).");
		for (Op op : compiler.instructions) {
			System.out.println(op);
		}
		System.out.println(compiler.map);
	}

	@Test
	public void rule() throws Exception {
		PrologCompiler compiler = new PrologCompiler();
		compiler.program("p(X,Y) :- q(X,Z),r(Z,Y).");
		for (Op op : compiler.instructions) {
			System.out.println(op);
		}
		System.out.println(compiler.map);
	}
}
