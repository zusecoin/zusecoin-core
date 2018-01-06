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

import org.zusecoin.wam.TextUtils;

public class Reference implements TraceTerm {

	private byte[] field;

	private int address;

	public Reference(byte[] field, int address) {
		this.field = field;
		this.address = address;
	}

	@Override
	public String toText() {
		int addr = TextUtils.readInt(field, 1);
		if (address == addr) {
			return "REF[unbound]";
		} else {
			return "REF[" + addr + "]";
		}
	}

}
