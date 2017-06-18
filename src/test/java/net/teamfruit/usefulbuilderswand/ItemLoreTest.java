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

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreContent;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreMeta;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreMetaEditable;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreRaw;

public class ItemLoreTest {
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
	public void testLore() {
		final String prefix = "*PRE+";
		final String begin = "{{";
		final String end = "}}";

		final List<String> descFormat = Lists.newArrayList();
		descFormat.add("HI=_${S:HI!}_");
		descFormat.add("SUSHI=_${B:SUSHI}_");
		descFormat.add("JOHN=_${I:JOHN}_");

		final ItemLoreDataFormat format = new ItemLoreDataFormat(prefix, begin, end, descFormat);

		final ItemLoreMetaEditable dataSrc = new ItemLoreMetaEditable();
		dataSrc.setText("HI!", "hi");
		dataSrc.setFlag("SUSHI", true);
		dataSrc.setNumber("JOHN", 12);

		final List<String> loreSrc = new ArrayList<String>();
		loreSrc.add(prefix+"HI=_"+begin+"HI!"+"hi"+end+"_");
		loreSrc.add(prefix+"SUSHI=_"+begin+"SUSHI"+end+"_");
		loreSrc.add(prefix+"JOHN=_"+begin+"JOHN"+"12"+end+"_");

		final ItemLoreMeta dataDst = new ItemLoreMetaEditable().fromContents(format, new ItemLoreContent().fromRaw(format, ItemLoreRaw.create().read(loreSrc)));

		final List<String> loreDst = ItemLoreRaw.create().read(Lists.newArrayList(loreSrc)).updateContents(format, new ItemLoreContent().fromMeta(format, dataSrc)).get();

		assertEquals(dataSrc, dataDst);
		assertEquals(loreSrc, loreDst);
	}

	@Test
	public void testLore2() {
		final String prefix = "*PRE+";
		final String begin = "{{";
		final String end = "}}";

		final List<String> descFormat = Lists.newArrayList();
		descFormat.add("HI=_${S:HI!}_");
		descFormat.add("SUSHI=_${b:SUSHI=${s:HI!}:${i:JOHN}}${B:SUSHI=§}_");
		descFormat.add("JOHN=_${I:JOHN}_");
		descFormat.add("JOHN2=_${i:JOHN=§}_");

		final ItemLoreDataFormat format = new ItemLoreDataFormat(prefix, begin, end, descFormat);

		final ItemLoreMetaEditable dataSrc = new ItemLoreMetaEditable();
		dataSrc.setText("HI!", "hi");
		dataSrc.setFlag("SUSHI", true);
		dataSrc.setNumber("JOHN", 12);

		final List<String> loreSrc = new ArrayList<String>();
		loreSrc.add(prefix+"HI=_"+begin+"HI!"+"hi"+end+"_");
		loreSrc.add(prefix+"SUSHI=_"+"hi"+begin+"SUSHI"+end+"_");
		loreSrc.add(prefix+"JOHN=_"+begin+"JOHN"+"12"+end+"_");
		loreSrc.add(prefix+"JOHN2=_"+"§1§2"+"_");

		final ItemLoreMeta dataDst = new ItemLoreMetaEditable().fromContents(format, new ItemLoreContent().fromRaw(format, ItemLoreRaw.create().read(loreSrc)));

		final List<String> loreDst = ItemLoreRaw.create().read(Lists.newArrayList(loreSrc)).updateContents(format, new ItemLoreContent().fromMeta(format, dataSrc)).get();

		assertEquals(dataSrc, dataDst);
		assertEquals(loreSrc, loreDst);
	}
}