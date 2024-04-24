package br.com.sysmap.bootcamp.domain.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class PointsCalculatorService {

    public int calculatePointsForToday() {
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        int points = 0;

        switch (currentDayOfWeek) {
            case SUNDAY:
                points = 25;
                break;
            case MONDAY:
                points = 7;
                break;
            case TUESDAY:
                points = 6;
                break;
            case WEDNESDAY:
                points = 2;
                break;
            case THURSDAY:
                points = 10;
                break;
            case FRIDAY:
                points = 15;
                break;
            case SATURDAY:
                points = 20;
                break;
        }

        return points;
    }
}
