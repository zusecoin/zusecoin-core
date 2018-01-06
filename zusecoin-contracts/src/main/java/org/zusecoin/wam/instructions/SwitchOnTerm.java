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
package org.zusecoin.wam.instructions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.zusecoin.wam.AbstractMachine;
import org.zusecoin.wam.FailureException;

public final class SwitchOnTerm extends Op {

	public final int variable;

	public final int constant;

	public final int list;

	public final int structure;

	public SwitchOnTerm(DataInputStream dis) throws IOException {
		this(dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt());
	}

	public SwitchOnTerm(int variable, int constant, int list, int structure) {
		super(Op.SOT);
		this.variable = variable;
		this.constant = constant;
		this.list = list;
		this.structure = structure;
	}

	@Override
	public void encode(DataOutputStream dos) throws IOException {
		dos.writeInt(variable);
		dos.writeInt(constant);
		dos.writeInt(list);
		dos.writeInt(structure);
	}

	@Override
	public void execute(AbstractMachine machine) throws FailureException {
		machine.switchOnTerm(variable, constant, list, structure);
	}

}
