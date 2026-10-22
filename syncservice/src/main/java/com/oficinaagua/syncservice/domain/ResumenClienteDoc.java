package com.oficinaagua.syncservice.domain;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("resumen_clientes")
public record ResumenClienteDoc(@Id Long id, String codigo, String nombre, String estado,
                                boolean eliminado, int contadores, BigDecimal saldo,
                                String hash, Instant sincronizadoEn) {}
