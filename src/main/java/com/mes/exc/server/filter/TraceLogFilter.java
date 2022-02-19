package com.mes.exc.server.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.shristool.LoggerTool;
import com.mes.exc.server.utils.SessionContants; 

@WebFilter
public class TraceLogFilter extends OncePerRequestFilter implements Ordered {
	private static final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
	private static final String NEED_TRACE_PATH_PREFIX = "/api";
	private static final String IGNORE_CONTENT_TYPE = "multipart/form-data";

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!isRequestValid(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		if (!(request instanceof ContentCachingRequestWrapper)) {
			request = new ContentCachingRequestWrapper(request);
		}
		if (!(response instanceof ContentCachingResponseWrapper)) {
			response = new ContentCachingResponseWrapper(response);
		}
		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		long startTime = System.currentTimeMillis();
		Calendar wStartTime = Calendar.getInstance();
		try {
			filterChain.doFilter(request, response);
			status = response.getStatus();
		} finally {
			String path = request.getRequestURI();
			BMSEmployee wBMSEmployee = null;
			HttpSession session = request.getSession();
			if (session.getAttribute(SessionContants.SessionUser) != null) {
				wBMSEmployee = (BMSEmployee) session.getAttribute(SessionContants.SessionUser);
			}
			if (wBMSEmployee == null)
				wBMSEmployee = new BMSEmployee();
			if (path.startsWith(BaseController.GetProjectName(request) + NEED_TRACE_PATH_PREFIX)
					&& !Objects.equals(IGNORE_CONTENT_TYPE, request.getContentType())
					&& (wBMSEmployee.ID > 0 || wBMSEmployee.ID == -100)) {

				try {
					LoggerTool.SaveApiLog(wBMSEmployee.CompanyID, wBMSEmployee.ID,
							BaseController.GetProjectName(request), path, request.getMethod(),
							JSON.toJSONString(request.getParameterMap()), getRequestBody(request),
							getResponseBody(response), wStartTime, Calendar.getInstance(),
							System.currentTimeMillis() - startTime, status); // updateResponse(res);

				} catch (Exception e) {
					logger.error("Save API Log Error:" + e.getMessage());
				}

			}
			updateResponse(response);
		}
	}

	private boolean isRequestValid(HttpServletRequest request) {
		try {
			new URI(request.getRequestURL().toString());
			return true;
		} catch (URISyntaxException ex) {
			return false;
		}
	}

	private String getRequestBody(HttpServletRequest request) {
		String requestBody = "";
		ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
		if (wrapper != null) {
			try {
				requestBody = IOUtils.toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
			} catch (IOException e) {
				// NOOP
			}
		}
		return requestBody;
	}

	private String getResponseBody(HttpServletResponse response) {
		String responseBody = "";
		ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response,
				ContentCachingResponseWrapper.class);
		if (wrapper != null) {
			try {
				responseBody = IOUtils.toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
			} catch (IOException e) {
				// NOOP
			}
		}
		return responseBody;
	}

	private void updateResponse(HttpServletResponse response) throws IOException {
		ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response,
				ContentCachingResponseWrapper.class);
		Objects.requireNonNull(responseWrapper).copyBodyToResponse();
	}

	public static class HttpTraceLog {

		private String path;
		private String parameterMap;
		private String method;
		private Long timeTaken;
		private String time;
		private Integer status;
		private String requestBody;
		private String responseBody;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getParameterMap() {
			return parameterMap;
		}

		public void setParameterMap(String parameterMap) {
			this.parameterMap = parameterMap;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public Long getTimeTaken() {
			return timeTaken;
		}

		public void setTimeTaken(Long timeTaken) {
			this.timeTaken = timeTaken;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getRequestBody() {
			return requestBody;
		}

		public void setRequestBody(String requestBody) {
			this.requestBody = requestBody;
		}

		public String getResponseBody() {
			return responseBody;
		}

		public void setResponseBody(String responseBody) {
			this.responseBody = responseBody;
		}
	}
}
