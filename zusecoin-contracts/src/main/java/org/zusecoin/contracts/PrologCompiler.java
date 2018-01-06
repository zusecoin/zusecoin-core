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
package org.zusecoin.contracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zusecoin.wam.instructions.GetStructure;
import org.zusecoin.wam.instructions.Op;
import org.zusecoin.wam.instructions.PutStructure;
import org.zusecoin.wam.instructions.SetValue;
import org.zusecoin.wam.instructions.SetVariable;
import org.zusecoin.wam.instructions.UnifyValue;
import org.zusecoin.wam.instructions.UnifyVariable;

import com.igormaznitsa.prologparser.PrologParser;
import com.igormaznitsa.prologparser.exceptions.PrologParserException;
import com.igormaznitsa.prologparser.terms.AbstractPrologTerm;
import com.igormaznitsa.prologparser.terms.PrologAtom;
import com.igormaznitsa.prologparser.terms.PrologStructure;
import com.igormaznitsa.prologparser.terms.PrologTermType;

public final class PrologCompiler extends BasePrologCompiler {

	private static String createTermString(PrologAtom atom) {
		return atom.getText() + "/0";
	}

	private static String createTermString(PrologStructure structure) {
		return structure.getFunctor().getText() + "/" + structure.getArity();
	}

	private final PrologParser parser;

	public final Map<String, Integer> map = new HashMap<>();

	public final List<Op> instructions = new ArrayList<>();

	public PrologCompiler() {
		parser = new PrologParser(null);
	}

	public void constructStructure(PrologStructure structure) {
		int register = setRegisterFor(structure.toString());
		instructions.add(new GetStructure(createTermString(structure), register));

		for (int i = 0; i < structure.getArity(); i++) {
			unifyValueOrVariable(structure.getElement(i).toString());
		}

		for (int i = 0; i < structure.getArity(); i++) {
			processTerm(structure.getElement(i));
		}
	}

	public void getStructureForAtom(PrologAtom atom) {
		String term = createTermString(atom);
		int register = setRegisterFor(term);
		instructions.add(new GetStructure(term, register));
	}

	public void processQueryTerm(AbstractPrologTerm term) {
		PrologTermType type = term.getType();
		if (PrologTermType.STRUCT.equals(type)) {
			queryStructure((PrologStructure) term);
		}
	}

	public void processTerm(AbstractPrologTerm term) {
		PrologTermType type = term.getType();
		if (PrologTermType.STRUCT.equals(type)) {
			constructStructure((PrologStructure) term);
		} else if (PrologTermType.VAR.equals(type)) {

		} else if (PrologTermType.ATOM.equals(type)) {

		} else if (PrologTermType.LIST.equals(type)) {

		} else if (PrologTermType.OPERATOR.equals(type)) {

		} else if (PrologTermType.OPERATORS.equals(type)) {

		}

	}

	public void program(String line) throws IOException, PrologParserException {
		AbstractPrologTerm term = parser.nextSentence(line);
		processTerm(term);
	}

	public void query(String line) throws IOException, PrologParserException {
		AbstractPrologTerm term = parser.nextSentence(line);
		processQueryTerm(term);
	}

	public void queryStructure(PrologStructure structure) {
		int register = setRegisterFor(structure.toString());
		instructions.add(new PutStructure(createTermString(structure), register));
		for (int i = 0; i < structure.getArity(); i++) {
			setVariableOrValue(structure.getElement(i).toString());
		}

		for (int i = 0; i < structure.getArity(); i++) {
			processQueryTerm(structure.getElement(i));
		}
	}

	public int setRegisterFor(String term) {
		Integer register = map.get(term);
		if (register == null) {
			register = map.size() + 1;
			map.put(term, register);
		}
		return register;
	}

	public void setVariableOrValue(String term) {
		Integer register = map.get(term);
		if (register != null) {
			instructions.add(new SetValue(register));
		} else {
			register = map.size() + 1;
			map.put(term, register);
			instructions.add(new SetVariable(register));
		}
	}

	public void unifyValueOrVariable(String term) {
		Integer register = map.get(term);
		if (register != null) {
			instructions.add(new UnifyValue(register));
		} else {
			register = map.size() + 1;
			map.put(term, register);
			instructions.add(new UnifyVariable(register));
		}
	}
}
