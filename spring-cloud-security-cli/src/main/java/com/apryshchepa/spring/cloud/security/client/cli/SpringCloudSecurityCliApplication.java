package  com.apryshchepa.spring.cloud.security.client.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import java.util.Arrays;

@SpringBootApplication
public class SpringCloudSecurityCliApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSecurityCliApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting cron job");

        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.header);
        resourceDetails.setAccessTokenUri("http://localhost:9000/services/oauth/token");

        resourceDetails.setScope(Arrays.asList("toll_read"));
        resourceDetails.setClientId("pluralsight");
        resourceDetails.setClientSecret("pluralsightsecret");
        resourceDetails.setUsername("agoldberg");
        resourceDetails.setPassword("pass1");

        OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails);

        String token = template.getAccessToken().toString();
        System.out.println("Token: " + token);

        String tollData = template.getForObject("http://localhost:9001/services/tolldata", String.class);

        System.out.println("Result: " + tollData);
    }
}
