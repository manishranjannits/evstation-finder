package com.niti.web.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;

import com.niti.constants.ServiceConstants;
import com.niti.simulator.data.SimulatorData;
import com.orientechnologies.orient.core.command.script.OCommandFunction;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

@SpringBootApplication(scanBasePackages = {"com.niti"})
public class Application {

	private static OrientGraphNoTx graphNoTx = null;
	
	@Bean
	public MessageRepository messageRepository() {
		return new InMemoryMessageRepository();
	}

	@Bean
	public Converter<String, Message> messageConverter() {
		return new Converter<String, Message>() {
			@Override
			public Message convert(String id) {
				return messageRepository().findMessage(Long.valueOf(id));
			}
		};
	}

	private static void initializeDb() {
        graphNoTx = new OrientGraphNoTx(ServiceConstants.DB_URL + ServiceConstants.DB_FOLDER, ServiceConstants.DB_USERNAME, ServiceConstants.DB_PASSWORD);
	}
	
	public static void clearDb() {
		graphNoTx.command(
		          new OCommandFunction(ServiceConstants.RESET_COMMAND)).execute();
		try {
			System.out.println("Refreshing Simulator database...");
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		initializeDb();
		clearDb();
		
		SimulatorData.fillSimulatorData(graphNoTx);
		SpringApplication.run(Application.class, args);
	}
	
	
	public static OrientGraphNoTx getGraphNoTx() {
		return graphNoTx;
	}
	
	
    

}
