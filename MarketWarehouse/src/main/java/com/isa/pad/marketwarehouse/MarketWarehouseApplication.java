package com.isa.pad.marketwarehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.model.Order;
import com.isa.pad.marketwarehouse.model.OrderLine;
import com.isa.pad.marketwarehouse.model.Product;
import com.isa.pad.marketwarehouse.repository.CustomerRepository;
import com.isa.pad.marketwarehouse.repository.OrderLineRepository;
import com.isa.pad.marketwarehouse.repository.OrderRepository;
import com.isa.pad.marketwarehouse.repository.ProductRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.math.BigDecimal;
import java.util.List;
@SpringBootApplication
@ComponentScan(basePackages = "com.isa.pad.marketwarehouse")
public class MarketWarehouseApplication extends WebMvcConfigurerAdapter {

    private static final Logger LOGGER = Logger.getLogger(MarketWarehouseApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(MarketWarehouseApplication.class, args);
    }

    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {
        configurer.
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType(MediaType.APPLICATION_XML.getSubtype(), MediaType.APPLICATION_XML).
                mediaType(MediaType.APPLICATION_JSON.getSubtype(), MediaType.APPLICATION_JSON);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new YamlJackson2HttpMessageConverter());
        converters.add(new YmlJackson2HttpMessageConverter());
    }

    @Bean
    public CommandLineRunner init(CustomerRepository repository) {
        return (args) -> {
            repository.deleteAll();
            repository.save(new Customer("Mike", "Spike", "Baker Street"));
            repository.save(new Customer("John", "Lennon", "London Street"));

            for (Customer c : repository.findAll()) {
                LOGGER.info("Customer: " + c);
            }
        };
    }


    final class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
        YamlJackson2HttpMessageConverter() {
            super(new YAMLMapper(), MediaType.parseMediaType("application/yaml"));
        }
    }

    final class YmlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
        YmlJackson2HttpMessageConverter() {
            super(new YAMLMapper(), MediaType.parseMediaType("application/yml"));
        }
    }

	@Bean
    public CommandLineRunner initOrderItems(OrderLineRepository orderLineRepository, ProductRepository productRepository,
                                            OrderRepository orderRepository, CustomerRepository customerRepository) {
		return (args) -> {
			productRepository.deleteAll();
			orderLineRepository.deleteAll();
			orderRepository.deleteAll();

			Product butter = new Product("Butter", new BigDecimal(45.00), "BUTTER897632SD");
            Product water = new Product("Water", new BigDecimal(12.00), "RESAN782785SD");
            Product bread = new Product("Bread", new BigDecimal(3.00), "FRANZ25935SD");
            productRepository.save(butter);
            productRepository.save(water);
            productRepository.save(bread);

			OrderLine butterLine = new OrderLine(butter, 2);
			orderLineRepository.save(butterLine);

            OrderLine waterLine = new OrderLine(water, 1);
            orderLineRepository.save(waterLine);

            OrderLine breadLine = new OrderLine(bread, 3);
            orderLineRepository.save(breadLine);

			List<Customer> mikes = customerRepository.findByFirstNameAndLastName("Mike", "Spike");
			List<Customer> johns = customerRepository.findByFirstNameAndLastName("John", "Lennon");

			Order mikeOrder = new Order(johns.get(0));
			mikeOrder.add(butterLine);
			orderRepository.save(mikeOrder);

            Order johnOrder = new Order(johns.get(0));
            johnOrder.add(waterLine);
            orderRepository.save(johnOrder);

			for (Order o : orderRepository.findAll()) {
				LOGGER.info("Order: " + o);
				LOGGER.info("OrderLine: " + o.getOrderLineList());
				LOGGER.info("---------------------------");
			}
		};
	}
}
