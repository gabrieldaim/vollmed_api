package med.voll.api.domain.medico;

import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.paciente.DadosCadastroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deveria devolver null quando o unico médico cadastrado não está disponivel na data")
    void escolherMedicoAleatoriaLivreNaDataCenario1() {
        var proximaSegundaAs10 = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .toLocalDate().atTime(10, 0);

        var medico = cadastrarMedico("Medico", "medico@voll.med" , "123456",Especialidade.CARDIOLOGIA);


        var medicoLivre = medicoRepository.escolherMedicoAleatoriaLivreNaData(Especialidade.CARDIOLOGIA,proximaSegundaAs10);

        assertThat(medicoLivre).isEqualTo(medico);
    }

    @Test
    @DisplayName("Deveria devolver o unico médico disponivel na data")
    void escolherMedicoAleatoriaLivreNaDataCenario2() {
        var proximaSegundaAs10 = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .toLocalDate().atTime(10, 0);

        var medico = cadastrarMedico("Medico", "medico@voll.med" , "123456",Especialidade.CARDIOLOGIA);
        var paceinte = cadastrarPaciente("Paciente", "paciente@email.com","00000000000");
        cadastrarConsulta(medico,paceinte,proximaSegundaAs10);


        var medicoLivre = medicoRepository.escolherMedicoAleatoriaLivreNaData(Especialidade.CARDIOLOGIA,proximaSegundaAs10);

        assertThat(medicoLivre).isNull();
    }

    private void cadastrarConsulta(Medico medico, Paciente paciente, LocalDateTime data) {
       entityManager.persist(new Consulta(null, medico, paciente, data));
    }

    private Medico cadastrarMedico(String nome, String email, String crm, Especialidade especialidade) {
        var medico = new Medico(dadosMedico(nome, email, crm, especialidade));
       entityManager.persist(medico);
        return medico;
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var paciente = new Paciente(dadosPaciente(nome, email, cpf));
       entityManager.persist(paciente);
        return paciente;
    }

    private DadosCadastroMedico dadosMedico(String nome, String email, String crm, Especialidade especialidade) {
        return new DadosCadastroMedico(
                nome,
                email,
                "61999999999",
                crm,
                especialidade,
                dadosEndereco()
        );
    }

    private DadosCadastroPaciente dadosPaciente(String nome, String email, String cpf) {
        return new DadosCadastroPaciente(
                nome,
                email,
                "61999999999",
                cpf,
                dadosEndereco()
        );
    }

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco(
                "rua xpto",
                "bairro",
                "00000000",
                "Brasilia",
                "DF",
                null,
                null
        );
    }
}