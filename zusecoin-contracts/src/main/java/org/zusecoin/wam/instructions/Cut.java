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

public final class Cut extends Op {

	public int permanentVariable;

	public Cut(DataInputStream dis) throws IOException {
		this(dis.readInt());
	}

	public Cut(int permanentVariable) {
		super(Op.CUT);
		this.permanentVariable = permanentVariable;
	}

	@Override
	public void encode(DataOutputStream dos) throws IOException {
		dos.writeInt(permanentVariable);
	}

	@Override
	public void execute(AbstractMachine machine) throws FailureException {
		machine.cut(permanentVariable);
	}
}
