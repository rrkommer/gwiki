//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.googlecode.sardine;

import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.ssl.SSLSocketFactory;

import com.googlecode.sardine.util.SardineException;

/**
 * The perfect name for a class. Provides the
 * static methods for working with the Sardine
 * interface.
 *
 * @author jonstevens
 */
public class SardineFactory
{
	/**
	 * Default begin() for when you don't need anything but no authentication
	 * and default settings for SSL.
	 */
	public static Sardine begin() throws SardineException
	{
		return Factory.instance().begin(null, null);
	}

	/**
	 * If you want to use custom HTTPS settings with Sardine, this allows you
	 * to pass in a SSLSocketFactory.
	 *
	 * @see <a href="http://hc.apache.org/httpcomponents-client/httpclient/xref/org/apache/http/conn/ssl/SSLSocketFactory.html">SSLSocketFactory</a>
	 */
	public static Sardine begin(SSLSocketFactory sslSocketFactory) throws SardineException
	{
		return Factory.instance().begin(null, null, sslSocketFactory);
	}

	/**
	 * Pass in a HTTP Auth username/password for being used with all
	 * connections
	 */
	public static Sardine begin(String username, String password) throws SardineException
	{
		return Factory.instance().begin(username, password);
	}

	/**
	 * Pass in a HTTP Auth username/password for being used with all
	 * connections
	 */
	public static Sardine begin(String username, String password, Integer port) throws SardineException
	{
		return Factory.instance().begin(username, password, port);
	}

	/**
	 * If you want to use custom HTTPS settings with Sardine, this allows you
	 * to pass in a SSLSocketFactory.
	 *
	 * @see <a href="http://hc.apache.org/httpcomponents-client/httpclient/xref/org/apache/http/conn/ssl/SSLSocketFactory.html">SSLSocketFactory</a>
	 */
	public static Sardine begin(String username, String password, SSLSocketFactory sslSocketFactory) throws SardineException
	{
		return Factory.instance().begin(username, password, sslSocketFactory);
	}

	/**
	 * Useful for when you need to define a http proxy
	 */
	public static Sardine begin(HttpRoutePlanner routePlanner) throws SardineException
	{
		return Factory.instance().begin(null, null, null, routePlanner);
	}

	/**
	 * Useful for when you need to define a http proxy
	 */
	public static Sardine begin(HttpRoutePlanner routePlanner, SSLSocketFactory sslSocketFactory) throws SardineException
	{
		return Factory.instance().begin(null, null, sslSocketFactory, routePlanner);
	}

	/**
	 * Useful for when you need to define a http proxy
	 */
	public static Sardine begin(String username, String password, HttpRoutePlanner routePlanner) throws SardineException
	{
		return Factory.instance().begin(username, password, routePlanner);
	}

	/**
	 * Useful for when you need to define a http proxy
	 */
	public static Sardine begin(String username, String password, SSLSocketFactory sslSocketFactory, HttpRoutePlanner routePlanner) throws SardineException
	{
		return Factory.instance().begin(username, password, sslSocketFactory);
	}

	/**
	 * Useful for when you need to define a http proxy
	 */
	public static Sardine begin(String username, String password, SSLSocketFactory sslSocketFactory, HttpRoutePlanner routePlanner, Integer port) throws SardineException
	{
		return Factory.instance().begin(username, password, sslSocketFactory, routePlanner, port);
	}
}
