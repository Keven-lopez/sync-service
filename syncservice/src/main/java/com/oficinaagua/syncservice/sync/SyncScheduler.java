package com.oficinaagua.syncservice.sync;

import com.oficinaagua.syncservice.job.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(SyncScheduler.class);

    private final JobService jobs;
    private final SyncWorker worker;

    public SyncScheduler(JobService jobs, SyncWorker worker) {
        this.jobs = jobs;
        this.worker = worker;
    }

    /**
     * Lanza la sincronización de forma automática. Usa el mismo flujo que el
     * endpoint POST, así que cada corrida queda registrada en sync_jobs.
     * La hora se interpreta en zona de Guatemala, no en la del contenedor (UTC).
     */
    @Scheduled(cron = "${sync.cron:0 0 2 * * *}", zone = "America/Guatemala")
    public void programada() {
        String jobId = jobs.crear();
        log.info("Sincronización programada iniciada (job {})", jobId);
        worker.ejecutar(jobId); // @Async: este método vuelve de inmediato
    }
}