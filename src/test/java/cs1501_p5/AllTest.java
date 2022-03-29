/**
 * Basic tests for CS1501 Project 5
 * @author	Dr. Farnan
 */
package cs1501_p5;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.math.BigInteger;

import static java.time.Duration.ofSeconds;

class AllTest {
	final int DEFAULT_TIMEOUT = 190;

	HashMap<String, String[]> mult_cases;
	HashMap<String, String[]> xgcd_cases;

	@BeforeEach
	void setup_cases() {
		mult_cases = new HashMap<String, String[]>();
		mult_cases.put("4digitA", new String[] {"1834", "5849"});
		mult_cases.put("4digitB", new String[] {"8448", "5593"});

		xgcd_cases = new HashMap<String, String[]>();
		xgcd_cases.put("4digitA", new String[] {"2274", "7926"});
		xgcd_cases.put("4digitB", new String[] {"5987", "1488"});
		xgcd_cases.put("Negative", new String[] {"293578", "-8735495"});
		xgcd_cases.put("Large", new String[] {"77958528089503718191259816113663135428273584496360396530885340861351630345297409910923552772373964789100118688891916002270519734001030606109957461192000520207909625343573557835652800155747020015702387", "39090126324167712257404981122318410644407352048539733353560447342128320310279721359182587240332681461618596554578523593664589347215940208671596005851601661748096835352435154910120244482719948722604345"});

	}

	void check_mult(String a, String b) {
		BigInteger biA = new BigInteger(a);
		BigInteger biB = new BigInteger(b);

		HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		HeftyInteger hiB = new HeftyInteger(biB.toByteArray());

		BigInteger biRes = biA.multiply(biB);
		HeftyInteger hiRes = hiA.multiply(hiB);

		assertEquals(0, biRes.compareTo(new BigInteger(hiRes.getVal())));
	}

	void check_xgcd(String a, String b) {
		BigInteger biA = new BigInteger(a);
		BigInteger biB = new BigInteger(b);

		HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		HeftyInteger hiB = new HeftyInteger(biB.toByteArray());

		HeftyInteger[] hiRes = hiA.XGCD(hiB);

		BigInteger biGCD = biA.gcd(biB);
		HeftyInteger hiGCD = hiRes[0];

		BigInteger x = new BigInteger(hiRes[1].getVal());
		BigInteger y = new BigInteger(hiRes[2].getVal());

		assertEquals(0, biGCD.compareTo(new BigInteger(hiGCD.getVal())));

		BigInteger biCheck = biA.multiply(x).add(biB.multiply(y));

		assertEquals(0, biGCD.compareTo(biCheck));
	}

	@ParameterizedTest(name = "Mult test {0}")
	@ValueSource(strings = {"4digitA", "4digitB"})
	void basic_mult(String c) {
		assertTimeoutPreemptively(ofSeconds(DEFAULT_TIMEOUT), () -> {
			String[] cur = mult_cases.get(c);
			check_mult(cur[0], cur[1]);
		});
	}

	@ParameterizedTest(name = "XGCD test {0}")
	@ValueSource(strings = {"4digitA", "4digitB", "Negative", "Large"})
	void basic_xgcd(String c) {
		assertTimeoutPreemptively(ofSeconds(DEFAULT_TIMEOUT), () -> {
			String[] cur = xgcd_cases.get(c);
			check_xgcd(cur[0], cur[1]);
		});
	}

	@Test
	void rightShift() {
		HeftyInteger shift = new HeftyInteger(new BigInteger("2345253454643").toByteArray());
		shift.rightShiftBy(1);
		BigInteger correct = new BigInteger("2345253454643").shiftRight(1);

		assertEquals(0, correct.compareTo(new BigInteger(shift.getVal())));
	}

	@Test
	void leftShift() {
		HeftyInteger shift = new HeftyInteger(new BigInteger("2345253454643").toByteArray());
		shift.leftShiftBy(1);
		BigInteger correct = new BigInteger("2345253454643").shiftLeft(1);

		assertEquals(0, correct.compareTo(new BigInteger(shift.getVal())));
	}

	@Test
	void divisionTest() {
		BigInteger biA = new BigInteger("203458908354");
		BigInteger biB = new BigInteger("8974");

		HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		HeftyInteger hiB = new HeftyInteger(biB.toByteArray());

		BigInteger biRes = biA.divide(biB);
		HeftyInteger hiRes = hiA.divide(hiB);

		assertEquals(0, biRes.compareTo(new BigInteger(hiRes.getVal())));


		biA = new BigInteger("-26814645080101830561641902478788463498295750333798013710709965955302664980137392446093113827546411043147299117890725809400002155031453456944335732095634557327018698706168451639683517372771609266452473");
		biB = new BigInteger("80806069454632401629257038231208616698197427511803479818776917334271859265383347115430451001679961243670450100848876048090241686327599683474437461532349765198731686982343405030842857484395976950276840");

		hiA = new HeftyInteger(biA.toByteArray());
		hiB = new HeftyInteger(biB.toByteArray());

		biRes = biA.divide(biB);
		hiRes = hiA.divide(hiB);

		assertEquals(0, biRes.compareTo(new BigInteger(hiRes.getVal())));
	}

	@Test
	void modTest() {
		BigInteger biA = new BigInteger("203458908354");
		BigInteger biB = new BigInteger("8974");

		HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		HeftyInteger hiB = new HeftyInteger(biB.toByteArray());

		BigInteger biRes = biA.mod(biB);
		HeftyInteger hiRes = hiA.mod(hiB);

		assertEquals(0, biRes.compareTo(new BigInteger(hiRes.getVal())));



		biA = new BigInteger("26814645080101830561641902478788463498295750333798013710709965955302664980137392446093113827546411043147299117890725809400002155031453456944335732095634557327018698706168451639683517372771609266452473");
		biB = new BigInteger("80806069454632401629257038231208616698197427511803479818776917334271859265383347115430451001679961243670450100848876048090241686327599683474437461532349765198731686982343405030842857484395976950276840");

		hiA = new HeftyInteger(biA.toByteArray());
		hiB = new HeftyInteger(biB.toByteArray());

		biRes = biA.mod(biB);
		hiRes = hiA.mod(hiB);

		assertEquals(0, biRes.compareTo(new BigInteger(hiRes.getVal())));
		
	}
}
