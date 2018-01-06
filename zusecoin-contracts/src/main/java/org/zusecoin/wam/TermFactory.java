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

import java.nio.ByteBuffer;

/**
 * Factory for creating prolog terms. These terms are encoded with their
 * corresponding tags and values so the machine can place them on the heap.
 */
public class TermFactory {

	public static final byte REF_TAG = (byte) 0;// REF

	public static final byte STRUCTURE_TAG = (byte) 1;

	public static final byte LIST_TAG = (byte) 2;

	/**
	 * Tag encoding for constants
	 */
	public static final byte CONSTANT_TAG = (byte) 4;

	public static final byte INT_TAG = (byte) 3;

	public static final byte FUNCTOR_TAG = (byte) 5;

	/**
	 * Encodes a string constant value as bytes. The specified value must begin with
	 * a lower case character.
	 * 
	 * @param value
	 *            the value to encode
	 * @return the constant as bytes
	 */
	public static byte[] constant(String value) {
		if (value == null) {
			throw new IllegalArgumentException("value is null");
		}
		// TODO: check that this is lower case
		return ByteBuffer.allocate(value.length() + 1).put(CONSTANT_TAG).put(value.getBytes()).array();
	}

	private static byte[] encodeInt(byte tag, int i) {
		return ByteBuffer.allocate(5).put(tag).putInt(i).array();
	}

	/**
	 * Encodes a functor and its arity. The subterm values of the functor are not
	 * encoded within this method
	 * 
	 * @param functor
	 * @return encoded functor
	 */
	public static byte[] functor(String functor) {
		return ByteBuffer.allocate(functor.length() + 5).put(FUNCTOR_TAG).put(functor.getBytes()).array();
	}

	public static byte[] integer(int value) {
		return encodeInt(INT_TAG, value);
	}

	public static byte[] list(int address) {
		return encodeInt(LIST_TAG, address);
	}

	public static byte[] structure(int address) {
		return encodeInt(STRUCTURE_TAG, address);
	}

	/**
	 * Creates a variable encoded as bytes. A variable may be bounded or unbounded.
	 * 
	 * @param address
	 *            the store address in the heap
	 * @return bytes to be placed on the heap
	 */
	public static byte[] variable(int address) {
		return encodeInt(REF_TAG, address);
	}

}
