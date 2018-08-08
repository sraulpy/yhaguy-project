package com.yhaguy.util.migracion;

import java.util.*;
import java.io.*;

import org.hibernate.Session;

import com.coreweb.domain.Tipo;
import com.coreweb.domain.TipoTipo;
import com.coreweb.domain.Usuario;
import com.coreweb.util.Misc;
import com.coreweb.util.Ruc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.DepartamentoApp;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaGrupoSociedad;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Localidad;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RucSet;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Zona;
import com.yhaguy.util.population.DBPopulationSET;
import com.yhaguy.util.population.DBPopulationTipos;
import com.yhaguy.util.population.UsuarioPerfilParser;

public class ClienteMigraConfig {

	static String xclienteProveedorCSV = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/CLIENTE-y-PROVEEDOR.txt";
	static String clienteCSV = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/CLIENTE_ELITE.csv";
	static String proveedoresCSV = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/PROVEEDOR_ELITE.csv";

	static String clientesCuentasCSV = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/Cuentas_Clientes_GE.csv";
	static String proveedoresCuentasCSV = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/Cuentas_Proveedores_GE.csv";

	static String zonaCSV = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/ZONA.csv";
	static String rubroEmpresaCSV = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/RUBROEMPRESA.csv";
	static String localidadCSV = "./WEB-INF/docs/migracion/CODIGO_DE_LOCALIDADES.csv";
	static String localidadJediCSV = "./WEB-INF/docs/migracion/CODIGO_DE_LOCALIDADES_jedi.csv";

	static String rucsDublicados = "./WEB-INF/docs/migracion/rucsDuplicados.txt";

	static String[] colClientes = { "IDPERSONA", "RAZONSOCIAL", "NOMBRE",
			"APELLIDO", "DIRECCION", "TELEFONO", "DOCUMENTO", "RUC", "ESTADO",
			"IDBARRIO", "ANIVERSARIO", "IDCIUDAD", "FECHAINGRESO", "FAX",
			"MAIL", "WEB", "IDRUBROEMPRESA", "IDCADENA", "CELULAR", "IDPAIS",
			"IDUSER", "FECHA_CARGA", "IDZONA", "IDREGIMEN",
			"IDPERSONA_CATEGORIA", "OBSERVACION" };

	static String[] colProveedores = { "IDPERSONA", "RAZONSOCIAL", "NOMBRE",
			"APELLIDO", "DIRECCION", "TELEFONO", "DOCUMENTO", "RUC", "ESTADO",
			"IDBARRIO", "ANIVERSARIO", "IDCIUDAD", "FECHAINGRESO", "FAX",
			"MAIL", "WEB", "IDRUBROEMPRESA", "IDCADENA", "CELULAR", "IDPAIS",
			"IDUSER", "FECHA_CARGA", "IDPERSONA_SECTOR", "OBSERVACION" };

	static String[] colClientesCuentas = { "IDPERSONA", "OBSERVACION",
			"FECHAAPERTURA", "IDCONDICION", "CONDICION", "LIMITECREDITO",
			"IDPLANVENTA", "PLANVENTA", "IDVENDEDOR", "NOMBRE", "APELLIDO",
			"IDMONEDA", "SALDO", "SALDOUSD", "ESTADO", "DESCRIPCION_ESTADO",
			"DESCRIPCION", "TIPO", "IDPLANCUENTA" };

	static String[] colProveedoresCuentas = { "IDPERSONA", "OBSERVACION",
			"FECHAAPERTURA", "IDCONDICION", "CONDICION", "IDPLANVENTA",
			"PLANVENTA", "IDMONEDA", "SALDO", "SALDOUSD", "ESTADO",
			"DESCRIPCION_ESTADO", "DESCRIPCION", "TIPO", "IDPLANCUENTA" };

	static Hashtable<String, Integer> colPClientes = new Hashtable<>();
	static Hashtable<String, Integer> colPProveedores = new Hashtable<>();

	static Hashtable<String, Integer> colPClientesCuentas = new Hashtable<>();
	static Hashtable<String, Integer> colPProveedoresCuentas = new Hashtable<>();

	static Hashtable<String, Object[]> rucs = new Hashtable<>();

	static Ruc vruc = new Ruc();
	static Misc m = new Misc();

	static int nCol = 25;

	static {
		// cargar las posiciones según los campos
		for (int i = 0; i < colClientes.length; i++) {
			String c = colClientes[i];
			colPClientes.put(c, i);
			// System.out.println(c +" - "+ i);
		}

		for (int i = 0; i < colProveedores.length; i++) {
			String c = colProveedores[i];
			colPProveedores.put(c, i);
			// System.out.println(c +" - "+ i);
		}

		for (int i = 0; i < colClientesCuentas.length; i++) {
			String c = colClientesCuentas[i];
			colPClientesCuentas.put(c, i);
		}

		for (int i = 0; i < colProveedoresCuentas.length; i++) {
			String c = colProveedoresCuentas[i];
			colPProveedoresCuentas.put(c, i);
		}

	}

	private static double getDatoDouble(String col, String[] dato,
			Hashtable<String, Integer> columnas) {
		String d = getCol(columnas, col, dato);
		// quitamos los puntos
		d = d.replaceAll("\\.", "");
		// las comas son los separadores decimales
		String dd = d.replace(',', '.');
		double out = 0;
		if (d.compareTo("Null") != 0) {
			out = Double.parseDouble(dd.trim());
		}
		return out;
	}

	public static double getDatoDoubleCuentasCliente(String col, String[] dato) {
		return getDatoDouble(col, dato, colPClientesCuentas);
	}

	public static double getDatoDoubleCuentasProveedor(String col, String[] dato) {
		return getDatoDouble(col, dato, colPProveedoresCuentas);
	}

	private static java.util.Date getDatoDate(String col, String[] dato,
			Hashtable<String, Integer> columnas) {
		String f = getCol(columnas, col, dato);
		Misc m = new Misc();
		java.util.Date fh = m.ParseFecha(f, "MM/dd/yyyy HH:mm:ss");
		return fh;
	}

	public static java.util.Date getDatoDateCuentasCliente(String col,
			String[] dato) {
		return getDatoDate(col, dato, colPClientesCuentas);
	}

	public static java.util.Date getDatoDateCuentasProveedor(String col,
			String[] dato) {
		return getDatoDate(col, dato, colPProveedoresCuentas);
	}

	static void addRuc(String ruc, String empresa, String idPersona,
			String idCadena) {
		if (vruc.validarRuc(ruc) == true) {
			String cad = "";
			if (idCadena.compareTo("0") != 0) {
				cad = "**";
			}
			int p = ClientesMigraPrioridad.getPrioridad(idPersona);

			Object[] dd = rucs.get(ruc);
			if (dd != null) {
				dd[0] = ((int) dd[0]) + 1;
				dd[2] = (String) dd[2] + " [(" + p + ")" + cad + idPersona
						+ ", " + empresa + "]";
			} else {
				dd = new Object[] { 1, ruc,
						"[(" + p + ")" + cad + idPersona + ", " + empresa + "]" };
				rucs.put(ruc, dd);
			}
		}
	}

	static void printRucs() throws Exception {
		System.out.println("------------- RUCS----------------");
		StringBuffer texto = new StringBuffer();
		Enumeration<Object[]> enu = rucs.elements();
		while (enu.hasMoreElements()) {
			Object[] dd = (Object[]) enu.nextElement();
			int n = (int) dd[0];
			String ruc = (String) dd[1];
			String empresa = (String) dd[2];
			if (n > 1) {
				String linea = n + ")  " + ruc + "  -  " + empresa;
				if (linea.length() > 300) {
					linea = linea.substring(0, 299);
				}
				texto.append(linea + "\n");
				System.out.println(linea);
			}
		}
		m.grabarStringToArchivo(rucsDublicados, texto.toString());

	}

	static Date parseFecha(String dd) {
		Date date = null;
		try {
			if (dd.compareTo("12/30/1899 00:00:00") != 0) {
				int mes = Integer.parseInt(dd.substring(0, 2));
				int dia = Integer.parseInt(dd.substring(3, 5));
				int anio = Integer.parseInt(dd.substring(6, 10));
				Calendar cal = Calendar.getInstance();
				cal.set(anio, (mes - 1), dia); // year is as expected, month is
												// zero based, date is as
												// expected
				date = cal.getTime();
			}
		} catch (Exception ex) {
			// no pudo armar la fecha, no hace nada
		}
		return date;
	}

	static String[] parserLinea(String linea) {
		String[] dato = linea.split("\t");
		return dato;
	}

	static String getColCliente(String col, String[] dato) {
		return getCol(colPClientes, col, dato);
	}

	static String getColProveedor(String col, String[] dato) {
		return getCol(colPProveedores, col, dato);
	}

	static String getColClientesCuentas(String col, String[] dato) {
		return getCol(colPClientesCuentas, col, dato);
	}

	static String getColProveedoresCuentas(String col, String[] dato) {
		return getCol(colPProveedoresCuentas, col, dato);
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,
			String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		if (col.compareTo("IDPERSONA") == 0) {
			out = out.replace(",", "");
		}
		if (col.compareTo("RUC") == 0) {
			out = out.replace(" ", "");
		}
		return out;
	}

	static BufferedReader abrirArchivoPath(String path) throws Exception {
		File f = new File(path);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		;
		return entrada;
	}

	static BufferedReader abrirArchivo(boolean cliPro) throws Exception {
		// String ff = clienteProveedorCSV;

		String ff = clienteCSV;

		if (cliPro == false) {
			ff = proveedoresCSV;
		}

		File f = new File(ff);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		;
		return entrada;
	}

	static void xxcheckArchivo(boolean conLog, boolean cliPro) throws Exception {
		checkArchivo(conLog, cliPro);
	}

	/**
	 * check archivo de clientes csv, que los campos esten correctos
	 * 
	 * @throws Exception
	 */
	static void checkArchivo(boolean conLog, boolean cliPro) throws Exception {

		BufferedReader entrada = abrirArchivo(cliPro);
		String linea = "";
		// leer la cabecera
		linea = entrada.readLine() + " ";

		int p = 0;
		int np = 0;

		while (entrada.ready()) {
			p++;
			linea = entrada.readLine() + " ";
			String[] dato = parserLinea(linea);
			System.out.println("[" + dato.length + "]" + p + ")" + linea);
			if ((conLog == true) && (dato.length != nCol)) {
				System.out.println("[" + dato.length + "]" + p + ")" + linea);
				np++;
			}

			String estado = getColCliente("ESTADO", dato);
			if ((conLog == true) && (estado.compareTo("A") != 0)) {
				System.out.println("[" + dato.length + "]" + p + ")" + linea);
			}

			String aniversario = getColCliente("ANIVERSARIO", dato);
			if ((conLog == true)
					&& ((aniversario.length() > 1) && (aniversario
							.compareTo("12/30/1899 00:00:00") != 0))) {
				String raz = getColCliente("RAZONSOCIAL", dato);
				// System.out.println(aniversario+" "+raz);
			}

			String idCadena = getColCliente("IDCADENA", dato);
			if ((conLog == false) && (idCadena.compareTo("0") != 0)) {
				System.out.println(idCadena + "   "
						+ getColCliente("RUC", dato) + "   "
						+ getColCliente("RAZONSOCIAL", dato));
			}

			String celular = getColCliente("CELULAR", dato);
			if ((conLog == true) && (celular.length() > 2)) {
				System.out.println(celular + "   " + getColCliente("RUC", dato)
						+ "   " + getColCliente("RAZONSOCIAL", dato));
			}

			String idPais = getColCliente("IDPAIS", dato);
			if ((conLog == true) && (idPais.compareTo("-1") != 0)) {
				System.out.println(idPais + "   " + getColCliente("RUC", dato)
						+ "   " + getColCliente("RAZONSOCIAL", dato));
			}

			String idZona = getColCliente("IDZONA", dato);
			if ((conLog == true) && (idZona.compareTo("-1") != 0)) {
				System.out.println(idZona + "   " + getColCliente("RUC", dato)
						+ "   " + getColCliente("RAZONSOCIAL", dato));
			}

			// String observacion = getColCliente("OBSERVACION", dato);

			/*
			 * String idPersonaCategoria = getColCliente("IDPERSONA_CATEGORIA",
			 * dato); if ((conLog == true) && (idPersonaCategoria.compareTo("0")
			 * != 0)) { System.out.println(idPersonaCategoria + "   " +
			 * getColCliente("IDPERSONA", dato) + "   " + getColCliente("RUC",
			 * dato) + "   " + getColCliente("RAZONSOCIAL", dato)); }
			 * 
			 * String idRegiemn = getColCliente("IDREGIMEN", dato); if ((conLog
			 * == true) && (idRegiemn.compareTo("0") != 0) &&
			 * (idRegiemn.compareTo("-99") != 0)) { System.out.println(idRegiemn
			 * + "   " + getColCliente("RUC", dato) + "   " +
			 * getColCliente("RAZONSOCIAL", dato)); }
			 */
			String idPersona = getColCliente("IDPERSONA", dato);
			Integer pos = ClientesMigraPrioridad.getPrioridad(idPersona);
			if ((conLog == true) && (pos != null)) {
				System.out.println("[" + pos.intValue() + "] " + idPersona
						+ "   " + getColCliente("RUC", dato) + "   "
						+ getColCliente("RAZONSOCIAL", dato));
			}

			String ruc = getColCliente("RUC", dato);
			String razonSocial = getColCliente("RAZONSOCIAL", dato);
			addRuc(ruc, razonSocial, idPersona, idCadena);

		}
		entrada.close();

		System.out.println("Registros: " + p + "  con errores: " + np);
		if (np > 0) {
			throw new Exception("Error en parse Archivo");
		}
		printRucs();
	}

	public static String[] SESSIONgetRazonSocialSET(String ruc, Session session)
			throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		RucSet set = (RucSet) rr.SESSIONgetObject(RucSet.class.getName(),
				"ruc", ruc.trim(), session);
		if (set == null) {
			set = (RucSet) rr.SESSIONgetObject(RucSet.class.getName(),
					"rucOld", ruc.trim(), session);
		}
		if (set != null) {
			// encontro
			String rucSet = set.getRuc();
			String razonSocialSet = set.getRazonSocial();
			String[] rs = { rucSet, razonSocialSet };
			return rs;
		}
		return null;
	}

	/**
	 * Busca una zonna por el ID del Jedi (guardado en el campo auxi)
	 * 
	 * @param idAuxi
	 * @return
	 * @throws Exception
	 */
	public static Zona SESSIONgetZona(String idAuxi, Session session)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Zona z = (Zona) rr.SESSIONgetObject(Zona.class.getName(), "auxi",
				idAuxi, session);
		return z;
	}

	/**
	 * Migra el archivo de zonas
	 * 
	 * @throws Exception
	 */
	public static void migrarZona() throws Exception {

		System.out.println("... migrar zonas");

		RegisterDomain rr = RegisterDomain.getInstance();
		rr.deleteAllObjects(Zona.class.getName());

		String linea = "";
		File f = new File(zonaCSV);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		// lee cabecera
		linea = entrada.readLine() + " ";

		while (entrada.ready()) {
			linea = entrada.readLine() + " ";
			String[] dato = linea.split("\t");

			String idZona = (dato[0]).trim();
			String zona = (dato[1]).trim();

			System.out.println(idZona + " " + zona);
			Zona z = new Zona();
			z.setDescripcion(zona);
			z.setAuxi(idZona);

			rr.saveObject(z, "MIG");

		}

		entrada.close();
	}

	/**
	 * Busca un rubroEmpresa por el ID del Jedi (guardado en el campo auxi)
	 * 
	 * @param idAuxi
	 * @return
	 * @throws Exception
	 */
	public static Tipo SESSIONgetRubroEmpresa(String idAuxi, Session session)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo t = (Tipo) rr.SESSIONgetObject(Tipo.class.getName(), "auxi",
				idAuxi, session);
		return t;
	}

	/**
	 * Migra el archivo de rubro empresa
	 * 
	 * @throws Exception
	 */
	public static void migrarRubroEmpresa() throws Exception {

		System.out.println("... migrar Rubro Empresa");

		RegisterDomain rr = RegisterDomain.getInstance();
		TipoTipo reTipo = (TipoTipo) rr.getObject(TipoTipo.class.getName(),
				"descripcion", Configuracion.ID_TIPO_RUBRO_EMPRESAS);

		String linea = "";
		File f = new File(rubroEmpresaCSV);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		// lee cabecera
		linea = entrada.readLine() + " ";

		while (entrada.ready()) {
			linea = entrada.readLine() + " ";
			String[] dato = linea.split("\t");

			String idRubroEmpresa = (dato[0]).trim();
			String rubroEmpresa = (dato[1]).trim();

			System.out.println(idRubroEmpresa + " " + rubroEmpresa);
			Tipo re = new Tipo();
			re.setTipoTipo(reTipo);
			re.setDescripcion(rubroEmpresa);
			re.setSigla(Configuracion.ID_TIPO_RUBRO_EMPRESAS);
			re.setAuxi(idRubroEmpresa);

			rr.saveObject(re, "MIG");

		}

		entrada.close();
	}

	/**
	 * Busca un rubroEmpresa por el ID del Jedi (guardado en el campo auxi)
	 * 
	 * @param idAuxi
	 * @return
	 * @throws Exception
	 */
	public static Localidad SESSIONgetLocalidad(String idAuxi, Session session)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Localidad lo = (Localidad) rr.SESSIONgetObject(
				Localidad.class.getName(), "auxi", idAuxi, session);
		return lo;
	}

	/**
	 * Migra el archivo localidades
	 * 
	 * @throws Exception
	 */
	public static void cruceLocalidades() throws Exception {

		System.out.println("... migrar localidades");

		ArrayList lLoc = new ArrayList();

		// localidades del Correo ======================

		String linea = "";
		File f = new File(localidadCSV);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		// lee cabecera
		linea = entrada.readLine() + " ";

		while (entrada.ready()) {
			linea = entrada.readLine() + " ";
			String[] dato = linea.split("\t");

			String cp = (dato[0]).trim();
			String localidad = (dato[1]).trim();
			String departamento = (dato[2]).trim();
			String localidadJedi = "-";
			String idJedi = "-";
			String pais = "PARAGUAY";

			String[] nDato = { cp, localidad, departamento, localidadJedi,
					idJedi, pais };

			lLoc.add(nDato);

			// System.out.println(localidad+ " " + departamento);
		}
		entrada.close();

		// localidades Jedi ===============================

		String linea2 = "";
		File f2 = new File(localidadJediCSV);
		BufferedReader entrada2 = new BufferedReader(new FileReader(f2));
		// lee cabecera
		linea2 = entrada2.readLine() + " ";

		while (entrada2.ready()) {
			linea2 = entrada2.readLine() + " ";
			String[] dato = linea2.split("\t");

			String idJedi = (dato[0]).trim();
			String localidadJedi = (dato[1]).trim();

			parseLocalidad(lLoc, localidadJedi, idJedi);

		}
		entrada2.close();

		Misc m = new Misc();

		// cargar en el sistema ------------
		RegisterDomain rr = RegisterDomain.getInstance();
		System.out.println("--------------------INI----------------------");
		for (Iterator iterator = lLoc.iterator(); iterator.hasNext();) {
			String[] dato = (String[]) iterator.next();
			System.out.println(m.formato(dato[1], 20, false) + " "
					+ m.formato(dato[3], 20, true));
			Localidad lo = new Localidad();
			lo.setPais(dato[5]);
			lo.setCp(dato[0]);
			lo.setLocalidad(dato[1]);
			lo.setDepartamento(dato[2]);
			lo.setAuxi(dato[4]);
			rr.saveObject(lo, "MIG");

		}

	}

	public static void parseLocalidad(ArrayList l, String locaJedi,
			String idJedi) {

		boolean si = false;
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			String[] dato = (String[]) iterator.next();
			String loca1 = dato[1];
			if (loca1.compareTo(locaJedi) == 0) {
				si = true;
				dato[3] = locaJedi;
				dato[4] = idJedi;
			}
		}
		if (si == false) {
			String[] nDato = { "-", "sin definir", "-", locaJedi, idJedi, "-" };
			l.add(nDato);
		}

	}

	// /======================= grupo sociedad =========================

	private static EmpresaGrupoSociedad grupoSociedadDefault;

	public static EmpresaGrupoSociedad getEmpresaGrupoSociedadDefault(
			Session session) throws Exception {
		if (grupoSociedadDefault == null) {
			grupoSociedadDefault = SESSIONgetEmpresaGrupoSociedad("0", session);
		}
		return grupoSociedadDefault;
	}

	/**
	 * Busca un rubroEmpresa por el ID del Jedi (guardado en el campo auxi)
	 */
	public static EmpresaGrupoSociedad SESSIONgetEmpresaGrupoSociedad(
			String idAuxi, Session session) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		EmpresaGrupoSociedad gr = (EmpresaGrupoSociedad) rr.SESSIONgetObject(
				EmpresaGrupoSociedad.class.getName(), "auxi", idAuxi, session);
		if (gr == null) {
			gr = getEmpresaGrupoSociedadDefault(session);
		}

		return gr;
	}

	/**
	 * carga los grupos de empresas definidos hasta ahora
	 */
	public static void cargaGruposEmpresas() throws Exception {

		System.out.println("... migrar Grupo Empresas");

		String[][] lista = { { "0", Configuracion.EMPRESA_GRUPO_NO_DEFINIDO } };
		/*
		 * Cadena es para las sucursales, NO para grupos de empresas con RUC y
		 * denominación diferente
		 * 
		 * Estos casos son todos sucursales
		 * 
		 * { "1", "TOYO" }, { "2", "ALG Repuestos" }, { "3", "ESTACION BAHIA" },
		 * { "4", "COMBUBAR" }, { "5", "ECOBAR" }, { "6", "COOP RAUL PEÑA" }, {
		 * "7", "TALLER AMARILLA" }, { "8", "PUERTO ALTO" }, { "9",
		 * "NEUMATICOS ITAPUA" }, { "10", "TORRES, ALICIO" } };
		 */
		RegisterDomain rr = RegisterDomain.getInstance();
		for (int i = 0; i < lista.length; i++) {
			String[] grs = lista[i];
			String idGrupo = grs[0];
			String descripcion = grs[1];

			EmpresaGrupoSociedad egr = new EmpresaGrupoSociedad();
			egr.setAuxi(idGrupo);
			egr.setDescripcion(descripcion);

			rr.saveObject(egr, "MIG");
		}

	}

	public static void cargaInicial() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();

		// Primero cargar todos los tipos
		DBPopulationTipos tt = new DBPopulationTipos();
		tt.cargaTipos();

		// Le menu y los usuarios..
		UsuarioPerfilParser.loadMenuConfig();

		// Crear el acceso, para eso es necesario una sucursal
		SucursalApp sucApp1 = new SucursalApp();
		sucApp1.setNombre("Central");
		sucApp1.setDescripcion("Casa Central");
		sucApp1.setEstablecimiento("1");
		sucApp1.setDireccion("Mcal. Estigarribia");
		sucApp1.setTelefono("111-222");
		Set<SucursalApp> sucursalesApp = new HashSet<SucursalApp>();
		sucursalesApp.add(sucApp1);
		rr.saveObject(sucApp1, "MIG");

		DepartamentoApp dto1 = new DepartamentoApp();
		dto1.setNombre("Administracion");
		dto1.setDescripcion("Departamento de Administración");
		dto1.setSucursal(sucApp1);
		rr.saveObject(dto1, "MIG");

		System.out.println("... cargando Usuarios:");
		// String[] usuariox = { "juan", "clientes", "proveedores", "articulos",
		// "importacion", "operador", "recepcionista" };
		String[] usuarios = { "juan", "clie", "prove", "arti", "impor", "oper",
				"recep" };

		for (int i = 0; i < usuarios.length; i++) {
			String login = usuarios[i];

			System.out.println("... " + i + ":" + login);

			Usuario usr = (Usuario) rr.getObject(
					com.coreweb.domain.Usuario.class.getName(), "login", login);

			AccesoApp acc = new AccesoApp();
			acc.setDescripcion("acceso " + login);
			acc.setDepartamento(dto1);
			acc.setSucursales(sucursalesApp);
			acc.setUsuario(usr);

			rr.saveObject(acc, "MIG");

			Set<AccesoApp> accesosApp = new HashSet<AccesoApp>();
			accesosApp.add(acc);

			Empresa emp = new Empresa();

			emp.setRazonSocial("Nombre " + login);
			emp.setNombre("Nombre " + login);
			emp.getRubroEmpresas().add(tt.rubroEmpresaFuncionario);
			emp.setRuc("11111111-" + i);
			emp.setMoneda(tt.guarani);

			Funcionario func = new Funcionario();
			func.setEmpresa(emp);
			func.setAccesos(accesosApp);
			rr.saveObject(func, "MIG");
		}
	}

	public static void migrarRucSet() throws Exception {
		DBPopulationSET.main(null);
	}
	


	public static void main(String[] args) throws Exception {

		/*********************************** INICIO *************************************/
		long ini = System.currentTimeMillis();

		/*********************************** PASO 1 *************************************/
		// Migrar RUCs
		migrarRucSet();

		/*********************************** PASO 2 *************************************/		
		cruceLocalidades();
		migrarZona();
		migrarRubroEmpresa();

		// Carga valores por defecto
		ClienteMigraAll.cargaValoresDehault();

		/*********************************** PASO 3 *************************************/

		//Carga de clientes
		ClienteMigraAll.cargaClientes(true); 

		//Carga la informacion referente a la Cta. Cte. del cliente(Cuentas + movimientos)
		MigracionSaldosClientesConsolidado.cargaHastTableConsolodadoYDetalle();
		ClienteMigraAll.migrarCuentasCliente();

		/*********************************** PASO 4 *************************************/

		// actualizar hash de empresas
		ClienteMigraAll.cargarHashEmpresa();

		//Carga de proveedores
		ClienteMigraAll.cargaClientes(false);
		
		//Carga la informacion referente a la Cta. Cte. del proveedor(Cuentas + movimientos)
		MigracionSaldosProveedoresConsolidado.cargaHastTableConsolodadoYDetalle();
		ClienteMigraAll.migrarCuentasProveedores();

		long fin = System.currentTimeMillis();
		System.out.printf("Migracion de Clientes y Proveedores completa: %.2f min", (((fin - ini) / 1000.0) / 60.0));

	}

}
