package com.oficinaagua.syncservice.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;

public record ResumenCliente(long id, String codigo, String nombre, String estado,
                             boolean eliminado, int contadores, BigDecimal saldo) {

    /** Hash del contenido: si cambia cualquier campo, cambia el hash. */
    public String hash() {
        String canonico = String.join("|",
                String.valueOf(id),
                Objects.toString(codigo, ""),
                Objects.toString(nombre, ""),
                Objects.toString(estado, ""),
                String.valueOf(eliminado),
                String.valueOf(contadores),
                saldo.setScale(2, RoundingMode.HALF_UP).toPlainString());
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(canonico.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
