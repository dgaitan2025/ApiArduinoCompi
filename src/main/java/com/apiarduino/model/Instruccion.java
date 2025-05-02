package com.apiarduino.model;

public class Instruccion {
    private String accion;
    private int parametro;

    public Instruccion() {}

    public Instruccion(String accion, int parametro) {
        this.accion = accion;
        this.parametro = parametro;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public int getParametro() {
        return parametro;
    }

    public void setParametro(int parametro) {
        this.parametro = parametro;
    }
}
