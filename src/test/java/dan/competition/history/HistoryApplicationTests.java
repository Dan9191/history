package dan.competition.history;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HistoryApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("dummy test")
	void dummyTest() {
		assertThat(true).isTrue();
	}

}
