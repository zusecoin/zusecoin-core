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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.zusecoin.wam.AbstractMachine;
import org.zusecoin.wam.FailureException;
import org.zusecoin.wam.instructions.Allocate;
import org.zusecoin.wam.instructions.Call;
import org.zusecoin.wam.instructions.Cut;
import org.zusecoin.wam.instructions.Deallocate;
import org.zusecoin.wam.instructions.Execute;
import org.zusecoin.wam.instructions.GetConstant;
import org.zusecoin.wam.instructions.GetLevel;
import org.zusecoin.wam.instructions.GetList;
import org.zusecoin.wam.instructions.GetStructure;
import org.zusecoin.wam.instructions.GetValue;
import org.zusecoin.wam.instructions.GetVariable;
import org.zusecoin.wam.instructions.NeckCut;
import org.zusecoin.wam.instructions.Op;
import org.zusecoin.wam.instructions.Proceed;
import org.zusecoin.wam.instructions.PutConstant;
import org.zusecoin.wam.instructions.PutList;
import org.zusecoin.wam.instructions.PutStructure;
import org.zusecoin.wam.instructions.PutUnsafeValue;
import org.zusecoin.wam.instructions.PutValue;
import org.zusecoin.wam.instructions.PutVariablePerm;
import org.zusecoin.wam.instructions.PutVariableTemp;
import org.zusecoin.wam.instructions.Retry;
import org.zusecoin.wam.instructions.RetryMeElse;
import org.zusecoin.wam.instructions.SetConstant;
import org.zusecoin.wam.instructions.SetLocalValue;
import org.zusecoin.wam.instructions.SetValue;
import org.zusecoin.wam.instructions.SetVariable;
import org.zusecoin.wam.instructions.SetVoid;
import org.zusecoin.wam.instructions.SwitchOnConstant;
import org.zusecoin.wam.instructions.SwitchOnStructure;
import org.zusecoin.wam.instructions.SwitchOnTerm;
import org.zusecoin.wam.instructions.Trust;
import org.zusecoin.wam.instructions.TrustMe;
import org.zusecoin.wam.instructions.Try;
import org.zusecoin.wam.instructions.TryMeElse;
import org.zusecoin.wam.instructions.UnifyConstant;
import org.zusecoin.wam.instructions.UnifyLocalValue;
import org.zusecoin.wam.instructions.UnifyValue;
import org.zusecoin.wam.instructions.UnifyVariable;
import org.zusecoin.wam.instructions.UnifyVoid;

public final class ContractFile {

	public static class Builder {

		private List<Op> ops = new ArrayList<>();

		private Builder add(Op op) {
			ops.add(op);
			return this;
		}

		public Builder allocate() {
			ops.add(new Allocate());
			return this;
		}

		public ContractFile build() {
			ContractFile file = new ContractFile();

			return file;
		}

		public Builder call(int instructionLabel) {
			return add(new Call(instructionLabel));
		}

		public Builder cut(int permamentVariable) {
			return add(new Cut(permamentVariable));
		}

		public Builder deallocate() {
			return add(new Deallocate());
		}

		public Builder execute(int instructionLabel) {
			return add(new Execute(instructionLabel));
		}

		public Builder getConstant(String value, int argumentRegister) {
			return add(new GetConstant(value, argumentRegister));
		}

		public Builder getLevel(int permanentVariable) {
			return add(new GetLevel(permanentVariable));
		}

		public Builder getList(int argumentRegister) {
			return add(new GetList(argumentRegister));
		}

		public Builder getStructure(String functor, int argumentRegister) {
			return add(new GetStructure(functor, argumentRegister));
		}

		public Builder getValue(int variable, int argumentRegister) {
			return add(new GetValue(variable, argumentRegister));
		}

		public Builder getVariable(int variable, int argumentRegister) {
			return add(new GetVariable(variable, argumentRegister));
		}

		public Builder neckCut() {
			return add(new NeckCut());
		}

		public Builder proceed() {
			return add(new Proceed());
		}

		public Builder putConstant(String constant, int argumentRegister) {
			return add(new PutConstant(constant, argumentRegister));
		}

		public Builder putList(int argumentRegister) {
			return add(new PutList(argumentRegister));
		}

		public Builder putStructure(String functor, int argumentRegister) {
			return add(new PutStructure(functor, argumentRegister));
		}

		public Builder putUnsafeValue(int permanentVariable, int argumentRegister) {
			return add(new PutUnsafeValue(permanentVariable, argumentRegister));
		}

		public Builder putValue(int argumentRegister, int variable) {
			return add(new PutValue(argumentRegister, variable));
		}

		public Builder putVariablePerm(int permanentVariable, int argumentRegister) {
			return add(new PutVariablePerm(permanentVariable, argumentRegister));
		}

		public Builder putVariableTemp(int register, int argumentRegister) {
			return add(new PutVariableTemp(register, argumentRegister));
		}

		public Builder retry(int instructionLabel) {
			return add(new Retry(instructionLabel));
		}

		public Builder retryMeElse(int instructionLabel) {
			return add(new RetryMeElse(instructionLabel));
		}

		public Builder setConstant(String constant) {
			return add(new SetConstant(constant));
		}

		public Builder setLocalValue(int variable) {
			return add(new SetLocalValue(variable));
		}

		public Builder setValue(int variable) {
			return add(new SetValue(variable));
		}

		public Builder setVariable(int variable) {
			return add(new SetVariable(variable));
		}

		public Builder setVoid(int n) {
			return add(new SetVoid(n));
		}

		public Builder switchOnConstant(Map<String, Integer> hash) {
			return add(new SwitchOnConstant(hash));
		}

		public Builder switchOnStructure(Map<String, Integer> hash) {
			return add(new SwitchOnStructure(hash));
		}

		public Builder switchOnTerm(int variable, int constant, int list, int structure) {
			return add(new SwitchOnTerm(variable, constant, list, structure));
		}

		public Builder trust(int instructionLabel) {
			return add(new Trust(instructionLabel));
		}

		public Builder trustMe() {
			return add(new TrustMe());
		}

		public Builder tryInstruction(int instructionLabel) {
			return add(new Try(instructionLabel));
		}

		public Builder tryMeElse(int instructionLabel) {
			return add(new TryMeElse(instructionLabel));
		}

		public Builder unifyConstant(String constant) {
			return add(new UnifyConstant(constant));
		}

		public Builder unifyLocalValue(int variable) {
			return add(new UnifyLocalValue(variable));
		}

		public Builder unifyValue(int variable) {
			return add(new UnifyValue(variable));
		}

		public Builder unifyVariable(int variable) {
			return add(new UnifyVariable(variable));
		}

		public Builder unifyVoid(int numberOfUnboundCells) {
			return add(new UnifyVoid(numberOfUnboundCells));
		}

	}

	public static class StreamBuilder {
		private List<Op> ops = new ArrayList<>();

		private StreamBuilder add(Op op) {
			ops.add(op);
			return this;
		}

		public StreamBuilder allocate() {
			return add(new Allocate());
		}

		public StreamBuilder call(DataInputStream dis) throws IOException {
			return add(new Call(dis));
		}

		public StreamBuilder cut(DataInputStream dis) throws IOException {
			return add(new Cut(dis));
		}

		public StreamBuilder deallocate() {
			return add(new Deallocate());
		}

		public StreamBuilder execute(DataInputStream dis) throws IOException {
			return add(new Execute(dis));
		}

		public StreamBuilder getConstant(DataInputStream dis) throws IOException {
			return add(new GetConstant(dis));
		}

		public StreamBuilder getLevel(DataInputStream dis) throws IOException {
			return add(new GetLevel(dis));
		}

		public StreamBuilder getList(DataInputStream dis) throws IOException {
			return add(new GetList(dis));
		}

		public StreamBuilder getNeckCut() {
			return add(new NeckCut());
		}

		public StreamBuilder getStructure(DataInputStream dis) throws IOException {
			return add(new GetStructure(dis));
		}

		public StreamBuilder getValue(DataInputStream dis) throws IOException {
			return add(new GetValue(dis));
		}

		public StreamBuilder getVariable(DataInputStream dis) throws IOException {
			return add(new GetVariable(dis));
		}

		public StreamBuilder proceed() {
			return add(new Proceed());
		}

		public StreamBuilder putConstant(DataInputStream dis) throws IOException {
			return add(new PutConstant(dis));
		}

		public StreamBuilder putList(DataInputStream dis) throws IOException {
			return add(new PutList(dis));
		}

		public StreamBuilder putStructure(DataInputStream dis) throws IOException {
			return add(new PutStructure(dis));
		}

		public StreamBuilder putUnsafeValue(DataInputStream dis) throws IOException {
			return add(new PutUnsafeValue(dis));
		}

		public StreamBuilder putValue(DataInputStream dis) throws IOException {
			return add(new PutValue(dis));
		}

		public StreamBuilder putVariablePerm(DataInputStream dis) throws IOException {
			return add(new PutVariablePerm(dis));
		}

		public StreamBuilder putVariableTemp(DataInputStream dis) throws IOException {
			return add(new PutVariableTemp(dis));
		}

		public StreamBuilder retry(DataInputStream dis) throws IOException {
			return add(new Retry(dis));
		}

		public StreamBuilder retryMeElse(DataInputStream dis) throws IOException {
			return add(new RetryMeElse(dis));
		}

		public StreamBuilder setConstant(DataInputStream dis) throws IOException {
			return add(new SetConstant(dis));
		}

		public StreamBuilder setLocalValue(DataInputStream dis) throws IOException {
			return add(new SetLocalValue(dis));
		}

		public StreamBuilder setValue(DataInputStream dis) throws IOException {
			return add(new SetValue(dis));
		}

		public StreamBuilder setVariable(DataInputStream dis) throws IOException {
			return add(new SetVariable(dis));
		}

		public StreamBuilder setVoid(DataInputStream dis) throws IOException {
			return add(new SetVoid(dis));
		}

		public StreamBuilder switchOnConstant(DataInputStream dis) throws IOException {
			return add(new SwitchOnConstant(dis));
		}

		public StreamBuilder switchOnStructure(DataInputStream dis) throws IOException {
			return add(new SwitchOnStructure(dis));
		}

		public StreamBuilder switchOnTerm(DataInputStream dis) throws IOException {
			return add(new SwitchOnTerm(dis));
		}

		public StreamBuilder trust(DataInputStream dis) throws IOException {
			return add(new Trust(dis));
		}

		public StreamBuilder trustMe(DataInputStream dis) throws IOException {
			return add(new TrustMe());
		}

		public StreamBuilder tryInstruction(DataInputStream dis) throws IOException {
			return add(new Try(dis));
		}

		public StreamBuilder tryMeElse(DataInputStream dis) throws IOException {
			return add(new TryMeElse(dis));
		}

		public StreamBuilder unifyConstant(DataInputStream dis) throws IOException {
			return add(new UnifyConstant(dis));
		}

		public StreamBuilder unifyLocalValue(DataInputStream dis) throws IOException {
			return add(new UnifyLocalValue(dis));
		}

		public StreamBuilder unifyValue(DataInputStream dis) throws IOException {
			return add(new UnifyValue(dis));
		}

		public StreamBuilder unifyVariable(DataInputStream dis) throws IOException {
			return add(new UnifyVariable(dis));
		}

		public StreamBuilder unifyVoid(DataInputStream dis) throws IOException {
			return add(new UnifyVoid(dis));
		}
	}

	public static final byte[] MAGIC = DatatypeConverter.parseHexBinary("deadface");

	private String contractHash;

	/**
	 * List of operations to perform
	 */
	private List<Op> ops;

	/**
	 * Timestamp of contract
	 */
	private long timestamp;

	public void execute(AbstractMachine machine) throws FailureException {
		for (Op op : ops) {
			op.execute(machine);
		}
	}

	public List<Op> getOps() {
		return Collections.unmodifiableList(ops);
	}

	public int operationCount() {
		return ops.size();
	}

}
