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

import java.util.ArrayList;

import org.zusecoin.wam.AbstractMachine;

public class Tracer {

	private boolean doTrace;

	private AbstractMachine machine;

	private ArrayList<String> labels = new ArrayList<>();

	private ArrayList<AbstractMachine> machines = new ArrayList<>();

	public Tracer(AbstractMachine machine) {
		this.machine = machine;
	}

	public void printTrace() {
		for (int i = 0; i < labels.size(); i++) {
			if (i == 0) {
				machine.compare(labels.get(i), null, machines.get(i));
			} else {
				machine.compare(labels.get(i), machines.get(i - 1), machines.get(i));
			}

		}
		/**
		 * AbstractMachine previous = null;
		 * 
		 * for(Entry<String, AbstractMachine> e : snapshots.entrySet()) {
		 * machine.compare(e.getKey(), previous, e.getValue()); previous = e.getValue();
		 * }
		 */
	}

	public void startTrace() {
		doTrace = true;
	}

	public void stopTrace() {
		doTrace = false;
	}

	public void trace(String label) {
		if (doTrace) {
			labels.add(label);
			machines.add(machine.takeSnapshot());
		}
	}

	public void trace(String operation, int value) {
		if (doTrace) {
			labels.add(operation + " " + value);
			machines.add(machine.takeSnapshot());
		}
	}

}
