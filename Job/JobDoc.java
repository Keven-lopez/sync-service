package com.oficinaagua.syncservice.job;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sync_jobs")
public record JobDoc(@Id String id, String estado, int creados, int actualizados,
                     int sinCambios, String error, Instant creadoEn,
                     Instant inicioEn, Instant finEn) {}
