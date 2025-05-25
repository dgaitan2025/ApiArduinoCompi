package com.apiarduino.controller;

import com.apiarduino.model.Instruccion;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/instrucciones")
@CrossOrigin(origins = "*")
public class InstruccionController {

    private final List<Instruccion> instrucciones = new ArrayList<>();
    private boolean enEjecucion = false;
    private boolean UserOnline = false;

    // ✅ SOLO permite cargar si no hay ejecución en curso

@PostMapping("/cargar")
public String cargarDesdeTexto(@RequestBody String texto) {
    if (UserOnline) {
        return "instrucciones pendientes por procesar. Intente más tarde.";
    }

    UserOnline = true;
    instrucciones.clear();

    // Limpieza básica
    String[] lineas = texto.split("\\r?\\n");
    StringBuilder sb = new StringBuilder();
    for (String linea : lineas) {
        linea = linea.trim();
        if (linea.isEmpty()) continue;
        if (linea.startsWith("//")) continue;
        if (linea.toUpperCase().startsWith("PROGRAM")) continue;
        if (linea.equalsIgnoreCase("BEGIN") || linea.equalsIgnoreCase("END")) continue;
        sb.append(linea);
    }

    String limpio = sb.toString().replaceAll("\\s+", "");

    // Nuevo patrón
    Pattern pattern = Pattern.compile("(girar\\([^\\)]+\\)(?:\\+[^;]+)*);|([a-zA-Z_]+)\\((-?\\d+)\\);");
    Matcher matcher = pattern.matcher(limpio);

    while (matcher.find()) {
        if (matcher.group(1) != null) {
            // Es un bloque girar(...) + ...
            instrucciones.add(new Instruccion(matcher.group(1), 0));
        } else {
            // Es una instrucción individual
            String accion = matcher.group(2);
            int parametro = Integer.parseInt(matcher.group(3));
            instrucciones.add(new Instruccion(accion, parametro));
        }
    }

    return "✅ Instrucciones cargadas: " + instrucciones.size();
}



    // ✅ Arduino solicita instrucciones — esto activa el bloqueo
    @GetMapping
    public Object obtenerTodasConControl() {
        
        if (enEjecucion) {
            return "⚠️ Ejecución en proceso. Intente más tarde.";
        }

        if (instrucciones.isEmpty()) {
            enEjecucion = true;
            UserOnline = true;
            return "⚠️ No hay instrucciones disponibles."; 
        }

        enEjecucion = true;
        return instrucciones;
    }

    // ✅ Arduino libera el sistema
    @PostMapping("/liberar")
    public String liberar() {
        enEjecucion = false;
        UserOnline = false;
        instrucciones.clear();
        return "✅ Ejecución liberada. Ya se pueden cargar nuevas instrucciones.";
    }
}
