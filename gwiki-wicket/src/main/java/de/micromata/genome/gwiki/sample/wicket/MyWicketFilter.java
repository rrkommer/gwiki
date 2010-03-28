package de.micromata.genome.gwiki.sample.wicket;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.wicket.protocol.http.WicketFilter;

public class MyWicketFilter extends WicketFilter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {

		super.doFilter(arg0, arg1, arg2);
	}

}
