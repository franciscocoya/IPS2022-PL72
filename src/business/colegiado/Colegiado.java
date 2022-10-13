
package business.colegiado;

import business.BusinessException;
import business.util.Argument;
import persistence.colegiado.ColegiadoCrud;
import persistence.colegiado.ColegiadoDto;

public class Colegiado {

	/**
	 * Añade un nuevo colegiado al sistema con los datos del dto
	 * 
	 * @param colegiado que contiene la información
	 * @return colegiado con todos sus datos
	 * 
	 * @throws IllegalArgumentException cuando es null or cualquiera de sus campos
	 * es null o vacío (strings) o el telefono no tiene 9 números o es negativo, o 
	 * titulacion si no es 0, 1 o 2, o numero de tarjeta si es negativo o no tiene 16 
	 * números, año debe ser > 0, y dni tenga 9 caracteres BusinessException cuando 
	 * ya existe ese colegiado (mismo dni)
	 */
	public static ColegiadoDto addColegiado(ColegiadoDto colegiado) throws BusinessException {
		comprobarArgumentos(colegiado);
		comprobarDni(colegiado);
		return ColegiadoCrud.addColegiado(colegiado);
	}
	
	/**
	 * Busca los colegiados de un año
	 * 
	 * @param año, debe ser mayor que 0
	 * @return lista de los colegiados del sistema por año
	 * 
	 * no @throws BusinessException y lanza una IllegalArgumentException si
	 * el DNI es vacío, null o no tiene 9 caracteres
	 */
	public static ColegiadoDto findColegiadoPorDni(String dni) throws BusinessException {
		comprobarArgumentos(dni);

		return ColegiadoCrud.findColegiadoDni(dni);
	}

	
	private static void comprobarDni(ColegiadoDto colegiado) throws BusinessException {
		checkDniSinRepetir(colegiado.DNI);
	}
	
	private static void checkDniSinRepetir(String dni) throws BusinessException {
		if (ColegiadoCrud.findColegiadoDni(dni) != null) {
			throw new BusinessException("No se puede añadir colegiados con el mismo dni");
		}
	}

	private static void comprobarArgumentos(ColegiadoDto colegiado) {
		Argument.isNotNull(colegiado);

		Argument.isNotNull(colegiado.DNI);
		Argument.isNotEmpty(colegiado.DNI);
		Argument.longitudNueve(colegiado.DNI);

		Argument.isNotNull(colegiado.nombre);
		Argument.isNotEmpty(colegiado.nombre);

		Argument.isNotNull(colegiado.apellidos);
		Argument.isNotEmpty(colegiado.apellidos);

		Argument.isNotNull(colegiado.poblacion);
		Argument.isNotEmpty(colegiado.poblacion);

		Argument.isNotNull(colegiado.centro);
		Argument.isNotEmpty(colegiado.centro);

		Argument.is012(colegiado.titulacion);

		Argument.isPositive(colegiado.annio);

		Argument.isPositive(colegiado.numeroTarjeta);

		Argument.isPositive(colegiado.telefono);

		Argument.longitudNueve(colegiado.telefono);

		Argument.menorQueMax(colegiado.annio);

		Argument.longitudCinco(colegiado.numeroTarjeta);
	}
	
	private static void comprobarArgumentos(String dni) {
		Argument.isNotEmpty(dni);
		Argument.longitudNueve(dni);
	}
	
	

	

}
