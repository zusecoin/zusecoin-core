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

import java.io.InputStream;
import java.security.Key;
import java.security.SignatureException;

import javax.crypto.Cipher;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

import com.google.common.io.ByteStreams;

public interface Decompiler<T> {

	default T decompile(String hash, byte[] input) throws Exception {
		return decompileEncrypted(hash, input, null);
	}

	default T decompile(String hash, InputStream is) throws Exception {
		return decompileEncrypted(hash, ByteStreams.toByteArray(is), null);
	}

	T decompileEncrypted(String hash, byte[] input, Key privateKey) throws Exception;

	default String decrypt(byte[] encryptedMessage, Key privateKey) throws Exception {
		Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
		c.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] plainText = c.doFinal(encryptedMessage);
		return new String(plainText);
	}

	default void validate(String publicKey, String message, String signature) throws SignatureException {
		if (!publicKey.equals(ECKey.signedMessageToKey(message, signature).toAddress(MainNetParams.get()).toString())) {
			throw new SignatureException("Signature is incorrect");
		}
	}
}
