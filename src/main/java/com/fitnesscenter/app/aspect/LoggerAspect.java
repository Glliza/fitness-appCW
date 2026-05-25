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

    @Before("execution(* com.fitness.service.ZoneService.createZone(..))")
    public void logZoneCreate(JoinPoint joinPoint) {
        log.info("Creating zone: {}", joinPoint.getArgs()[0]);
    }

    @Before("execution(* com.fitness.service.ZoneService.deleteZone(..))")
    public void logZoneDelete(JoinPoint joinPoint) {
        log.info("Deleting zone with id: {}", joinPoint.getArgs()[0]);
    }

    @Before("execution(* com.fitness.service.EquipmentService.changeStatus(..))")
    public void logEquipmentStatusChange(JoinPoint joinPoint) {
        log.info("Changing equipment status: id={}, newStatus={}", joinPoint.getArgs()[0], joinPoint.getArgs()[1]);
    }

    @Before("execution(* com.fitness.service.ConsumablesService.addExpense(..))")
    public void logConsumablesExpense(JoinPoint joinPoint) {
        log.info("Consumables expense: consumableId={}, zoneId={}, amount={}", joinPoint.getArgs());
    }

    @AfterReturning("execution(* com.fitness.service.TORepairService.updateRequestStatus(..))")
    public void logStatusChange(JoinPoint joinPoint) {
        log.info("Request status changed: {}", joinPoint.getArgs());
    }
}
