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
 * Contiene las funcionalidades utilizadas en la migracion de los movimientos de
 * proveedores desde los archivos del sistema de "Carlos Rivas". Para migrar
 * actualizar el archivo de listado de movimientos pendientes de proveedores
 * ubicado en
 * "./WEB-INF/docs/MigracionMariano/CtaCte/MovimientosPendientesProveedores/MovimientosPendientesProveedores.txt"
 * 
 * @author Miguel Genez
 * 
 */
public class MigracionMovimientosProveedores {

	private static String pathMovPenProvSinEditar = "./WEB-INF/docs/MigracionMariano/CtaCte/MovimientosPendientesProveedores/MovimientosPendientesProveedores.txt";
	private static String pathMovPenProvMigrar = "./WEB-INF/docs/MigracionMariano/CtaCte/MovimientosPendientesProveedores/MovimientosPendientesProveedoresMigrar.csv";
	private static String separador = ";";
	private static int nroColumnasTotal = 8;
	private static int nroEspaciosPrimeraColumna = 51;
	private static String textoReferenciaCabecera ="NOMBRE DEL PROVEEDOR";

	/**
	 * Formatea y transforma el listado de movimientos de proveederos a un
	 * archivo csv.
	 * 
	 * @throws IOException
	 */
	public void listadoMovProvToCsv() throws IOException {
		
		System.out.printf("--> %s\n", "INICIANDO CONVERSION LISTA DE MOVIMIENTOS PROVEEDORES ---> CSV...");

		String linea = "";

		// Listado de movimientos
		FileReader fr = new FileReader(pathMovPenProvSinEditar);
		BufferedReader entrada = new BufferedReader(fr);

		// Archivo formateado a csv
		FileWriter fw = new FileWriter(pathMovPenProvMigrar);
		BufferedWriter salida = new BufferedWriter(fw);

		while (entrada.ready()) {

			linea = entrada.readLine();
			linea = linea.trim();

			if (linea.length() > nroEspaciosPrimeraColumna) {

				// Para tratar el nombre de la empresa
				String s1 = linea.substring(0, nroEspaciosPrimeraColumna);

				// Para obtener lo demas y formatearlo
				String s2 = linea.substring(nroEspaciosPrimeraColumna, linea.length());

				// Lo que se va a guardar en el archivo
				String s3 = "";

				// Quitarle los espacios y anhadir separador al final
				s1 = s1.trim() + this.separador;

				// Reemplezar todos los espacios por el separador
				s2 = s2.replaceAll(" +", separador);

				// Formatear los separadores de miles
				s2 = s2.replaceAll(",", "");

				s3 = s1 + s2 + "\n";

				/*
				 *  Si no es la cabecera y tiene en total 8 columnas
				 *  lleva al csv
				 */
				if (s3.split(this.separador).length == nroColumnasTotal
						&& s3.contains(this.textoReferenciaCabecera) == false)
					salida.write(s3);
			}
		}

		entrada.close();
		salida.close();
		System.out.printf("--> %s\n", "CONVERSION LISTA DE MOVIMIENTOS PROVEEDORES ---> CSV TERMINADO");

	}

	/**
	 * Migra todos los movimientos de proveedores desde el archivo csv
	 * correspondiente.
	 * 
	 * @throws Exception
	 */
	public void migrarMovimientosProveedores() throws Exception {
		
		System.out.printf("--> %s\n", "MIGRANDO MOVIMIENTOS PENDIENTES DE PROVEEDORES...");

		RegisterDomain rr = RegisterDomain.getInstance();
		Misc m = new Misc();
		
		//Valores default
		Tipo caracterMovimiento = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR);
		Tipo moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		TipoMovimiento tm = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO);
		SucursalApp sucursal = rr.getSucursalAppByNombre(Configuracion.ID_SUCURSALAPP_CENTRAL_MRA);
		Tipo estadoCtaCteActivo = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_ACTIVO);
		
		String linea = "";

		// Listado de movimientos
		FileReader fr = new FileReader(pathMovPenProvMigrar);
		BufferedReader entrada = new BufferedReader(fr);
		
		//Se utiliza para asociar cada movimiento a su empresa correspondiente
		String codigoProveedorAnterior = "";
		long idEmpresa = 0;
		
		while (entrada.ready()) {

			linea = entrada.readLine();
			CtaCteEmpresaMovimiento mov = new CtaCteEmpresaMovimiento();
			String[] row = linea.split(";");

			// En el atributo auxi se guarda el codigo de proveedor
			String codigoProveedor = "-PR" + row[3] + "-";
			mov.setAuxi(codigoProveedor);

			if (codigoProveedor.compareTo(codigoProveedorAnterior) != 0) {
				idEmpresa = this.getIdEmpresaByCodigoProveedor(codigoProveedor);
				
				CtaCteEmpresa ctaCte = rr
						.getCtaCteEmpresaByIdEmpresa(idEmpresa);
				if (ctaCte.getEstadoComoCliente() != null
						&& ctaCte
								.getEstadoComoProveedor()
								.getSigla()
								.compareTo(
										Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA) == 0) {
					ctaCte.setEstadoComoProveedor(estadoCtaCteActivo);
					rr.saveObject(ctaCte, "MIGRA");
				}
				
				codigoProveedorAnterior = codigoProveedor;
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
			
			mov.setImporteOriginal(Double.parseDouble(row[5]));
			mov.setSaldo(Double.parseDouble(row[7]));
			mov.setSucursal(sucursal);
			rr.saveObject(mov, "Migracion");
		}
		entrada.close();
		System.out.printf("--> %s\n",
				"MIGRACION DE MOVIMIENTOS DE PROVEEDORES TERMINADO...");
	}

	/**
	 * Obtiene el id de la empresa en el "Ovevete" de acuerdo al codigo 
	 * de proveedor correspondiente a el sistema "Carlos Rivas".
	 * El formato del codigo de proveedor es -PR00-
	 * @param codigoProveedor
	 * @return
	 * @throws Exception
	 */
	public long getIdEmpresaByCodigoProveedor(String codigoProveedor) throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		IiD empresa = rr.getEmpresaByCodigoProveedor(codigoProveedor);
		long idEmpresa = 0;
		if (empresa != null) {
			idEmpresa = empresa.getId();
		} else {
			System.err.println("No se encontro empresa para el proveedor: "
					+ codigoProveedor);
		}
		return idEmpresa;
	}

	public static void main(String[] args) throws Exception {
		MigracionMovimientosProveedores migracion = new MigracionMovimientosProveedores();
		//Convierte el listado de movimientos a csv
		migracion.listadoMovProvToCsv();
		//Migra los movimientos desde el csv formateado
		migracion.migrarMovimientosProveedores();
	}
}
