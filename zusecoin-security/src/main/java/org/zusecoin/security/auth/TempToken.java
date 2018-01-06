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
package org.zusecoin.security.auth;

import java.util.Date;

import org.zusecoin.storage.Id;

/**
 * Temporary token that is generated when the user initiates an authentication
 * flow. This token should be persisted upon creation. After being used to
 * generate a session or after a period of time after creation, it can be
 * deleted.
 */
public class TempToken {

	/**
	 * Date token created
	 */
	public Date created;

	@Id
	public String id;

	/**
	 * True if token has been used to generate a user session, otherwise false
	 */
	public Boolean isUsed;

	public TempToken() {
	}

	/**
	 * Create a temporary token
	 * 
	 * @param id
	 *            unique id of token
	 * @param created
	 *            the date token created
	 * @param isUsed
	 *            if token has been used to generate a session
	 */
	public TempToken(String id, Date created, Boolean isUsed) {
		this.id = id;
		this.created = created;
		this.isUsed = isUsed;
	}
}
