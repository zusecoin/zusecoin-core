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
package org.zusecoin.wam.trace;

import static org.zusecoin.wam.TermFactory.CONSTANT_TAG;
import static org.zusecoin.wam.TermFactory.FUNCTOR_TAG;
import static org.zusecoin.wam.TermFactory.LIST_TAG;
import static org.zusecoin.wam.TermFactory.REF_TAG;
import static org.zusecoin.wam.TermFactory.STRUCTURE_TAG;

import org.zusecoin.wam.AbstractMachine;

public class TraceTermFactory {

	public static TraceTerm create(int address, AbstractMachine machine) {
		byte[] field = machine.get(address);
		switch (field[0]) {
		case REF_TAG:
			return new Reference(field, address);
		case STRUCTURE_TAG:
			return new Structure(field, machine);
		case LIST_TAG:
			return null;
		case CONSTANT_TAG:
			return null;
		case FUNCTOR_TAG:
			return new Functor(field);
		}
		return null;
	}
}
