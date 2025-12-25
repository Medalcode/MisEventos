package com.example.miseventos.models;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitarios para el modelo Evento
 */
public class EventoTest {

    private Evento evento;

    @Before
    public void setUp() {
        // Crear un evento de prueba antes de cada test
        evento = new Evento(
                1,
                "Reunión Importante",
                "2025-12-25",
                "Discutir proyecto X",
                "Oficina Central",
                "Alta",
                100
        );
    }

    @Test
    public void testConstructor_WithId() {
        assertEquals(1, evento.getId());
        assertEquals("Reunión Importante", evento.getTitulo());
        assertEquals("2025-12-25", evento.getFecha());
        assertEquals("Discutir proyecto X", evento.getObservacion());
        assertEquals("Oficina Central", evento.getLugar());
        assertEquals("Alta", evento.getImportancia());
        assertEquals(100, evento.getUsuarioId());
    }

    @Test
    public void testConstructor_WithoutId() {
        Evento nuevoEvento = new Evento(
                "Nueva Reunión",
                "2025-12-26",
                "Test",
                "Online",
                "Media",
                101
        );

        assertEquals(0, nuevoEvento.getId()); // El ID debe ser 0 por defecto
        assertEquals("Nueva Reunión", nuevoEvento.getTitulo());
        assertEquals(101, nuevoEvento.getUsuarioId());
    }

    @Test
    public void testSetters() {
        evento.setId(2);
        evento.setTitulo("Título Modificado");
        evento.setFecha("2026-01-01");
        evento.setObservacion("Nueva observación");
        evento.setLugar("Nuevo lugar");
        evento.setImportancia("Baja");
        evento.setUsuarioId(200);

        assertEquals(2, evento.getId());
        assertEquals("Título Modificado", evento.getTitulo());
        assertEquals("2026-01-01", evento.getFecha());
        assertEquals("Nueva observación", evento.getObservacion());
        assertEquals("Nuevo lugar", evento.getLugar());
        assertEquals("Baja", evento.getImportancia());
        assertEquals(200, evento.getUsuarioId());
    }

    @Test
    public void testToString_ContainsKeyInfo() {
        String str = evento.toString();
        
        assertTrue("toString debe contener el ID", str.contains("id=1"));
        assertTrue("toString debe contener el título", str.contains("Reunión Importante"));
        assertTrue("toString debe contener la fecha", str.contains("2025-12-25"));
        assertTrue("toString debe contener la importancia", str.contains("Alta"));
    }

    @Test
    public void testEvento_WithNullObservacion() {
        Evento eventoSinObs = new Evento(
                1,
                "Test",
                "2025-12-25",
                null,
                "Lugar",
                "Baja",
                100
        );

        assertNull("La observación puede ser nula", eventoSinObs.getObservacion());
    }

    @Test
    public void testEvento_WithEmptyObservacion() {
        Evento eventoObsVacia = new Evento(
                1,
                "Test",
                "2025-12-25",
                "",
                "Lugar",
                "Baja",
                100
        );

        assertEquals("", eventoObsVacia.getObservacion());
        assertTrue("Observación vacía debe ser cadena vacía", 
                   eventoObsVacia.getObservacion().isEmpty());
    }
}
