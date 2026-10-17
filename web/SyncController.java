package com.oficinaagua.syncservice.web;

import com.oficinaagua.syncservice.api.SyncApi;
import com.oficinaagua.syncservice.job.JobDoc;
import com.oficinaagua.syncservice.job.JobService;
import com.oficinaagua.syncservice.model.EstadoJob;
import com.oficinaagua.syncservice.model.JobAccepted;
import com.oficinaagua.syncservice.model.JobStatus;
import com.oficinaagua.syncservice.sync.SyncWorker;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SyncController implements SyncApi {

    private final JobService jobs;
    private final SyncWorker worker;

    public SyncController(JobService jobs, SyncWorker worker) {
        this.jobs = jobs;
        this.worker = worker;
    }

    @Override
    public ResponseEntity<JobAccepted> iniciarSincronizacionResumen() {
        String jobId = jobs.crear();
        worker.ejecutar(jobId); // @Async: devuelve de inmediato
        return ResponseEntity.accepted()
                .location(URI.create("/api/sync/jobs/" + jobId))
                .body(new JobAccepted().jobId(jobId));
    }

    @Override
    public ResponseEntity<JobStatus> obtenerJob(String jobId) {
        return jobs.obtener(jobId)
                .map(this::aDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private JobStatus aDto(JobDoc j) {
        return new JobStatus()
                .jobId(j.id())
                .estado(EstadoJob.fromValue(j.estado()))
                .creados(j.creados())
                .actualizados(j.actualizados())
                .sinCambios(j.sinCambios())
                .error(j.error());
    }
}
