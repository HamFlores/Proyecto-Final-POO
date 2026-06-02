package com.ProyectoFinal.CalculadoraHuellaCarbono.Utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Converter(autoApply = false)
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate date) {
        if (date == null) return null;
        // Guardamos como String "YYYY-MM-DD" para nuevos registros
        return date.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbValue) {
        if (dbValue == null || dbValue.isBlank()) return null;

        try {
            // Intenta parsear como "YYYY-MM-DD" primero (formato correcto)
            return LocalDate.parse(dbValue);
        } catch (Exception e) {
            try {
                // Fallback: el driver guardó la fecha como epoch en milisegundos
                long epochMillis = Long.parseLong(dbValue.trim());
                return Instant.ofEpochMilli(epochMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();
            } catch (Exception e2) {
                throw new IllegalArgumentException(
                        "No se pudo parsear la fecha desde la BD: '" + dbValue + "'", e2);
            }
        }
    }
}
