package business.inscripcion;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import business.BusinessException;
import business.curso.Curso;
import business.util.DateUtils;
import persistence.curso.CursoCRUD;
import persistence.curso.CursoDto;
import persistence.inscripcionCursoFormacion.InscripcionCursoFormacionCRUD;
import persistence.inscripcionCursoFormacion.InscripcionCursoFormacionDto;
import persistence.inscripcionCursoFormacion.InscripcionCursoFormacionCRUD;

/**
 * 
 * @author Francisco Coya
 * @version v1.0.0
 *
 */
public class InscripcionCursoFormativo {

	/**
	 * Realización de la apertura de inscripciones a un curso formativo.
	 * 
	 * @param inscripcionCurso
	 * @param fechaApertura
	 * @param fechaCierre
	 * @param plazasDisponibles
	 * 
	 * @throws BusinessException Si la fecha de inscripcion no es válida.
	 * @throws SQLException
	 */
	public static void abrirCursoFormacion(final CursoDto curso, final LocalDate fechaApertura,
			final LocalDate fechaCierre, final String plazasDisponibles) throws BusinessException, SQLException {

		int plazas = 0;

		try {
			plazas = Integer.parseInt(plazasDisponibles);

		} catch (NumberFormatException e) {
			throw new BusinessException("Por favor, introduzca un número válido para las plazas disponibles");
		}

		if (curso == null || Curso.getSelectedCourse() == null) {
			throw new BusinessException("Por favor, seleccione un curso de la lista.");

			// Fecha inscripciones anterior a fecha actual
		} else if (DateUtils.checkDateIsBefore(fechaApertura, LocalDate.now())
				|| DateUtils.checkDateIsBefore(fechaCierre, LocalDate.now())) {

			throw new BusinessException(
					"Por favor, corriga el periodo de inscripción. La fecha de inscripción ha de ser a partir del día de hoy");

			// Si fecha cierre < fecha apertura
			// ó fecha inicio < fecha apertura
		} else if (DateUtils.checkDateIsBefore(fechaCierre, fechaApertura)
				|| DateUtils.checkDateIsBefore(curso.fechaInicio, fechaApertura)) {

			throw new BusinessException(
					"Por favor, corriga el periodo de inscripción. La fecha de impartición del curso seleccionado está programada para: "
							+ curso.fechaInicio);

			// Si las fechas de apertura y cierre son el mismo dia
		} else if (fechaApertura.isEqual(fechaCierre)) {
			throw new BusinessException(
					"Por favor, corriga el periodo de inscripción. El periodo mínimo de inscripciones ha de ser de un día natural");
		}
		// Si el curso seleccionado ya está abierto
		else if (Curso.isCourseOpened(curso)) {
			throw new BusinessException(
					"Por favor, actualice el listado de cursos en el botón. El curso seleccionado ya tiene abiertas inscripciones.");

			// Si no se ha seleccionado un número de plazas
		} else if (plazas <= 0) {
			throw new BusinessException("Por favor, introduzca un número válido para las plazas disponibles");

		}

		InscripcionCursoFormacionDto inscripcionCurso = new InscripcionCursoFormacionDto();

		inscripcionCurso.curso = curso;
		inscripcionCurso.fechaApertura = fechaApertura;
		inscripcionCurso.fechaCierre = fechaCierre;

		InscripcionCursoFormacionCRUD.addNewInscripcion(inscripcionCurso);

		curso.plazasDisponibles = plazas;
		curso.estado = CursoDto.CURSO_ABIERTO;

		CursoCRUD.abrirCurso(curso);
	}
	
	public static List<CursoDto> getCursosAbiertos(){
		return InscripcionCursoFormacionCRUD.listaCursosAbiertos();
	
	}
	
	public static boolean PlazasLibres(CursoDto curso) throws BusinessException {
		return InscripcionCursoFormacionCRUD.PlazasLibres(curso);
	}

	public static boolean isCursoAbierto(CursoDto cursoSeleccionado) {
		// TODO Auto-generated method stub
		return InscripcionCursoFormacionCRUD.isCursoAbierto(cursoSeleccionado);
	}
	
}
