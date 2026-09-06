package com.vetplanet.agendamento.controller;

import com.vetplanet.agendamento.dto.AlterarStatusRequestDto;
import com.vetplanet.agendamento.dto.ConsultaResponseDto;
import com.vetplanet.agendamento.dto.CriarConsultaRequestDto;
import com.vetplanet.agendamento.service.AlterarStatusConsultaService;
import com.vetplanet.agendamento.service.CriarConsultaService;
import com.vetplanet.agendamento.service.ListarConsultasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consultas — o que alimenta a Agenda.
 *
 * <p>Sem {@code @PreAuthorize} de classe: marcar e confirmar atendimento é o
 * trabalho do dia, não administração do sistema. Qualquer sessão válida basta,
 * o que o {@code SecurityConfig} já garante.
 */
@RestController
@RequestMapping("/api/consultas")
@Tag(name = "Agendamento — consultas", description = "Atendimentos marcados; alimenta a Agenda")
public class ConsultaController {

    private final CriarConsultaService criarConsultaService;
    private final ListarConsultasService listarConsultasService;
    private final AlterarStatusConsultaService alterarStatusConsultaService;

    public ConsultaController(
            CriarConsultaService criarConsultaService,
            ListarConsultasService listarConsultasService,
            AlterarStatusConsultaService alterarStatusConsultaService) {
        this.criarConsultaService = criarConsultaService;
        this.listarConsultasService = listarConsultasService;
        this.alterarStatusConsultaService = alterarStatusConsultaService;
    }

    @PostMapping
    @Operation(
            summary = "Marca uma consulta",
            description =
                    "Só o animal é informado — o tutor é resolvido a partir dele e gravado junto, "
                            + "para o histórico continuar apontando o responsável da época se o "
                            + "animal mudar de dono. Sem status, entra como CONFIRMADA: na maior "
                            + "parte das vezes o horário já foi acertado antes de chegar aqui.")
    public ResponseEntity<ConsultaResponseDto> criar(
            @Valid @RequestBody CriarConsultaRequestDto request) {
        ConsultaResponseDto consulta = criarConsultaService.criarConsulta(request);
        return ResponseEntity.created(URI.create("/api/consultas/" + consulta.idConsulta()))
                .body(consulta);
    }

    @GetMapping
    @Operation(
            summary = "Consultas de um intervalo",
            description =
                    "É como a Agenda lê: o mês na grade, o dia no painel. Os nomes de animal e "
                            + "tutor vêm resolvidos numa consulta só, não uma por linha.")
    public List<ConsultaResponseDto> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime de,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ate) {
        return listarConsultasService.listarConsultas(de, ate);
    }

    @GetMapping("/animal/{idAnimal}")
    @Operation(summary = "Histórico de um animal, do mais recente para o mais antigo")
    public List<ConsultaResponseDto> listarDoAnimal(@PathVariable UUID idAnimal) {
        return listarConsultasService.listarConsultasDoAnimal(idAnimal);
    }

    @PatchMapping("/{idConsulta}/status")
    @Operation(
            summary = "Confirma, cancela ou conclui",
            description =
                    "Cancelar é status, não exclusão: a consulta continua no histórico. Um "
                            + "endpoint com o status no corpo, e não quatro rotas com verbo, "
                            + "porque os quatro estados são o mesmo eixo.")
    public ConsultaResponseDto alterarStatus(
            @PathVariable UUID idConsulta, @Valid @RequestBody AlterarStatusRequestDto request) {
        return alterarStatusConsultaService.alterarStatus(idConsulta, request.status());
    }
}
