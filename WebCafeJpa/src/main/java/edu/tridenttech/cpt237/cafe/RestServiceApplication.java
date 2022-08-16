package edu.tridenttech.cpt237.cafe;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import edu.tridenttech.cpt237.cafe.model.MenuItem;
import edu.tridenttech.cpt237.cafe.repository.MenuItemRepository;

@SpringBootApplication
public class RestServiceApplication {
	
	private static final Logger log = LoggerFactory.getLogger(RestServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
    
	@Bean
	public ApplicationRunner dbloader(MenuItemRepository menuItemRepository) {
		String configPath = "src/main/resources/cafeMenu.txt";
		return (args) -> {
			ArrayList<MenuItem> menuItems = new ArrayList<>();
			Scanner input = new Scanner(new File(configPath));
			while (input.hasNext()) {
				String line = input.nextLine();
				String[] fields = line.split(",");
				char typeChar = fields[0].toUpperCase().charAt(0);
				String name = fields[1];
				String type;
				MenuItem item;
				double price = Double.parseDouble(fields[2]);
				switch (typeChar) {
					case 'D': {
						type = "Beverage";
					} break;
					case 'S': {
						type = "Sandwich";
					} break;
					case 'B': {
						type = "Bakery";
					} break;
					default: {
						System.err.printf("Unknown type: %c%n", typeChar);
						continue;
					}
				}
				item =  new MenuItem(type, name, price);
				menuItems.add(item);
			}
			log.info(String.format("Storing %d menu items.%n", menuItems.size()));
			menuItemRepository.saveAll(menuItems);
		input.close();
	};
	}
}
