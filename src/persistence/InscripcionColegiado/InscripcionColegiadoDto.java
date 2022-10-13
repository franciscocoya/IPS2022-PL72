package persistence.InscripcionColegiado;

import java.time.LocalDate;

import persistence.colegiado.ColegiadoDto;
import persistence.inscripcionCursoFormacion.InscripcionCursoFormacionDto;

public class InscripcionColegiadoDto {
	public ColegiadoDto colegiado;
	public InscripcionCursoFormacionDto inscripcion;
	public LocalDate fechaSolicitud;
	public double cantidadPagar;
	public String estado; // PAGADO (con tarjeta) o PENDIENTE (transferencia)
	public String formaDePago; // TARJETA O TRANSFERENCIA 
}
