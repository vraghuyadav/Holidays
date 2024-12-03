package com.hcl.federalHolidays.config;


import com.hcl.federalHolidays.entity.Holiday;
import com.hcl.federalHolidays.repository.FederalHolidayRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@Configuration
@AllArgsConstructor
public class HolidayBatchConfig {


    private FederalHolidayRepository federalHolidayRepository;


  /*  @Bean
    public FlatFileItemReader<Holiday> reader() {
        FlatFileItemReader<Holiday> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/holidays.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }*/

    @Bean
    public FlatFileItemReader<Holiday> reader() {
        FlatFileItemReader<Holiday> reader = new FlatFileItemReader<>();
        reader.setName("csvReader");
        reader.setResource(new ClassPathResource("holidays.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<Holiday>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("countryCode","CountryName" ,"date", "holidayName", "weekDay");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Holiday.class);

                // Set a custom date format
                DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                setCustomEditors(Collections.singletonMap(Date.class, new CustomDateEditor(dateFormat, false)));
            }});
        }});
        return reader;
    }

    private LineMapper<Holiday> lineMapper() {
        DefaultLineMapper<Holiday> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "countryCode", "countryName", "date", "holidayName", "weekDay");

        BeanWrapperFieldSetMapper<Holiday> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Holiday.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    @Bean
    public HolidayProcessor processor() {
        return new HolidayProcessor();
    }

    @Bean
    public RepositoryItemWriter<Holiday> writer() {
        RepositoryItemWriter<Holiday> writer = new RepositoryItemWriter<>();
        writer.setRepository(federalHolidayRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step",jobRepository).
                <Holiday, Holiday>chunk(10,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository,PlatformTransactionManager transactionManager) {
        return new JobBuilder("importCustomers",jobRepository)
                .flow(step1(jobRepository,transactionManager)).end().build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

}