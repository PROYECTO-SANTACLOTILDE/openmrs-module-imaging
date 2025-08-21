package org.openmrs.module.imaging.web.controller;

import junit.framework.Assert;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Ignore;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Ignore
public class BaseWebControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	protected RequestMappingHandlerAdapter handlerAdapter;
	
	@Autowired
	protected List<RequestMappingHandlerMapping> handlerMappings;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public MockHttpServletRequest request(RequestMethod method, String requestURI) {
		MockHttpServletRequest request = new MockHttpServletRequest(method.toString(), requestURI);
		request.addHeader("content-type", "application/json");
		return request;
	}
	
	public static class Parameter {
		
		public String name;
		
		public String value;
		
		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	public MockHttpServletRequest newRequest(RequestMethod method, String requestURI, Parameter... parameters) {
		MockHttpServletRequest request = request(method, requestURI);
		for (Parameter parameter : parameters) {
			request.addParameter(parameter.name, parameter.value);
		}
		return request;
	}
	
	public MockHttpServletRequest newRequest(RequestMethod method, String requestURI, Map<String, String> headers,
	        Parameter... parameters) {
		MockHttpServletRequest request = newRequest(method, requestURI, parameters);
		headers.forEach(request::addHeader);
		return request;
	}
	
	public MockHttpServletRequest newDeleteRequest(String requestURI, Parameter... parameters) {
		return newRequest(RequestMethod.DELETE, requestURI, parameters);
	}
	
	public MockHttpServletRequest newGetRequest(String requestURI, Parameter... parameters) {
		return newRequest(RequestMethod.GET, requestURI, parameters);
	}
	
	public MockHttpServletRequest newGetRequest(String requestURI, Map<String, String> headers, Parameter... parameters) {
		return newRequest(RequestMethod.GET, requestURI, headers, parameters);
	}
	
	public MockHttpServletRequest newPostRequest(String requestURI, Object content) {
		return newWriteRequest(requestURI, content, RequestMethod.POST);
	}
	
	public MockHttpServletRequest newPutRequest(String requestURI, Object content) {
		return newWriteRequest(requestURI, content, RequestMethod.PUT);
	}
	
	private MockHttpServletRequest newWriteRequest(String requestURI, Object content, RequestMethod requestMethod) {
		MockHttpServletRequest request = request(requestMethod, requestURI);
		try {
			String json = new ObjectMapper().writeValueAsString(content);
			request.setContent(json.getBytes("UTF-8"));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return request;
	}
	
	public MockHttpServletRequest newPostRequest(String requestURI, Map<String, String> params) {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", requestURI);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			request.addParameter(entry.getKey(), entry.getValue());
		}
		return request;
	}

	public MockHttpServletRequest newPostRequest(String requestURI, MultipartFile file, int configurationId) {
		MockHttpServletRequest request = request(RequestMethod.POST, requestURI);
		request.setMethod("POST");
		request.setContentType("multipart/form-data");
		request.setParameter("configurationId", String.valueOf(configurationId));
		request.setAttribute("file", file); // simulate @RequestParam("file")
		return request;
	}
	
	public MockHttpServletRequest newPutRequest(String requestURI, String content) {
		MockHttpServletRequest request = request(RequestMethod.PUT, requestURI);
		try {
			String json = new ObjectMapper().writeValueAsString(content);
			request.setContent(json.getBytes("UTF-8"));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return request;
	}
	
	public MockHttpServletResponse handle(HttpServletRequest request) throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		HandlerExecutionChain chain = null;
		for (RequestMappingHandlerMapping mapping : handlerMappings) {
			chain = mapping.getHandler(request);
			if (chain != null) {
				break;
			}
		}
		Assert.assertNotNull("Handler not found for URI: " + request.getRequestURI(), chain);
		
		handlerAdapter.handle(request, response, chain.getHandler());
		Object controller = chain.getHandler();
		if (controller instanceof DicomStudyController) {
			Object result = ((DicomStudyController) controller).useStudiesByPatient(request.getParameter("patient"),
			    (MockHttpServletRequest) request, response);
			if (result != null) {
				response.setContentType("application/json");
				response.getWriter().write(new ObjectMapper().writeValueAsString(result));
			}
		}
		return response;
	}
	
	public <T> T deserialize(MockHttpServletResponse response, Class<T> type) throws Exception {
		String content = response.getContentAsString();
		Assert.assertFalse("Response is empty", content.isEmpty());
		return objectMapper.readValue(response.getContentAsString(), type);
	}
	
	public <T> T deserialize(MockHttpServletResponse response, final TypeReference<T> typeReference) throws Exception {
		String content = response.getContentAsString();
		Assert.assertFalse("Response is empty", content.isEmpty());
		return objectMapper.readValue(response.getContentAsString(), typeReference);
	}
}
