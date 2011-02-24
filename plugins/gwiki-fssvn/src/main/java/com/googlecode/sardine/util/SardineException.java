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
