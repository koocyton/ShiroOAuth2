package com.doopp.gauss.server.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.doopp.gauss.server.json.MappingFastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan("com.doopp.gauss.api.controller")
public class WebMvcConfigurer extends WebMvcConfigurerAdapter{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/public/");
        super.addResourceHandlers(registry);
    }

    // 异步
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(30*1000L); //tomcat默认10秒
        configurer.setTaskExecutor(mvcTaskExecutor());//所借助的TaskExecutor
    }

    @Bean
    public ThreadPoolTaskExecutor mvcTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setQueueCapacity(100);
        executor.setMaxPoolSize(25);
        return executor;
    }

    // 上传文件
    @Bean
    public CommonsMultipartResolver multipartResolver() throws Exception {
        CommonsMultipartResolver commonsMultipartResolver =  new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(5400000);
        commonsMultipartResolver.setUploadTempDir(new FileSystemResource("/tmp"));
        return commonsMultipartResolver;
    }

    // 模版
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/template/pages/");
        freeMarkerConfigurer.setDefaultEncoding("UTF-8");
        return freeMarkerConfigurer;
    }

    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
        freeMarkerViewResolver.setSuffix(".html");
        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
        freeMarkerViewResolver.setRequestContextAttribute("rc");
        return freeMarkerViewResolver;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(this.fastJsonHttpMessageConverter());
    }

    private MappingFastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<MediaType>(){{
           add(MediaType.APPLICATION_JSON_UTF8);
        }};
        SerializerFeature[] serializerFeatures = {
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.QuoteFieldNames
        };
        MappingFastJsonHttpMessageConverter jsonConverter = new MappingFastJsonHttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(mediaTypes);
        jsonConverter.setSerializerFeature(serializerFeatures);
        return jsonConverter;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
        super.configureDefaultServletHandling(configurer);
        configurer.enable();
    }
}
