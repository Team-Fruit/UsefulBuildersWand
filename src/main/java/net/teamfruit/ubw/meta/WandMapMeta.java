package net.teamfruit.ubw.meta;

import java.util.Map;

public class WandMapMeta implements IWandMeta {
	private final Map<String, Object> data;

	public WandMapMeta(final Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public WandItemMetaType getType(final String path) {
		return WandMetaUtils.getType(this.data.get(path));
	}

	@Override
	public Integer getNumber(final String path) {
		return WandMetaUtils.toNumber(this.data.get(path));
	}

	@Override
	public String getText(final String path) {
		return WandMetaUtils.toText(this.data.get(path));
	}

	@Override
	public Boolean getFlag(final String path) {
		return WandMetaUtils.toFlag(this.data.get(path));
	}
}