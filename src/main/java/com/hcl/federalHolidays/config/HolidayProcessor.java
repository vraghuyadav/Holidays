package com.hcl.federalHolidays.config;

import com.hcl.federalHolidays.entity.Holiday;
import org.springframework.batch.item.ItemProcessor;

public class HolidayProcessor implements ItemProcessor<Holiday, Holiday> {

    @Override
    public Holiday process(Holiday holiday) throws Exception {
//        if(holiday.getCountryCode().equals(840)) {
//            return holiday;
//        }else{
//            return null;
//        }
        return holiday;
    }
}