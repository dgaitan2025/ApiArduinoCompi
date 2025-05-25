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

    texto = texto.replaceAll("\\s+", ""); // limpia espacios

    Pattern bloquePattern = Pattern.compile("([^;]+);");
    Matcher bloqueMatcher = bloquePattern.matcher(texto);

    Pattern instruccionPattern = Pattern.compile("([a-zA-Z_]+)\\((-?\\d+)\\)");

    while (bloqueMatcher.find()) {
        String bloque = bloqueMatcher.group(1);

        if (bloque.contains("girar(")) {
            // Guarda el bloque completo como una instrucción compuesta
            instrucciones.add(new Instruccion(bloque, 0));
        } else {
            // Extrae instrucciones individuales
            Matcher matcher = instruccionPattern.matcher(bloque);
            while (matcher.find()) {
                String accion = matcher.group(1);
                int parametro = Integer.parseInt(matcher.group(2));
                instrucciones.add(new Instruccion(accion, parametro));
            }
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
