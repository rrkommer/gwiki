package de.micromata.genome.gwiki.plugin.rssfeed_1_0;

////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////

import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.*;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.*;
import org.junit.Assert;
import org.junit.Test;
import junit.framework.TestCase;
import de.micromata.genome.util.xml.xmlbuilder.Xml;

/**
 * @author ingojoseph
 * 
 */
public class RSSFeedTest extends TestCase {

	@Test
	public void testRSSBuilder() {
		String xml = rss("2.0")
				.nest(channel(
						(title(Xml.text("Feeds"))),//
						(link(Xml.text("micromata.de"))),//
						(description(Xml.text("beschreibung"))),//
						(copyright(Xml.text("ingo 2011"))),//
						(language(Xml.text("de-de"))),//
						(pubData(Xml.text("Sat, 15 Jan 2011 09:17:31 +0100"))),//
						(item((title(Xml.text("test title"))),//
								(description(Xml.text("beschreibung"))),//
								(link(Xml.text("google.de"))),//
								(author(Xml
										.text("Autor des Artikels, E-Mail-Adresse"))),//
								(pubData(Xml
										.text("Mon, 17 Jan 2011 05:33:02 +0100"))))),//
						(item((title(Xml.text("test title2"))),//
								(description(Xml.text("beschreibung2"))),//
								(link(Xml.text("apple.de"))),//
								(author(Xml.text("ingo"))),//
								(pubData(Xml
										.text("Tue, 18 Jan 2011 09:33:11 +0100"))))),//
						(item((title(Xml.text("test title3"))),//
								(description(Xml.text("beschreibung3"))),//
								(link(Xml.text("wikipedia.de"))),//
								(author(Xml.text("ingo"))),//
								(pubData(Xml
										.text("Wed, 19 Jan 2011 10:01:59 +0100")))))//
				)).toString();
		Assert.assertTrue(xml
				.equals("\n<rss version=\"2.0\">\n  <channel>\n    <title>Feeds</title>\n    <link>micromata.de</link>\n    <description>beschreibung</description>\n    <copyright>ingo 2011</copyright>\n    <language>de-de</language>\n    <pubData>Sat, 15 Jan 2011 09:17:31 +0100</pubData>\n    <item>\n      <title>test title</title>\n      <description>beschreibung</description>\n      <link>google.de</link>\n      <author>Autor des Artikels, E-Mail-Adresse</author>\n      <pubData>Mon, 17 Jan 2011 05:33:02 +0100</pubData>\n    </item>\n    <item>\n      <title>test title2</title>\n      <description>beschreibung2</description>\n      <link>apple.de</link>\n      <author>ingo</author>\n      <pubData>Tue, 18 Jan 2011 09:33:11 +0100</pubData>\n    </item>\n    <item>\n      <title>test title3</title>\n      <description>beschreibung3</description>\n      <link>wikipedia.de</link>\n      <author>ingo</author>\n      <pubData>Wed, 19 Jan 2011 10:01:59 +0100</pubData>\n    </item>\n  </channel>\n</rss>"));
		return;
	}

	@Test
	public void testAtomBuilder() {
		String atom = feed("http://www.w3.org/2005/Atom")
				.nest(author((name(Xml.text("Ingo Joseph")))),//
						(title(Xml.text("GWiki Atom-Feeds"))),//
						(link("http://localhost:8081/")),//
						(subtitle(Xml.text("Hier siehst du Feeds von GWiki!"))),//
						(updated(Xml.text("Sat, 15 Jan 2011 09:17:31 +0100"))),//
						(entry((title(Xml.text("test title"))),//
								(link("google.de")),//
								(updated(Xml
										.text("Mon, 17 Jan 2011 05:33:02 +0100"))),//
								(summary("html").nest(Xml
										.text("test zusammenfassung"))),
								(content("html").nest(Xml.text("voller Inhalt"))),//
								author((name(Xml.text("autorxyz"))))//
						))//
				).toString();
		Assert.assertTrue(atom
				.equals("\n<feed xmlns=\"http://www.w3.org/2005/Atom\">\n  <author>\n    <name>Ingo Joseph</name>\n  </author>\n  <title>GWiki Atom-Feeds</title>\n  <link href=\"http://localhost:8081/\"/>\n  <subtitle>Hier siehst du Feeds von GWiki!</subtitle>\n  <updated>Sat, 15 Jan 2011 09:17:31 +0100</updated>\n  <entry>\n    <title>test title</title>\n    <link href=\"google.de\"/>\n    <updated>Mon, 17 Jan 2011 05:33:02 +0100</updated>\n    <summary type=\"html\">test zusammenfassung</summary>\n    <content type=\"html\">voller Inhalt</content>\n    <author>\n      <name>autorxyz</name>\n    </author>\n  </entry>\n</feed>"));
		return;
	}
}