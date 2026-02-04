package hazardhub.com.hub;

import hazardhub.com.hub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class HubApplicationTests {

	@MockitoBean
	private UserService userService;

	@Test
	void contextLoads() {
	}

}
