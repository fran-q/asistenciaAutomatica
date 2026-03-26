package com.appasistencia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.appasistencia.models.*;
import com.appasistencia.repositories.*;
import org.springframework.security.crypto.password.PasswordEncoder;

// Punto de entrada de la aplicacion Spring Boot
@SpringBootApplication
public class AsistenciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsistenciaApplication.class, args);
	}

	// =====================================================================
	// CARGA DE DATOS DE PRUEBA
	// Para activar: descomentar @Bean y el metodo cargarDatosPrueba()
	// Para desactivar: volver a comentar
	// Contraseña de todos los usuarios: Test1234!
	// =====================================================================

	 @Bean
	 CommandLineRunner cargarDatosPrueba(
	 		InstitucionRepository institucionRepo,
	 		UsuarioRepository usuarioRepo,
	 		UsuarioProfesorRepository profesorRepo,
	 		UsuarioAlumnoRepository alumnoRepo,
	 		CarreraRepository carreraRepo,
	 		MateriaRepository materiaRepo,
	 		CursoRepository cursoRepo,
	 		CursoMateriaRepository cursoMateriaRepo,
	 		AsignacionRepository asignacionRepo,
	 		PasswordEncoder passwordEncoder
	 ) {
	 	return args -> {
	 		// Evitar doble carga si ya hay datos
	 		if (institucionRepo.count() > 0) {
	 			System.out.println(">>> Datos de prueba ya existen, saltando carga.");
	 			return;
	 		}
	 		System.out.println(">>> Cargando datos de prueba...");

	 		String pass = passwordEncoder.encode("Test1234!");

	 		// === INSTITUCION ===
	 		Institucion inst = institucionRepo.save(new Institucion(
	 			"Instituto Tecnologico Regional", "Av. San Martin 1500", "1145678901", "contacto@itr.edu.ar"
	 		));

	 		// === 3 ADMINISTRADORES ===
	 		String[][] admins = {
	 			{"Carlos", "Mendez", "admin1@test.com", "1140001001", "30000001", "Calle Falsa 100"},
	 			{"Laura", "Gutierrez", "admin2@test.com", "1140001002", "30000002", "Calle Falsa 101"},
	 			{"Miguel", "Torres", "admin3@test.com", "1140001003", "30000003", "Calle Falsa 102"}
	 		};
	 		Genero[] gAdmin = {Genero.MASCULINO, Genero.FEMENINO, Genero.MASCULINO};
	 		for (int i = 0; i < 3; i++) {
	 			Usuario u = new Usuario(admins[i][0], admins[i][1], admins[i][2], admins[i][3],
	 				admins[i][5], TipoDocumento.DNI, admins[i][4], gAdmin[i], pass, Rol.ADMIN);
	 			u.setInstitucion(inst);
	 			usuarioRepo.save(u);
	 		}

	 		// === 6 PROFESORES (Usuario + perfil UsuarioProfesor) ===
	 		String[][] profs = {
	 			{"Roberto", "Sanchez", "prof1@test.com", "1140002001", "40000001", "LP-001", "Ing. en Sistemas", "TITULAR"},
	 			{"Ana", "Martinez", "prof2@test.com", "1140002002", "40000002", "LP-002", "Lic. en Matematica", "ADJUNTO"},
	 			{"Diego", "Fernandez", "prof3@test.com", "1140002003", "40000003", "LP-003", "Dr. en Fisica", "TITULAR"},
	 			{"Silvia", "Lopez", "prof4@test.com", "1140002004", "40000004", "LP-004", "Lic. en Quimica", "SUPLENTE"},
	 			{"Marcelo", "Diaz", "prof5@test.com", "1140002005", "40000005", "LP-005", "Ing. Electronico", "ADJUNTO"},
	 			{"Patricia", "Romero", "prof6@test.com", "1140002006", "40000006", "LP-006", "Lic. en Letras", "INTERINO"}
	 		};
	 		Genero[] gProf = {Genero.MASCULINO, Genero.FEMENINO, Genero.MASCULINO, Genero.FEMENINO, Genero.MASCULINO, Genero.FEMENINO};
	 		UsuarioProfesor[] profesores = new UsuarioProfesor[6];
	 		for (int i = 0; i < 6; i++) {
	 			Usuario u = new Usuario(profs[i][0], profs[i][1], profs[i][2], profs[i][3],
	 				"Direccion Prof " + (i+1), TipoDocumento.DNI, profs[i][4], gProf[i], pass, Rol.PROFESOR);
	 			u.setInstitucion(inst);
	 			u = usuarioRepo.save(u);
	 			profesores[i] = profesorRepo.save(new UsuarioProfesor(
	 				u, profs[i][5], profs[i][6], CategoriaProfesor.valueOf(profs[i][7])
	 			));
	 		}

	 		// === 12 ALUMNOS (Usuario + perfil UsuarioAlumno) ===
	 		String[][] alums = {
	 			{"Juan", "Perez", "alum01@test.com", "1140003001", "50000001", "LA-001"},
	 			{"Maria", "Gonzalez", "alum02@test.com", "1140003002", "50000002", "LA-002"},
	 			{"Lucas", "Rodriguez", "alum03@test.com", "1140003003", "50000003", "LA-003"},
	 			{"Camila", "Garcia", "alum04@test.com", "1140003004", "50000004", "LA-004"},
	 			{"Tomas", "Herrera", "alum05@test.com", "1140003005", "50000005", "LA-005"},
	 			{"Valentina", "Molina", "alum06@test.com", "1140003006", "50000006", "LA-006"},
	 			{"Nicolas", "Castro", "alum07@test.com", "1140003007", "50000007", "LA-007"},
	 			{"Sofia", "Alvarez", "alum08@test.com", "1140003008", "50000008", "LA-008"},
	 			{"Mateo", "Acosta", "alum09@test.com", "1140003009", "50000009", "LA-009"},
	 			{"Lucia", "Ruiz", "alum10@test.com", "1140003010", "50000010", "LA-010"},
	 			{"Franco", "Medina", "alum11@test.com", "1140003011", "50000011", "LA-011"},
	 			{"Martina", "Flores", "alum12@test.com", "1140003012", "50000012", "LA-012"}
	 		};
	 		double[] promedios = {7.5, 8.2, 6.8, 9.0, 7.1, 8.5, 6.3, 7.9, 8.8, 7.0, 6.5, 9.2};
	 		Genero[] gAlum = {Genero.MASCULINO, Genero.FEMENINO, Genero.MASCULINO, Genero.FEMENINO,
	 			Genero.MASCULINO, Genero.FEMENINO, Genero.MASCULINO, Genero.FEMENINO,
	 			Genero.MASCULINO, Genero.FEMENINO, Genero.MASCULINO, Genero.FEMENINO};
	 		for (int i = 0; i < 12; i++) {
	 			Usuario u = new Usuario(alums[i][0], alums[i][1], alums[i][2], alums[i][3],
	 				"Direccion Alum " + (i+1), TipoDocumento.DNI, alums[i][4], gAlum[i], pass, Rol.ALUMNO);
	 			u.setInstitucion(inst);
	 			u = usuarioRepo.save(u);
	 			alumnoRepo.save(new UsuarioAlumno(u, alums[i][5], promedios[i]));
	 		}

	 		// === 6 CARRERAS ===
	 		String[][] cars = {
	 			{"Ingenieria en Sistemas", "Formacion en desarrollo de software y sistemas", "5", "Ingeniero en Sistemas"},
	 			{"Licenciatura en Administracion", "Gestion empresarial y organizacional", "4", "Licenciado en Administracion"},
	 			{"Tecnicatura en Redes", "Infraestructura y conectividad de redes", "3", "Tecnico en Redes"},
	 			{"Ingenieria Electronica", "Diseno de circuitos y sistemas electronicos", "5", "Ingeniero Electronico"},
	 			{"Licenciatura en Matematica", "Formacion en matematica pura y aplicada", "4", "Licenciado en Matematica"},
	 			{"Tecnicatura en Multimedia", "Produccion de contenidos digitales y multimedia", "3", "Tecnico en Multimedia"}
	 		};
	 		Carrera[] carreras = new Carrera[6];
	 		for (int i = 0; i < 6; i++) {
	 			carreras[i] = carreraRepo.save(new Carrera(
	 				cars[i][0], cars[i][1], Integer.parseInt(cars[i][2]), cars[i][3], inst
	 			));
	 		}

	 		// === 90 MATERIAS (15 por carrera, 5 por anio) ===
	 		String[][] mats = {
	 			// Carrera 0: Ing. Sistemas
	 			{"Programacion I", "Matematica I", "Fisica I", "Algebra", "Introduccion a Sistemas",
	 			 "Programacion II", "Matematica II", "Base de Datos I", "Arquitectura de Computadoras", "Ingles Tecnico I",
	 			 "Programacion III", "Base de Datos II", "Redes I", "Ingenieria de Software I", "Sistemas Operativos"},
	 			// Carrera 1: Lic. Administracion
	 			{"Intro a la Administracion", "Contabilidad I", "Economia I", "Matematica Financiera", "Derecho Comercial",
	 			 "Administracion de Empresas", "Contabilidad II", "Economia II", "Marketing I", "Recursos Humanos I",
	 			 "Gestion Estrategica", "Finanzas", "Marketing II", "Comercio Internacional", "Direccion de Proyectos"},
	 			// Carrera 2: Tec. Redes
	 			{"Fundamentos de Redes", "Sistemas Operativos I", "Programacion Basica", "Matematica Discreta", "Electronica Basica",
	 			 "Redes LAN", "Sistemas Operativos II", "Seguridad Informatica I", "Cableado Estructurado", "Protocolos de Red",
	 			 "Redes WAN", "Seguridad Informatica II", "Admin. de Servidores", "Cloud Computing", "Proyecto Final Redes"},
	 			// Carrera 3: Ing. Electronica
	 			{"Analisis Matematico I", "Fisica General I", "Quimica General", "Dibujo Tecnico", "Intro a la Electronica",
	 			 "Analisis Matematico II", "Fisica General II", "Circuitos Electricos", "Electronica Analogica", "Senales y Sistemas",
	 			 "Electronica Digital", "Microprocesadores", "Control Automatico", "Telecomunicaciones", "Proyecto Integrador"},
	 			// Carrera 4: Lic. Matematica
	 			{"Analisis I", "Algebra Lineal", "Geometria Analitica", "Logica Matematica", "Calculo Numerico I",
	 			 "Analisis II", "Algebra II", "Probabilidad", "Estadistica I", "Calculo Numerico II",
	 			 "Analisis III", "Topologia", "Estadistica II", "Ecuaciones Diferenciales", "Investigacion Operativa"},
	 			// Carrera 5: Tec. Multimedia
	 			{"Diseno Grafico I", "Fotografia Digital", "Intro a Programacion Web", "Comunicacion Visual", "Historia del Arte Digital",
	 			 "Diseno Grafico II", "Edicion de Video", "Desarrollo Web I", "Animacion 2D", "Produccion de Audio",
	 			 "Diseno UX/UI", "Desarrollo Web II", "Animacion 3D", "Motion Graphics", "Proyecto Final Multimedia"}
	 		};
	 		int[] horas = {6, 6, 4, 4, 4, 6, 6, 4, 4, 4, 6, 4, 4, 6, 4};
	 		Materia[][] materias = new Materia[6][15];
	 		for (int c = 0; c < 6; c++) {
	 			for (int m = 0; m < 15; m++) {
	 				materias[c][m] = materiaRepo.save(new Materia(
	 					mats[c][m], "Contenidos de " + mats[c][m], horas[m], carreras[c]
	 				));
	 			}
	 		}

	 		// === 18 CURSOS (3 por carrera: 1er, 2do, 3er anio) ===
	 		Turno[] turnos = {Turno.MANIANA, Turno.TARDE, Turno.NOCHE};
	 		Curso[][] cursos = new Curso[6][3];
	 		for (int c = 0; c < 6; c++) {
	 			for (int a = 0; a < 3; a++) {
	 				cursos[c][a] = cursoRepo.save(new Curso(
	 					carreras[c].getNombre() + " - " + (a+1) + "A",
	 					a + 1, "A", turnos[a], carreras[c], 2026
	 				));
	 			}
	 		}

	 		// === 90 CURSO-MATERIAS (5 materias por curso) ===
	 		// Materias 0-4 van al curso anio 1, 5-9 al anio 2, 10-14 al anio 3
	 		CursoMateria[][] cms = new CursoMateria[6][15];
	 		for (int c = 0; c < 6; c++) {
	 			for (int m = 0; m < 15; m++) {
	 				cms[c][m] = cursoMateriaRepo.save(new CursoMateria(cursos[c][m / 5], materias[c][m]));
	 			}
	 		}

	 		// === 90 ASIGNACIONES (cada profesor cubre las 15 curso-materias de su carrera) ===
	 		for (int p = 0; p < 6; p++) {
	 			for (int m = 0; m < 15; m++) {
	 				asignacionRepo.save(new Asignacion(profesores[p], cms[p][m]));
	 			}
	 		}

	 		System.out.println(">>> Datos de prueba cargados exitosamente!");
	 		System.out.println(">>> 1 Institucion | 3 Admins | 6 Profesores | 12 Alumnos");
	 		System.out.println(">>> 6 Carreras | 90 Materias | 18 Cursos | 90 CursoMaterias | 90 Asignaciones");
	 		System.out.println(">>> Password para todos: Test1234!");
	 	};
	 }
}
