package org.poem;


import org.poem.common.filter.FileUploadManagerFiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration
public class LargeFileUploaderApp {

    private static final Logger logger = LoggerFactory.getLogger(LargeFileUploaderApp.class);

    @Bean
    public FileUploadManagerFiler getFileUploadManagerFilter() {
        return new FileUploadManagerFiler();
    }


    @Bean
    public FilterRegistrationBean fileUpLoadFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(getFileUploadManagerFilter());
        filterRegBean.addUrlPatterns("/*");
        return filterRegBean;
    }

    /**
     * this run application
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(LargeFileUploaderApp.class,args);
    }
}
