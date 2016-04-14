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

package com.googlecode.sardine.util;

import java.io.IOException;

/**
 * Specialized type of exception for Sardine so
 * that it is easy to get the error information from it.
 *
 * @author jonstevens
 */
@SuppressWarnings("serial")
public class SardineException extends IOException
{
	private int statusCode;
	private String responsePhrase;
	private String url;

	/** */
	public SardineException(Exception ex)
	{
		this.initCause(ex);
	}

	/** */
	public SardineException(String msg, String url)
	{
		this(msg, url, -1, null, null);
	}

	/** */
	public SardineException(String msg, String url, Exception initCause)
	{
		this(msg, url, -1, null, initCause);
	}

	/** */
	public SardineException(String msg, String url, int statusCode, String responsePhrase)
	{
		this(msg, url, statusCode, responsePhrase, null);
	}

	/** */
	public SardineException(String url, int statusCode, String responsePhrase)
	{
		this("The server has returned an HTTP error", url, statusCode, responsePhrase, null);
	}

	/** */
	public SardineException(String msg, String url, int statusCode, String responsePhrase, Exception initCause)
	{
		super(msg + responsePhrase != null ? ", response: " + responsePhrase : "" + ", statusCode: " + statusCode);
		this.url = url;
		this.statusCode = statusCode;
		this.responsePhrase = responsePhrase;
		if (initCause != null)
			this.initCause(initCause);
	}

	/**
	 * The url that caused the failure.
	 */
	public String getUrl()
	{
		return this.url;
	}

	/**
	 * The http client status code.
	 * A status code of -1 means that there isn't one and probably isn't a response phrase either.
	 */
	public int getStatusCode()
	{
		return this.statusCode;
	}

	/**
	 * The http client response phrase.
	 */
	public String getResponsePhrase()
	{
		return this.responsePhrase;
	}
}
