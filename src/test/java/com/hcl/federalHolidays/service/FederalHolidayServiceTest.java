package com.hcl.federalHolidays.service;


import com.hcl.federalHolidays.entity.Holiday;
import com.hcl.federalHolidays.repository.FederalHolidayRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FederalHolidayServiceTest {
    private final FederalHolidayRepository repository = Mockito.mock(FederalHolidayRepository.class);
    private final FederalHolidayService service = new FederalHolidayService(repository);

    Calendar calendar = Calendar.getInstance();
    @Test
    void testGetAllHolidays() {

        calendar.set(2025, Calendar.JANUARY, 1);
        when(repository.findAll()).thenReturn(List.of(new Holiday(1L, 840,"USA",calendar.getTime(),"USA New Year","MOnDAy")));
        List<Holiday> holidays = service.getAllHolidays();
        assertEquals(1, holidays.size());
    }

    @Test
    void testGetHolidayById() {
        calendar.set(2025, Calendar.JANUARY, 1);
        when(repository.findById(1L)).thenReturn(Optional.of(new Holiday(1L, 840,"USA", calendar.getTime(),"","")));
        Holiday holiday = service.getHolidayById(1L);
        assertEquals("New Year's Day", holiday.getHolidayName());
    }

    @Test
    void testAddHoliday() {
        calendar.set(2025, Calendar.JULY, 4);
        Holiday holiday = new Holiday(null, 840,"USA", calendar.getTime(),"Independence Day","Friday");
        when(repository.save(holiday)).thenReturn(new Holiday(1L, 840,"USA",calendar.getTime(),"Independence Day","Friday"));
        Holiday savedHoliday = service.addHoliday(holiday);
        assertNotNull(savedHoliday.getId());
    }

    @Test
    void testUpdateHoliday() {
        calendar.set(2025, Calendar.SEPTEMBER, 2);
        Holiday existing = new Holiday(1L,840, "USA", calendar.getTime(),"Laborday","Monday");
        Holiday updated = new Holiday(null, 840,"USA",new Date(2025,Calendar.SEPTEMBER,1), "Labor Day", "Monday");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(new Holiday(1L, 840,"USA",new Date(2025,Calendar.SEPTEMBER,1), "Labor Day", "Monday"));

        Holiday result = service.updateHoliday(1L, updated);
        assertEquals(new Date(2025,Calendar.SEPTEMBER,1), result.getDate());
    }

    @Test
    void testDeleteHoliday() {
        service.deleteHoliday(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
