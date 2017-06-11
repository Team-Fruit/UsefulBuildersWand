package net.teamfruit.usefulbuilderswand;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

import net.teamfruit.usefulbuilderswand.ItemLoreMeta.ItemLoreDataFormat;

public class ItemLoreDataTest {
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
	public void test() {
		final String prefix = "*PRE+";
		final String begin = "{{";
		final String end = "}}";

		final List<String> descFormat = Lists.newArrayList();
		descFormat.add("HI=_${S:HI!}_");
		descFormat.add("SUSHI=_${B:SUSHI}_");
		descFormat.add("JOHN=_${I:JOHN}_");

		final ItemLoreDataFormat format = new ItemLoreDataFormat(prefix, begin, end, descFormat);

		final ItemLoreMeta dataSrc = new ItemLoreMeta();
		dataSrc.setText("HI!", "hi");
		dataSrc.setFlag("SUSHI", true);
		dataSrc.setNumber("JOHN", 12);

		final List<String> loreSrc = new ArrayList<String>();
		loreSrc.add(prefix+"HI=_"+begin+"HI!"+"hi"+end+"_");
		loreSrc.add(prefix+"SUSHI=_"+begin+"SUSHI"+end+"_");
		loreSrc.add(prefix+"JOHN=_"+begin+"JOHN"+"12"+end+"_");

		final ItemLoreMeta dataDst = new ItemLoreMeta();
		dataDst.fromLore(format, loreSrc);

		final List<String> loreDst = new ArrayList<String>();
		dataSrc.toLore(format, loreDst);

		assertEquals(dataSrc, dataDst);
		assertEquals(loreSrc, loreDst);
	}
}
