package com.hcl.federalHolidays.controller;


import com.hcl.federalHolidays.entity.Holiday;
import com.hcl.federalHolidays.service.FederalHolidayService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class FederalHolidayController {

    private final FederalHolidayService service;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job Csvjob;

    public FederalHolidayController(FederalHolidayService service) {
        this.service = service;
    }

   @GetMapping
    public List<Holiday> getAllHolidays() {
        return service.getAllHolidays();
    }
   @GetMapping("/{id}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHolidayById(id));
    }

    @PostMapping
    public ResponseEntity<Holiday> addHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(service.addHoliday(holiday));
    }

   @PutMapping("/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable Long id, @RequestBody Holiday updatedHoliday) {
        return ResponseEntity.ok(service.updateHoliday(id, updatedHoliday));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        service.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/importCustomers")
    public void importCsvToDBJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(Csvjob, jobParameters);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }


}

