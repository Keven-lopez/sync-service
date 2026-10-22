package com.oficinaagua.syncservice.sync;

import com.oficinaagua.syncservice.domain.ResumenClienteDoc;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ResumenMongoStore {

    private final MongoTemplate mongo;

    public ResumenMongoStore(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    /** id -> hash de lo que ya existe en Mongo (una sola consulta). */
    public Map<Long, String> cargarHashes() {
        Query q = new Query();
        q.fields().include("hash");
        Map<Long, String> hashes = new HashMap<>();
        for (Document d : mongo.find(q, Document.class, "resumen_clientes")) {
            hashes.put(((Number) d.get("_id")).longValue(), d.getString("hash"));
        }
        return hashes;
    }

    /** save() con _id existente reemplaza; sin _id existente inserta (upsert). */
    public void guardar(ResumenClienteDoc doc) {
        mongo.save(doc);
    }
}
