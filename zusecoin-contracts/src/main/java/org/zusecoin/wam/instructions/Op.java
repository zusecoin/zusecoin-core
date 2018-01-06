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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.zusecoin.wam.AbstractMachine;
import org.zusecoin.wam.FailureException;

public abstract class Op {

	/**
	 * Allocate
	 */
	public static final byte ALC = 0x0;

	/**
	 * Call
	 */
	public static final byte CALL = 0x1;

	/**
	 * Cut
	 */
	public static final byte CUT = 0x1;

	/**
	 * Deallocate
	 */
	public static final byte DALC = 0x0;

	/**
	 * Execute
	 */
	public static final byte EXE = 0x0;

	/**
	 * Get constant
	 */
	public static final byte GCON = 0x0;

	/**
	 * Get level
	 */
	public static final byte GLVL = 0x0;

	/**
	 * Get list
	 */
	public static final byte GLST = 0x0;

	/**
	 * Get structure
	 */
	public static final byte GSTR = 0x0;

	/**
	 * Get value
	 */
	public static final byte GVAL = 0x0;

	/**
	 * Get variable
	 */
	public static final byte GVAR = 0x0;

	/**
	 * Neck cut
	 */
	public static final byte NCUT = 0x0;

	/**
	 * Proceed
	 */
	public static final byte PROC = 0x0;

	/**
	 * Put constant
	 */
	public static final byte PCON = 0x0;

	/**
	 * Put list
	 */
	public static final byte PLST = 0x0;

	/**
	 * Put structure
	 */
	public static final byte PSTR = 0x0;

	/**
	 * Put unsafe value
	 */
	public static final byte PUVL = 0x0;

	public static final byte PVAL = 0x0;

	/**
	 * Put variable perm
	 */
	public static final byte PVAP = 0x1;

	/**
	 * Put variable temp
	 */
	public static final byte PVAT = 0x0;

	/**
	 * Retry
	 */
	public static final byte RTR = 0x0;

	/**
	 * Retry
	 */
	public static final byte RTE = 0x0;

	/**
	 * Set constant
	 */
	public static final byte SCON = 0x0;

	/**
	 * Set local value
	 */
	public static final byte SLVA = 0x0;

	/**
	 * Set value
	 */
	public static final byte SVAL = 0x0;

	/**
	 * Set variable
	 */
	public static final byte SVAR = 0x0;

	/**
	 * Set void
	 */
	public static final byte SVD = 0x0;

	/**
	 * Switch on constant
	 */
	public static final byte SOC = 0x0;

	/**
	 * Switch on structure
	 */
	public static final byte SOS = 0x0;

	/**
	 * Switch on term
	 */
	public static final byte SOT = 0x0;

	/**
	 * Trust
	 */
	public static final byte TRS = 0x0;

	/**
	 * Trust me
	 */
	public static final byte TRSM = 0x0;

	/**
	 * Try
	 */
	public static final byte TRY = 0x0;

	/**
	 * Try me else
	 */
	public static final byte TME = 0x0;

	/**
	 * Unify constant
	 */
	public static final byte UCON = 0x0;

	/**
	 * Unify local value
	 */
	public static final byte ULV = 0x0;

	/**
	 * Unify value
	 */
	public static final byte UVAL = 0x0;

	/**
	 * Unify variable
	 */
	public static final byte UVAR = 0x0;

	/**
	 * Unify void
	 */
	public static final byte UVD = 0x0;

	/**
	 * Operation code or type
	 */
	private final byte code;

	/**
	 * Constructs an Op with specific type
	 * 
	 * @param code
	 *            the op type
	 */
	public Op(byte code) {
		this.code = code;
	}

	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.write(getCode());
			encode(dos);
		}
		os.flush();
		return os.toByteArray();
	}

	/**
	 * Encodes the operation as byte array
	 * 
	 * @return
	 * @throws IOException
	 */
	protected abstract void encode(DataOutputStream dos) throws IOException;

	public abstract void execute(AbstractMachine machine) throws FailureException;

	public byte getCode() {
		return code;
	}

	// "PVAR"
	// put value: "PVAL"
	// put unsafe value: "PUVL"
	// put structure: "PSTR"
	// put list : "PLST"
	// put constant: "PCNT"
	// set variable: "SVAR"
	// set value: "SVAL"
	// set local value: "SLVL"
	// set constant: "SCNT"
	// set void: "SVD"

}
