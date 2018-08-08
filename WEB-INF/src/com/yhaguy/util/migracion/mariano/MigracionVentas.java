package com.yhaguy.util.migracion.mariano;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Sessions;

import com.coreweb.domain.Tipo;
import com.coreweb.extras.barraProgreso.BarraProgresoVM;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.VentaMigracion;
import com.yhaguy.gestion.notacredito.NotaCreditoControlBody;
import com.yhaguy.gestion.venta.VentaControlBody;

public class MigracionVentas {

	private Misc m = new Misc();
	private VentaControlBody ventaControl = new VentaControlBody();
	private NotaCreditoControlBody notaCreditoControl = new NotaCreditoControlBody();
	private RegisterDomain rr = RegisterDomain.getInstance();

	private String pathListaVentasDelDia = "";
	private String pahtVentasDelDiaMigrar = "";
	private String separador = ",";

	private static final String codOperacionNotaCredito = "NOT_CR_CR";
	private static final String codOperacionFacContado = "CT";
	private static final String codOperacionFacContado_2 = "CR-EFECTIVO";
	private static final String codOperacionFacCred15 = "CR-D15";
	private static final String codOperacionFacCred30 = "CR-M1";
	private static final String codOperacionFacCred30_2 = "CR-D30";
	private static final String codOperacionFacCred60 = "CR-D60";
	private static final String textoReferenciaPie = "D FACTURAS";
	private static final String textoReferenciaSeparador = "___";

	MyArray migrarInfo = new MyArray();
	int cantidadVentasContado = 0;
	int cantidadVentasCredito = 0;
	int cantidadNotasCredito = 0;

	public String getPathListaVentasDelDia() {
		return pathListaVentasDelDia;
	}

	public void setPathListaVentasDelDia(String pathListaVentasDelDia) {
		this.pathListaVentasDelDia = pathListaVentasDelDia;
	}

	public String getPahtVentasDelDiaMigrar() {
		return pahtVentasDelDiaMigrar;
	}

	public void setPahtVentasDelDiaMigrar(String pahtVentasDelDiaMigrar) {
		this.pahtVentasDelDiaMigrar = pahtVentasDelDiaMigrar;
	}

	/**
	 * Convierte el listado de ventas cargado a un csv.
	 * 
	 * @throws IOException
	 */
	public void listadoVentasToCsv() throws IOException {

		String linea = "";

		// Listado de movimientos
		FileReader fr = new FileReader(pathListaVentasDelDia);
		BufferedReader entrada = new BufferedReader(fr);

		// Archivo formateado a csv
		FileWriter fw = new FileWriter(pahtVentasDelDiaMigrar);
		BufferedWriter salida = new BufferedWriter(fw);

		while (entrada.ready()) {

			linea = entrada.readLine();
			String s = linea;

			// Si es el pie de pagina, terminar (Para que solo carge los rows al
			// csv)
			if (s.contains(this.textoReferenciaPie)) {
				break;
			}

			// Reemplazar todas las " por nada
			s = s.replaceAll("\"", "");
			s = s.trim();

			if (s.length() > 0
					&& s.contains(this.textoReferenciaSeparador) == false
					&& Character.toString(s.charAt(0)).compareTo(separador) == 0) {

				// Para quitar el separador que esta al inicio de cada fila
				s = s.substring(1, s.length());
				s += "\n";

				// Para detectar filas donde todas las columnas son vacias
				String p = s.replace(",", "");
				if (p.trim().length() > 0) {
					salida.write(s);

				}
			}
		}
		entrada.close();
		salida.close();
	}

	/**
	 * Migra las ventas obtenidas del archivo del csv.
	 * 
	 * @throws Exception
	 */
	public MyArray migrarVentas(boolean guardar, UtilDTO utilDto, int totalMovimientos) throws Exception {

		System.out.printf("--> %s\n", "MIGRANDO VENTAS...");

		migrarInfo = new MyArray();
		migrarInfo.setPos1(true);

		cantidadVentasContado = 0;
		cantidadVentasCredito = 0;
		cantidadNotasCredito = 0;
		
		
		String linea = "";

		FileReader fr = new FileReader(pahtVentasDelDiaMigrar);
		BufferedReader entrada = new BufferedReader(fr);

		while (entrada.ready()) {

			linea = entrada.readLine();
			String[] row = linea.split(this.separador);

			if (guardar == true) {
				this.guardarDatoMigracion(row, utilDto);

			} else {
				this.probarDatoMigracion(row);
			}
		}
		
		entrada.close();

		// Total de ventas
		migrarInfo.setPos2(cantidadVentasContado + cantidadVentasCredito
				+ cantidadNotasCredito);

		// Total de ventas contado
		migrarInfo.setPos3(cantidadVentasContado);

		// Total de ventas credito
		migrarInfo.setPos4(cantidadVentasCredito);

		// Total de notas de credito
		migrarInfo.setPos5(cantidadNotasCredito);

		return migrarInfo;

	}

	public void guardarDatoMigracion(String[] row, UtilDTO dtoUtil) throws Exception {
		

		MyPair guarani = dtoUtil.getMonedaGuarani();
		
		if (row[4].compareTo(codOperacionFacContado) == 0
				|| row[4].compareTo(codOperacionFacContado_2) == 0) {

			ventaControl.addPedidoVentaContado(
					this.getNroFactFormateado(row[0]),
					this.m.ParseFecha(row[1], "dd/MM/yy"), row[2], guarani,
					Double.parseDouble(row[6]), Double.parseDouble(row[7]),
					Double.parseDouble(row[8]));
			cantidadVentasContado += 1;

		} else if (row[4].compareTo(codOperacionFacCred30) == 0
				|| row[4].compareTo(codOperacionFacCred30_2) == 0
				|| row[4].compareTo(codOperacionFacCred15) == 0
				|| row[4].compareTo(codOperacionFacCred60) == 0) {

			ventaControl.addPedidoVentaCredito(
					this.getNroFactFormateado(row[0]),
					this.m.ParseFecha(row[1], "dd/MM/yy"), row[2], guarani,
					Double.parseDouble(row[6]), Double.parseDouble(row[7]),
					Double.parseDouble(row[8]));
			cantidadVentasCredito += 1;

		} else if (row[4].compareTo(codOperacionNotaCredito) == 0) {
			
			notaCreditoControl.addSolicitudNotaCreditoVenta(row[0],
					this.m.ParseFecha(row[1], "dd/MM/yy"), row[2], guarani,
					Double.parseDouble(row[6]), Double.parseDouble(row[7]),
					Double.parseDouble(row[8]));
			cantidadNotasCredito += 1;

		} else {

			System.err.println("Error migrar venta: " + row[0]
					+ " Codigo de Operacion no previsto" + row[4]);
		}

	}

	public void probarDatoMigracion(String[] row) throws Exception {

		Cliente c = rr.getClienteByCodigoMRA(row[2]);

		if (c == null) {
			this.migrarInfo.setPos6(this.migrarInfo.getPos6()
					+ "\n-El cliente " + row[2]
					+ " no se encuentra cargado en el sistema");
			this.migrarInfo.setPos1(false);
		}

		if (row[4].compareTo(codOperacionFacContado) == 0
				|| row[4].compareTo(codOperacionFacContado_2) == 0) {

			cantidadVentasContado += 1;

		} else if (row[4].compareTo(codOperacionFacCred30) == 0
				|| row[4].compareTo(codOperacionFacCred30_2) == 0
				|| row[4].compareTo(codOperacionFacCred15) == 0
				|| row[4].compareTo(codOperacionFacCred60) == 0) {

			cantidadVentasCredito += 1;

		} else if (row[4].compareTo(codOperacionNotaCredito) == 0) {

			cantidadNotasCredito += 1;

		} else {

			this.migrarInfo.setPos6(this.migrarInfo.getPos6() + "\n- Venta: "
					+ row[0] + " Codigo de Operacion no previsto" + row[4]);
		}

	}

	/**
	 * Retorna el numero de factura formateado a 001-001-0000001
	 * 
	 * @param nroFactura
	 * @return
	 */
	public String getNroFactFormateado(String nroFactura) {
		String sucursal = "";
		String boca = "";
		String nro = "";

		sucursal = nroFactura.substring(0, 1);
		boca = nroFactura.substring(1, 4);
		nro = nroFactura.substring(4, nroFactura.length());

		return "00" + sucursal + "-" + boca + "-" + nro;
	}

	/**
	 * Obtiene la lista copleta de la informacion de migraciones realizadas
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<MyArray> getMigracionesVentas() throws Exception {

		List<VentaMigracion> migraciones = (List<VentaMigracion>) rr
				.getObjects(VentaMigracion.class.getName());
		List<MyArray> listaMigraciones = new ArrayList<MyArray>();

		for (VentaMigracion m : migraciones) {
			MyArray vm = new MyArray();
			vm.setId(m.getId());
			vm.setPos1(m.getModificado());
			vm.setPos2(m.getFechaDesde());
			vm.setPos3(m.getFechaHasta());
			vm.setPos4(m.getCaja());
			listaMigraciones.add(vm);

		}

		return listaMigraciones;

	}

	/**
	 * Guarda la informacion de la migracion, como la fecha de la migracion, el
	 * rango de fechas de las ventas migradas, etc. Pos 1 = fechaDesde Pos 2 =
	 * fechaHasta Pos 3 = idCaja
	 * 
	 * @param migracion
	 * @throws Exception
	 */
	public void guardarDatosMigracion(MyArray migracion) throws Exception {

		VentaMigracion vMigracion = new VentaMigracion();
		vMigracion.setFechaDesde((Date) migracion.getPos1());
		vMigracion.setFechaHasta((Date) migracion.getPos2());
		vMigracion.setIdCaja((long) migracion.getPos3());
		vMigracion.setCaja((String) migracion.getPos4());
		rr.saveObject(vMigracion, "Migracion");

	}

}
