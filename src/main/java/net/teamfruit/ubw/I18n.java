package net.teamfruit.ubw;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class I18n {
	/*
	public static String format(final String langkey, final Object... args) {
		return net.minecraft.client.resources.I18n.format(langkey, args);
	}
	*/

    public static String format(final Locale locale, final String langkey, final Object... args) {
        return locale.format(langkey, args);
    }

    public static class Locale {
        /**
         * Splits on "="
         */
        private static final Splitter splitter = Splitter.on('=').limit(2);
        private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

        private final Map<String, String> langMap;
        private final boolean isUnicode;

        private Locale(final Map<String, String> langMap) {
            int i = 0;
            int j = 0;

            for (final String s : langMap.values()) {
                final int k = s.length();
                j += k;

                for (int l = 0; l < k; ++l)
                    if (s.charAt(l) >= 256)
                        ++i;
            }

            final float f = (float) i / (float) j;
            this.langMap = langMap;
            this.isUnicode = f > 0.1D;
        }

        public static class LocaleBuilder {
            private Map<String, String> langMap = Maps.newHashMap();

            public LocaleBuilder from(final Map<String, String> langMap) {
                this.langMap.putAll(langMap);
                return this;
            }

			/*
			public LocaleBuilder fromLocaleDataFiles(final IResourceManager manager, final List<String> langNameList) {
				for (final String s : langNameList) {
					final String s1 = String.format("lang/%s.lang", new Object[] { s });
			
					for (final Object s2 : manager.getResourceDomains())
						try {
							@SuppressWarnings("unchecked")
							final List<IResource> resources = manager.getAllResources(new ResourceLocation((String) s2, s1));
							for (final IResource resource : resources)
								fromResource(resource);
						} catch (final IOException ioexception) {
						}
				}
				return this;
			}
			*/

            public LocaleBuilder fromFile(final File file) throws IOException {
                InputStream input = null;
                try {
                    fromInputStream(input = new FileInputStream(file));
                } finally {
                    IOUtils.closeQuietly(input);
                }
                return this;
            }

			/*
			public LocaleBuilder fromResource(final IResource resource) throws IOException {
				InputStream input = null;
				try {
					fromInputStream(input = resource.getInputStream());
				} finally {
					IOUtils.closeQuietly(input);
				}
				return this;
			}
			*/

            public LocaleBuilder fromInputStream(final InputStream input) throws IOException {
                final Iterator<String> iterator = IOUtils.readLines(input, Charsets.UTF_8).iterator();
                while (iterator.hasNext()) {
                    final String s = iterator.next();

                    if (!s.isEmpty() && s.charAt(0) != 35) {
                        final String[] astring = Iterables.toArray(splitter.split(s), String.class);

                        if (astring != null && astring.length == 2) {
                            final String s1 = astring[0];
                            final String s2 = pattern.matcher(astring[1]).replaceAll("%$1s");
                            this.langMap.put(s1, s2);
                        }
                    }
                }
                return this;
            }

            public Locale build() {
                return new Locale(this.langMap);
            }
        }

        public String translate(final String p_135026_1_) {
            final String s1 = this.langMap.get(p_135026_1_);
            return s1 == null ? p_135026_1_ : s1;
        }

        public String format(final String langkey, final Object... args) {
            final String s1 = translate(langkey);

            try {
                return String.format(s1, args);
            } catch (final IllegalFormatException illegalformatexception) {
                return "Format error: " + s1;
            }
        }

        public boolean isUnicode() {
            return this.isUnicode;
        }
    }
}
