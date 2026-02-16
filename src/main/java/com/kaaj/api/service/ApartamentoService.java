package com.kaaj.api.service;

import com.kaaj.api.model.Apartamento;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.repository.ApartamentoRepository;
import com.kaaj.api.repository.CondominioRepository;
import com.kaaj.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ApartamentoService {

    private final ApartamentoRepository apartamentoRepository;
    private final CondominioRepository condominioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Apartamento> obtenerApartamentosPorCondominio(Integer condominioId) {
        return apartamentoRepository.findByCondominioId(condominioId);
    }

    @Transactional
    public Apartamento crearApartamento(Apartamento apartamento, Integer condominioId) {
        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));

        apartamento.setCondominio(condominio);
        apartamento.setFechaRegistro(LocalDateTime.now());
        apartamento.setFechaActualizacion(LocalDateTime.now());

        // Validar que no exista otro apartamento con mismo edificio y n??mero
        Optional<Apartamento> existente = apartamentoRepository
                .findByCondominioAndEdificioAndNumero(
                        condominioId,
                        apartamento.getEdificio(),
                        apartamento.getNumero());

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un apartamento con ese edificio y n??mero en este condominio");
        }

        return apartamentoRepository.save(apartamento);
    }

    @Transactional
    public Apartamento actualizarApartamento(Integer id, Map<String, Object> updates) {
        Apartamento apartamento = apartamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "edificio":
                    apartamento.setEdificio((String) value);
                    break;
                case "numero":
                    apartamento.setNumero((String) value);
                    break;
                case "tipo":
                    apartamento.setTipo((String) value);
                    break;
                case "area":
                    apartamento.setArea(new BigDecimal(value.toString()));
                    break;
                case "habitaciones":
                    apartamento.setHabitaciones(Integer.parseInt(value.toString()));
                    break;
                case "banos":
                    apartamento.setBanos(Integer.parseInt(value.toString()));
                    break;
                case "observaciones":
                    apartamento.setObservaciones((String) value);
                    break;
                case "cuotaMantenimientoBase":
                    apartamento.setCuotaMantenimientoBase(new BigDecimal(value.toString()));
                    break;
                case "estaAlquilado":
                    apartamento.setEstaAlquilado(Boolean.parseBoolean(value.toString()));
                    break;
                case "estaHabitado":
                    apartamento.setEstaHabitado(Boolean.parseBoolean(value.toString()));
                    break;
                case "propietarioUsuarioId":
                    if (value != null) {
                        Usuario propietario = usuarioRepository.findById(Integer.parseInt(value.toString()))
                                .orElseThrow(() -> new RuntimeException("Usuario propietario no encontrado"));
                        apartamento.setPropietario(propietario);
                    } else {
                        apartamento.setPropietario(null);
                    }
                    break;
                case "inquilinoUsuarioId":
                    if (value != null) {
                        Usuario inquilino = usuarioRepository.findById(Integer.parseInt(value.toString()))
                                .orElseThrow(() -> new RuntimeException("Usuario inquilino no encontrado"));
                        apartamento.setInquilino(inquilino);
                    } else {
                        apartamento.setInquilino(null);
                    }
                    break;
            }
        });

        apartamento.setFechaActualizacion(LocalDateTime.now());
        return apartamentoRepository.save(apartamento);
    }

    @Transactional
    public void eliminarApartamento(Integer id) {
        Apartamento apartamento = apartamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        // Verificar que no tenga pagos pendientes o documentos asociados
        // (esto se implementar?? m??s adelante)

        apartamentoRepository.delete(apartamento);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasCondominio(Integer condominioId) {
        Long totalApartamentos = apartamentoRepository.countByCondominioId(condominioId);
        Long habitados = apartamentoRepository.countHabitadosByCondominioId(condominioId);
        Long alquilados = apartamentoRepository.countAlquiladosByCondominioId(condominioId);

        return Map.of(
                "totalApartamentos", totalApartamentos,
                "habitados", habitados,
                "alquilados", alquilados,
                "disponibles", totalApartamentos - habitados);
    }

    @Transactional(readOnly = true)
    public Apartamento obtenerApartamentoPorUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuario.getApartamento();
    }
}