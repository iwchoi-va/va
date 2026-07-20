package xwing.filter;  
 
import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XwingFilter implements Filter {
	public static final Pattern TRIDENT_PTN = Pattern.compile(".*Trident/([5-9]|\\d{2,}).*");
	
	public static final String XHTML_MIME_TYPE = "application/xhtml+xml";
	public static final String HTML_MIME_TYPE = "text/html";

	public static final String EXPIRATION_TIME = "expirationTime";
	public static final String CACHEABILITY = "cacheability";
	public static final String PRIVATE = "private";
 
	private String cacheability;
	private long seconds = -1;

	public void init(FilterConfig filterConfig) throws ServletException {
		String expireTime = filterConfig.getInitParameter(EXPIRATION_TIME);

		if (expireTime != null && expireTime.trim().length() > 0) {
			try {
				String cacheable = filterConfig.getInitParameter(CACHEABILITY);
				this.cacheability = cacheable == null ? PRIVATE : cacheable;
				this.seconds = Long.valueOf(expireTime).longValue();
			} catch (Exception e) {
				throw new ServletException("The Initialization of XwingFilter", e);
			}
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		this.executeCacheFilter(req, res);
		this.setContentType(req, res);		
		chain.doFilter(req, res);
	}

	protected void executeCacheFilter(ServletRequest req, ServletResponse res) {
		HttpServletResponse response = (HttpServletResponse) res;
		
		if (this.seconds > 0) {
			StringBuilder cacheControl = new StringBuilder(this.cacheability).append(", max-age=").append(this.seconds);
			response.addHeader("Cache-Control", cacheControl.toString());
			response.setDateHeader("Expires", System.currentTimeMillis() + this.seconds * 1000L);
			if (response.containsHeader("Pragma")) response.setHeader("Pragma", null);
		} else {
			response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.addHeader("Pragma", "no-cache");
			response.addHeader("Expires", "-1");
		}
	}
	
	protected void setContentType(ServletRequest req, ServletResponse res) {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (request.getRequestURI().endsWith(".xhtml")) {
			if (request.getHeader("Accept").contains(XHTML_MIME_TYPE)) {
				response.setContentType(XHTML_MIME_TYPE);
			} else if (TRIDENT_PTN.matcher(request.getHeader("user-agent")).matches()) {
				response.setContentType(XHTML_MIME_TYPE);
			} else {
				response.setContentType(HTML_MIME_TYPE);
			}
		}
	}
	
	public void destroy() {

	}
}