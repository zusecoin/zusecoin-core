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

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;

import org.bitcoinj.core.ECKey;

public interface Compiler<T> {

	default String compile(T file, ECKey ecKey) throws Exception {
		if (file == null) {
			throw new IllegalArgumentException("Input file is null");
		}

		if (ecKey == null) {
			throw new IllegalArgumentException("ECKey is null");
		}
		return compileEncrypted(file, ecKey, null);
	}

	/**
	 * Compile <code>DocumentFile</code> to binary and generated a signature using
	 * the specified key.
	 * 
	 * @param ecKey
	 *            the key to use to generate signature
	 * @return a string in the format [documentFile + "." + publicKeyHash + "." +
	 *         signature64];
	 * @throws Exception
	 */
	String compileEncrypted(T file, ECKey ecKey, Key key) throws Exception;

	default String encrypt(String message, Key publicKey) throws Exception {
		Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
		c.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherTextArray = c.doFinal(message.getBytes());
		return Base64.getEncoder().encodeToString(cipherTextArray);
	}

}
