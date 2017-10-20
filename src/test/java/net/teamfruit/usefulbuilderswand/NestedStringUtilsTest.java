package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.meta.NestedStringUtils.*;
import static org.junit.Assert.*;

import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NestedStringUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testA1() {
		final String a = substringNested("({{{a=b},{c=d}}}", "{", "}");
		final String b = "{{a=b},{c=d}}";
		assertEquals(b, a);
	}

	@Test
	public void testA2() {
		final String a = substringNested("({({a=b},({c=d}}", "({", "}");
		final String b = "({a=b},({c=d}";
		assertEquals(b, a);
	}

	@Test
	public void testA3() {
		final String a = substringNested("{{{a=b})}),{c=d})})", "{", "})");
		final String b = "{{a=b})}),{c=d})";
		assertEquals(b, a);
	}

	@Test
	public void testA4() {
		final String a = substringNested("({({a=b({}),({})c=d})})", "({", "})");
		final String b = "({a=b({}),({})c=d})";
		assertEquals(b, a);
	}

	@Test
	public void testA5() {
		final String a = substringNested("({({({({a=b},({}}c=d}}", "({", "}");
		final String b = "({({({a=b},({}}c=d}";
		assertEquals(b, a);
	}

	@Test
	public void testB1() {
		final String a = substringNested("({{{a=b},{c=d}}}", "{", "}", "", "");
		final String b = "{{a=b},{c=d}}";
		assertEquals(b, a);
	}

	@Test
	public void testB2() {
		final String a = substringNested("$({({$({({a=$({b},({c=d$({}}", "({", "}", "$", "");
		final String b = "$({({a=$({b},({c=d$({}";
		assertEquals(b, a);
	}

	@Test
	public void testB3() {
		final String a = substringNested("%&{&%}){%&{){%&{{a=b})})%&{)&%}),{c=d%&{})%&{&%})})%&{", "{", "})", "%&", "&%");
		final String b = "%&{){%&{{a=b})})%&{)&%}),{c=d%&{})%&{&%})";
		assertEquals(b, a);
	}

	@Test
	public void testB4() {
		final String a = substringNested("#})({({###({a=b({###({#})}),({###({})#})c=d###({})###({})#})", "({", "})", "###", "#");
		final String b = "({###({a=b({###({#})}),({###({})#})c=d###({})###({";
		assertEquals(b, a);
	}

	@Test
	public void testB5() {
		final String a = substringNested("\\}\\({\\}({({({({\\({a\\}=b},\\({({\\}}}\\({c=d\\({}\\}}\\({", "({", "}", "\\", "\\");
		final String b = "({({({\\({a\\}=b},\\({({\\}}}\\({c=d\\({}\\}";
		assertEquals(b, a);
	}

	@Test
	public void testC1() {
		final String a = substringBeforeNested("$({({$({({a=$({b},({c=d$({}}", "({", "$");
		final String b = "$({";
		assertEquals(b, a);
	}

	@Test
	public void testC2() {
		final String a = substringBeforeNested("#})({({###({a=b({###({#})}),({###({})#})c=d###({})###({})#})", "({", "###");
		final String b = "#})";
		assertEquals(b, a);
	}

	@Test
	public void testC3() {
		final String a = substringBeforeNested("\\}\\({\\}({({({({\\({a\\}=b},\\({({\\}}}\\({c=d\\({}\\}}\\({", "({", "\\");
		final String b = "\\}\\({\\}";
		assertEquals(b, a);
	}

	@Test
	public void testD1() {
		final String a = substringAfterNested("$({({$({({a=$({b},({c=d$({}}e", "({", "}", "$", "");
		final String b = "e";
		assertEquals(b, a);
	}

	@Test
	public void testD2() {
		final String a = substringAfterNested("#})({({###({a=b({###({#})}),({###({})#})c=d###({})###({})})#})", "({", "})", "###", "#");
		final String b = "})#})";
		assertEquals(b, a);
	}

	@Test
	public void testD3() {
		final String a = substringAfterNested("\\}\\({\\}({({({({\\({a\\}=b},\\({({\\}}}\\({c=d\\({}\\}}\\({", "({", "}", "\\", "\\");
		final String b = "\\({";
		assertEquals(b, a);
	}

	@Test
	public void testD4() {
		final String a = substringAfterNested("\\}\\({\\}({({({({\\({a\\}=b}:,\\({({\\}}}#:\\({c=d\\({}\\}}\\({", "({", "}", "\\", "\\");
		final String b = "\\({";
		assertEquals(b, a);
	}

	@Test
	public void testE1() {
		final String[] a = splitOutsideNested("\\}\\({\\}:({({({({\\({a\\}=b}:,\\({({\\}}}#:\\({c=d\\({}\\}}#:\\({", "({", "}", "\\", "\\", ":", "#");
		final String[] b = new String[] { "\\}\\({\\}", "({({({({\\({a\\}=b}:,\\({({\\}}}#:\\({c=d\\({}\\}}#:\\({" };
		assertArrayEquals(b, a);
	}

	@Test
	public void testE2() {
		final String[] a = splitOutsideNested("\\}::\\({\\}:({({({({\\({a\\}=b}:,\\({({\\}}}::\\({c=d\\({}\\}}:\\({", "({", "}", "\\", "\\", ":", ":");
		final String[] b = new String[] { "\\}::\\({\\}", "({({({({\\({a\\}=b}:,\\({({\\}}}::\\({c=d\\({}\\}}", "\\({" };
		assertArrayEquals(b, a);
	}

	@Test
	public void testE3() {
		final String[] a = splitOutsideNested("\\}\\({\\}:({({({({\\({a\\}=b}:,\\({({\\}}}#:\\({c=d\\({}\\}}#:\\({", "({", "}", "\\", "\\", ":", "#");
		final String[] b = new String[] { "\\}\\({\\}", "({({({({\\({a\\}=b}:,\\({({\\}}}#:\\({c=d\\({}\\}}#:\\({" };
		assertArrayEquals(b, a);
	}

	@Test
	public void testE4() {
		final String[] a = splitOutsideNested("\\}::\\({\\}:::({({({({\\({a\\}=b}:,\\({({\\}}}::\\({c=d\\({}\\}}:\\({", "({", "}", "\\", "\\", ":", ":");
		final String[] b = new String[] { "\\}::\\({\\}::", "({({({({\\({a\\}=b}:,\\({({\\}}}::\\({c=d\\({}\\}}", "\\({" };
		assertArrayEquals(b, a);
	}

	public void testInteractive() {
		Scanner sc = null;
		try {
			sc = new Scanner(new CloseShieldInputStream(System.in));
			String in;
			while (!StringUtils.isEmpty(in = sc.nextLine()))
				System.out.println(substringNested(in, "${", "};", "st", "$"));
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(sc);
		}
	}

}
