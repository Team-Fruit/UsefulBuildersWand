package net.teamfruit.usefulbuilderswand;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.WandData.AbstractData.TestData;
import net.teamfruit.usefulbuilderswand.WandData.AbstractSettings.TestSettings;

public class WandDataTest {

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
		final TestSettings settings = new TestSettings(WandData.it);

		final String title = (String) WandData.it.get("item.title");
		@SuppressWarnings("unchecked")
		final List<String> lore = (List<String>) WandData.it.get("item.lore");

		final Map<String, Object> itemmap = Maps.newHashMap();
		itemmap.put("feature.meta.mode", true);
		itemmap.put("feature.meta.durability.unbreakable", false);
		itemmap.put("feature.meta.size", 49);
		// itemmap.put("feature.meta.count.use", 114514);
		itemmap.put("feature.meta.count.place", 3.13);
		itemmap.put("feature.meta.owner.data", true);
		itemmap.put("feature.meta.owner.name", "Kamesuta${feature.meta.owner.name}");
		itemmap.put("feature.meta.owner.id", "4f2a29432d954959b53e60cd86edd245");
		// itemmap.put("feature.meta.durability.data", 114514);
		itemmap.put("feature.meta.durability.max", 3.13);
		itemmap.put("feature.meta.durability.blockcount", true);
		final TestData itemdata = new TestData(itemmap);

		final long time = System.nanoTime();
		Log.log.info(settings.resolve(itemdata, title));
		for (final String l : lore)
			Log.log.info(settings.resolve(itemdata, l));
		Log.log.info(String.format("time: %s", (System.nanoTime()-time)/1000000000.0));
	}

}
