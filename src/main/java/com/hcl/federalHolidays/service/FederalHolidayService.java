package com.hcl.federalHolidays.service;

import com.hcl.federalHolidays.entity.Holiday;
import com.hcl.federalHolidays.repository.FederalHolidayRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FederalHolidayService {
    private final FederalHolidayRepository repository;

    public FederalHolidayService(FederalHolidayRepository repository) {
        this.repository = repository;
    }

    public List<Holiday> getAllHolidays() {
        return repository.findAll();
    }

    public Holiday getHolidayById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Holiday not found"));
    }

    public Holiday addHoliday(Holiday holiday) {
        return repository.save(holiday);
    }

    public Holiday updateHoliday(Long id, Holiday updatedHoliday) {
        Holiday existing = getHolidayById(id);
        existing.setHolidayName(updatedHoliday.getHolidayName());
        existing.setDate(updatedHoliday.getDate());
        existing.setCountryCode(updatedHoliday.getCountryCode());
        return repository.save(existing);
    }

    public void deleteHoliday(Long id) {
        repository.deleteById(id);
    }
}
