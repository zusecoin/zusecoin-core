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

import static org.zusecoin.wam.TermFactory.CONSTANT_TAG;
import static org.zusecoin.wam.TermFactory.FUNCTOR_TAG;
import static org.zusecoin.wam.TermFactory.LIST_TAG;
import static org.zusecoin.wam.TermFactory.REF_TAG;
import static org.zusecoin.wam.TermFactory.STRUCTURE_TAG;
import static org.zusecoin.wam.TermFactory.constant;
import static org.zusecoin.wam.TermFactory.functor;
import static org.zusecoin.wam.TermFactory.list;
import static org.zusecoin.wam.TermFactory.structure;
import static org.zusecoin.wam.TermFactory.variable;
import static org.zusecoin.wam.TextUtils.readString;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.zusecoin.wam.trace.TraceTerm;
import org.zusecoin.wam.trace.TraceTermFactory;
import org.zusecoin.wam.trace.Tracer;

public final class AbstractMachine {

	/**
	 * Builds an <code>AbstractMachine</code>
	 * 
	 * Y[n] - permanentVariable in the environment frame (pg25) A[n] -
	 * argumentRegister (pg 22) - need this this it may be a root X[n] - register
	 * (variableRegister) V[n] - variable store[a] - contents of cell at address A
	 * (pg 16)
	 * 
	 * address is used for heap
	 * 
	 */
	public static class AbstractMachineBuilder {

		private int codeArea = 8192;

		/**
		 * Continuation pointer
		 */
		private int cp;

		private int heap = 8192;

		private int pdl = 8192;

		private int register = 100;

		private int stack = 8192;

		private int trail = 8192;

		public AbstractMachineBuilder(int CP) {
			this.cp = CP;
		}

		public AbstractMachine build() {
			if (cp <= register) {
				throw new IllegalArgumentException(
						"Continuation Pointer must be higher address than register:" + cp + " > " + register);
			}
			AbstractMachine machine = new AbstractMachine();
			machine.continuationPointer = cp;
			machine.instructionLabel = cp;// TODO: cp = ic ???
			machine.register = register;
			machine.codeArea = codeArea;
			machine.heap = codeArea + register;
			machine.trail = machine.heap + heap + stack;
			machine.unifyTop = machine.trail + trail;
			machine.unifyBottom = machine.unifyTop + pdl;
			machine.memory = new byte[machine.unifyBottom + 1][];
			// memory.heapBacktrack = heap;
			machine.tracer = new Tracer(machine);
			return machine;
		}

		public AbstractMachineBuilder codeArea(int value) {
			this.codeArea = value;
			return this;
		}

		public AbstractMachineBuilder heap(int size) {
			this.heap = size;
			return this;
		}

		public AbstractMachineBuilder pdl(int pdl) {
			this.pdl = pdl;
			return this;
		}

		public AbstractMachineBuilder register(int value) {
			this.register = value;
			return this;
		}

		public AbstractMachineBuilder stack(int value) {
			this.stack = value;
			return this;
		}

		public AbstractMachineBuilder trail(int value) {
			this.trail = value;
			return this;
		}
	}

	public static byte[] asBytes(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	/**
	 * Returns the address portion (the first 4 bytes) of the specified field.
	 * 
	 * @param field
	 *            the bytes/data of the field
	 * 
	 * @return the address portion of the field
	 * @throws IllegalArgumentException
	 *             if the field is not 5 bytes
	 */
	public static int getAddressOfFieldAt(byte[] field) {
		if (field.length != 5) {
			throw new IllegalArgumentException("Not an address field. Is it a functor? len = " + field.length);
		}
		return ByteBuffer.allocate(5).put(field).getInt(1);
	}

	private static boolean isConstant(byte[] field) {
		return field[0] == CONSTANT_TAG;
	}

	private static boolean isList(byte[] field) {
		return field[0] == LIST_TAG;
	}

	private static boolean isStructure(byte[] field) {
		return field[0] == STRUCTURE_TAG;
	}

	private static boolean isVariable(byte[] field) {
		return field[0] == REF_TAG;
	}

	public static int readInt(byte[] input, int offset) {
		return ByteBuffer.wrap(input, offset, input.length - 1).getInt();
	}

	private static byte[] toBytes(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	public int codeArea;

	/**
	 * Continuation pointer [CP] - Instruction to return to after current call
	 * (CodeArea)
	 */
	public int continuationPointer;

	/**
	 * Environment stack pointer register [E] (stack) - most recent environment
	 */
	public int currentEnv;

	/**
	 * Cut pointer [B0] (stack)
	 */
	public int cut;

	/**
	 * Current address [H] (heap)
	 */
	public int heap;

	/**
	 * Heap backtracking register [HB]- Value of heap at the time of the latest
	 * choice point
	 */
	public int heapBacktrack;

	/**
	 * Instruction counter [P]- address of next instruction to execute in the
	 * CodeArea
	 */
	public int instructionLabel;

	/**
	 * Latest choice point [B] (stack)
	 */
	public int latestChoicePoint;

	protected byte[][] memory;

	public Mode mode = Mode.READ;

	/**
	 * Structure pointer register [S]. Next subterm to be matched. Subterms are
	 * references to items in the registry. This starts at zero and increments
	 * upwards on copies
	 */
	public int nextSubterm;

	/**
	 * Arity of current procedure
	 */
	private int numOfArgs;

	/**
	 * Top of register
	 */
	public int register;

	private HashMap<String, Integer> structureMap = new HashMap<>();

	public Tracer tracer;

	/**
	 * Trail pointer [TR]
	 */
	public int trail;

	public int unifyBottom;

	public int unifyTop;

	private AbstractMachine() {
	}

	public int addressAsInt(int address) {
		byte[] field = get(address);
		if (field == null) {
			throw new IllegalStateException("Null field at that address: " + address);
		}
		return ByteBuffer.wrap(field).getInt();
	}

	/**
	 * Allocate a new environment on the stack, setting its continuation environment
	 * and continuation point fields to current E and CP, respectively. Continue
	 * execution
	 * 
	 * This is an I2 instruction for M2
	 */
	public void allocate() {
		// TODO: verify latest
		int newE = (currentEnv > latestChoicePoint)
				? currentEnv + addressAsInt(addressAsInt(continuationPointer) - 1) + 2
				: latestChoicePoint + addressAsInt(latestChoicePoint) + 8;
		storePointer(currentEnv, newE);
		storePointer(continuationPointer, newE + 1);
		currentEnv = newE;
		continueExecution();
	}

	/**
	 * Reads arity of functor at the specified address. A functor field is
	 * TAG:arity:functor_name
	 * 
	 * @param address
	 *            address of functor
	 * 
	 * @return arity of functor
	 */
	private int arity(int address) {
		return readInt(memory[address], 1);
	}

	private void assertAddressInMemoryArea(int address) {
		if (address > memory.length) {
			throw new ExecutionException("Attempting to reference address outside of memory: " + address);
		}
	}

	/**
	 * Throws IllegalStateException if specified registerIndex is not within bounds
	 * of the register. Only makes upper bound check.
	 * 
	 * @param registerIndex
	 */
	private void assertRegisterInBounds(int registerIndex) {
		if (registerIndex > register) {
			throw new IllegalStateException("Register index out of bounds: " + registerIndex);
		}
	}

	/**
	 * Returns a textual representation of the data at the specified address
	 * 
	 * @param address
	 *            the address of the data
	 * @return
	 */
	public String asText(int address) {
		if (address >= memory.length) {
			return null;
		}
		byte[] field = memory[address];
		if (field != null) {
			try {
				return field(address, field);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Backtracks by resetting the cut and instruction counter
	 * 
	 * See PDF/112
	 * 
	 * @throws FailureException
	 */
	public void backtrack() throws FailureException {
		if (hasNextStack()) {
			int bplus = latestChoicePoint + readPointer(latestChoicePoint);
			cut = readPointer(bplus + 7);
			instructionLabel = readPointer(bplus + 4);
		} else {
			throw new FailureException();
		}
	}

	/**
	 * Backtracks if specified fail value is true, otherwise continue execution
	 * 
	 * @param fail
	 * @throws FailureException
	 */
	private void backtrackOrContinue(boolean fail) throws FailureException {
		if (fail) {
			backtrack();
		} else {
			continueExecution();
		}
	}

	/**
	 * Binds the data of the two specified addresses. One of these addresses must be
	 * an unbound REF cell. In prolog, this is a cell that references itself.
	 * Mathematically speaking, an unbound REF cell is a 'free variable' or one that
	 * can take a substitution of another variable. When we bind this free variable
	 * it becomes a 'bounded variable' that now takes another variable as its value.
	 * 
	 * These addresses may be heap/heap; stack/stack; heap/stack (WAM BINDING RULE
	 * 2???)
	 * 
	 * If a1 is an unbound REF and a2 is a LIST, STRUCTURE OR CONSTANT or if a2 is
	 * an unbound REF and a2 < a1 (WAM BINDING RULE 1), then copy a2 to a1. WAM does
	 * not allow a more recent variable to reference an older variable. If the
	 * addresses are stack/stack or heap/stack, this ordering is critical for
	 * correct operation of WAM.
	 * 
	 * Otherwise, copy a1 to a2. a1 is either bounded or free. We know that a2 is
	 * unbounded so copying every time from a1 to a2 makes sense.
	 * 
	 * Trailing is done as part of this bind operation. Optionally, you could also
	 * implement an occurs-check in this method.
	 * 
	 * See page 62
	 * 
	 * @param a1
	 *            address 1
	 * @param a2
	 *            address 2
	 */
	void bind(int a1, int a2) {// TODO we are not checking if unbound
		boolean a1Bound = isUnbound(a1);
		boolean a2Bound = isUnbound(a2);
		if (!a1Bound && !a2Bound) {
			throw new IllegalArgumentException("Can't bind unbound variables");
		}
		if ((isVariable(a1) && a1Bound) && (isVariable(a2) || passWamBindingRule1(a1, a2))) {
			copy(a2, a1);
			trail(a1);
		} else {
			copy(a1, a2);
			trail(a2);
		}
	}

	public void call(int instructionLabel) throws FailureException {
		if (isMemoryCellNull(instructionLabel)) {
			backtrack();
		} else {
			continuationPointer = nextInstructionLabel();
			numOfArgs = arity(instructionLabel);
			cut = latestChoicePoint;
			instructionLabel = readPointer(instructionLabel);// TODO:
		}
	}

	private int codeAreaAddress() {
		return 0;
	}

	public void compare(String label, AbstractMachine m1, AbstractMachine m2) {
		System.out.println("\r\n" + label);
		if (m1 == null) {
			for (int i = 0; i < m2.getMemorySize(); i++) {
				if (m2.get(i) != null) {
					TraceTerm term = TraceTermFactory.create(i, m2);
					System.out.println(i + " : " + term.toText());
				}
			}
		} else {
			if (m1.heap != m2.heap) {
				System.out.println("Heap: " + m1.heap + " => " + m2.heap);
			}

			if (m1.instructionLabel != m2.instructionLabel) {
				System.out.println("Instruction Counter: " + m1.instructionLabel + " => " + m2.instructionLabel);
			}

			for (int i = 0; i < m1.getMemorySize(); i++) {
				if (!Arrays.equals(m1.get(i), m2.get(i))) {
					TraceTerm term = TraceTermFactory.create(i, m2);
					System.out.println(i + " : " + term.toText());
				}
			}
		}

	}

	/**
	 * Set the next instruction counter
	 */
	public void continueExecution() {
		instructionLabel = nextInstructionLabel();
	}

	/**
	 * Copy bytes from source address to destination address
	 * 
	 * @param source
	 *            the source location in memory
	 * @param destination
	 *            the destination location in memory
	 * @throws IllegalStateException
	 *             if attempt to copy null data. The source address contains null
	 *             data
	 */
	private void copy(int source, int destination) {
		if (source != destination) {
			if (memory[source] == null) {
				throw new IllegalStateException(
						"Can't copy null reference: source = " + source + ", destination = " + destination);
			}
			memory[destination] = memory[source];
		}
	}

	/**
	 * Copies from argument registry into memory
	 * 
	 * @param startMemoryAddress
	 *            start address in memory
	 * @param argCount
	 *            number of arguments to copy
	 */
	private void copyArgsToMemory(int startMemoryAddress) {
		for (int i = 0; i < numOfArgs; i++) {
			copy(i, startMemoryAddress + 1);
		}
	}

	/**
	 * Copies arguments from the previous choice point (in memory) into the argument
	 * registry
	 * 
	 * @return number of arguments copied
	 */
	private int copyFromChoicePointToRegister() {
		int argCount = readPointer(latestChoicePoint);
		int startAddress = argCount + 1;
		for (int i = 0; i < argCount; i++) {
			copy(i, startAddress + i);
		}
		return argCount;
	}

	/**
	 * Creates a choice point beginning at the specified address
	 * 
	 * @param address
	 */
	private void createChoicePoint(int address) {
		set(address, toBytes(numOfArgs));
		copyArgsToMemory(address + 1);

		int n = address + numOfArgs;
		storePointer(currentEnv, n + 1);
		storePointer(continuationPointer, n + 2);
		storePointer(latestChoicePoint, n + 3);
		storePointer(asBytes(nextInstructionLabel()), n + 4);
		storePointer(trail, n + 5);
		storePointer(heap, n + 6);
		storePointer(cut, n + 7);
	}

	/**
	 * Discard all (if any) choice points after that indicated by Y[n], and tidy the
	 * trail up to that point. Continue execution.
	 * 
	 * @param permanentVariable
	 *            the address of the permanent variable
	 */
	public void cut(int permanentVariable) {
		int i = readPointer(currentEnv + 2 + permanentVariable);
		if (latestChoicePoint > i) {
			latestChoicePoint = i;
			tidyTrail();
		}
		continueExecution();
	}

	/**
	 * Remove the environment frame at stack location E from the stack by resetting
	 * E to the value of its CE field and the continuation pointer CP to the value
	 * of its CP field. Continue execution
	 */
	public void deallocate() {
		continuationPointer = readPointer(currentEnv + 1);
		currentEnv = readPointer(currentEnv);
		continueExecution();
	}

	/**
	 * Derefences a given field with format TAG:address
	 * 
	 * @param field
	 * @return
	 */
	public int deref(byte[] field) {
		return deref(getAddressOfFieldAt(field));
	}

	/**
	 * Dereferences the variable at the specified address. If the variable is a REF,
	 * then continue deref recursion until LIST, CONSTANT, or STRUCTURE address is
	 * found.
	 * 
	 * When we bind variables together, it creates a chain of references (REF).
	 * Dereferencing will follow back along that chain until it finds an unbound
	 * reference or a non-REF cell. For instance, we could have g(f(X1)). There two
	 * reference chains: g -> f AND f-> X1. So X1 would dereference back to f. If we
	 * wanted to dereference f, we would get the address to g.
	 * 
	 * If the address points to a non-REF cell, then this method returns the
	 * specified address.
	 * 
	 * @param address
	 *            address to dereference. This address may be heap or registry
	 * 
	 * @return dereferenced address
	 */
	public int deref(int address) {
		assertAddressInMemoryArea(address);
		byte[] field = memory[address];
		if (field == null) {
			throw new ExecutionException("Attempting to deref address that does not exist: " + address);
		}
		if (field.length == 5) {
			int value = getAddressOfFieldAt(field);
			if (isVariable(field) && address != value) {
				return deref(value);
			}
		}
		return address;
	}

	/**
	 * If instructionAddress is defined, then save the current choice point’s
	 * address in B0 (cut pointer) and continue execution with instruction at
	 * instructionAddress; otherwise, backtrack.
	 * 
	 * @param instructionAddress
	 *            (p) - address of instruction
	 * @throws FailureException
	 */
	public void execute(int instructionAddress) throws FailureException {
		if (isMemoryCellNull(instructionAddress)) {
			backtrack();
		} else {
			numOfArgs = arity(instructionAddress);
			cut = latestChoicePoint;
			instructionLabel = readPointer(instructionAddress);
		}
	}

	private String field(int address, byte[] b) {
		int addr = -1;
		switch (b[0]) {
		case REF_TAG:
			addr = readInt(b, 1);
			if (address == addr) {
				return "REF[unbound]";
			} else {
				return "REF[" + addr + "]";
			}

		case STRUCTURE_TAG:
			addr = readInt(b, 1);
			return "STR[" + addr + "] (" + readString(memory[addr], 4) + ")";
		case LIST_TAG:
			return "LIS_";
		case CONSTANT_TAG:
			return "CON_" + readString(b, 1);
		case FUNCTOR_TAG:
			return "FUNCTOR (" + readString(b, 4) + ", " + readInt(b, 1) + ")";
		}
		return "<>";
	}

	/**
	 * Get byte array (data) at specified address
	 * 
	 * @param address
	 *            the memory address
	 * @return byte array at specified address
	 */
	public byte[] get(int address) {
		return memory[address];
	}

	/**
	 * Returns the address portion of the field at the specified address
	 * 
	 * @param address
	 *            address of field
	 * 
	 * @return the address portion of the field at the specified address
	 */
	public int getAddressOfFieldAt(int address) {
		return getAddressOfFieldAt(memory[address]);
	}

	/**
	 * If the dereferenced value of register A[i] is an unbound variable, bind that
	 * variable to constant c. Otherwise, fail if it is not the constant c.
	 * Backtrack on failure, otherwise continue execution
	 * 
	 * @param value
	 *            constant
	 * @param argRegister
	 *            the argument registry address
	 * @throws FailureException
	 * @throws IllegalStateException
	 *             if the specified registerIndex is greater than the current
	 *             register
	 */
	public void getConstant(String value, int argRegister) throws FailureException {
		assertRegisterInBounds(argRegister);

		boolean fail = false;
		int addr = deref(getAddressOfFieldAt(argRegister));
		switch (memory[addr][0]) {
		case REF_TAG:
			set(addr, constant(value));
			trail(addr);
			break;
		case CONSTANT_TAG:
			fail = !Arrays.equals(constant(value), memory[addr]);
			break;
		default:
			fail = true;
		}

		backtrackOrContinue(fail);
	}

	/**
	 * Set cut pointer (B0) to the permVariableIndex (Y[n]) of the current
	 * environment
	 * 
	 * Continue execution.
	 * 
	 * @param permanentVariable
	 */
	public void getLevel(int permanentVariable) {
		cut = readPointer(currentEnv + permanentVariable + 2);
		continueExecution();
	}

	/**
	 * If the dereferenced value of registerIndex (A[i]) is an unbound variable,
	 * then bind that variable to a new LIS cell pushed on the heap and set mode to
	 * write; otherwise, if it is a LIS cell, then set register S to the heap
	 * address it contains and set mode to read. If it is not a LIS cell, fail.
	 * Backtrack on failure, otherwise continue execution
	 * 
	 * @param argumentRegister
	 *            - argument registry address
	 * @throws FailureException
	 * @throws IllegalStateException
	 *             if the specified argumentRegister is greater than the current
	 *             register
	 */
	public void getList(int argumentRegister) throws FailureException {
		assertRegisterInBounds(argumentRegister);

		boolean fail = false;
		int addr = deref(getAddressOfFieldAt(argumentRegister));
		switch (memory[addr][0]) {
		case REF_TAG:
			pushHeap(list(heap + 1));
			bind(addr, heap++);
			writeMode();
			break;
		case LIST_TAG:
			nextSubterm = getAddressOfFieldAt(memory[addr]);
			readMode();
			break;
		default:
			fail = true;
		}

		backtrackOrContinue(fail);
	}

	public int getMemorySize() {
		return memory.length;
	}

	/**
	 * If the dereferenced value of registerIndex (A[i]) is an unbound variable,
	 * then bind that variable to a new STR cell pointing to f pushed on the heap
	 * and set mode to write; otherwise, if it is a STR cell pointing to functor f,
	 * then set register S to the heap address following that functor cell’s and set
	 * mode to read. If it is not a STR cell or if the functor is different than f,
	 * fail. Backtrack on failure, otherwise continue execution
	 * 
	 * @param f
	 *            functor address
	 * @param argumentRegister
	 *            - argument register address
	 * @throws FailureException
	 * @throws IllegalStateException
	 *             if the specified argumentRegister is greater than the current
	 *             register
	 */
	public boolean getStructure(String functor, int argumentRegister) throws FailureException {
		assertRegisterInBounds(argumentRegister);

		boolean fail = false;
		int addr = deref(argumentRegister);
		switch (memory[addr][0]) {
		case REF_TAG:
			if (isUnbound(addr)) {
				pushHeap(structure(heap + 1));
				memory[heap + 1] = functor.getBytes();
				bind(addr, heap);
				heap += 2;
				writeMode();
			} else {
				fail = true;
			}
			break;
		case STRUCTURE_TAG:
			int a = getAddressOfFieldAt(addr);
			if (Arrays.equals(memory[a], functor.getBytes())) {
				nextSubterm = a + 1;
				readMode();
			} else {
				fail = true;
			}
			break;
		default:
			fail = true;
		}

		backtrackOrContinue(fail);
		nextSubterm = 1;// errata
		return !fail;
	}

	public Tracer getTracer() {
		return tracer;
	}

	/**
	 * Unify variable V[n] and register A[i]. Backtrack on failure, otherwise
	 * continue execution
	 * 
	 * @param variable
	 *            the variable address
	 * @param argumentRegister
	 *            the argument register address
	 * @throws FailureException
	 * @throws IllegalStateException
	 *             if the specified argumentRegister is greater than the current
	 *             register
	 */
	public void getValue(int variable, int argumentRegister) throws FailureException {
		assertRegisterInBounds(argumentRegister);
		boolean fail = !unify(getAddressOfFieldAt(variable), getAddressOfFieldAt(argumentRegister));
		backtrackOrContinue(fail);
	}

	/**
	 * Place the contents of register A[i] into variable V[n]. Continue execution
	 * 
	 * @param variable
	 *            the variable address
	 * @param argumentRegister
	 *            the argument register address
	 * @throws IllegalStateException
	 *             if the specified registerIndex is greater than the current
	 *             register
	 */
	public void getVariable(int variable, int argumentRegister) {
		assertRegisterInBounds(argumentRegister);
		copy(argumentRegister, variable);
		continueExecution();
	}

	public boolean hasNextStack() {// TODO: implement
		return false;
	}

	/**
	 * Returns instruction size of procedure p
	 * 
	 * @param p
	 *            procedure index
	 * 
	 * @return instruction size of procedure p
	 */
	public int instructionSize(int p) {
		if (p > memory.length || memory[p] == null) {
			return 0;
			// throw new IllegalStateException(
			// "No instruction found at procedure index " + p);
		}
		return readInt(memory[p], 0);
	}

	/**
	 * Returns true if data exists at the specified address
	 * 
	 * @param address
	 *            the address to check for data.
	 * @return
	 */
	private boolean isMemoryCellNull(int address) {
		return memory[address] == null;
	}

	private boolean isPdlEmpty() {
		return unifyTop == unifyBottom;
	}

	private boolean isUnbound(int addr) {
		return getAddressOfFieldAt(addr) == addr;
	}

	/**
	 * public boolean isUnbound(int address) { return address < heap; }
	 */

	private boolean isVariable(int address) {
		return memory[address][0] == REF_TAG;
	}

	/**
	 * If there is a choice point after that indicated by B0 (cut pointer), discard
	 * it and tidy the trail up to that point. Continue execution
	 */
	public void neckCut() {
		if (latestChoicePoint > cut) {
			latestChoicePoint = cut;
			tidyTrail();
		}
		continueExecution();
	}

	private int nextInstructionLabel() {
		return instructionLabel + instructionSize(instructionLabel);
	}

	/**
	 * Always make the variable of higher address reference that of lower address.
	 * In other words, an older (less recently created) variable cannot reference a
	 * younger (more recently created) variable.
	 * 
	 * @param address1
	 * @param address2
	 * @return true if address2 < address1
	 */
	private boolean passWamBindingRule1(int address1, int address2) {
		return address2 < address1;
	}

	/**
	 * Pop the unification stack
	 * 
	 * @return address popped from the stack
	 */
	private int popUnifyStack() {
		System.out.println("UT:" + unifyTop);
		return readPointer(--unifyTop);// TODO:???
	}

	public void printMachine() {
		PrintStream out = System.out;
		out.println("=====Machine State=====");
		out.println("Continuation Pointer: " + continuationPointer);
		out.println("Instruction Counter: " + this.instructionLabel);
		out.println("Register: " + "0-" + register);
		out.println("S - nextSubterm: " + this.nextSubterm);

		// out.println("Code Area: " + (register + 1) + "-" + this.codeArea);
		out.println("Heap: " + (register + 1) + "-" + heap);
		out.println("Trail: " + (heap + 1) + "-" + trail);
		out.println("Unify Stack: " + unifyTop + " - " + unifyBottom);
		out.println();
	}

	public void printMemory(String header) {
		System.out.println("======" + header + "========");
		System.out.println("--- register");

		for (int i = 0; i < memory.length; i++) {
			if (i == register + 1) {
				System.out.println("--- heap");
			} else if (i == heap + 1) {
				System.out.println("--- trail");
			} else if (i == unifyTop) {
				System.out.println("--- unify stack");
			}
			String text = asText(i);
			if (text != null) {
				System.out.println(i + " : " + text);
			}
		}
		System.out.println("******" + header + "******");
		System.out.println();

	}

	/**
	 * Continue execution at instruction whose address is indicated by the
	 * continuation register CP
	 */
	public void proceed() {
		instructionLabel = continuationPointer;
	}

	public void pushHeap(byte[] data) {
		set(heap, data);
	}

	/**
	 * Push unbound variable onto the heap
	 * 
	 * @return unbound variable
	 */
	byte[] pushUnboundVar() {
		byte[] unboundRef = variable(heap);
		pushHeap(unboundRef);
		return unboundRef;
	}

	/**
	 * Push address on unification stack
	 * 
	 * @param address
	 *            - address to push
	 */
	private void pushUnifyStack(int address) {
		set(unifyTop++, asBytes(address));
	}

	/**
	 * Place a constant cell containing constant into register A[i]. Continue
	 * execution
	 * 
	 * @param constant
	 *            the constant to put in the register
	 * @param argumentRegister
	 *            the argument register address
	 * @throws IllegalStateException
	 *             if the specified argumentRegister is greater than the current
	 *             register
	 */
	public void putConstant(String constant, int argumentRegister) {
		assertRegisterInBounds(argumentRegister);
		set(argumentRegister, constant(constant));
		continueExecution();
	}

	/**
	 * Set register A[i] to contain a LIS cell pointing to the current top of the
	 * heap. Continue execution
	 * 
	 * @param argumentRegister
	 *            the argument register address
	 */
	public void putList(int argumentRegister) {
		set(argumentRegister, list(heap++));
		continueExecution();
	}

	/**
	 * Push a new functor cell containing f onto the heap and set register A[i] to
	 * an STR cell pointing to that functor cell. Continue execution
	 * 
	 * This method puts a new structure into both the heap AND the register. We need
	 * to put a reference in the register as part of query construction.
	 *
	 * @param functor
	 * @param argumentRegister
	 *            the register (argument) index of the structure.
	 */
	public void putStructure(String functor, int argumentRegister) {
		pushHeap(functor(functor));
		// structureMap.put(functor + "/" + arity, heap);
		set(argumentRegister, structure(heap++));
		continueExecution();

		tracer.trace("put_structure " + functor + ", " + argumentRegister);

	}

	/**
	 * If the dereferenced value of permanent variable (Y[n]) is not an unbound
	 * stack variable in the current environment, set A[registerIndex] to that
	 * value. Otherwise, bind the referenced stack variable to a new unbound
	 * variable cell pushed on the heap, and set A[registerIndex] to point to that
	 * cell. Continue execution
	 * 
	 * @param permanentVariable
	 * @param argumentRegister
	 *            the argument register address
	 * @throws IllegalStateException
	 *             if the specified registerIndex is greater than the current
	 *             register
	 */
	public void putUnsafeValue(int permanentVariable, int argumentRegister) {
		assertRegisterInBounds(argumentRegister);

		int addr = deref(currentEnv + permanentVariable + 1);
		if (addr < currentEnv) {
			putValue(argumentRegister, addr);
		} else {
			pushUnboundVar();
			bind(addr, heap);
			putValue(argumentRegister, heap);
		}
	}

	/**
	 * Place the contents of V[n] into register A[registerIndex]. Continue execution
	 * 
	 * @param argumentRegister
	 *            the argument register address
	 * @param variable
	 * @throws IllegalStateException
	 *             if the specified registerIndex is greater than the current
	 *             register
	 */
	public void putValue(int argumentRegister, int variable) {
		assertRegisterInBounds(argumentRegister);
		copy(variable, argumentRegister);
		continueExecution();
	}

	/**
	 * Initialize the n-th stack variable in the current environment to ‘unbound’
	 * and let A[i] point to it. Continue execution
	 * 
	 * @param permanentVariable
	 * @param argumentRegister
	 *            the argument register address
	 * @throws IllegalStateException
	 *             if the specified registerIndex is greater than the current
	 *             register
	 */
	public void putVariablePerm(int permanentVariable, int argumentRegister) {
		assertRegisterInBounds(argumentRegister);

		int addr = currentEnv + permanentVariable + 1;
		setUnboundVar(addr);
		copy(addr, argumentRegister);
		continueExecution();
	}

	/**
	 * Push a new unbound REF cell onto the heap and copy it into both register X[n]
	 * and register A[registerIndex]. Continue execution
	 * 
	 * @param register
	 * @param argumentRegister
	 *            the argument register address
	 * @throws IllegalStateException
	 *             if the specified registerIndex is greater than the current
	 *             register
	 */
	public void putVariableTemp(int register, int argumentRegister) {
		assertRegisterInBounds(argumentRegister);

		pushUnboundVar();
		copy(heap, register);
		copy(heap++, argumentRegister);
		continueExecution();
	}

	/**
	 * Sets to read mode.
	 */
	public void readMode() {
		mode = Mode.READ;
	}

	private int readPointer(int address) {
		return addressAsInt(address);
	}

	/**
	 * Reset choice point, including values in the register, heap pointer,...
	 */
	private void resetChoicePoint() {
		int argCount = copyFromChoicePointToRegister();
		int startAddress = latestChoicePoint + argCount;

		currentEnv = readPointer(startAddress + 1);
		continuationPointer = readPointer(startAddress + 2);
		set(startAddress + 4, asBytes(nextInstructionLabel()));
		unwindTrail(readPointer(startAddress + 5));
		heap = readPointer(startAddress + 6);
	}

	/**
	 * Having backtracked to the current choice point, reset all the necessary
	 * information from it and update its next clause field to following
	 * instruction. Continue execution
	 * 
	 * @param instructionLabel
	 */
	public void retry(int instructionLabel) {
		resetChoicePoint();
		heapBacktrack = latestChoicePoint;
		this.instructionLabel = instructionLabel;
		tracer.trace("retry", instructionLabel);
	}

	/**
	 * Having backtracked to the current choice point, reset all the necessary
	 * information from it and update its next clause field to L. Continue execution
	 * 
	 * @param instructionLabel
	 */
	public void retryMeElse(int instructionLabel) {
		int argCount = copyFromChoicePointToRegister();
		int startAddress = latestChoicePoint + argCount;

		currentEnv = readPointer(startAddress + 1);
		continuationPointer = readPointer(startAddress + 2);
		set(startAddress + 5, asBytes(instructionLabel));

		unwindTrail(readPointer(startAddress + 5));
		heap = readPointer(startAddress + 6);
		heapBacktrack = heap;

		continueExecution();
		tracer.trace("try_me_else", instructionLabel);
	}

	public void set(int address, byte[] data) {
		memory[address] = data;
	}

	public void set(String address, byte[] data) {
		set(Integer.valueOf(address), data);
	}

	/**
	 * Push the constant c onto the heap. Continue execution
	 * 
	 * This is different than AbstractMachine.putConstant which puts the constant in
	 * the registry.
	 * 
	 * @param constant
	 */
	public void setConstant(String constant) {
		set(heap++, constant(constant));
		continueExecution();
		tracer.trace("set_constant " + constant);
	}

	/**
	 * If the dereferenced value of Vn is an unbound heap variable, push a copy of
	 * it onto the heap. If the dereferenced value is an unbound stack address, push
	 * a new unbound REF cell onto the heap and bind the stack variable to it.
	 * Continue execution
	 * 
	 * @param variable
	 */
	public void setLocalValue(int variable) {
		int addr = deref(variable);
		if (addr < heap) {
			copy(addr, heap++);
		} else {
			pushUnboundVar();
			bind(addr, heap++);
		}
		continueExecution();
		tracer.trace("set_local_value", variable);
	}

	private void setUnboundVar(int address) {
		set(address, variable(address));
	}

	/**
	 * Push V[n]’s value onto the heap. Continue execution
	 * 
	 * @param variable
	 */
	public void setValue(int variable) {
		copy(variable, heap++);
		continueExecution();
		tracer.trace("set_value", variable);
	}

	/**
	 * Push a new unbound REF cell onto the heap and copy it into variable V[n].
	 * Continue execution
	 * 
	 * @param variable
	 *            V[n]
	 */
	public void setVariable(int variable) {
		pushUnboundVar();
		copy(heap++, variable);
		continueExecution();

		tracer.trace("set_variable", variable);
	}

	/**
	 * Push n new unbound REF cells onto the heap. Continue execution
	 */
	public void setVoid(int n) {
		for (int i = heap; i < heap + n; i++) {
			setUnboundVar(i);
		}
		heap += n;
		continueExecution();
		tracer.trace("set_void", n);
	}

	/**
	 * Stores pointer data into specified memory index
	 * 
	 * @param pointer
	 * @param memoryIndex
	 */
	private void storePointer(byte[] pointer, int memoryIndex) {
		set(memoryIndex, pointer);
	}

	/**
	 * Stores pointer into specified memory index
	 * 
	 * @param pointer
	 * @param memoryIndex
	 */
	private void storePointer(int pointer, int memoryIndex) {
		storePointer(asBytes(pointer), memoryIndex);
	}

	/**
	 * The dereferenced value of register A being a constant, jump to the
	 * instruction associated to it in hashtable T of size N. If the constant found
	 * in A is not one in the table, backtrack.
	 * 
	 * @param hash
	 * @throws FailureException
	 */
	public void switchOnConstant(Map<String, Integer> hash) throws FailureException {// TODO: implement
		byte[] term = memory[deref(getAddressOfFieldAt(0))];
		String key = TextUtils.readString(term, 1);
		Integer inst = hash.get(key);
		if (inst != null) {
			instructionLabel = inst;
		} else {
			backtrack();
		}
	}

	/**
	 * The dereferenced value of register A being a structure, jump to the
	 * instruction associated to it in hashtable T of sizeN. If the functor of the
	 * structure found in A is not one in the table, backtrack.
	 * 
	 * @param hash
	 * @throws FailureException
	 */
	public void switchOnStructure(Map<String, Integer> hash) throws FailureException {// TODO: implement
		byte[] term = memory[deref(getAddressOfFieldAt(0))];
		String key = TextUtils.readString(term, 1);// ??? read structure
		Integer inst = hash.get(key);
		if (inst != null) {
			instructionLabel = inst;
		} else {
			backtrack();
		}
	}

	/**
	 * Jump to the instruction labeled, respectively, V, C, L, or S, depending on
	 * whether the dereferenced value of argument register A1 is a variable, a
	 * constant, a non-empty list, or a structure, respectively.
	 * 
	 * If there is no jump/switch on one of the specified types, use -1 for the
	 * value. For example, switchOnTerm(4, 1, -1, -1) will not jump on a list or
	 * structure.
	 * 
	 * 
	 * @param variable
	 * @param constant
	 * @param list
	 * @param structure
	 */
	public void switchOnTerm(int variable, int constant, int list, int structure) {
		int da = deref(getAddressOfFieldAt(0));
		switch (memory[da][0]) {
		case REF_TAG:
			instructionLabel = variable;
			break;
		case CONSTANT_TAG:
			instructionLabel = constant;
			break;
		case LIST_TAG:
			instructionLabel = list;
			break;
		case STRUCTURE_TAG:
			instructionLabel = structure;
		}
	}

	public AbstractMachine takeSnapshot() {
		AbstractMachine machine = new AbstractMachine();
		machine.codeArea = codeArea;
		machine.continuationPointer = this.continuationPointer;
		machine.currentEnv = currentEnv;
		machine.cut = cut;
		machine.heap = heap;
		machine.heapBacktrack = heapBacktrack;
		machine.instructionLabel = instructionLabel;
		machine.latestChoicePoint = latestChoicePoint;
		machine.memory = new byte[memory.length][];
		System.arraycopy(memory, 0, machine.memory, 0, memory.length);
		machine.mode = (mode.equals(Mode.READ)) ? Mode.READ : Mode.WRITE;
		machine.nextSubterm = nextSubterm;
		machine.numOfArgs = numOfArgs;
		machine.register = register;
		machine.trail = trail;
		machine.unifyBottom = unifyBottom;
		machine.unifyTop = unifyTop;
		return machine;
	}

	private void tidyTrail() {// TODO: use address???
		int i = addressAsInt(latestChoicePoint + readPointer(latestChoicePoint) + 5);
		while (i < trail) {
			int memInt = addressAsInt(i);
			if (memInt < heapBacktrack || (heap < memInt || memInt < latestChoicePoint)) {
				i++;
			} else {
				copy(--trail, i);
			}
		}
	}

	/**
	 * Store address in trail if address is less than heap backtrack position OR if
	 * address is bounded between heap position and previous choice point.
	 * 
	 * If the address is less than choice point, this is referred to as a
	 * conditional binding and does not need to be stored in the trail.
	 * 
	 * @param address
	 *            address to store
	 */
	public void trail(int address) {
		if (address < heapBacktrack || (heap < address && address < latestChoicePoint)) {
			set(trail++, asBytes(address));
		}
	}

	/**
	 * Having backtracked to the current choice point, reset all necessary
	 * information from it, then discard it by resetting (Heap Backtrack) B to its
	 * predecessor. Continue execution
	 * 
	 * @param instructionLabel
	 */
	public void trust(int instructionLabel) {
		resetChoicePoint();
		heapBacktrack = heap;
		this.instructionLabel = instructionLabel;
		tracer.trace("trust", instructionLabel);
	}

	/**
	 * Having backtracked to the current choice point, reset all the necessary
	 * information from it, then discard it by resetting B to its predecessor.
	 * Continue execution
	 */
	public void trustMe() {
		int argCount = copyFromChoicePointToRegister();
		int startAddress = latestChoicePoint + argCount;

		currentEnv = readPointer(startAddress + 1);
		continuationPointer = readPointer(startAddress + 2);
		unwindTrail(readPointer(startAddress + 5));
		heap = readPointer(startAddress + 6);
		latestChoicePoint = readPointer(startAddress + 3);
		heapBacktrack = heap;

		continueExecution();
	}

	/**
	 * Allocate a new choice point frame on the stack setting its next clause field
	 * to the following instruction and the other fields according to the current
	 * context, and set B to point to it. Continue execution
	 * 
	 * @param instructionLabel
	 */
	public void tryInstruction(int instructionLabel) {
		int newB = -1;
		if (currentEnv > latestChoicePoint) {
			newB = currentEnv + 2 + addressAsInt(addressAsInt(currentEnv + 1) - 1);
		} else {
			newB = latestChoicePoint + addressAsInt(latestChoicePoint) + 8;
		}

		createChoicePoint(newB);

		latestChoicePoint = newB;
		heapBacktrack = heap;
		this.instructionLabel = instructionLabel;
	}

	/**
	 * Allocate a new choice point frame on the stack setting its next clause field
	 * to L and the other fields according to the current context, and set B to
	 * point to it. Continue execution
	 * 
	 * @param instructionLabel
	 */
	public void tryMeElse(int instructionLabel) {
		int newB = -1;
		if (currentEnv > latestChoicePoint) {
			newB = currentEnv + 2 + addressAsInt(addressAsInt(currentEnv + 1) - 1);
		} else {
			newB = latestChoicePoint + addressAsInt(latestChoicePoint) + 8;
		}

		createChoicePoint(newB);
		latestChoicePoint = newB;
		heapBacktrack = heap;
		continueExecution();
	}

	protected boolean unify(int address1, int address2) {
		pushUnifyStack(address1);
		pushUnifyStack(address2);

		boolean fail = false;
		while (!(isPdlEmpty() || fail)) {
			int d1 = deref(popUnifyStack());
			// TODO: what if functor - nothing
			// to deref????
			int d2 = deref(popUnifyStack());
			if (d1 != d2) {
				byte[] term1 = memory[d1];
				byte[] term2 = memory[d2];
				if (isVariable(term1)) {
					bind(d1, d2);
				} else {
					switch (term2[0]) {
					case REF_TAG:
						bind(d1, d2);
						break;
					case CONSTANT_TAG:
						// TODO: support order operations
						// Create method that can handle more sophisticated comparisions
						fail = !isConstant(term1) || !Arrays.equals(term1, term2);
						break;
					case LIST_TAG:
						if (isList(term1)) {
							fail = true;
						} else {
							int v1 = getAddressOfFieldAt(term1);
							int v2 = getAddressOfFieldAt(term2);
							pushUnifyStack(v1);
							pushUnifyStack(v2);
							pushUnifyStack(v1 + 1);
							pushUnifyStack(v2 + 1);
						}
						break;
					case STRUCTURE_TAG:
						if (!isStructure(term1)) {
							fail = true;
						} else if (Arrays.equals(term1, term2)) {
							final int v1 = getAddressOfFieldAt(term1);
							int v2 = getAddressOfFieldAt(term2);

							for (int i = 0; i < arity(v1); i++) {
								pushUnifyStack(v1 + i);
								pushUnifyStack(v2 + i);
							}
						} else {
							fail = true;
						}
						break;
					}
				}
			}
		}
		return !fail;
	}

	/**
	 * In read mode, dereference the heap address S. If the result is an unbound
	 * variable, bind that variable to the constant c; otherwise, fail if the result
	 * is different than constant c. In write mode, push the constant c onto the
	 * heap. Backtrack on failure, otherwise continue execution
	 * 
	 * @param constant
	 * @throws FailureException
	 */
	public void unifyConstant(String constant) throws FailureException {
		boolean fail = false;
		if (Mode.READ.equals(mode)) {
			int addr = deref(nextSubterm);
			byte[] val = memory[addr];

			if (isVariable(val)) {// TODO: check unbound ref
				set(addr, constant(constant));
				trail(addr);
			} else if (isConstant(val)) {
				fail = Arrays.equals(constant(constant), val);
			} else {
				fail = true;
			}
		} else {
			set(heap++, constant(constant));// TODO: Push Heap?
		}

		backtrackOrContinue(fail);

		tracer.trace("Unify Constant: " + constant);
	}

	/**
	 * In read mode, unify variable V[n] and heap address S. In write mode, if the
	 * dereferenced value of V[n] is an unbound heap variable, push a copy of it
	 * onto the heap. If the dereferenced value is an unbound stack address, push a
	 * new unbound REF cell onto the heap and bind the stack variable to it. In
	 * either mode, increment S by one. Backtrack on failure, otherwise continue
	 * execution
	 * 
	 * @param variable
	 * @throws FailureException
	 */
	public void unifyLocalValue(int variable) throws FailureException {// TODO
		boolean fail = false;
		if (Mode.READ.equals(mode)) {
			fail = unify(variable, nextSubterm);
		} else {
			int addr = deref(variable);
			if (addr < heap) {
				copy(addr, heap++);
			} else {
				pushUnboundVar();
				bind(addr, heap++);
			}
		}
		nextSubterm++;
		backtrackOrContinue(fail);

		tracer.trace("unify_local_value", variable);
	}

	/**
	 * In read mode, unify variable Vn and heap address S (the next subterm); in
	 * write mode, push the value of Vn onto the heap. In either mode, increment S
	 * by one. Backtrack on failure, otherwise continue execution
	 * 
	 * @param variable
	 * @throws FailureException
	 */
	public void unifyValue(int variable) throws FailureException {
		boolean fail = false;
		if (Mode.READ.equals(mode)) {
			fail = unify(variable, nextSubterm);
		} else {
			copy(variable, heap++);
		}
		nextSubterm++;
		backtrackOrContinue(fail);

		tracer.trace("unify_value", variable);
	}

	/**
	 * In read mode, place the contents of heap address S into variable V[n]; in
	 * write mode, push a new unbound REF cell onto the heap and copy it into X[i].
	 * In either mode, increment S by one. Continue execution
	 * 
	 * @param vn
	 */
	public void unifyVariable(int variable) {
		if (Mode.READ.equals(mode)) {
			copy(nextSubterm, variable);
		} else {
			pushUnboundVar();
			copy(heap++, variable);
		}
		nextSubterm++;
		continueExecution();
	}

	/**
	 * In write mode, push n new unbound REF cells onto the heap. In read mode, skip
	 * the next n heap cells starting at location S. Continue execution
	 * 
	 * @param numberOfUnboundCells
	 */
	public void unifyVoid(final int numberOfUnboundCells) {
		if (Mode.READ.equals(mode)) {
			nextSubterm += numberOfUnboundCells;
		} else {
			for (int i = heap; i < heap + numberOfUnboundCells; i++) {
				setUnboundVar(i);
			}
			heap += numberOfUnboundCells;
		}
		continueExecution();
	}

	/**
	 * Reset all variables since the last choice point to an unbound state. Sets
	 * address from startAddress to trail end as unbound upon backtracking.
	 * 
	 * @param startAddress
	 *            start address
	 */
	private void unwindTrail(int startAddress) {
		for (int i = startAddress; i < trail; i++) {
			setUnboundVar(readPointer(i));
		}
		trail = startAddress;
	}

	public void writeMode() {
		mode = Mode.WRITE;
	}
}
