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
import java.util.HashMap;
import java.util.Map;

import org.zusecoin.wam.AbstractMachine;
import org.zusecoin.wam.FailureException;

public final class SwitchOnConstant extends Op {

	public final Map<String, Integer> hash;

	public SwitchOnConstant(DataInputStream dis) throws IOException {
		super(Op.SOC);
		int size = dis.readInt();
		hash = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			hash.put(dis.readUTF(), dis.readInt());
		}
	}

	public SwitchOnConstant(Map<String, Integer> hash) {
		super(Op.SOC);
		this.hash = hash;
	}

	@Override
	public void encode(DataOutputStream dos) throws IOException {
		dos.writeInt(dos.size());
		for (Map.Entry<String, Integer> item : hash.entrySet()) {
			dos.writeUTF(item.getKey());
			dos.writeInt(item.getValue());
		}
	}

	@Override
	public void execute(AbstractMachine machine) throws FailureException {
		machine.switchOnConstant(hash);
	}
}
