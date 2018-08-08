package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.css.sac.SiblingSelector;

import com.coreweb.domain.AgendaEvento;
import com.coreweb.domain.AgendaEventoDetalle;
import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.coreweb.util.Ruc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.*;

public class ClienteMigraAll extends ClienteMigraConfig {

	static Hashtable<String, Object> rucEmprsa = new Hashtable<>();
	static Hashtable<Long, Long> existeCliente = new Hashtable<>();
	static Hashtable<Long, Long> existeProveedor = new Hashtable<>();

	static RegisterDomain rr = RegisterDomain.getInstance();
	static CtaCteMigracion ctaCteMigracion = new CtaCteMigracion();
	static Misc m = new Misc();
	static Ruc vRuc = new Ruc();

	static boolean siGraba = true;

	public static Hashtable<String, Object[]> ctaCteTodo = MigracionSaldosClientesConsolidado.ctaCteTodo;
	public static Hashtable<String, Object[]> ctaCteProveedorTodo = MigracionSaldosProveedoresConsolidado.ctaCteProveedoresTodo;
	public static MigracionSaldosClientesDetallado detallado;
	public static MigracionSaldosProveedoresDetallado detalladoProveedores;

	
	public static void cargarHashEmpresa() throws Exception{
		rucEmprsa = new Hashtable<>();
		List l = rr.getObjects(Empresa.class.getName());
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			Empresa em = (Empresa) iterator.next();
			
			
			if (("80024884-8".compareTo(em.getRuc()) == 0)){
				// yhaguy
				System.out.println(em.getCtaCteEmpresa());
				System.out.println("Yhaguy");
			}
			rucEmprsa.put(em.getRuc(),em );
		}
		
	}
	
	public static void grabarDB(Domain d, Session session) throws Exception {
		if (siGraba == true) {
			rr.SESSIONsaveObjectDomain(d, session, "MIG");
		}
	}

	public static Transaction commitDB(long p, Session session, Transaction tx)
			throws Exception {
		if (siGraba == true) {
			if (p % 100 == 0) {
				session.flush();
				session.clear();
			}

			if (p % 1000 == 0) {
				println("[" + p + "] ini commit");
				tx.commit();
				println("... fin commit");
				tx = null;
			}
		}
		return tx;
	}

	public static void println(String s) {
		System.out.println(s);
	}

	public static void println(String s, long p) {
		if (p % 100 == 0) {
			println(p + " - " + s);
		}
	}

	public static Empresa getEmpresa(String[] dato, Session session,
			boolean cliPro) throws Exception {

		String nombreSucursal = "PRINCIPAL";
		String nombreFantasia = "NOMBRE FANTASIA";

		Empresa empresa = new Empresa();

		// --------------- Moneda -----------------
		empresa.setMoneda(monedaDefault);
		empresa.getMonedas().add(monedaDefault);

		// -------- Empresa ------------------------
		String ci = getColCliente("DOCUMENTO", dato);
		String ruc = getColCliente("RUC", dato);
		String razonSocial = getColCliente("RAZONSOCIAL", dato);
		nombreFantasia = razonSocial;
		empresa.setPais(paisPy);
		empresa.setRegimenTributario(regimenTributarioNoExenta);

		// empresa razon social
		if (razonSocial.length() == 0) {
			String apellido = getColCliente("APELLIDO", dato);
			String nombre = getColCliente("NOMBRE", dato);
			razonSocial = apellido + ", " + nombre;
		}

		// empresa ruc
		String observacion = "";
		// xxx.xxx-x
		if ((ruc.length() > 7)) {

			String[] infoSet = SESSIONgetRazonSocialSET(ruc, session);

			if (infoSet == null) {
				// el ruc cargado no está en la BD del SET
				// observacion =
				// "El RUC ["+ruc+"] no está en la BD del SET. Verificar";
			} else {
				// uso la razón social del SET, y guardo el que estaba como
				// nombre
				nombreFantasia = razonSocial;
				ruc = infoSet[0];
				razonSocial = infoSet[1];

				observacion = "La razon social y el ruc están validados con la base de datos de la SET.";
			}
		} else {
			ruc = "";
		}

		empresa.setRuc(ruc);
		empresa.setCi(ci);
		empresa.setRazonSocial(razonSocial);
		empresa.setNombre(nombreFantasia);
		empresa.setObservacion(observacion);
		empresa.setTipoPersona(personaFisica);
		if (m.esPersonaJuridica(ruc) == true) {
			empresa.setTipoPersona(personaJuridica);
		}

		if (razonSocial.length() == 0) {
			throw new Exception("Sin razón social");
		}

		// empresa aniversario e ingreso
		String aniversario = getColCliente("ANIVERSARIO", dato);
		String fechaIngreso = getColCliente("FECHAINGRESO", dato);
		empresa.setFechaAniversario(parseFecha(aniversario));
		empresa.setFechaIngreso(parseFecha(fechaIngreso));

		// empresa rubro empresa
		String rubroEmpresa = getColCliente("IDRUBROEMPRESA", dato);
		Tipo rubroEmpresaTipo = SESSIONgetRubroEmpresa(rubroEmpresa, session);
		if (rubroEmpresaTipo != null) {
			empresa.getRubroEmpresas().add(rubroEmpresaTipo);
		}

		/*
		 * si es grupo de empresa, hay que crear una sucursal, en donde cada
		 * cliente es una sucursal NO hay casos en donse sea un grupo de
		 * empresas, es decir, una lista de clientes con RUC diferentes pero el
		 * mismo dueño
		 */
		String idCadena = getColCliente("IDCADENA", dato);
		empresa.setEmpresaGrupoSociedad(grupoSociedadNoDefinido);

		// EmpresaGrupoSociedad egs = SESSIONgetEmpresaGrupoSociedad(idCadena,
		// session);

		nombreSucursal = getColCliente("RAZONSOCIAL", dato);
		if (nombreSucursal.length() < 2) {
			nombreSucursal = razonSocial;
		}
		// buscar si hay otro cliente con este mismo ruc, si si, crear una
		// sucursal para este.
		boolean siEstaba = false;
		
		if (("80024884-8".compareTo(empresa.getRuc()) == 0)){
			// yhaguy
			System.out.println("Es cliente:"+cliPro);
			System.out.println("Yhaguy");
			
		}
		
		

		if (("44444401-7".compareTo(empresa.getRuc()) != 0)
				&& (empresa.getRuc().length() > 7)) {

			Object o = rucEmprsa.get(empresa.getRuc());
			if (o != null) {
				Empresa empresa2 = (Empresa) o;
				//Empresa empresa2 = rr.getEmpresaById(((Empresa) o).getId());
				

				empresa = empresa2;
				siEstaba = true;
			} else {
				rucEmprsa.put(empresa.getRuc(), empresa);
			}

			/*
			 * if (cliPro == false){ // Para los proveedores, hay que buscar por
			 * ruc, por que los clientes ya están cargados (NO en el Hash)
			 * Empresa empresa2 = rr.SESSIONgetEmpresaByRuc(empresa.getRuc(),
			 * session); if (empresa2 != null) { // ya estaba cargada, usar esa,
			 * y de esta forma // se asigna la sucursal a la empresa. empresa =
			 * empresa2; } }
			 */
		}

		// --------- Sucursal --------------------------
		Sucursal sucursal = new Sucursal();

		String idPesona = getColCliente("IDPERSONA", dato);
		String direccion = getColCliente("DIRECCION", dato);
		String telefono = getColCliente("TELEFONO", dato);
		String fax = getColCliente("FAX", dato);

		if (cliPro == true) {
			sucursal.setAuxi("CL:" + idPesona);
		} else {
			sucursal.setAuxi("PR:" + idPesona);
		}
		sucursal.setNombre(nombreSucursal);
		sucursal.setDireccion(direccion);
		sucursal.setTelefono(telefono + " FAX:" + fax);

		String idPersona = "";
		if (cliPro == true) {
			idPersona = getColCliente("IDPERSONA", dato);
			// ------- Zona --------------
			try {
				String idZona = getColCliente("IDZONA", dato);
				long idZonaL = Long.parseLong(idZona);
				if (idZonaL > 1) {
					Zona zona = SESSIONgetZona(idZona, session);
					sucursal.setZona(zona);
				}
			} catch (Exception e) {
				// NADA, no tiene zona asignada
			}
		} else {
			idPersona = getColProveedor("IDPERSONA", dato);
		}
		empresa.setCodigoEmpresa(idPersona + "-" + empresa.getCodigoEmpresa());
		// ------ localidad ------------------
		try {
			String idLocalidad = getColCliente("IDCIUDAD", dato);
			long idLocalidadL = Long.parseLong(idLocalidad);
			if (idLocalidadL > -1) {
				Localidad lo = SESSIONgetLocalidad(idLocalidad, session);
				sucursal.setLocalidad(lo);
			}
		} catch (Exception e) {
			// NADA, no tiene localidad
		}

		empresa.getSucursales().add(sucursal);

		// ---------- Contacto ------------------
		String nombreContacto = (getColCliente("NOMBRE", dato) + getColCliente(
				"APELLIDO", dato)).trim();
		if (nombreContacto.length() > 4) {
			Contacto contacto = new Contacto();
			contacto.setNombre(nombreContacto);

			String mail = getColCliente("MAIL", dato);
			contacto.setCorreo(mail);

			empresa.getContactos().add(contacto);
			contacto.setSucursal(sucursal);
		}

		// ------ Fin sucursal - contacto -----------------

		// ------------- FIN -----------------

		return empresa;
	}

	public static Cliente getCliente(String[] dato) {
		Cliente cl = new Cliente();

		String idPersona = getColCliente("IDPERSONA", dato);
		int p = ClientesMigraPrioridad.getPrioridad(idPersona);

		cl.setPrioridad(p);
		cl.setIdPersonaJedi(idPersona);
		cl.setCategoriaCliente(categoriaClienteNodefinida);
		cl.setTipoCliente(tipoClienteNoDefinida);

		// el listado es de sólo clientes activos
		cl.setEstadoCliente(estadoClienteActivo);

		return cl;
	}

	static Tipo regimenTributarioExenta;
	static Tipo regimenTributarioNoExenta;
	static Tipo monedaDefault;
	static Tipo paisPy;
	static Tipo paisNoDefinido;
	static Tipo personaFisica;
	static Tipo personaJuridica;
	static Tipo categoriaClienteNodefinida;
	static Tipo tipoClienteNoDefinida;
	static Tipo estadoClienteActivo;
	static EmpresaGrupoSociedad grupoSociedadNoDefinido;

	public static void cargaValoresDehault() throws Exception {

		// empresa grupo: Sin Grupo

		// regimen tributario: No Exenta
		regimenTributarioExenta = rr
				.getTipoPorSigla(Configuracion.SIGLA_REGIMEN_TRIB_EXENTA);
		regimenTributarioNoExenta = rr
				.getTipoPorSigla(Configuracion.SIGLA_REGIMEN_TRIB_NO_EXENTA);

		// pais: Py
		paisPy = rr.getTipoPorSigla(Configuracion.SIGLA_PAIS_PARAGUAY);
		paisNoDefinido = rr
				.getTipoPorSigla(Configuracion.SIGLA_PAIS_NO_DEFINIDO);

		// moneda: Gs
		monedaDefault = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);

		// Persona física y jurídica
		personaFisica = rr.getTipoPorSigla("PF");
		personaJuridica = rr.getTipoPorSigla("PJ");

		// Categoria y tipo de cliente no definodos
		categoriaClienteNodefinida = rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_CATEGORIA_CLIENTE, 1);
		tipoClienteNoDefinida = rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_CLIENTE_TIPO_NO_DEFINIDO, 1);
		estadoClienteActivo = rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_ESTADO_CLIENTE, 1);

		/*
		 * categoriaClienteNodefinida = rr.getCategoriaClienteById(1);
		 * tipoClienteNoDefinida = rr.getTipoClienteById(1); estadoClienteActivo
		 * = rr
		 * .getEstadoClienteByDescripcion(Configuracion.ESTADO_CLIENTE_ACTIVO);
		 */

		grupoSociedadNoDefinido = rr
				.getGrupoEmpresaByDescripcion(Configuracion.EMPRESA_GRUPO_NO_DEFINIDO);

	}

	public static boolean esCliente(String[] dato) {
		boolean out = false; // proveedor
		if (dato.length > colProveedores.length) {
			out = true; // cliente
		}
		return out;
	}

	public static void cargaClientes(boolean xcliPro) throws Exception {
		// chequear que parseo 24 campos
		checkArchivo(false, xcliPro);
		// Prioridad de clientes
		ClientesMigraPrioridad.prioridadCliente(false);

		Session session = rr.SESSIONgetSession();
		Transaction tx = null;

		String linea = "";

		BufferedReader entrada = abrirArchivo(xcliPro);
		// leer la cabecera y linea de numeros
		linea = entrada.readLine() + " ";
		// linea = entrada.readLine() + " ";

		long p = 0;
		long np = 0;
		// recorrer el archivo
		while (entrada.ready()) {
			/*
			 * System.out.println("-------p:"+p); while ((p % 100) != 0){ p++;
			 * linea = entrada.readLine() + " "; }
			 * System.out.println("-------p:"+p);
			 */

			p++;
			if (tx == null) {
				tx = session.beginTransaction();
			}
			// lee el dato
			linea = entrada.readLine() + " ";

			try {
				String[] dato = parserLinea(linea);
				System.out.println(linea);
				boolean cliPro = esCliente(dato);

				Empresa em = getEmpresa(dato, session, cliPro);
				String idPersona = "";
				if (cliPro == true) {
					idPersona = getColCliente("IDPERSONA", dato);
				} else {
					idPersona = getColProveedor("IDPERSONA", dato);
				}
				em.setCodigoEmpresa("-" + idPersona + "-"
						+ em.getCodigoEmpresa());

				if (em.esNuevo() == false) {
					// acrtualizar la empresa
					// rr.saveObject(em, "MIG");
					grabarDB(em, session);

					if (cliPro == true) {
						// para grabar la prioridad más alta

						int pos1 = ClientesMigraPrioridad
								.getPrioridad(idPersona);
						Cliente cl = (Cliente) rr
								.SESSIONgetObject(Cliente.class.getName(),
										"empresa", em, session);

						String observacion = getColCliente("OBSERVACION", dato);
						if (observacion == null) {
							observacion = "";
						}
						cl.setObservaciones(observacion);

						long pos2 = cl.getPrioridad();
						if (pos1 < pos2) {
							cl.setPrioridad(pos1);
							grabarDB(cl, session);
						}
					}

					if (cliPro == false) {
						// mover este código, estoy insertando en dos lados los
						// proveedores :(

						Long pAux = existeProveedor.get(em.getId());
						if (pAux == null) {
							// proveedor
							String idSector = getColProveedor(
									"IDPERSONA_SECTOR", dato);
							if (idSector.compareTo("2") == 0) {
								em.setPais(paisNoDefinido);
							}

							Proveedor pro = new Proveedor();
							grabarDB(pro, session);
							pro.setEmpresa(em);

							grabarDB(pro, session);
							println(p + ") " + em.getRazonSocial(), p);
							existeProveedor.put(em.getId(), pro.getId());
						}
					}

					println(p + ") [SUCURSAL] " + em.getRazonSocial(), p);
				} else {
					// cuando la empresa NO es nueva

					if (cliPro == true) {
						// crea cliente y graba todo
						Cliente cl = getCliente(dato);
						cl.setEmpresa(em);

						// rr.saveObject(cl, "MIG");
						grabarDB(cl, session);
						println(p + ") [" + cl.getPrioridad() + "] "
								+ em.getRazonSocial(), p);

						existeCliente.put(em.getId(), cl.getId());

					} else {
						// proveedor

						String idSector = getColProveedor("IDPERSONA_SECTOR",
								dato);
						if (idSector.compareTo("2") == 0) {
							em.setPais(paisNoDefinido);
						}
						Proveedor pro = new Proveedor();
						grabarDB(pro, session);
						pro.setEmpresa(em);
						grabarDB(pro, session);
						println(p + ") " + em.getRazonSocial(), p);
						existeProveedor.put(em.getId(), pro.getId());

					}
				}

			} catch (Exception e) {
				println("err [" + p + "] " + e.getMessage() + " - " + linea
						+ "\n" + linea);
				e.printStackTrace();
			}

			tx = commitDB(p, session, tx);
		}
		if (tx != null) {
			tx.commit();
		}

		rr.SESSIONcloseSession(session);

		System.out.println("Clientes total:" + p + "\n"
				+ "Clientes con problemas:" + np);
	}

	public static void dropTablas() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		System.out.println("..... borrand Cliente..");
		rr.deleteAllObjects(Cliente.class.getName());
		System.out.println("..... borrand Proveedor..");
		rr.deleteAllObjects(Proveedor.class.getName());
		System.out.println("..... borrand Empresa..");
		rr.deleteAllObjects(Empresa.class.getName());
		System.out.println("..... borrand Sucursal..");
		rr.deleteAllObjects(Sucursal.class.getName());
		System.out.println("..... borrand Contacto..");
		rr.deleteAllObjects(Contacto.class.getName());
		System.out.println(".......................  0km");

	}

	// =================================================================
	// ==== Migrar las cuentas de los clientes y proveedores ===========
	// =================================================================

	static Cliente buscarClienteByCodigoEmpresa(String idPersona)
			throws Exception {
		Cliente cl = null;

		String query = "Select cl from Cliente cl where cl.empresa.codigoEmpresa like '%-"
				+ idPersona + "-%' ";
		List<Cliente> l =  rr.hql(query);
		
		if(l.size() == 0)
			return null;
		
		cl = l.get(0);
		
		return cl;
	}

	/**
	 * Metodo para obtener el Proveedor de acuerdo al codigo de la empresa
	 * 
	 * @param idPersona
	 *            Representa el codigo de la empresa(idPersona de Jedi)
	 * @return Proveedor
	 * @throws Exception
	 */
	static Proveedor buscarProveedorByCodigoEmpresa(String idPersona)
			throws Exception {
		Proveedor proveedor;
		String query = "select pro from Proveedor pro where pro.empresa.codigoEmpresa like '%-"
				+ idPersona + "-%' ";
		proveedor = (Proveedor) rr.hqlToObject(query);
		return proveedor;
	}

	
	/**
	 * Este metodo migra la cuenta corriente correspondiente a todos 
	 * los proveedores, ademas de sus movimientos.
	 * @throws Exception
	 */
	public static void migrarCuentasProveedores() throws Exception {

		
		// Abrir el archivo con los datos
		BufferedReader entrada = abrirArchivoPath(proveedoresCuentasCSV);

		// Lee la cabecera (no se trata)
		String linea = entrada.readLine();
		
		// Recorrer el archivo
		while (entrada.ready()) {


				// Leer la fila corriente
				linea = entrada.readLine();
				String[] dato = parserLinea(linea);

				String idPersona = getColProveedoresCuentas("IDPERSONA", dato).trim();
				String observacion = getColProveedoresCuentas("OBSERVACION", dato);
				Date FECHAAPERTURA = getDatoDateCuentasProveedor("FECHAAPERTURA",dato);
				String IDCONDICION = getColProveedoresCuentas("IDCONDICION", dato);
				String CONDICION = getColProveedoresCuentas("CONDICION", dato);
				String IDPLANVENTA = getColProveedoresCuentas("IDPLANVENTA", dato);
				String PLANVENTA = getColProveedoresCuentas("PLANVENTA", dato);
				String IDMONEDA = getColProveedoresCuentas("IDMONEDA", dato);
				double SALDO = getDatoDoubleCuentasProveedor("SALDO", dato);
				double SALDOUSD = getDatoDoubleCuentasProveedor("SALDOUSD", dato);
				String ESTADO = getColProveedoresCuentas("ESTADO", dato);
				String DESCRIPCION_ESTADO = getColProveedoresCuentas("DESCRIPCION_ESTADO", dato);
				String DESCRIPCION = getColProveedoresCuentas("DESCRIPCION", dato);
				String TIPO = getColProveedoresCuentas("TIPO", dato);
				String IDPLANCUENTA = getColProveedoresCuentas("IDPLANCUENTA",dato);
				
				//Buscar el proveedor
				Proveedor proveedor = buscarProveedorByCodigoEmpresa(idPersona);
				
				if (proveedor == null) {
					throw new Exception("No se encontró el proveedor: "	+ idPersona);
				}
				
				/*
				 * Si aun no tiene asignada una Cta. Cte. crea una nueva con los Datos 
				 * procesados provenientes de la informacion del proveedor. Sino almacena
				 * la informacion del proveedor en la Cta. Cte. ya existente.
				 */
				
				CtaCteEmpresa ctaCteExistente = proveedor.getEmpresa().getCtaCteEmpresa();
				
				if (ctaCteExistente  == null) {	
					CtaCteEmpresa nuevaCtaCte = new CtaCteEmpresa();
					nuevaCtaCte.setFechaAperturaCuentaProveedor(FECHAAPERTURA);
					nuevaCtaCte.setEstadoComoProveedor(ctaCteMigracion.getEstadoComoClienteProveedor(DESCRIPCION_ESTADO));
					rr.saveObject(nuevaCtaCte, "MIG");
					proveedor.getEmpresa().setCtaCteEmpresa(nuevaCtaCte);
					
				}else{
					ctaCteExistente.setFechaAperturaCuentaProveedor(FECHAAPERTURA);
					ctaCteExistente.setEstadoComoProveedor(ctaCteMigracion.getEstadoComoClienteProveedor(DESCRIPCION_ESTADO));
					rr.saveObject(ctaCteExistente, "MIG");
					proveedor.getEmpresa().setCtaCteEmpresa(ctaCteExistente);
				}
				
				// ToDo observación hay que ponerlo en la agenda de la empresa
			
				System.out.println("Proveedor:" + idPersona + "-" + proveedor.getId() + "- " + proveedor.getRazonSocial());

				Object[] dd = ctaCteProveedorTodo.get(idPersona);
				
				if (dd == null){
					System.err.println("No tiene pendientes: " + idPersona);
				}else{
					
					ArrayList<String[]> pendiente = (ArrayList<String[]>) dd[1];
					
					for (Iterator iterator = pendiente.iterator(); iterator.hasNext();) {
						
						String[] datos = (String[]) iterator.next();

						String det_IDPERSONA = detalladoProveedores.getDato("IDPERSONA", datos);
						java.util.Date det_FECHA = detalladoProveedores.getDatoDate("FECHA", datos);
						java.util.Date det_VENCIMIENTO = detalladoProveedores.getDatoDate("VENCIMIENTO", datos);
						String det_LOCAL = detalladoProveedores.getDato("LOCAL",datos);
						String det_BOCA = detalladoProveedores.getDato("BOCA",datos);
						String det_NROMOVIMIENTO = detalladoProveedores.getDato("NROMOVIMIENTO", datos);
						String det_OBSERVACION =detalladoProveedores.getDato("OBSERVACION", datos);
						String det_CONCEPTO = detalladoProveedores.getDato("CONCEPTO", datos);
						String det_FACTURA = detalladoProveedores.getDato("FACTURA", datos);
						String det_PERSONA = detalladoProveedores.getDato("PERSONA", datos);
						String det_RUC =detalladoProveedores.getDato("RUC", datos);
						String det_TELEFONO =detalladoProveedores.getDato("TELEFONO", datos);
						String det_DIRECCION = detalladoProveedores.getDato("DIRECCION", datos);
						String det_IDMONEDA = detalladoProveedores.getDato("IDMONEDA", datos);
						double det_TOTALEXENTA = detalladoProveedores.getDatoDouble("TOTALEXENTA", datos);
						double det_TOTALGRAVADA = detalladoProveedores.getDatoDouble("TOTALGRAVADA", datos);
						double det_TOTALIVA = detalladoProveedores.getDatoDouble("TOTALIVA", datos);
						double det_TOTAL = detalladoProveedores.getDatoDouble("TOTAL", datos);

						/*
						 * Se procede a procesar los datos de cada movimiento hallando sus equivalencias
						 * en el sistema y asignandolas a un nuevo movimiento.
						 */
						CtaCteEmpresaMovimiento ctaMov = new CtaCteEmpresaMovimiento();
						ctaMov.setIdEmpresa(proveedor.getEmpresa().getId());
						ctaMov.setFechaEmision(det_FECHA);
						ctaMov.setFechaVencimiento(det_VENCIMIENTO);
						String local = String.format("%03d",Integer.parseInt(det_LOCAL));
						String boca = String.format("%03d",Integer.parseInt(det_BOCA));
						String nroMovimiento = String.format("%07d",Integer.parseInt(det_NROMOVIMIENTO));
						ctaMov.setNroComprobante(local + "-" + boca + "-"+ nroMovimiento);
						ctaMov.setTipoMovimiento(ctaCteMigracion.getTipoMovimientoPorDetFacturaCompraGasto(det_CONCEPTO));
						ctaMov.setImporteOriginal(det_TOTAL);
						ctaMov.setSaldo(det_TOTAL);
						ctaMov.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
						ctaMov.setMoneda(ctaCteMigracion.getMoneda(det_IDMONEDA));
						ctaMov.setSucursal(rr.getSucursalAppByNombre(Configuracion.ID_SUCURSALAPP_CENTRAL_MRA));
						rr.saveObject(ctaMov, "MIG");
					}
				}
				
				rr.saveObject(proveedor, "MIG");

		}

	}
	
	public static void migrarCuentasCliente() throws Exception {

		// abrir el archivo con los datos
		BufferedReader entrada = abrirArchivoPath(clientesCuentasCSV);

		// lee la cabecera (no se trata)
		String linea = entrada.readLine();
		// recorrerlo
		while (entrada.ready()) {


				// leer la fila corriente
				linea = entrada.readLine();
				String[] dato = parserLinea(linea);

				String idPersona = getColClientesCuentas("IDPERSONA", dato);
				String observacion = getColClientesCuentas("OBSERVACION", dato);
				Date FECHAAPERTURA = getDatoDateCuentasCliente("FECHAAPERTURA",
						dato);
				String IDCONDICION = getColClientesCuentas("IDCONDICION", dato);
				String CONDICION = getColClientesCuentas("CONDICION", dato);
				double LIMITECREDITO = getDatoDoubleCuentasCliente(
						"LIMITECREDITO", dato);
				String IDPLANVENTA = getColClientesCuentas("IDPLANVENTA", dato);
				String PLANVENTA = getColClientesCuentas("PLANVENTA", dato);
				String IDVENDEDOR = getColClientesCuentas("IDVENDEDOR", dato);
				String NOMBRE = getColClientesCuentas("NOMBRE", dato) + " "
						+ getColClientesCuentas("APELLIDO", dato);
				String IDMONEDA = getColClientesCuentas("IDMONEDA", dato);
				double SALDO = getDatoDoubleCuentasCliente("SALDO", dato);
				double SALDOUSD = getDatoDoubleCuentasCliente("SALDOUSD", dato);
				String ESTADO = getColClientesCuentas("ESTADO", dato);
				String DESCRIPCION_ESTADO = getColClientesCuentas(
						"DESCRIPCION_ESTADO", dato);
				String DESCRIPCION = getColClientesCuentas("DESCRIPCION", dato);
				String TIPO = getColClientesCuentas("TIPO", dato);
				String IDPLANCUENTA = getColClientesCuentas("IDPLANCUENTA",
						dato);

				// tratar los datos ===================================

				// buscar el cliente
				Cliente cl = buscarClienteByCodigoEmpresa(idPersona);
				if (cl == null) {
					throw new Exception("No se encontró el cliente: "
							+ idPersona);
				}

				if (cl.getEmpresa().getCtaCteEmpresa() == null) {
					// cl.getEmpresa().setCtaCteEmpresa(new CtaCteEmpresa());
				}

				// cl.getEmpresa().getCtaCteEmpresa().setFechaAperturaCuentaCliente(FECHAAPERTURA);
				// ToDo observación hay que ponerlo en la agenda de la empresa

				System.out.println("Cliente:" + idPersona + "-" + cl.getId()
						+ "- " + cl.getRazonSocial());

				// crear la ctacte
				CtaCteEmpresa ctaCte = new CtaCteEmpresa();
				ctaCte.setFechaAperturaCuentaCliente(FECHAAPERTURA);
				ctaCte.setEstadoComoCliente(ctaCteMigracion
						.getEstadoComoClienteProveedor(DESCRIPCION_ESTADO));
				ctaCte.setCondicionPagoCliente(ctaCteMigracion
						.getCondicionPagoCliente(CONDICION));
				ctaCte.setLineaCredito(ctaCteMigracion.getLineaCreditoPorMonto(LIMITECREDITO));
				rr.saveObject(ctaCte, "MIG");
				cl.getEmpresa().setCtaCteEmpresa(ctaCte);

				// buscar los detalles
				// está bien buscar por el IdPersona???
				Object[] dd = ctaCteTodo.get(idPersona);
				if (dd == null) {
					System.err.println("NO tiene pendientes " + idPersona);

				} else {
					ArrayList<String[]> pendiente = (ArrayList<String[]>) dd[1];

					for (Iterator iterator = pendiente.iterator(); iterator
							.hasNext();) {
						String[] datos = (String[]) iterator.next();

						String det_IDPERSONA = detallado.getDatoDetalle(
								"IDPERSONA", datos);
						java.util.Date det_FECHA = detallado
								.getDatoDateDetalle("FECHA", datos);
						java.util.Date det_VENCIMIENTO = detallado
								.getDatoDateDetalle("VENCIMIENTO", datos);
						String det_LOCAL = detallado.getDatoDetalle("LOCAL",
								datos);
						String det_BOCA = detallado.getDatoDetalle("BOCA",
								datos);
						String det_NROMOVIMIENTO = detallado.getDatoDetalle(
								"NROMOVIMIENTO", datos);
						String det_OBSERVACION = detallado.getDatoDetalle(
								"OBSERVACION", datos);
						String det_FACTURA = detallado.getDatoDetalle(
								"FACTURA", datos);
						String det_PERSONA = detallado.getDatoDetalle(
								"PERSONA", datos);
						String det_RUC = detallado.getDatoDetalle("RUC", datos);
						String det_TELEFONO = detallado.getDatoDetalle(
								"TELEFONO", datos);
						String det_DIRECCION = detallado.getDatoDetalle(
								"DIRECCION", datos);
						String det_ESTADO = detallado.getDatoDetalle("ESTADO",
								datos);
						String det_IDMONEDA = detallado.getDatoDetalle(
								"IDMONEDA", datos);
						double det_LIMITECREDITO = detallado
								.getDatoDoubleDetalle("LIMITECREDITO", datos);
						double det_TOTALEXENTA = detallado
								.getDatoDoubleDetalle("TOTALEXENTA", datos);
						double det_TOTALGRAVADA = detallado
								.getDatoDoubleDetalle("TOTALGRAVADA", datos);
						double det_TOTALIVA = detallado.getDatoDoubleDetalle(
								"TOTALIVA", datos);
						double det_TOTAL = detallado.getDatoDoubleDetalle(
								"TOTAL", datos);
						// String det_IDVENDEDOR =
						// detallado.getDatoDetalle("IDVENDEDOR", datos);
						// String det_VENDEDOR =
						// detallado.getDatoDetalle("VENDEDOR", datos);
						// String det_IDVENDEDOR1 =
						// detallado.getDatoDetalle("IDVENDEDOR1", datos);

						CtaCteEmpresaMovimiento ctaMov = new CtaCteEmpresaMovimiento();
						ctaMov.setIdEmpresa(cl.getEmpresa().getId());
						ctaMov.setFechaEmision(det_FECHA);
						ctaMov.setFechaVencimiento(det_VENCIMIENTO);
						String local = String.format("%03d",
								Integer.parseInt(det_LOCAL));
						String boca = String.format("%03d",
								Integer.parseInt(det_BOCA));
						System.out.println("Numero del movimiento: " + det_NROMOVIMIENTO);
						String nroMovimiento = String.format("%07d",
								Integer.parseInt(det_NROMOVIMIENTO));
						ctaMov.setNroComprobante(local + "-" + boca + "-"
								+ nroMovimiento);
						ctaMov.setTipoMovimiento(ctaCteMigracion
								.getTipoMovimientoPorDetFacturaVenta(det_FACTURA));
						ctaMov.setImporteOriginal(det_TOTAL);
						ctaMov.setSaldo(det_TOTAL);
						ctaMov.setTipoCaracterMovimiento(rr
								.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
						ctaMov.setMoneda(ctaCteMigracion
								.getMoneda(det_IDMONEDA));
						ctaMov.setSucursal(rr
								.getSucursalAppByNombre(Configuracion.ID_SUCURSALAPP_CENTRAL_MRA));
						rr.saveObject(ctaMov, "MIG");
					}

				}

				rr.saveObject(cl, "MIG");

		}

	}

	// =================================================================
	// =================================================================
	// =================================================================

	public static void xmain(String[] args) throws Exception {

		long ini = System.currentTimeMillis();

		cargaClientes(true); // clientes
		// cargaClientes(false); // proveedores

		long fin = System.currentTimeMillis();
		System.out.println("Tiempo en minutos:"	+ ((((fin - ini) + 0.0) / 1000) / 60));
	}
}
