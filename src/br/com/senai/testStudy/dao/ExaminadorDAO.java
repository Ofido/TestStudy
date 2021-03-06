package br.com.senai.testStudy.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.senai.testStudy.model.Alternativa;
import br.com.senai.testStudy.model.Disciplina;
import br.com.senai.testStudy.model.EscolaCliente;
import br.com.senai.testStudy.model.Examinador;
import br.com.senai.testStudy.model.Materia;
import br.com.senai.testStudy.model.Professor;
import br.com.senai.testStudy.model.QuestaoProva;
import br.com.senai.testStudy.util.MetodosBasicos;

@Repository
public class ExaminadorDAO implements MetodosBasicos<Examinador> {
	private final Connection CONEXAO;
	private static final String ADD = "INSERT INTO examinador"
			+ " (sexo_examinador, email_examinador, foto_examinador,"
			+ " nascimento_examinador, cpf_examinador, rg_examinador,"
			+ " nome_examinador, senha_examinador, disciplina_examinador) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String LISTAR = "SELECT * FROM examinador";
	private static final String BUSCAR = "SELECT * FROM examinador WHERE id_examinador =?";
	private static final String ALTERAR = "UPDATE examinador SET sexo_examinador=?, email_examinador=?,"
			+ "nascimento_examinador=?, cpf_examinador=?, rg_examinador=?, nome_examinador=?"
			+ " WHERE id_examinador=?";
	private static final String EXCLUIR = "DELETE FROM examinador WHERE id_examinador = ?";
	private static final String ALT_FOTO = "UPDATE examinador SET foto_examinador = ? WHERE id_examinador = ?";
	private static final String LISTAR_QUESTOES_PENDENTES = "SELECT questao_prova.*, disciplina.*, materia.* "
			+ "FROM questao_prova, materia, disciplina, examinador WHERE status_questao = "
			+ "'enviado' AND disciplina_questao = examinador.disciplina_examinador"
			+ " AND examinador.disciplina_examinador = ? GROUP BY id_questao";
	private static final String LISTAR_QUESTOES_PENDENTES_ANTIGAS = " SELECT DISTINCT questao_prova.*, disciplina.*, materia.*"
			+ " FROM questao_prova, materia, disciplina, examinador WHERE status_questao = 'Em aberto' AND examinador_responsavel_questao"
			+ " = ? GROUP BY id_questao";
	// COMANDO RESPONS�VEL POR BUSCAR UMA QUEST�O PARA TER O STATUS ALTERADO
	private static final String BUSCAR_QUESTAO_ID = "SELECT questao_prova.*, disciplina.*, materia.*, professor.*, escola_cliente.*"
			+ " FROM questao_prova, materia, professor, escola_cliente, disciplina WHERE questao_prova.disponibilidade_questao"
			+ " = 'disp' AND status_questao = 'enviado' AND questao_prova.disciplina_questao ="
			+ " disciplina.id_disciplina AND questao_prova.materia_questao = materia.id_materia AND questao_prova.id_questao = ? "
			+ "AND professor.id_escola_cliente = escola_cliente.id_escola_cliente group by id_questao";
	// COMANDO RESPONS�VEL POR BUSCAR UMA QUEST�O ANTIGA PARA TER O STATUS ALTERADO
	private static final String BUSCAR_QUESTAO_ANTIGA_ID = "SELECT questao_prova.*, disciplina.*, materia.*"
			+ " FROM questao_prova, materia, disciplina WHERE questao_prova.disponibilidade_questao"
			+ " = 'disp' AND status_questao = 'Em aberto' AND questao_prova.disciplina_questao ="
			+ " disciplina.id_disciplina AND questao_prova.materia_questao = materia.id_materia AND questao_prova.id_questao = ?";
	// COMANDO RESPONS�VEL POR BUSCAR AS ALTERNATIVAS DA QUEST�O PARA TER O
	// STATUS ALTERADO
	private static final String BUSCAR_ALT_ID = "select * from alternativa, questao_prova, disciplina"
			+ ", materia WHERE questao_prova.id_questao = alternativa.id_questao"
			+ " AND alternativa.id_questao = ? AND questao_prova.disciplina_questao = "
			+ "disciplina.id_disciplina AND materia.id_materia = questao_prova.materia_questao";
	private static final String LISTAR_DISC_PADRAO = "SELECT * FROM disciplina where padrao_disciplina = 'padrao'";
	private static final String ALTERAR_STATUS = "UPDATE questao_prova SET status_questao = ?, examinador_responsavel_questao = ? WHERE id_questao = ?";
	private static final String LOGIN = "SELECT * FROM examinador, disciplina, escola_cliente WHERE senha_examinador = ? AND email_examinador = ? AND "
			+ "examinador.disciplina_examinador = disciplina.id_disciplina AND disciplina.escola_disciplina = escola_cliente.id_escola_cliente";

	@Autowired
	public ExaminadorDAO(DataSource dataSource) {
		try {
			this.CONEXAO = dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// METODO DE LOGIN
	public Examinador existeExaminador(Examinador exam) {
		if (exam == null) {
			throw new IllegalArgumentException(
					"N�O EXISTE ESSE EXAMINADOR, N�O FOI POSSIVEL FAZER LOGIN");
		}
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(LOGIN);
			stmt.setString(1, exam.getSenha());
			stmt.setString(2, exam.getEmail());

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				EscolaCliente escola = new EscolaCliente();
				escola.setCnpjEmp(rs.getString("cnpj_emp"));
				escola.setEmailEmp(rs.getString("email_emp"));
				escola.setIdEmp(rs.getInt("id_escola_cliente"));
				escola.setNomeEmp(rs.getString("nome_emp"));
				escola.setNomeEmpresarialEmp(rs.getString("razao_social_emp"));
				escola.setTelefoneEmp(rs.getString("telefone_emp"));
				
				Disciplina d = new Disciplina();
				d.setEscola(escola);
				d.setIdDisciplina(rs.getInt("id_disciplina"));
				d.setNomeDisciplina(rs.getString("nome_disciplina"));
				d.setPadraoDisciplina(rs.getString("padrao_disciplina"));
				
				exam = new Examinador();
				exam.setIdExaminador(rs.getInt("id_examinador"));
				exam.setSexo(rs.getString("sexo_examinador"));
				exam.setEmail(rs.getString("email_examinador"));
				exam.setFoto(rs.getBytes("foto_examinador"));
				exam.setCpf(rs.getString("cpf_examinador"));
				exam.setRg(rs.getString("rg_examinador"));
				exam.setNascimento(rs.getDate("nascimento_examinador"));
				exam.setNome(rs.getString("nome_examinador"));
				exam.setSenha(rs.getString("senha_examinador"));
				exam.setDisciplinaExaminador(d);
			} else{
				exam = null;
			}
			rs.close();
			stmt.close();
			return exam;
		} catch (SQLException erro) {
			throw new RuntimeException(erro);
		}
	}
	
	

	public void alteraStatus(QuestaoProva qp, Integer id) {
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(ALTERAR_STATUS);
			stmt.setString(1, qp.getStatusQuestao());
			stmt.setInt(2, id);
			stmt.setInt(3, qp.getIdQuestaoProva());
			stmt.execute();
			stmt.close();
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}
	}

	public List<Disciplina> discPadrao() {
		List<Disciplina> disciplinas = new ArrayList<Disciplina>();
		try {
			PreparedStatement stmt = CONEXAO
					.prepareStatement(LISTAR_DISC_PADRAO);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				EscolaCliente escola = new EscolaCliente();
				escola.setIdEmp(1);
				Disciplina d = new Disciplina();
				d.setEscola(escola);
				d.setIdDisciplina(rs.getInt("id_disciplina"));
				d.setNomeDisciplina(rs.getString("nome_disciplina"));
				d.setPadraoDisciplina(rs.getString("padrao_disciplina"));
				disciplinas.add(d);
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return disciplinas;
	}

	public List<Alternativa> buscarAlter(Integer id) {
		List<Alternativa> alternativas = new ArrayList<Alternativa>();
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(BUSCAR_ALT_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Disciplina d = new Disciplina();
				d.setIdDisciplina(rs.getInt("id_disciplina"));
				d.setNomeDisciplina(rs.getString("nome_disciplina"));

				Materia m = new Materia();
				m.setIdMateria(rs.getInt("id_materia"));
				m.setNomeMateria(rs.getString("nome_materia"));
				m.setDisciplina(d);

				QuestaoProva qp = new QuestaoProva();
				qp.setCorpoQuestao(rs.getString("corpo_questao"));
				qp.setDificuldade(rs.getInt("dificuldade"));
				qp.setDisponibilidadeQuestao(rs
						.getString("disponibilidade_questao"));
				qp.setIdQuestaoProva(rs.getInt("id_questao"));
				qp.setMateria(m);
				qp.setStatusQuestao(rs.getString("status_questao"));
				qp.setTipoQuestao(rs.getString("tipo_questao"));
				qp.setTituloQuestao(rs.getString("titulo_questao"));
				qp.setUltimoUsoQuestao(rs.getDate("ultimo_uso_questao"));
				qp.setUsoQuestao(rs.getInt("uso_questao"));
				qp.setVisualizacaoQuestao(rs.getString("visualizacao_questao"));

				Alternativa a = new Alternativa();
				a.setCerta(rs.getString("certa_prova"));
				a.setCorpoAlternativa(rs.getString("corpo_alternativa"));
				a.setIdAlternativa(rs.getInt("id_alternativa"));
				a.setQuestaoAlternativa(qp);

				alternativas.add(a);
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return alternativas;
	}

	public QuestaoProva buscarQuestao(Integer id) {
		QuestaoProva qp = null;
		try {
			PreparedStatement stmt = CONEXAO
					.prepareStatement(BUSCAR_QUESTAO_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				EscolaCliente ec = new EscolaCliente();
				ec.setCnpjEmp(rs.getString("cnpj_emp"));
				ec.setEmailEmp(rs.getString("email_emp"));
				ec.setIdEmp(rs.getInt("id_escola_cliente"));
				ec.setNomeEmp(rs.getString("nome_emp"));
				ec.setNomeEmpresarialEmp(rs.getString("razao_social_emp"));
				ec.setTelefoneEmp(rs.getString("telefone_emp"));
				
				Professor p = new Professor();
				p.setCpf(rs.getString("cpf_professor"));
				p.setEmail(rs.getString("email_professor"));
				p.setEscolaProfessor(ec);
				p.setIdProfessor(rs.getInt("id_professor"));
				p.setNome(rs.getString("nome_professor"));
				p.setRg(rs.getString("rg_professor"));
				p.setSenha(rs.getString("senha_professor"));
				p.setSexo(rs.getString("sexo_professor"));
				
				Disciplina d = new Disciplina();
				d.setIdDisciplina(rs.getInt("id_disciplina"));
				d.setNomeDisciplina(rs.getString("nome_disciplina"));

				Materia m = new Materia();
				m.setIdMateria(rs.getInt("id_materia"));
				m.setNomeMateria(rs.getString("nome_materia"));
				m.setDisciplina(d);

				qp = new QuestaoProva();
				qp.setCorpoQuestao(rs.getString("corpo_questao"));
				qp.setDificuldade(rs.getInt("dificuldade"));
				qp.setDisponibilidadeQuestao(rs
						.getString("disponibilidade_questao"));
				qp.setIdQuestaoProva(rs.getInt("id_questao"));
				qp.setMateria(m);
				qp.setStatusQuestao(rs.getString("status_questao"));
				qp.setTipoQuestao(rs.getString("tipo_questao"));
				qp.setTituloQuestao(rs.getString("titulo_questao"));
				qp.setUltimoUsoQuestao(rs.getDate("ultimo_uso_questao"));
				qp.setUsoQuestao(rs.getInt("uso_questao"));
				qp.setVisualizacaoQuestao(rs.getString("visualizacao_questao"));
				qp.setAutorQuestao(p);
				System.out.println("Dentro do DAO " +qp.getAutorQuestao().getEmail());
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return qp;
	}
	
	public QuestaoProva buscarQuestaoAntigaID(Integer id) {
		QuestaoProva qp = null;
		try {
			PreparedStatement stmt = CONEXAO
					.prepareStatement(BUSCAR_QUESTAO_ANTIGA_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Disciplina d = new Disciplina();
				d.setIdDisciplina(rs.getInt("id_disciplina"));
				d.setNomeDisciplina(rs.getString("nome_disciplina"));

				Materia m = new Materia();
				m.setIdMateria(rs.getInt("id_materia"));
				m.setNomeMateria(rs.getString("nome_materia"));
				m.setDisciplina(d);

				qp = new QuestaoProva();
				qp.setCorpoQuestao(rs.getString("corpo_questao"));
				qp.setDificuldade(rs.getInt("dificuldade"));
				qp.setDisponibilidadeQuestao(rs
						.getString("disponibilidade_questao"));
				qp.setIdQuestaoProva(rs.getInt("id_questao"));
				qp.setMateria(m);
				qp.setStatusQuestao(rs.getString("status_questao"));
				qp.setTipoQuestao(rs.getString("tipo_questao"));
				qp.setTituloQuestao(rs.getString("titulo_questao"));
				qp.setUltimoUsoQuestao(rs.getDate("ultimo_uso_questao"));
				qp.setUsoQuestao(rs.getInt("uso_questao"));
				qp.setVisualizacaoQuestao(rs.getString("visualizacao_questao"));

			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return qp;
	}

	public List<QuestaoProva> listarPendencias(Integer id) {
		List<QuestaoProva> questoes = new ArrayList<QuestaoProva>();
		try {
			PreparedStatement stmt = CONEXAO
					.prepareStatement(LISTAR_QUESTOES_PENDENTES);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Disciplina d = new Disciplina();
				d.setIdDisciplina(rs.getInt("id_disciplina"));
				d.setNomeDisciplina(rs.getString("nome_disciplina"));

				Materia m = new Materia();
				m.setIdMateria(rs.getInt("id_materia"));
				m.setNomeMateria(rs.getString("nome_materia"));
				m.setDisciplina(d);

				QuestaoProva q = new QuestaoProva();
				q.setCorpoQuestao(rs.getString("corpo_questao"));
				q.setDificuldade(rs.getInt("dificuldade"));
				q.setDisponibilidadeQuestao(rs
						.getString("disponibilidade_questao"));
				q.setIdQuestaoProva(rs.getInt("id_questao"));
				q.setMateria(m);
				q.setStatusQuestao(rs.getString("status_questao"));
				q.setTipoQuestao(rs.getString("tipo_questao"));
				q.setTituloQuestao(rs.getString("titulo_questao"));
				q.setUltimoUsoQuestao(rs.getDate("ultimo_uso_questao"));
				q.setUsoQuestao(rs.getInt("uso_questao"));
				q.setVisualizacaoQuestao(rs.getString("visualizacao_questao"));
				questoes.add(q);

			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return questoes;
	}

	@Override
	public void adicionar(Examinador examinador) {
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(ADD);

			stmt.setString(1, examinador.getSexo());
			stmt.setString(2, examinador.getEmail());
			stmt.setBlob(3, (examinador.getFoto() == null) ? null
					: new ByteArrayInputStream(examinador.getFoto()));
			stmt.setDate(4, examinador.getNascimento());
			stmt.setString(5, examinador.getCpf());
			stmt.setString(6, examinador.getRg());
			stmt.setString(7, examinador.getNome());
			stmt.setString(8, examinador.getSenha());
			stmt.setInt(9, examinador.getDisciplinaExaminador()
					.getIdDisciplina());
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void remover(Examinador examinador) {
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(EXCLUIR);
			stmt.setInt(1, examinador.getIdExaminador());
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void alterar(Examinador examinador) {
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(ALTERAR);
			stmt.setString(1, examinador.getSexo());
			stmt.setString(2, examinador.getEmail());
			stmt.setDate(3, examinador.getNascimento());
			stmt.setString(4, examinador.getCpf());
			stmt.setString(5, examinador.getRg());
			stmt.setString(6, examinador.getNome());
			stmt.setInt(7, examinador.getIdExaminador());
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public List<Examinador> listar() {
		List<Examinador> examinadores = new ArrayList<Examinador>();
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(LISTAR);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Examinador exam = new Examinador();
				exam.setIdExaminador(rs.getInt("id_examinador"));
				exam.setSexo(rs.getString("sexo_examinador"));
				exam.setEmail(rs.getString("email_examinador"));
				exam.setFoto(rs.getBytes("foto_examinador"));
				exam.setCpf(rs.getString("cpf_examinador"));
				exam.setRg(rs.getString("rg_examinador"));
				exam.setNascimento(rs.getDate("nascimento_examinador"));
				exam.setNome(rs.getString("nome_examinador"));
				exam.setSenha(rs.getString("senha_examinador"));

				examinadores.add(exam);
			}
			stmt.close();
			rs.close();
			return examinadores;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Examinador buscarID(Integer id) {
		Examinador exam = null;
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(BUSCAR);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				exam = new Examinador();
				exam.setIdExaminador(rs.getInt("id_examinador"));
				exam.setSexo(rs.getString("sexo_examinador"));
				exam.setEmail(rs.getString("email_examinador"));
				exam.setFoto(rs.getBytes("foto_examinador"));
				exam.setCpf(rs.getString("cpf_examinador"));
				exam.setRg(rs.getString("rg_examinador"));
				exam.setNascimento(rs.getDate("nascimento_examinador"));
				exam.setNome(rs.getString("nome_examinador"));
				exam.setSenha(rs.getString("senha_examinador"));
			}
			stmt.close();
			return exam;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// ALTERA FOTO
	public void alterarFoto(Examinador exam) {
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(ALT_FOTO);

			stmt.setBlob(1, (exam.getFoto() == null) ? null
					: new ByteArrayInputStream(exam.getFoto()));
			stmt.setInt(2, exam.getIdExaminador());

			stmt.execute();
			stmt.close();
		} catch (SQLException erro) {
			throw new RuntimeException(erro);
		}
	}

	public List<QuestaoProva> listarPendenciasAntigas(Integer id) {
		List<QuestaoProva> questoes = new ArrayList<QuestaoProva>();
		try {
			PreparedStatement stmt = CONEXAO.prepareStatement(LISTAR_QUESTOES_PENDENTES_ANTIGAS);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Disciplina d = new Disciplina();
				d.setIdDisciplina(rs.getInt("id_disciplina"));
				d.setNomeDisciplina(rs.getString("nome_disciplina"));

				Materia m = new Materia();
				m.setIdMateria(rs.getInt("id_materia"));
				m.setNomeMateria(rs.getString("nome_materia"));
				m.setDisciplina(d);

				QuestaoProva q = new QuestaoProva();
				q.setCorpoQuestao(rs.getString("corpo_questao"));
				q.setDificuldade(rs.getInt("dificuldade"));
				q.setDisponibilidadeQuestao(rs
						.getString("disponibilidade_questao"));
				q.setIdQuestaoProva(rs.getInt("id_questao"));
				q.setMateria(m);
				q.setStatusQuestao(rs.getString("status_questao"));
				q.setTipoQuestao(rs.getString("tipo_questao"));
				q.setTituloQuestao(rs.getString("titulo_questao"));
				q.setUltimoUsoQuestao(rs.getDate("ultimo_uso_questao"));
				q.setUsoQuestao(rs.getInt("uso_questao"));
				q.setVisualizacaoQuestao(rs.getString("visualizacao_questao"));
				questoes.add(q);
			}
			stmt.close();
			rs.close();
			return questoes;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
