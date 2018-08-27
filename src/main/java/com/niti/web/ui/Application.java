/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niti.web.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;

import com.niti.constants.ServiceConstants;
import com.niti.simulator.data.SimulatorData;
import com.orientechnologies.orient.core.command.script.OCommandFunction;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

@SpringBootApplication
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
