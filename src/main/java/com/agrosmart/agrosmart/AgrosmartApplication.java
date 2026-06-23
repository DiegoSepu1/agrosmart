package com.agrosmart.agrosmart;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class AgrosmartApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgrosmartApplication.class, args);
	}

	@Bean
	public CommandLineRunner fixDatabase(JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				// Intentar hacer id_agricultor nullable para soportar compras de invitados
				jdbcTemplate.execute("ALTER TABLE orden MODIFY id_agricultor INT(11) NULL");
				System.out.println(">>> Base de datos actualizada: 'id_agricultor' ahora es NULLABLE.");
			} catch (Exception e) {
				System.out.println(">>> Nota: No se pudo modificar la columna 'id_agricultor' (puede que ya sea nullable o la tabla no exista aún).");
			}
		};
	}

}
