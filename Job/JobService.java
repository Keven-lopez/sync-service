package com.oficinaagua.syncservice.job;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final MongoTemplate mongo;

    public JobService(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public String crear() {
        String id = UUID.randomUUID().toString();
        mongo.save(new JobDoc(id, "PENDIENTE", 0, 0, 0, null, Instant.now(), null, null));
        return id;
    }

    public void enProgreso(String id) {
        actualizar(id, new Update().set("estado", "EN_PROGRESO").set("inicioEn", Instant.now()));
    }

    public void completado(String id, int creados, int actualizados, int sinCambios) {
        actualizar(id, new Update().set("estado", "COMPLETADO")
                .set("creados", creados).set("actualizados", actualizados)
                .set("sinCambios", sinCambios).set("finEn", Instant.now()));
    }

    public void fallido(String id, String error) {
        actualizar(id, new Update().set("estado", "FALLIDO")
                .set("error", error).set("finEn", Instant.now()));
    }

    public Optional<JobDoc> obtener(String id) {
        return Optional.ofNullable(mongo.findById(id, JobDoc.class));
    }

    private void actualizar(String id, Update update) {
        mongo.updateFirst(Query.query(Criteria.where("_id").is(id)), update, JobDoc.class);
    }
}
