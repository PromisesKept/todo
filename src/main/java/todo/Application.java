package todo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;

@SpringBootApplication
public class Application {

	public static void main(String[] args) { SpringApplication.run(Application.class, args); }

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void listBeans() {
		String[] beanNames = applicationContext.getBeanNamesForType(UserDetailsService.class);
		System.out.println("\n\n\nUserDetailsService beans: " + Arrays.asList(beanNames) + "\n\n\n");
	}


}
