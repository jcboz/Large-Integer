/**
 * @author	Dr. Farnan
 */
package cs1501_p5;

import java.util.Random;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		// YOUR CODE HERE (replace the return, too...)

		HeftyInteger a, b;
		byte[] aArr = copy(other.getVal());
		byte[] bArr = copy(val);
		a = new HeftyInteger(aArr);
		b = new HeftyInteger(bArr);

		boolean isNeg = (a.isNegative() && !b.isNegative()) || (!a.isNegative() && b.isNegative());

		if(a.isNegative() && b.isNegative()) {
			a = a.negate();
			b = b.negate();
		} else if(a.isNegative() && !b.isNegative()) {
			a = a.negate();
		} else if(!a.isNegative() && b.isNegative()) {
			b = b.negate();
		}

		// System.out.println("A = " + new BigInteger(a.getVal()));
		// System.out.println("B = " + new BigInteger(b.getVal()));

		aArr = a.getVal();
		bArr = b.getVal();

		HeftyInteger res = new HeftyInteger(new byte[1]);
		byte [] value = b.getVal();
		for (int curr = value.length - 1; curr >= 0; curr--) {
			byte currByte = (byte) value[curr];
			for (int i = 0; i < 8; i++) {
				int currBit = ((currByte & 0xFF) >> i) & 1;
				if (currBit == 1) res = res.add(a);
				a.leftShiftBy(1);
			}
		} 
		if(isNeg) {
			res = res.negate();
		}

		// System.out.println("RESULT = " + new BigInteger(res.getVal()));

		return res;
	}

	public void trim() {
		int count = 0;
		int maxAllowed = 5;
		for (int i = maxAllowed; i < val.length; i++)
			if (val[i] == 0x00) 
				count++;
		if (count == val.length) 
			val = new byte[1];
		int newN = val.length - count;

		byte[] trim = new byte[newN];
		for (int i = 0; i < newN; i++) 
			trim[i] = val[count++];
		this.val = trim;
	}

	public void leftShiftBy(int n) {
		for (int i = 0; i < n; i++) {
			byte currLeftmost = (byte) 0;
			byte lastLeftmost = (byte) 0;
			byte mask = (byte) 0b11111110;
			for (int j = val.length - 1; j >= 0; j--) {
				currLeftmost = (byte) ((val[j] & 0xFF) >>> 7);
				val[j] = (byte) ((val[j] & 0xFF) << 1);
				val[j] = (byte) (((val[j] & 0xFF) & mask) | lastLeftmost);
				lastLeftmost = currLeftmost;
			}
			if (!isNegative() && currLeftmost == 1) {
				extend((byte)1);
			} else if (isNegative() && currLeftmost == 0) {
				extend((byte)0);
			}
		}
	}

	public void rightShiftBy(int n) {
		for (int i = 0; i < n; i++) {
			byte currRightmost = (byte)0;
			byte lastRightmost = (byte)0;
			if (isNegative()) {
				lastRightmost = (byte) 1;
			}
			byte mask = (byte)0b01111111;
			for (int j = 0; j < val.length; j++) {
				currRightmost = (byte) ((val[j] & 0xFF) & 1);
				val[j] = (byte) ((val[j] & 0xFF) >> 1);
				val[j] = (byte) (((val[j] & 0xFF) & mask) | (lastRightmost << 7));
				lastRightmost = currRightmost;
			}
		}
	}

	public byte[] copy(byte[] a) {
		byte[] cop = new byte[a.length];
		for (int i = 0; i < cop.length; i++) 
			cop[i] = a[i];
		return cop;
	}

	public HeftyInteger divide(HeftyInteger other) {
		HeftyInteger a, b;
		byte[] aArr = copy(val);
		byte[] bArr = copy(other.getVal());
		a = new HeftyInteger(aArr);
		b = new HeftyInteger(bArr);

		boolean isNeg = (a.isNegative() && !b.isNegative()) || (!a.isNegative() && b.isNegative());

		if(a.isNegative() && b.isNegative()) {
			a = a.negate();
			b = b.negate();
		} else if(a.isNegative() && !b.isNegative()) {
			a = a.negate();
		} else if(!a.isNegative() && b.isNegative()) {
			b = b.negate();
		}

		// a is dividend, b is divisor
		aArr = a.getVal();
		bArr = b.getVal();
		a = new HeftyInteger(aArr);
		b = new HeftyInteger(bArr);

		int numBits = 8 * a.getVal().length;
		b.leftShiftBy(numBits);
		HeftyInteger q = new HeftyInteger(new byte[1]);
		HeftyInteger r = new HeftyInteger(copy(a.getVal()));
		HeftyInteger one = new HeftyInteger(ONE);
		for (int i = 0; i < numBits; i++) {
			b.rightShiftBy(1);
			int compare = b.compareTo(r);
			if (compare > 0) {
				q.leftShiftBy(1);
			} else {
				r = r.subtract(b);
				q.leftShiftBy(1);
				q = q.add(one);
			}
		}
		q.trim();
		if (isNeg) {
			q = q.negate();
		}
		return q;
	}

	public HeftyInteger mod(HeftyInteger other) {
		HeftyInteger a, b;
		byte[] aArr = copy(val);
		byte[] bArr = copy(other.getVal());
		a = new HeftyInteger(aArr);
		b = new HeftyInteger(bArr);

		boolean isNeg = (a.isNegative() && !b.isNegative()) || (!a.isNegative() && b.isNegative());

		if(a.isNegative() && b.isNegative()) {
			a = a.negate();
			b = b.negate();
		} else if(a.isNegative() && !b.isNegative()) {
			a = a.negate();
		} else if(!a.isNegative() && b.isNegative()) {
			b = b.negate();
		}

		// a is dividend, b is divisor
		aArr = a.getVal();
		bArr = b.getVal();
		a = new HeftyInteger(aArr);
		b = new HeftyInteger(bArr);

		int numBits = 8 * a.getVal().length;
		b.leftShiftBy(numBits);
		HeftyInteger q = new HeftyInteger(new byte[1]);
		HeftyInteger r = new HeftyInteger(copy(a.getVal()));
		HeftyInteger one = new HeftyInteger(ONE);
		for (int i = 0; i < numBits; i++) {
			b.rightShiftBy(1);
			int compare = b.compareTo(r);
			if (compare > 0) {
				q.leftShiftBy(1);
			} else {
				r = r.subtract(b);
				q.leftShiftBy(1);
				q = q.add(one);
			}
		}
		r.trim();
		return r;
	}

	public boolean isZero() {
		for (int i = 0; i < val.length; i++) {
			if (val[i] != ((byte) 0))
				return false;
		}
		return true;
	}
		

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public HeftyInteger[] XGCD(HeftyInteger other) {
		HeftyInteger a = new HeftyInteger(copy(val));
		HeftyInteger b = new HeftyInteger(copy(other.getVal()));

		boolean thisNegative = this.isNegative();
		boolean otherNegative = other.isNegative();

		if (thisNegative) {
			a = a.negate();
		}
		if (otherNegative) {
			b = b.negate();
		}

		if(b.isZero()) {
			return new HeftyInteger[]{ this, new HeftyInteger(ONE), new HeftyInteger(new byte[1])};
		}
		HeftyInteger[] bRes = b.XGCD(this.mod(b));
		HeftyInteger x = bRes[2];
		HeftyInteger div = a.divide(b);
		HeftyInteger y = bRes[1].subtract(div.multiply(x));
		HeftyInteger res = bRes[0];

		if (thisNegative) {
			x = x.negate();
		}
		if (otherNegative) {
			y = y.negate();
		}
		return new HeftyInteger[] {res, x, y};
	 }

	 public int compareTo(HeftyInteger other) {
		HeftyInteger compare = this.subtract(other);
		byte[] compareArr = compare.getVal();
		boolean zero = true;
		for (int i = 0; i < compareArr.length; i++) {
			if (compareArr[i] != ((byte) 0))
				zero = false; 
		}
		if (zero) {
			return 0;
		} else if (compare.isNegative()) {
			return -1;
		} else {
			return 1;
		}
	 }
}
