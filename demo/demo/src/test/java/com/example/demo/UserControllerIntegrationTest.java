package com.example.demo;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class UserControllerIntegrationTest {

	private static final String DROP_USERS_TABLE = """
		BEGIN
			EXECUTE IMMEDIATE 'DROP TABLE users PURGE';
		EXCEPTION
			WHEN OTHERS THEN
				IF SQLCODE != -942 THEN
					RAISE;
				END IF;
		END;
		""";

	private static final String CREATE_USERS_TABLE = """
		BEGIN
			EXECUTE IMMEDIATE '
				CREATE TABLE users (
					user_id VARCHAR2(50) PRIMARY KEY,
					password VARCHAR2(255) NOT NULL,
					name VARCHAR2(50) NOT NULL,
					created_at DATE DEFAULT SYSDATE NOT NULL,
					ip_address VARCHAR2(50) NOT NULL
				)
			';
		EXCEPTION
			WHEN OTHERS THEN
				IF SQLCODE != -955 THEN
					RAISE;
				END IF;
		END;
		""";

	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	void setUp() {
		jdbcTemplate.execute(DROP_USERS_TABLE);
		jdbcTemplate.execute(CREATE_USERS_TABLE);
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void signUpAndLoginShouldPersistUser() throws Exception {
		mockMvc.perform(post("/api/users/signup")
				.contentType("application/json")
				.content("""
					{
					  "userId": "alpha",
					  "password": "pw1234",
					  "name": "Alpha"
					}
					""")
				.with(request -> {
					request.setRemoteAddr("127.0.0.1");
					return request;
				}))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userId", is("alpha")))
			.andExpect(jsonPath("$.name", is("Alpha")))
			.andExpect(jsonPath("$.ipAddress", is("127.0.0.1")));

		Integer count = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM users WHERE user_id = ?",
			Integer.class,
			"alpha"
		);
		String storedIp = jdbcTemplate.queryForObject(
			"SELECT ip_address FROM users WHERE user_id = ?",
			String.class,
			"alpha"
		);

		org.junit.jupiter.api.Assertions.assertEquals(1, count);
		org.junit.jupiter.api.Assertions.assertEquals("127.0.0.1", storedIp);

		mockMvc.perform(post("/api/users/login")
				.contentType("application/json")
				.content("""
					{
					  "userId": "alpha",
					  "password": "pw1234"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId", is("alpha")))
			.andExpect(jsonPath("$.name", is("Alpha")));
	}

	@Test
	void userCrudEndpointsShouldWork() throws Exception {
		mockMvc.perform(post("/api/users/signup")
				.contentType("application/json")
				.content("""
					{
					  "userId": "beta",
					  "password": "pw1234",
					  "name": "Beta"
					}
					""")
				.with(request -> {
					request.setRemoteAddr("192.168.0.15");
					return request;
				}))
			.andExpect(status().isCreated());

		mockMvc.perform(get("/api/users/beta"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId", is("beta")))
			.andExpect(jsonPath("$.name", is("Beta")));

		mockMvc.perform(get("/api/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].userId", is("beta")));

		mockMvc.perform(put("/api/users/beta")
				.contentType("application/json")
				.content("""
					{
					  "name": "Beta Updated",
					  "password": "pw9999"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("Beta Updated")));

		mockMvc.perform(post("/api/users/login")
				.contentType("application/json")
				.content("""
					{
					  "userId": "beta",
					  "password": "pw9999"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("Beta Updated")));

		mockMvc.perform(delete("/api/users/beta"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is("회원이 삭제되었습니다.")));

		Integer countAfterDelete = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM users WHERE user_id = ?",
			Integer.class,
			"beta"
		);
		org.junit.jupiter.api.Assertions.assertEquals(0, countAfterDelete);
	}
}
