package com.etnetera.hr;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.etnetera.hr.data.FrameworkVersion;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.OrderBy;


/**
 * Class used for Spring Boot/MVC based tests.
 * 
 * @author Etnetera
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JavaScriptFrameworkTests {

	@Autowired
	private MockMvc mockMvc;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private JavaScriptFrameworkRepository repository;

	private void prepareData() throws Exception {
		JavaScriptFramework react = new JavaScriptFramework("ReactJS");
		JavaScriptFramework vue = new JavaScriptFramework("Vue.js");
		
		repository.save(react);
		repository.save(vue);
	}

	@Test
	public void frameworksTest() throws Exception {
		prepareData();

		mockMvc.perform(get("/frameworks"))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));

	}
	
	@Test
	public void addFrameworkInvalid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(post("/frameworks/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));
		
		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(post("/frameworks/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].message", is("Size")));
		
	}

	@Test
	public void removeFramework() throws Exception {
		prepareData();

		mockMvc.perform(delete("/frameworks/delete/2"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/frameworks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")));

	}

	@Test
	public void removeNotExistingFramework() throws Exception {

		mockMvc.perform(delete("/frameworks/delete/1097"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Framework with id: 1097 is not presented in database !"));

	}


	@Test
	public void updateFramework() throws Exception {
		prepareData();

		JavaScriptFramework javaScriptFramework = new JavaScriptFramework();
		javaScriptFramework.setName("Angular");


		mockMvc.perform(put("/frameworks/update/1")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(javaScriptFramework)))
				.andExpect(status().isOk());


		mockMvc.perform(get("/frameworks/search/An"))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Angular")));
	}

	@Test
	public void searchFramework() throws Exception {
		prepareData();

		mockMvc.perform(get("/frameworks/search/Re"))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")));

		mockMvc.perform(get("/frameworks/search/V"))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(2)))
				.andExpect(jsonPath("$[0].name", is("Vue.js")));
	}

	@Test
	public void setVersion() throws Exception {
		prepareData();

		FrameworkVersion frameworkVersion = new FrameworkVersion();
		frameworkVersion.setVersion(3.1f);
		frameworkVersion.setDescription("This version is better than previous, but I'm not sure :)");

		mockMvc.perform(put("/frameworks/setversion/1")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(frameworkVersion)))
				.andExpect(status().isOk());


		mockMvc.perform(get("/frameworks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].versions", hasSize(1)))
				.andExpect(jsonPath("$[0].versions[0].version").value(3.1))
				.andExpect(jsonPath("$[0].versions[0].description").value("This version is better than previous, but I'm not sure :)"));
	}


	@Test
	public void setVersionInvalid() throws Exception {
		FrameworkVersion frameworkVersion = new FrameworkVersion();
		frameworkVersion.setDescription("This version is better than previous, but I'm not sure :)");

		prepareData();

		mockMvc.perform(put("/frameworks/setversion/1")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(frameworkVersion)))
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("version")))
				.andExpect(jsonPath("$.errors[0].message", is("NotNull")))
				.andExpect(status().isBadRequest());

		frameworkVersion.setVersion(0f);

		mockMvc.perform(put("/frameworks/setversion/1")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(frameworkVersion)))
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("version")))
				.andExpect(jsonPath("$.errors[0].message", is("Min")))
				.andExpect(status().isBadRequest());

		frameworkVersion.setVersion(2.3f);

		mockMvc.perform(put("/frameworks/setversion/1097")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(frameworkVersion)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Framework with id: 1097 is not presented in database !"));

	}

}
