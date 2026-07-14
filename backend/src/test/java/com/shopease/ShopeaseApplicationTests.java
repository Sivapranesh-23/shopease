package com.shopease;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.shopease.repository.search.ProductSearchRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@SpringBootTest
@ActiveProfiles("test")
class ShopeaseApplicationTests {

    @MockBean
    private ProductSearchRepository productSearchRepository;

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    void contextLoads() {
        // Assert that Spring Boot context boots up successfully.
        // This catches any ConflictingBeanDefinitionException or other autowiring issues.
    }
}
