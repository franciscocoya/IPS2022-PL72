package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import business.BusinessException;
import business.InscripcionColegiado.InscripcionColegiado;
import business.util.StringUtils;
import persistence.Colegiado_Inscripcion.Colegiado_Inscripcion;
import persistence.InscripcionColegiado.InscripcionColegiadoDto;
import persistence.InscripcionColegiado.listaEsperaInscripcionCurso.ListaEsperaInscripcionCursoDto;
import persistence.colegiado.ColegiadoDto;
import persistence.curso.CursoCRUD;
import persistence.curso.CursoDto;
import persistence.curso.Precio_Colectivos;
import persistence.curso.profesorado.ProfesorDto;
import persistence.solicitudVisados.SolicitudVisadoDto;

public class DtoAssembler {

	public static final String SEPARADOR_TITULACIONES = ",";
	public static final String SPECIAL_CHARACTERS_REGEX = "[!@#$%&*()^:[-].;_+=|<>?{}/]";

	public static List<ColegiadoDto> toColegiadoList(ResultSet rs) throws SQLException {
		List<ColegiadoDto> colegiados = new ArrayList<>();

		while (rs.next()) {
			colegiados.add(resultSetToColegiadoDto(rs));
		}

		return colegiados;
	}

	public static List<CursoDto> toInscripcionList(ResultSet rs) throws SQLException {
		List<CursoDto> inscripciones = new ArrayList<CursoDto>();
		while (rs.next()) {
			inscripciones.add(resultSetToInscripcionDto(rs));
		}
		return inscripciones;
	}

	/**
	 * 
	 * @see #parseTitulacionesColegiado(String)
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static ColegiadoDto resultSetToColegiadoDto(ResultSet rs) throws SQLException {
		ColegiadoDto c = new ColegiadoDto();

		c.DNI = rs.getString("DNI");
		c.nombre = rs.getString("nombre");
		c.apellidos = rs.getString("apellidos");
		c.poblacion = rs.getString("poblacion");
		c.titulacion = parseTitulacionesColegiado(rs.getString("titulacion"));
		c.telefono = rs.getInt("telefono");
		c.centro = rs.getString("centro");
		c.annio = rs.getInt("ano");
		c.numeroCuenta = rs.getString("numeroCuenta");
		c.fechaSolicitud = LocalDate.parse(rs.getString("fechaSolicitud"));
		c.numeroColegiado = rs.getString("numero");

		c.TipoColectivo = rs.getString("TipoColectivo");

		c.perito = rs.getString("perito");
		c.posicionPerito = rs.getInt("posicionPerito");
		c.visado = rs.getBoolean("visado");
		
		return c;

	}

	/**
	 * Devuelve un listado de titulaciones a partir de la cadena indicada. El
	 * formato de la cadena entrante es el siguiente:
	 * titulacion1,titulacion2,titulacion3 Puede ser que haya algún espacio en
	 * blanco, a tener en cuenta.
	 * 
	 * @since HU. 19061
	 * @param titulaciones Listado de titulaciones en formato cadena separador por
	 *                     comas.
	 * 
	 * @return Listado de titulaciones.
	 */
	public static List<String> parseTitulacionesColegiado(String titulaciones) {
		if (titulaciones == null || titulaciones.isEmpty()) {
			return new ArrayList<>();
		}

		// Nota: Se introduce un filtro para el caso en el que se introduzcan dos comas
		// seguidas
		return Arrays.asList(titulaciones.trim().split(SEPARADOR_TITULACIONES)).stream().filter(t -> !t.isEmpty())
				.map(t -> t.trim().strip()).collect(Collectors.toList());
	}

	/**
	 * Devuelve la lista de colegiados en forma de cadena separados por coma.
	 * 
	 * <code>
	 * Ejemplo: List(c1, c2, c3, c4)
	 * Salida: "c1,c2,c3,c4"
	 * </code>
	 * 
	 * @param titulaciones Lista de titulaciones de un colegiado.
	 * 
	 * @return Cadena con las titulaciones separadas por coma.
	 */
	public static String listaTitulacionesColegiadoToString(List<String> titulaciones) {
		return String.join(SEPARADOR_TITULACIONES, titulaciones).replace("[", "").replace("]", "");
	}

	public static String listaTitulacionesColegiadoToStringForTable(List<String> titulaciones) {
		return "<html>".concat(String.join("<br />", titulaciones).replace("[", "").replace("]", "")).concat("</html>");
	}

	public static List<CursoDto> toCursoList(ResultSet rs) throws SQLException {
		List<CursoDto> cursos = new ArrayList<>();

		while (rs.next()) {
			cursos.add(resultSetToCursoDto(rs));
		}

		return cursos;
	}

	public static CursoDto resultSetToCursoDto(ResultSet rs) throws SQLException {
		CursoDto newCursoDto = new CursoDto();

		newCursoDto.codigoCurso = rs.getInt("IdCurso");
		newCursoDto.titulo = rs.getString("Titulo");
		newCursoDto.isCancelable= rs.getBoolean("Is_Cancelable");
		try {
			newCursoDto.numeroInscritos = InscripcionColegiado.getTotalInscrito(newCursoDto);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		
		if (rs.getString("FechaApertura") != null && !rs.getString("FechaApertura").isEmpty()) {
			newCursoDto.fechaApertura = LocalDate.parse(rs.getString("FechaApertura"));
		}

		if (rs.getString("FechaCierre") != null && !rs.getString("FechaCierre").isEmpty()) {
			newCursoDto.fechaCierre = LocalDate.parse(rs.getString("FechaCierre"));
		}

		if (rs.getString("FechaImpartir") != null && !rs.getString("FechaImpartir").isEmpty()) {
			newCursoDto.fechaInicio = LocalDate.parse(rs.getString("FechaImpartir"));
		}

		newCursoDto.plazasDisponibles = rs.getInt("Plazas");

		// TODO: Cantidad a pagar colegiado
		if (rs.getString("CantidadPagarColectivo") != null) {
			newCursoDto.precio = Precio_Colectivos.StringToPrecio_Colectivos(rs.getString("CantidadPagarColectivo"))
					.getPrecio("Colegiado");
		}

		boolean isCursoAbierto = CursoCRUD.isCursoAbierto(newCursoDto);
		
		newCursoDto.estado = isCursoAbierto ? CursoDto.CURSO_ABIERTO : CursoDto.CURSO_PLANIFICADO;

		return newCursoDto;

	}

	private static CursoDto resultSetToInscripcionDto(ResultSet rs) throws SQLException {
		CursoDto c = new CursoDto();

		c.codigoCurso = rs.getInt("IDCURSO");
		c.titulo = rs.getString("TITULO");
		c.fechaInicio = LocalDate.parse(rs.getString("FECHAIMPARTIR"));
		c.plazasDisponibles = rs.getInt("PLAZAS");
		c.fechaApertura = LocalDate.parse(rs.getString("FECHAAPERTURA"));
		c.fechaCierre = LocalDate.parse(rs.getString("FECHACIERRE"));
		c.CantidadPagarColectivo = rs.getString("CantidadPagarColectivo");

		return c;
	}

	public static ArrayList<Colegiado_Inscripcion> toInscripcionColegiadosList(ResultSet rs) throws SQLException {
		ArrayList<Colegiado_Inscripcion> inscripciones = new ArrayList<Colegiado_Inscripcion>();
		while (rs.next()) {
			inscripciones.add(resultSetToInscripcionColegiadosDto(rs));
		}
		return inscripciones;
	}

	private static Colegiado_Inscripcion resultSetToInscripcionColegiadosDto(ResultSet rs) throws SQLException {
		InscripcionColegiadoDto I = new InscripcionColegiadoDto();
		ColegiadoDto c = new ColegiadoDto();

		c.nombre = rs.getString("nombre");
		c.apellidos = rs.getString("apellidos");
		I.precio = rs.getDouble("CantidadPagar");
		I.estado = rs.getString("ESTADO");
		I.fechaSolicitud = LocalDate.parse(rs.getString("FechaPreInscripcion"));
		I.cantidadPagada = rs.getDouble("CantidadAbonada");

		return new Colegiado_Inscripcion(c, I);

	}

	public static List<ProfesorDto> toProfesorDtoList(ResultSet rs) throws SQLException {
		List<ProfesorDto> profesores = new ArrayList<>();

		while (rs.next()) {
			profesores.add(resultSetToProfesorDto(rs));
		}

		return profesores;
	}

	private static ProfesorDto resultSetToProfesorDto(ResultSet rs) throws SQLException {
		ProfesorDto p = new ProfesorDto();
		p.nombre = rs.getString("nombre");
		p.apellidos = rs.getString("apellidos");
		p.idCurso = rs.getInt("idCurso");

		return p;
	}

	public static InscripcionColegiadoDto resultsetToIncripcionTransferencia(ResultSet rs) throws SQLException {
		InscripcionColegiadoDto d = new InscripcionColegiadoDto();

		int i = 1;
		d.colegiado = new ColegiadoDto();
		d.colegiado.DNI = rs.getString(i++);
		d.colegiado.nombre = rs.getString(i++);
		d.colegiado.apellidos = rs.getString(i++);
		d.cantidadPagada = rs.getDouble(i++);
		String fechaTransferencia = rs.getString(i++);
		d.codigoTransferencia = rs.getString(i++);
		String fechaPreinscripcion = rs.getString(i++);
		if (fechaTransferencia == null) {
			d.fechaTransferencia = null;
			d.fechaPreinscripcion = null;
		} else {
			d.fechaTransferencia = LocalDate.parse(fechaTransferencia);
			d.fechaPreinscripcion = LocalDate.parse(fechaPreinscripcion);
		}
		d.precio = rs.getDouble(i++);
		d.estado = rs.getString(i++);
		d.incidencias = rs.getString(i++);
		d.devolver = rs.getString(i++);
		return d;
	}

	public static InscripcionColegiadoDto resultsetToIncripcionTransferenciaProcesar(ResultSet rs) throws SQLException {
		InscripcionColegiadoDto d = new InscripcionColegiadoDto();
		int i = 1;
		d.colegiado = new ColegiadoDto();
		d.colegiado.DNI = rs.getString(i++);
		d.colegiado.nombre = rs.getString(i++);
		d.colegiado.apellidos = rs.getString(i++);
		d.cantidadPagada = rs.getDouble(i++);
		d.precio = rs.getDouble(i++);
		d.estado = rs.getString(i++);
		d.incidencias = rs.getString(i++);
		d.devolver = rs.getString(i++);
		return d;
	}


	public static InscripcionColegiadoDto resultsetToIncripcion(ResultSet rs) throws SQLException {
		InscripcionColegiadoDto d = new InscripcionColegiadoDto();
		d.colegiado = new ColegiadoDto();
		d.colegiado.DNI = rs.getString("ColegiadoDNI");
		d.colegiado.nombre = rs.getString("nombre");
		d.colegiado.apellidos = rs.getString("apellidos");
		d.curso = new CursoDto();
		d.curso.codigoCurso = rs.getInt("CursoId");
		d.curso.titulo = rs.getString("Titulo");
		d.cantidadPagada = rs.getDouble("CantidadAbonada");
		d.precio = rs.getDouble("cantidadPagar");
		d.fechaCancelacion = LocalDate.now();
		d.estado = rs.getString("Estado");
		d.incidencias = rs.getString("incidencias");
		d.devolver = rs.getString("Devolver");
		d.curso.porcentaje_devolucion = rs.getDouble("Porcentaje_devolucion");
		d.formaDePago = rs.getString("formaDePago");
		return d;
	}

	public static InscripcionColegiadoDto resultsetToIncripcionDni(ResultSet rs) throws SQLException {
		InscripcionColegiadoDto d = new InscripcionColegiadoDto();
		d.colegiado = new ColegiadoDto();
		d.colegiado.DNI = rs.getString("ColegiadoDNI");
		d.curso = new CursoDto();
		d.curso.codigoCurso = rs.getInt("CursoId");
		d.cantidadPagada = rs.getDouble("CantidadAbonada");
		d.precio = rs.getDouble("cantidadPagar");
		d.estado = rs.getString("Estado");
		d.incidencias = rs.getString("incidencias");
		d.devolver = rs.getString("Devolver");
		d.formaDePago = rs.getString("formaDePago");
		
		return d;
	}
	
	/**
	 * 
	 * @since HU. 19733
	 * @param rs           Resultset con el contenido de la lista de espera.
	 * @param anonimizeDni true si se requiere anonimizar el DNI del usuario.
	 * @return
	 * @throws SQLException
	 */
	public static ListaEsperaInscripcionCursoDto toListaEsperaInscripcionCursoDto(ResultSet rs, boolean anonimizeDni)
			throws SQLException {
		ListaEsperaInscripcionCursoDto res = new ListaEsperaInscripcionCursoDto();

		String dniOriginal = rs.getString(ListaEsperaInscripcionCursoDto.DNI_USUARIO);
		res.dniUsuario = anonimizeDni ? StringUtils.anonimizeDni(dniOriginal) : dniOriginal;

		res.nombreUsuario = rs.getString(ListaEsperaInscripcionCursoDto.NOMBRE_USUARIO);
		res.idCurso = rs.getInt(ListaEsperaInscripcionCursoDto.ID_CURSO);
		res.posicionUsuarioLista = rs.getInt(ListaEsperaInscripcionCursoDto.POS_USUARIO);

		return res;

	}

	/**
	 * 
	 * @since HU. 19733
	 * @param rs Resultset con el la lista de espera para el curso.
	 * @return
	 * @throws SQLException
	 */
	public static List<ListaEsperaInscripcionCursoDto> toListaEsperaInscripcionCursoDtoList(ResultSet rs)
			throws SQLException {
		List<ListaEsperaInscripcionCursoDto> res = new ArrayList<>();

		while (rs.next()) {
			res.add(toListaEsperaInscripcionCursoDto(rs, true));
		}

		return res;
	}

	public static List<SolicitudVisadoDto> toSolicitudVisadosList(ResultSet rs) throws SQLException {
		List<SolicitudVisadoDto> res = new ArrayList<>();

		while (rs.next()) {
			res.add(toSolicitudVisadoDto(rs));
		}

		return res;
	}

	private static SolicitudVisadoDto toSolicitudVisadoDto(ResultSet rs) throws SQLException {
		SolicitudVisadoDto s = new SolicitudVisadoDto();
		
		s.dniPerito = rs.getString("dniPerito");
		s.descripcion = rs.getString("descripcion");
		s.estado = rs.getString("estado");
		s.dniVisado = rs.getString("dniVisado");
		
		return s;
	}

}