package com.makotovh.medianow;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.r2dbc.core.DatabaseClient;

@SpringBootApplication
public class MediaNowApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaNowApplication.class, args);

		ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///medianowdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

		var client = DatabaseClient.create(connectionFactory);

		client.sql("CREATE TABLE Plan" +
						"(id bigint auto_increment primary key," +
						"code varchar(10)," +
						"name VARCHAR(255)," +
						"description VARCHAR(255))")
				.fetch()
				.all()
				.collectList()
				.block();
	}

}
