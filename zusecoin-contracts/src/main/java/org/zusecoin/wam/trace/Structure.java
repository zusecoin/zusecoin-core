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

import static org.zusecoin.wam.TextUtils.readInt;
import static org.zusecoin.wam.TextUtils.readString;

import org.zusecoin.wam.AbstractMachine;

public class Structure implements TraceTerm {

	private byte[] field;

	private String text;

	public Structure(byte[] field, AbstractMachine machine) {
		this.field = field;
		int addr = readInt(field, 1);
		this.text = "STR[" + addr + "] (" + readString(machine.get(addr), 4) + ")";
	}

	@Override
	public String toText() {
		return text;
	}
}
