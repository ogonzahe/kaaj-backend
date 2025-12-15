package com.kaaj;

import com.kaaj.api.ReporteController;
import com.kaaj.api.service.ReporteService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(useDefaultFilters = false, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ReporteController.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ReporteService.class)
})
public class KaajBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(KaajBackendApplication.class, args);
	}
}
