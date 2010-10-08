package gobo.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;

public class AuthSubFilter implements Filter {

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(true);
		if (session.getAttribute("token") == null) {

			/**
			 * After Google's Auth.
			 */
			if (request.getParameter("token") != null) {

				String oneTimeToken = AuthSubUtil.getTokenFromReply(request.getQueryString());
				try {
					String sessionToken = AuthSubUtil.exchangeForSessionToken(oneTimeToken, null);
					sessionToken = URLDecoder.decode(sessionToken, "UTF-8");
					session.setAttribute("token", sessionToken);

				} catch (AuthenticationException e) {
					e.printStackTrace();
					response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Server rejected one time use token.");

				} catch (GeneralSecurityException e) {
					e.printStackTrace();
					response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Security error while retrieving session token.");
				}

			} else {

				/**
				 * redirect to Google
				 */
				StringBuilder thisURL =
					new StringBuilder(request.getScheme()).append("://").append(
						request.getServerName());
				if ((request.getScheme().equalsIgnoreCase("http") && request.getServerPort() != 80)
					|| (request.getScheme().equalsIgnoreCase("https") && request.getServerPort() != 443)) {
					thisURL.append(":").append(request.getServerPort());
				}
				thisURL.append(((HttpServletRequest) request).getRequestURI());

				String redirectURL =
					AuthSubUtil
						.getRequestUrl(
							thisURL.toString(),
							"http://spreadsheets.google.com/feeds https://spreadsheets.google.com/feeds http://docs.google.com/feeds https://docs.google.com/feeds",
							false,
							true);

				response.sendRedirect(redirectURL);
				return;
			}
		}

		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
}
