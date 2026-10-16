package com.oficinaagua.syncservice.sync;

import com.oficinaagua.syncservice.domain.ResumenCliente;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ResumenMysqlReader {

    private static final String SQL = """
        SELECT c.id, c.codigo, c.nombre, c.estado,
               (c.deleted_at IS NOT NULL) AS eliminado,
               (SELECT COUNT(*) FROM contadores k
                 WHERE k.cliente_id = c.id AND k.deleted_at IS NULL) AS contadores,
               COALESCE((SELECT SUM(b.monto) FROM boletas b
                 WHERE b.cliente_id = c.id AND b.anulada_en IS NULL), 0)
               - COALESCE((SELECT SUM(p.monto) FROM pagos p
                 JOIN boletas b ON b.id = p.boleta_id
                 WHERE b.cliente_id = c.id AND b.anulada_en IS NULL
                   AND p.revertido_en IS NULL), 0) AS saldo
        FROM clientes c
        """;

    private final JdbcTemplate jdbc;

    public ResumenMysqlReader(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ResumenCliente> leerResumenes() {
        return jdbc.query(SQL, (rs, i) -> new ResumenCliente(
                rs.getLong("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("estado"),
                rs.getBoolean("eliminado"),
                rs.getInt("contadores"),
                rs.getBigDecimal("saldo")));
    }
}
