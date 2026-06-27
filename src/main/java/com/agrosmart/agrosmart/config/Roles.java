package com.agrosmart.agrosmart.config;

/**
 * Utilidad central para la lógica de perfiles/roles.
 *
 * El proyecto NO usa Spring Security; el control de acceso se hace por sesión
 * (ver {@link SessionAuthInterceptor}). Aquí se centraliza qué rol es admin
 * y cuál es cliente para evitar comparar strings mágicos por todo el código.
 */
public final class Roles {

    /** Rol del cliente final (comprador). Coincide con el valor histórico de la BD. */
    public static final String CLIENTE = "agricultor";

    /** Rol administrador. Coincide con el enum 'administrador_zona' de la BD. */
    public static final String ADMIN = "administrador_zona";

    private Roles() {}

    /** true si el rol corresponde a un administrador del sistema. */
    public static boolean esAdmin(String rol) {
        if (rol == null) return false;
        String r = rol.trim().toLowerCase();
        return r.equals(ADMIN) || r.equals("admin") || r.startsWith("administrador");
    }

    /** true si el rol corresponde a un cliente (agricultor). */
    public static boolean esCliente(String rol) {
        return CLIENTE.equalsIgnoreCase(rol);
    }
}
