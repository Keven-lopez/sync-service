package com.oficinaagua.syncservice.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import com.oficinaagua.syncservice.domain.ResumenCliente;
import com.oficinaagua.syncservice.domain.ResumenClienteDoc;
import com.oficinaagua.syncservice.job.JobService;
import java.time.Instant;
import java.util.Map;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SyncWorker {

    private final ResumenMysqlReader reader;
    private final ResumenMongoStore store;
    private final JobService jobs;
    private static final Logger log = LoggerFactory.getLogger(SyncWorker.class);

    public SyncWorker(ResumenMysqlReader reader, ResumenMongoStore store, JobService jobs) {
        this.reader = reader;
        this.store = store;
        this.jobs = jobs;
    }

    @Async
    public void ejecutar(String jobId) {
        jobs.enProgreso(jobId);
        try {
            Map<Long, String> hashes = store.cargarHashes();
            int creados = 0, actualizados = 0, sinCambios = 0;

            for (ResumenCliente r : reader.leerResumenes()) {
                String hash = r.hash();
                String previo = hashes.get(r.id());

                if (hash.equals(previo)) {
                    sinCambios++;
                    continue;
                }
                store.guardar(new ResumenClienteDoc(r.id(), r.codigo(), r.nombre(),
                        r.estado(), r.eliminado(), r.contadores(), r.saldo(),
                        hash, Instant.now()));
                if (previo == null) creados++; else actualizados++;
            }
            jobs.completado(jobId, creados, actualizados, sinCambios);
        } catch (Exception e) {
            log.error("Sync fallida (job {})", jobId, e);
            jobs.fallido(jobId, NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        }
    }
}
