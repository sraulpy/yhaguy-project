package com.yhaguy.util.migracion.mariano;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.coreweb.domain.IiD;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CtaCteEmpresa;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoMovimiento;

/**
 * Contiene las funcionalidades utilizadas en la migracion de los ivas"
 * movimientos de clientes desde los archivos del sistema de "Carlos R
 * 
 * @author Miguel Genez
 * 
 */
public class MigracionMovimientosClientes {

	private static String pathMovPenClientesSinEditar = "./WEB-INF/docs/MigracionMariano/CtaCte/MovimientosPendientesClientes/MovimientosPendientesClientes.txt";
	private static String pathMovPenClientesMigrar = "./WEB-INF/docs/MigracionMariano/CtaCte/MovimientosPendientesClientes/MovimientosPendientesClientesMigrar.csv";
	private static String textoCodigo = "COD:";
	private static String textoRuc = "RUC:";
	private static String tipoOperaEfectivo = "CR-EFECTIV";
	private static String separador = ";";
	private static int nroColumnasTotal = 8;

	/**
	 * Formatea y transforma el listado de movimientos de clientes a un archivo
	 * csv.
	 * 
	 * @throws IOException
	 */
	public void listadoMovClieToCsv() throws IOException {

		System.out
				.printf("--> %s\n",
						"INICIANDO CONVERSION LISTADO MOVIMIENTOS PENDIENTES CLIENTES ---> CSV...");

		String linea = "";

		// Listado de movimientos
		FileReader fr = new FileReader(pathMovPenClientesSinEditar);
		BufferedReader entrada = new BufferedReader(fr);

		// Archivo formateado a csv
		FileWriter fw = new FileWriter(pathMovPenClientesMigrar);
		BufferedWriter salida = new BufferedWriter(fw);

		// Corresponde al codigo del cliente en "Sistema Carlos Rivas"
		String codigo = "";

		// Indices utilizados para extraer el codigo del cliente
		int indice1 = 0;
		int indice2 = 0;

		while (entrada.ready()) {

			linea = entrada.readLine();
			linea = linea.trim();

			// Se extrae el codigo del cliente
			if (linea.contains(textoCodigo) == true) {

				indice1 = linea.lastIndexOf(textoCodigo);
				indice2 = linea.lastIndexOf(textoRuc);

				// Para extraer el codigo ubicado entre el String "COD:" y el
				// String "RUC:"
				codigo = linea.substring(indice1 + textoCodigo.length(),
						indice2);
				codigo = codigo.trim();
			}

			// En sintesis si la linea es un row de la tabla
			if (linea.length() > 0 && linea.contains("_") == false
					&& Character.isDigit(linea.charAt(0)) == true
					&& linea.contains("(") == false
					&& linea.contains("TEL:") == false) {

				String s = codigo + " " + linea;

				// Reemplezar los espacios por el separador
				s = s.replaceAll(" +", separador);
				// Formatear los separadores de miles
				s = s.replaceAll(",", "");

				// Si tiene el tipo efectivo, no tiene vencimiento
				// Por eso hay que agregar un separador donde corresponda
				if (s.contains(tipoOperaEfectivo)) {
					String s1 = s.substring(0, s.lastIndexOf(tipoOperaEfectivo)
							+ tipoOperaEfectivo.length());
					String s2 = s.substring(s.lastIndexOf(tipoOperaEfectivo)
							+ tipoOperaEfectivo.length(), s.length());
					s = s1 + separador + s2;
				}

				s = s + separador + "\n";
				salida.write(s);

			}
		}

		entrada.close();
		salida.close();
		System.out
				.printf("--> %s\n",
						"CONVERSION LISTADO MOVIMIENTOS PENDIENTES CLIENTES ---> CSV TERMINAD0.");

	}

	/**
	 * Migra todos los movimientos de clientes desde el archivo csv
	 * correspondiente
	 * 
	 * @throws Exception
	 */
	public void migrarMovimientosClientes() throws Exception {

		System.out.printf("--> %s\n",
				"MIGRANDO MOVIMIENTOS PENDIENTES DE CLIENTES...");

		RegisterDomain rr = RegisterDomain.getInstance();
		Misc m = new Misc();

		// Valores default
		Tipo caracterMovimiento = rr
				.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE);
		Tipo moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		TipoMovimiento tm = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
		SucursalApp sucursal = rr
				.getSucursalAppByNombre(Configuracion.ID_SUCURSALAPP_CENTRAL_MRA);
		Tipo estadoCtaCteActivo = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_ACTIVO);

		String linea = "";

		// Listado de movimientos
		FileReader fr = new FileReader(pathMovPenClientesMigrar);
		BufferedReader entrada = new BufferedReader(fr);

		/*
		 * Utilizado para asociar el movimiento pendiente a la empresa
		 * correspondiente
		 */
		String codigoClienteAnterior = "";
		long idEmpresa = 0;

		while (entrada.ready()) {

			linea = entrada.readLine();
			CtaCteEmpresaMovimiento mov = new CtaCteEmpresaMovimiento();
			String[] row = linea.split(";");

			// En el atributo auxi se guarda el codigo de cliente
			String codigoCliente = "-CL" + row[0] + "-";
			mov.setAuxi(codigoCliente);

			if (codigoCliente.compareTo(codigoClienteAnterior) != 0) {
				idEmpresa = this.getIdEmpresaByCodigoCliente(codigoCliente);
				CtaCteEmpresa ctaCte = rr
						.getCtaCteEmpresaByIdEmpresa(idEmpresa);
				if (ctaCte.getEstadoComoCliente() != null
						&& ctaCte
								.getEstadoComoCliente()
								.getSigla()
								.compareTo(
										Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA) == 0) {
					ctaCte.setEstadoComoCliente(estadoCtaCteActivo);
					rr.saveObject(ctaCte, "MIGRA");
				}
				codigoClienteAnterior = codigoCliente;
			}

			mov.setIdEmpresa(idEmpresa);
			mov.setNroComprobante(row[1]);

			if (row[2].trim().length() > 0) {
				Date fechaEmision = m.ParseFecha(row[2], "dd/MM/yy");
				mov.setFechaEmision(fechaEmision);
			}

			mov.setTipoCaracterMovimiento(caracterMovimiento);
			mov.setTipoMovimiento(tm);
			mov.setMoneda(moneda);

			if (row[4].trim().length() > 0) {
				Date fechaVencimiento = m.ParseFecha(row[4], "dd/MM/yy");
				mov.setFechaVencimiento(fechaVencimiento);
			}

			mov.setImporteOriginal(Double.parseDouble(row[5]));
			mov.setSaldo(Double.parseDouble(row[6]));
			mov.setSucursal(sucursal);
			rr.saveObject(mov, "Migracion");
		}
		entrada.close();
		System.out.printf("--> %s\n",
				"MIGRACION MOVIMIENTOS PENDIENTES CLIENTES TERMINADO.");
	}

	/**
	 * Para obtener el id de la empresa correspondiente al cliente
	 * 
	 * @param codigoCliente
	 * @return
	 * @throws Exception
	 */
	public long getIdEmpresaByCodigoCliente(String codigoCliente)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		IiD empresa = rr.getEmpresaByCodigoCliente(codigoCliente);
		long idEmpresa = 0;
		if (empresa != null) {
			idEmpresa = empresa.getId();
		} else {
			System.err.println("No se encontro empresa para: " + codigoCliente);
		}
		return idEmpresa;
	}

	public static void main(String[] args) throws Exception {
		MigracionMovimientosClientes migra = new MigracionMovimientosClientes();
		// Convierte el listado de movimientos a csv
		migra.listadoMovClieToCsv();
		// Migra los movimientos desde el csv formateado
		migra.migrarMovimientosClientes();
	}
}
