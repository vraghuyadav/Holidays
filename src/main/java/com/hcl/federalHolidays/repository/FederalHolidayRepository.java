package com.hcl.federalHolidays.repository;

import com.hcl.federalHolidays.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FederalHolidayRepository extends JpaRepository<Holiday,Long> {
}
