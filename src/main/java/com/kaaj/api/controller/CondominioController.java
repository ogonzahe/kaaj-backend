package com.kaaj.api.controller;

import com.kaaj.api.model.Condominio;
import com.kaaj.api.repository.CondominioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/condominios")
public class CondominioController {

    private final CondominioRepository condominioRepository;

    @GetMapping
    public ResponseEntity<List<Condominio>> getAllCondominios() {
        try {
            List<Condominio> condominios = condominioRepository.findAll();
            return ResponseEntity.ok(condominios);
        } catch (Exception e) {
            log.error("Error al obtener todos los condominios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Condominio> getCondominioById(@PathVariable Integer id) {
        try {
            Optional<Condominio> condominio = condominioRepository.findById(id);
            return condominio.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener condominio por id", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCondominio(@RequestBody Condominio condominio) {
        try {
            // Validaciones b?sicas
            if (condominio.getNombre() == null || condominio.getNombre().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El nombre es requerido");
                return ResponseEntity.badRequest().body(error);
            }

            if (condominio.getNumeroCasas() == null || condominio.getNumeroCasas() < 1) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El n?mero de casas debe ser mayor a 0");
                return ResponseEntity.badRequest().body(error);
            }

            condominio.setActivo(true);
            Condominio savedCondominio = condominioRepository.save(condominio);
            return ResponseEntity.ok(savedCondominio);
        } catch (Exception e) {
            log.error("Error al crear condominio", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear condominio");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCondominio(@PathVariable Integer id,
            @RequestBody Condominio condominioDetails) {
        try {
            Optional<Condominio> condominioOpt = condominioRepository.findById(id);
            if (condominioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Condominio condominio = condominioOpt.get();
            condominio.setNombre(condominioDetails.getNombre());
            condominio.setEntidad(condominioDetails.getEntidad());
            condominio.setNumeroCasas(condominioDetails.getNumeroCasas());
            condominio.setResponsable(condominioDetails.getResponsable());
            condominio.setTelefono(condominioDetails.getTelefono());
            condominio.setEmail(condominioDetails.getEmail());
            condominio.setAdministracion(condominioDetails.getAdministracion());
            condominio.setDireccion(condominioDetails.getDireccion());
            condominio.setCiudad(condominioDetails.getCiudad());
            condominio.setCodigoPostal(condominioDetails.getCodigoPostal());
            condominio.setActivo(condominioDetails.getActivo());

            Condominio updated = condominioRepository.save(condominio);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error al actualizar condominio", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar condominio");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestBody Map<String, Boolean> estado) {
        try {
            Optional<Condominio> condominioOpt = condominioRepository.findById(id);
            if (condominioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Condominio condominio = condominioOpt.get();
            condominio.setActivo(estado.get("activo"));
            condominioRepository.save(condominio);

            return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente"));
        } catch (Exception e) {
            log.error("Error al cambiar estado", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cambiar estado");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCondominio(@PathVariable Integer id) {
        try {
            if (!condominioRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            condominioRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Condominio eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar condominio", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar condominio");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // M?todos adicionales
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Condominio>> buscarCondominios(@RequestParam String nombre) {
        try {
            List<Condominio> condominios = condominioRepository.findByNombreContaining(nombre);
            return ResponseEntity.ok(condominios);
        } catch (Exception e) {
            log.error("Error al buscar condominios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Condominio>> getCondominiosActivos() {
        try {
            List<Condominio> condominios = condominioRepository.findByActivo(true);
            return ResponseEntity.ok(condominios);
        } catch (Exception e) {
            log.error("Error al obtener condominios activos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<?> getEstadisticasCondominio(@PathVariable Integer id) {
        try {
            Optional<Condominio> condominioOpt = condominioRepository.findById(id);
            if (condominioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> estadisticas = new HashMap<>();
            Condominio condominio = condominioOpt.get();
            
            estadisticas.put("condominio", condominio);
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error("Error al obtener estadisticas del condominio", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener estad?sticas");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}