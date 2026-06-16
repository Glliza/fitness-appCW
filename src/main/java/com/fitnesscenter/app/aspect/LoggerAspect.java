package com.fitnesscenter.app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    @Before("execution(* com.fitnesscenter.app.service.ZoneService.createZone(..))")
    public void logZoneCreate(JoinPoint joinPoint) {
        log.info(" ЛОГИРОВАНИЕ ");
        log.info("Создание зоны: {}", joinPoint.getArgs()[0]);
    }

    @Before("execution(* com.fitnesscenter.app.service.ZoneService.deleteZone(..))")
    public void logZoneDelete(JoinPoint joinPoint) {
        log.info(" ЛОГИРОВАНИЕ ");
        log.info("Удаление зоны с ID: {}", joinPoint.getArgs()[0]);
    }

    @Before("execution(* com.fitnesscenter.app.service.EquipmentService.changeStatus(..))")
    public void logEquipmentStatusChange(JoinPoint joinPoint) {
        log.info(" ЛОГИРОВАНИЕ ");
        log.info("Изменение статуса оборудования: id={}, новый статус={}", joinPoint.getArgs()[0], joinPoint.getArgs()[1]);
    }

    @Before("execution(* com.fitnesscenter.app.service.ConsumablesService.addExpense(..))")
    public void logConsumablesExpense(JoinPoint joinPoint) {
        log.info(" ЛОГИРОВАНИЕ ");
        log.info("Списание расходников: consumableId={}, zoneId={}, amount={}", joinPoint.getArgs());
    }

    @AfterReturning("execution(* com.fitnesscenter.app.service.TORepairService.updateRequestStatus(..))")
    public void logStatusChange(JoinPoint joinPoint) {
        log.info(" ЛОГИРОВАНИЕ ");
        log.info("Статус заявки изменён: {}", joinPoint.getArgs());
    }
}