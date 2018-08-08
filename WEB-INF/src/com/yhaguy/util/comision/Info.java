package com.yhaguy.util.comision;

import java.util.*;

import com.coreweb.util.Misc;

/* informacion para hacer los calculos de comisiones */

public class Info extends Cons {

	public static Hashtable<String, Venta> difRecibos = new Hashtable<String, Venta>();

	// para llevar un total de ventas (Natalia Crespi)
	public static Vendedor VENDEDOR_TOTAL = new VendedorMostrador();

	// tabla de vendedores externos
	public static Hashtable<String, VendedorExterno> vendedoresExternos = new Hashtable<String, VendedorExterno>();
	public static Hashtable<String, VendedorExterno> vendedoresExternosByUser = new Hashtable<String, VendedorExterno>();

	// tabla de vendedores de mostrador
	public static Hashtable<String, VendedorMostrador> vendedoresMostrador = new Hashtable<String, VendedorMostrador>();
	public static Hashtable<String, VendedorMostrador> vendedoresMostradorById = new Hashtable<String, VendedorMostrador>();

	// tabla de Auxiliares
	public static Hashtable<String, List<Rango>> vendedoresAuxiliares = new Hashtable<String, List<Rango>>();
	public static Hashtable<String, List<Rango>> vendedoresAuxiliaresById = new Hashtable<String, List<Rango>>();

	// tabla de vendedores externos
	public static Hashtable<String, VendedorCrespi> vendedoresCrespi = new Hashtable<String, VendedorCrespi>();
	public static Hashtable<String, VendedorCrespi> vendedoresCrespiById = new Hashtable<String, VendedorCrespi>();

	// tabla de metas
	public static Hashtable<String, double[]> metas = new Hashtable<String, double[]>();
	public static Hashtable<String, String> auxMetasConocidas = new Hashtable<String, String>();

	public static void defineMetas() {
		metas.put(meta0, metaV0);
		metas.put(metaCO, metaCOv);
		metas.put(metaDF, metaDFv);
		metas.put(metaGN, metaGNv);
		metas.put(metaLR, metaLRv);
		metas.put(metaSS, metaSSv);
		metas.put(metaDefault, metaVD);

		// carga metas conocidas
		// carga las metas que se conocen
		for (int i = 0; i < idVendedorPorMeta.length; i++) {
			String[] idVMeta = idVendedorPorMeta[i];
			String idV = idVMeta[0];
			String meta = idVMeta[1];

			auxMetasConocidas.put(idV, meta);
		}

	}

	/* carga los USUARIO con los ID que se conocen */
	public static void cargaVendedores() throws Exception {

		// Carga el vendedor total, el que lleva el total de las ventas
		VENDEDOR_TOTAL.setId("ID-TOTAL");
		VENDEDOR_TOTAL.setUsuario("USU-TOTAL");
		// vendedoresMostrador.put(VENDEDOR_TOTAL.getUsuario(),
		// (VendedorMostrador) VENDEDOR_TOTAL);
		// vendedoresMostradorById.put(VENDEDOR_TOTAL.getId(),
		// (VendedorMostrador) VENDEDOR_TOTAL);

		for (int i = 0; i < usuarioVendedor.length; i++) {
			Object[] d = usuarioVendedor[i]; // tipo usuario idvendedor
			int tipo = (int) d[0];
			String usuario = ((String) d[1]).trim();
			String idV = ((String) d[2]).trim();

			if (tipo == EXTERNO) {
				VendedorExterno ve = new VendedorExterno();
				ve.setId(idV);
				ve.setUsuario(usuario);

				// si se conoce la meta
				String meta = auxMetasConocidas.get(idV);
				if (meta == null) {
					meta = metaDefault;
				}
				ve.setIdMeta(meta);
				ve.setMeta(metas.get(meta));

				vendedoresExternos.put(idV, ve);
				vendedoresExternosByUser.put(usuario, ve);

			} else if (tipo == MOSTRADOR) {
				VendedorMostrador vm = new VendedorMostrador();
				vm.setId(idV);
				vm.setUsuario(usuario);

				vendedoresMostrador.put(usuario, vm);
				vendedoresMostradorById.put(idV, vm);

			} else if (tipo == AUXILIAR) {
				error("** Error AUXILIAR - " + idV + " - " + usuario + ".");
				throw new Exception(
						"Error de tipo (AUXILIAR) en el arreglo (usuarioVendedor)"
								+ idV + " - " + usuario + ".");
				/*
				 * VendedorMostrador vm = new VendedorMostrador();
				 * vm.setId(idV); vm.setUsuario(usuario);
				 * 
				 * vendedoresAuxiliares.put(usuario, vm);
				 */

			} else if (tipo == LOS_CRESPI) {
				VendedorCrespi vm = new VendedorCrespi();
				vm.setId(idV);
				vm.setUsuario(usuario);

				vendedoresCrespi.put(usuario, vm);
				vendedoresCrespiById.put(idV, vm);

			} else {
				throw new Exception("Error de tipo (" + tipo
						+ ") en el arreglo (usuarioVendedor)");
			}

		}

		// carga los auxiliares
		for (int i = 0; i < auxiliaresTemporales.length; i++) {
			// {"PROLON", "id-most", new Rango("1900.01.01", "2020.12.31")},

			Object[] d = auxiliaresTemporales[i]; // tipo usuario idvendedor
			String usuario = ((String) d[0]).trim();
			String idV = ((String) d[1]).trim();

			List<Rango> lr = new ArrayList<Rango>();
			for (int j = 2; j < d.length; j++) {
				Rango r = (Rango) d[j];
				lr.add(r);
			}

			vendedoresAuxiliares.put(usuario, lr);
			vendedoresAuxiliaresById.put(idV, lr);

		}

	}

	public static Object getVendedorAuxiliar(String idVendedor, String usuario,
			String fecha) {
		// buscar por idUser
		List<Rango> lr = vendedoresAuxiliares.get(usuario);
		if (lr == null) {
			// buscar por idVendedor
			lr = vendedoresAuxiliaresById.get(idVendedor);
		}

		if (lr != null) {
			boolean siEs = false;
			for (Iterator iterator = lr.iterator(); iterator.hasNext();) {
				Rango r = (Rango) iterator.next();
				// System.out.println("" + usuario + "[ " + r.desde + " - " +
				// r.hasta + " ]" + fecha + " " + r.pertenece(fecha));
				siEs = (siEs || r.pertenece(fecha));

			}
			if (siEs == true) {
				return "SI ES AUXILIAR";
			}
		}
		return null;
	}

	/*
	 * public static VendedorExterno getVendedorExterno(String id, String user)
	 * { VendedorExterno v = null; v = vendedoresExternos.get(id); if (v ==
	 * null) { v = vendedoresExternosByUser.get(user); } return v; }
	 * 
	 * 
	 * 
	 * public static VendedorMostrador getVendedorMostrador(String id, String
	 * user) { VendedorMostrador v = null; v = vendedoresMostrador.get(user); if
	 * (v == null) { v = vendedoresMostradorById.get(id); } return v; }
	 */
	/**
	 * Crea un vendedor externo por dafault
	 */
	public static VendedorExterno crearDefaultVendedorExterno(
			String idVendedor, String user) {
		VendedorExterno vex = new VendedorExterno();
		vex.setId(idVendedor);
		vex.setUsuario(user);

		// poner una meta por default
		vex.setIdMeta(metaDefault);
		vex.setMeta(metas.get(metaDefault));
		vendedoresExternos.put(idVendedor, vex);
		vendedoresExternosByUser.put(user, vex);
		return vex;
	}

	/**
	 * Crea un vendedor externo por dafault
	 */
	public static VendedorMostrador crearDefaultVendedorMostrador(
			String idVendedor, String user) {
		VendedorMostrador vem = new VendedorMostrador();
		vem.setId(idVendedor);
		vem.setUsuario(user);

		vendedoresMostrador.put(user, vem);
		vendedoresMostradorById.put(idVendedor, vem);
		return vem;
	}

	public static Venta rowTablaToVenta(Tabla t) {

		Venta venta = new Venta();

		venta.setId(t.getRow("IDMOVIMIENTO").toString());

		venta.setNroRecibo((String) t.getRow("NRORECIBO"));

		venta.setFechaRecibo((String) t.getRow("FECHA_RECIBO"));

		venta.setMontoTotalRecibo((double) t.getRow("TOTAL_RECIBO"));

		venta.setMontoDetalleRecibo((double) t.getRow("MONTO_DETALLE_RECIBO"));

		venta.setDebe((double) t.getRow("DEBE"));

		venta.setFecha((String) t.getRow("FECHA_MOV"));
		venta.setNroFactura((String) t.getRow("NROMOVIMIENTO"));

		if (venta.getNroFactura().compareTo("21643") == 0) {
			int mm = 0;
			mm++;
		}

		String idVendedor = ((String) t.getRow("IDVENDEDOR"));
		String idUsuario = ((String) t.getRow("IDUSER"));

		venta.setIdVendedor(idVendedor);
		venta.setIdUsuario(idUsuario);

		String apellido = ((String) t.getRow("APELLIDO"));
		String nombre = ((String) t.getRow("NOMBRE"));

		venta.setNombreVendedor(apellido + " " + nombre);
		venta.setIdArticulo((String) t.getRow("IDARTICULO"));
		venta.setArticulo((String) t.getRow("SUBSTR"));

		venta.setIdProveedor((String) t.getRow("IDPROVEEDOR"));
		venta.setProveedor((String) t.getRow("DESCRIPCION_PROVEEDOR"));

		venta.setIdMarca((String) t.getRow("IDMARCA"));
		venta.setMarca((String) t.getRow("DESCRIPCION_MARCA"));

		venta.setImporte((double) t.getRow("GRAVADA"));
		venta.setCantidad((double) t.getRow("CANTIDAD"));
		venta.setPrecioIva((double) t.getRow("PRECIO_IVA"));
		venta.setContado((Boolean) t.getRow("ESCONTADO"));

		venta.setContado((Boolean) t.getRow("ESCONTADO"));

		venta.setIdTipoMovimiento((long) t.getRow("IDTIPOMOVIMIENTO"));

		String idPersona = ((String) t.getRow("IDPERSONA"));
		String persona = ((String) t.getRow("PERSONA"));

		venta.setIdPersona(idPersona);
		venta.setPersona(persona);

		return venta;
	}

	/* recorre las ventas y las asignas a los vendedores */
	public static void recorreVentasCobranzas(Tabla t, boolean esCobranza)
			throws Exception {

		Misc m = new Misc();
		ArrayList<Venta> ventaRecibo = new ArrayList<Venta>();
		boolean mismoParcial = true;
		double auxCCMontoDetalle = 0;
		double auxCCDebe = 0;
		boolean auxPrimera = true;

		t.inicioRecorrido();

		while (t.hayFilas()) {

			t.leerFila();

			Venta v = rowTablaToVenta(t);

			String jd = v.getIdVendedor();
			String us = v.getIdUsuario();

			String rec = v.getNroRecibo();
			String nventa = v.getNroFactura();
			
			if ((esCobranza == true)&&(jd.compareTo("20")==0)&&
					(us.compareTo("RGIMENEZ")==0)&&
					(rec.compareTo("135960")==0)&&
					(nventa.compareTo("5")==0)){
				int x = 10;
				System.out.println(x);
			}
			
			
			
			// ********************************************
			// para clientes que NO suman a la comision ni a la meta

			String idPersona = v.getIdPersona();
			if (esClienteNoConsiderado(idPersona) == true) {
				v.setImporte(0);
			}

			// ********************************************
			// para el calculo de pagos parciales
			if (esCobranza == true) {
				if (auxPrimera == true) {
					auxPrimera = false;
					auxCCDebe = v.getDebe();
					auxCCMontoDetalle = v.getMontoDetalleRecibo();
				}

				mismoParcial = m.esIgual(auxCCDebe, v.getDebe())
						&& m.esIgual(auxCCMontoDetalle,
								v.getMontoDetalleRecibo());

				// ver si cambia de recibo

				if (mismoParcial == true) {

					ventaRecibo.add(v);
				} else {

					calculoPagoParcial(ventaRecibo);
					ventaRecibo = new ArrayList<Venta>();
					ventaRecibo.add(v);
					auxCCDebe = v.getDebe();
					auxCCMontoDetalle = v.getMontoDetalleRecibo();
				}
			}

			// *********** fin pago parcial *****************

			VendedorExterno vexId = vendedoresExternos.get(v.getIdVendedor());
			VendedorExterno vexUs = vendedoresExternosByUser.get(v
					.getIdUsuario());

			VendedorMostrador vemId = vendedoresMostradorById.get(v
					.getIdVendedor());
			VendedorMostrador vemUs = vendedoresMostrador.get(v.getIdUsuario());

			VendedorCrespi vecId = vendedoresCrespiById.get(v.getIdVendedor());
			VendedorCrespi vecUs = vendedoresCrespi.get(v.getIdUsuario());

			// VendedorMostrador veA =
			// vendedoresAuxiliares.get(v.getIdUsuario());
			Object veAvend = getVendedorAuxiliar(v.getIdVendedor(), "--",
					v.getFecha_AAAA_MM_DD());
			Object veAuser = getVendedorAuxiliar("--", v.getIdUsuario(),
					v.getFecha_AAAA_MM_DD());

			Vendedor vendHizoVenta = null;

			// venta de un auxiliar
			if (veAuser != null) {

				// ver que no sea una venta que hizo cuando era auxiliar
				if (veAvend != null) {
					// no deberia cobrar comision por esto

				} else {
					// NO compartida
					v.setEsCompartida(false);
					// asignar la venta al vendedor externo o de mostrador o
					// crespi
					if (vemId != null) {
						// se le asigna la venta al del mostrador
						vemId.addVenta(v, esCobranza);
						vemId.setNombre(v.getNombreVendedor());

						vendHizoVenta = vemId;

					} else if (vecId != null) {
						// se le asigna la venta al del mostrador
						vecId.addVenta(v, esCobranza);
						vecId.setNombre(v.getNombreVendedor());

						vendHizoVenta = vecId;

					} else {
						if (vexId == null) {
							vexId = crearDefaultVendedorExterno(
									v.getIdVendedor(), "v-externo");
						}
						vexId.addVenta(v, esCobranza);
						vexId.setNombre(v.getNombreVendedor());

						vendHizoVenta = vexId;
					}

				}

				/*****************/
			} else {

				// ver que no sea una venta que hizo cuando era auxiliar
				if (veAvend != null) {
					// no deberia cobrar comision por esto
				} else {
					Vendedor veId = null;
					Vendedor veUs = null;

					// definir vendedor
					if (vexId != null) {
						veId = vexId;
					} else if (vemId != null) {
						veId = vemId;
					} else if (vecId != null) {
						veId = vecId;
					} else {
						veId = crearDefaultVendedorMostrador(v.getIdVendedor(),
								"user-mos");
					}
					// definir usuario
					if (vexUs != null) {
						veUs = vexUs;
					} else if (vemUs != null) {
						veUs = vemUs;
					} else if (vecUs != null) {
						veUs = vecUs;
					} else {
						veUs = crearDefaultVendedorMostrador("id-most",
								v.getIdUsuario());
					}

					if (veId == veUs) {
						v.setEsCompartida(false);
						veId.addVenta(v, esCobranza);

						vendHizoVenta = veId;
					} else {
						v.setEsCompartida(true);
						veId.addVenta(v, esCobranza);
						veUs.addVenta(v, esCobranza);

						vendHizoVenta = veId;
					}

					// //////////** OJO **//////
					String vend = v.getIdVendedor().trim();
					String user = v.getIdUsuario().trim();
					if ((vend.compareTo("12") == 0)
							&& (user.compareTo("0") == 0)) {
						v.setEsCompartida(false);
					}

					if ((vend.compareTo("92") == 0)
							&& (user.compareTo("SSANCHEZ") == 0)) {
						// System.out.println("================================================== "+v.getNroFactura());
						v.setEsCompartida(false);
					}

					if (v.getIdUsuario().compareTo("0") == 0) {
						v.setEsCompartida(false);
					}

				}

			}

			// ver si es marca asignada a otro vendedor
			ventaMarcaAsignada(v, vendHizoVenta, esCobranza);

			// para las ventas totales
			if (esCobranza == true) {
				VENDEDOR_TOTAL.addVentaProveedorCobranza(v);
			}

		}
		if (esCobranza == true) {
			// calculo pago parcial
			calculoPagoParcial(ventaRecibo);
		}

		
		
		/*
		 * System.out.println(
		 * "=============================================================");
		 * Collection<VendedorExterno> vec = vendedoresExternos.values(); for
		 * (Iterator iterator = vec.iterator(); iterator.hasNext();) {
		 * VendedorExterno veci = (VendedorExterno) iterator.next();
		 * System.out.println(veci.getId() + " - " + veci.getVentas().size()); }
		 * System.out.println(
		 * "=============================================================");
		 */

	}

	public static void ventaMarcaAsignada(Venta venta, Vendedor hizoVenta,
			boolean esCobranza) {

		if (true){
			return;
		}
		
		if ((hizoVenta == null) || (esCobranza == false)
				|| (venta.getIdTipoMovimiento() == 13)) {
			return;
		}

		for (int i = 0; i < idMarcasVendedorAsignado.length; i++) {
			Object[] row = idMarcasVendedorAsignado[i];
			String idMarca = (String) row[0];
			String idAsignado = (String) row[2];
			String[] listVendedoresGrupo = (String[]) row[3];

			String idMarcaVenta = venta.getIdMarca();
			String idVendedor = hizoVenta.getId();

			if ((idMarca.compareTo(idMarcaVenta) == 0)
					&& (idAsignado.compareTo(idVendedor) != 0)) {
				// ver si el vendedor actual está entre los que se debe considar
				// como
				// compañero para compartir la venta
				for (int j = 0; j < listVendedoresGrupo.length; j++) {
					String idCom = listVendedoresGrupo[j];
					if (idCom.compareTo(idVendedor) == 0) {
						// venta que también hay que asignar a otro vendedor
						// y quitar la venta del que hizo la venta
						Vendedor venAsig = vendedoresMostradorById
								.get(idAsignado);

						hizoVenta.addMarcaNOAsignada(venta);
						venAsig.addMarcaAsignada(venta);

						// System.out.println(idAsignado+":"+venta.printFormato(false));
					}

				}

			}
		}

	}

	public static void calculoPagoParcial(ArrayList<Venta> arr) {

		Venta v0 = arr.get(0);

		if ((v0.getIdTipoMovimiento() == 13)) {
			System.out
					.println("calculoPagoParcial.NDC: No se si deberia entrar por aca.");
			return;
		}
		/*
		 * if ((v0.getNroRecibo().trim().length()<3)){ String nf =
		 * v0.getNroFactura(); if ( ya.get(nf) != null){ for (int i = 0; i <
		 * arr.size(); i++) { Venta v = arr.get(i); v.vaciar(); } return; }else{
		 * ya.put(nf, "ya-procesado"); return; } }
		 */

		if (v0.getNroFactura().trim().compareTo("42017") == 0) {
			System.out.println("parar aca");
		}

		// todos deberìan tener el mismo monto
		double montoDetalle = v0.getMontoDetalleRecibo();
		double debe = v0.getDebe();
		double por = montoDetalle / debe;
		double dif = 0.001 * 0.001;
		if ((1 - por) < 0.0000001) {
			por = 1;
		}
		if (por < 1) {
			// es venta parcial, gravar diferencia
			for (int i = 0; i < arr.size(); i++) {
				Venta v = arr.get(i);
				v.setGrabadoOriginal(v.getImporte());
				v.setPorcentajeParcial(por);
				v.setParcial();
				v.setImporte(v.getImporte() * por);
			}
		} else if (por > 1) {
			Cons.error("porcentaje mayor que 1:" + v0.toString());
		}

		for (int i = 0; i < arr.size(); i++) {
			Venta v = arr.get(i);
			if (v.getDebe() != debe) {
				Cons.error("Los DEBE deberian ser iguales a (" + debe + "):["
						+ v.getDebe() + "]" + v.toString());
			}
		}

	}

	public static double getPorcentaje(Venta venta, int indiceMeta,
			int tipoVendedor, String idVendedor, Vendedor ve) {

		boolean esCompartida = venta.isEsCompartida();
		boolean esContado = venta.isContado();
		String idProveedor = venta.getIdProveedor();
		String idPersona = venta.getIdPersona();
		String idMarca = venta.getIdMarca();
		

		// ver si no es un cliente con porcentaje especial
		double porCliente = getPorcentajeClienteEspecial(idPersona);
		if (porCliente > 0) {
			// saber si es siempre compartida o no
			if (getPorcentajeClienteEspecialNOCompartida(idPersona)==true){
				venta.setEsCompartida(false);
			}
			
			return porCliente;
		}
		// ***************************************
		// ver si la marca no tiene un porcentaje especial
		double porMarca = getPorcentajeMarcaEspecial(idMarca);
		if (porMarca > 0) {
			return porMarca;
		}
		
		

		// ***************************************
		int indice = 0;
		double out = 0;
		// Obtiene porcentaje para los Vendedores Externos
		if (tipoVendedor == EXTERNO) {
			if (esCompartida == true) {
				indice = indiceMetaMinima;
			} else if (esContado == true) {
				indice = indiceMetaMaxima;
			} else {
				indice = indiceMeta;
			}

			Object[] op = porcentajePorProveedor.get(idProveedor);
			if (op == null) {
				op = porcentajePorProveedor.get(idPorcDefault);
			}
			double[] listaPorcentajes = (double[]) op[2];

			out = listaPorcentajes[indice];

			if (esCompartida == true) {
				out = PORC_EXTERNO_COBRANZA;
				out = (double) out / 2;
			}
		}
		// Obtiene porcentaje para los Vendedores de Mostrador
		if (tipoVendedor == MOSTRADOR) {
			if (esContado == true) {
				out = getPorcMostrador(idVendedor, CONTADO);
				// out = PORC_MOSTRADOR_CONTADO;
			} else {
				out = getPorcMostrador(idVendedor, COBRANZA);
				// out = PORC_MOSTRADOR_COBRANZA;
			}
			if (esCompartida) {
				out = out / 2;
			}
		}
		if (tipoVendedor == LOS_CRESPI) {
			// dr aca
			// Marca en ventas, para las metas de los Crespi
			String marcaGrupo = ConsCrespi.getMarcaGrupo(idMarca, idVendedor);

			ResumenMarca rma = ve.getResumenMarca().get(marcaGrupo);
			double importe = 0;
			if (rma != null){
				importe = rma.getTotalVenta();
			}

			
			out = ConsCrespi.getPorcentaje(idVendedor, idMarca, importe,
					venta.isContado());
			
			if (esCompartida == true){
				out = (double) out / 2;
			}
			
		}
		
		// si que la venta no corresponda con un proveedor que NO paga comision
		if (cobraComision(idVendedor, venta.getIdProveedor()) == false){
			out = 0; 
		}else{
			
			// ver si no era porcentaje especial por proveedor
			String k = "";
			Double[] porVenProv = {};
			
			k = idVendedor+"-"+idProveedor;
			porVenProv = comisionEspecialPorVendedorProveedor.get(k);
			
			if (porVenProv == null){
				// ver si no es para todos
				k = "TD"+"-"+idProveedor;
				porVenProv = comisionEspecialPorVendedorProveedor.get(k);
			}
			
			if (porVenProv != null){
				if (esContado == true){
					out = porVenProv[0];
				}else{
					out = porVenProv[1];
				}
				if (esCompartida == true){
					out = (double) out / 2;
				}
			}
			
		}

		return out;
	}

	public static String printArrayNumero(double[] ar, int largo, double mul) {
		Misc m = new Misc();
		String out = "";
		for (int i = 0; i < ar.length; i++) {
			double d = ar[i];
			out += m.formato((d * mul), largo, false, true);
		}
		return out;
	}

	public static String printDatosUsado() {
		Misc m = new Misc();
		String out = "";

		out += "Mes considerado: " + MES_CORRIENTE + "\n";

		/*
		 * out += "Porcentaje Mostrador contado: ********** ahora es una tabla "
		 * + m.formato((PORC_MOSTRADOR_CONTADO * 100), 5, false) + "%\n"; out +=
		 * "Porcentaje Mostrador credito: " + m.formato((PORC_MOSTRADOR_COBRANZA
		 * * 100), 5, false) + "%\n"; out += "\n";
		 */

		out += "Porcentajes de comisiones segun las proveedores\n";
		out += "------------------------------------------\n";
		out += m.formato("Prove", 34);
		out += m.formato("Grupo", 10);
		for (int i = 0; i < 10; i++) {
			out += m.formato(i + "", 6, false);
		}
		out += "\n";
		out += m.repeatSrt("-", (44 + (6 * 10))) + "\n";

		ArrayList<Object[]> cc = new ArrayList(porcentajePorProveedor.values());
		Collections.sort(cc, new ArrayComparator());

		Iterator<Object[]> itePM = cc.iterator();
		while (itePM.hasNext()) {
			Object[] op = itePM.next();
			String key = (String) op[0];
			String proveedor = (String) op[1];
			double[] po = (double[]) op[2];
			String proveedorPo = (String) op[3];

			out += "(" + m.formato(key, 5, false) + ") "
					+ m.formato(proveedor, 25, true) + " "
					+ m.formato(proveedorPo, 10) + ""
					+ printArrayNumero(po, 6, 100) + "\n";
		}
		out += m.repeatSrt("-", (44 + (6 * 10))) + "\n";

		// ****************************************************************

		out += "\n\n";
		out += "Tablas de metas (en millones) usadas en los Vendedores Externos\n";
		out += "---------------------------------------------------------------\n";

		out += m.formato("Metas", 8);
		for (int i = 0; i < 10; i++) {
			out += m.formato(i + "", 8, false);
		}
		out += "\n";
		out += m.repeatSrt("-", (8 + (8 * 10))) + "\n";
		Iterator<String> iteMe = metas.keySet().iterator();
		while (iteMe.hasNext()) {
			String key = iteMe.next();
			double[] op = metas.get(key);
			double[] opAux = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			for (int i = 0; i < op.length; i++) {
				opAux[i] = op[i] / 1000000;
			}
			out += m.formato(key, 8) + printArrayNumero(opAux, 8, 1) + "\n";
		}
		out += m.repeatSrt("-", (8 + (8 * 10))) + "\n\n";

		out += Cons.textoPorDevolucion();

		out += Cons.textoUsuariosAuxiliares();

		out += Cons.textoClienteNoConsiderados();
		out += Cons.textoPorcentajeEspecialClientes();
		out += Cons.textoPorcentajeEspecialMarca();
		
		out += Cons.textoVendedoresSinComisionXProveedor();
		out += Cons.textoComisionEspecialPorVendedorProveedor();

		return out;
	}

	public static void calculaComisiones() throws Exception {

		Misc m = new Misc();

		String resumen = "";
		resumen += printDatosUsado();
		resumen += "\n\n";

		/*
		 * primero a los vendedores externos, porque dependiendo del porcentaje,
		 * que obtenga, el vendedor de mostrador tiene el 50% de eso.
		 */

		print("-----------------------------------");
		print("------- Vendedores Externos  ------");
		print("-----------------------------------");

		int vueltas = -10 * 1000 * 1000;
		resumen += VendedorExterno.printResumenCabecera() + "\n";

		Enumeration<VendedorExterno> enuVex = vendedoresExternos.elements();
		while (enuVex.hasMoreElements() && vueltas < 3) {

			vueltas++;

			VendedorExterno vex = enuVex.nextElement();

			vex.initResumenProveedor();
			vex.calculaTotales();

			String idVendedor = vex.getId();

			System.out.println("Calculando comisiones .... "
					+ vex.getNombreArchivo());

			String str = "";
			// recorrer las cobranzas

			Iterator<Venta> ite = vex.getCobranzasIterator();
			while (ite.hasNext()) {
				Venta venta = ite.next();

				double por = 0;
				if (venta.getNroRecibo().compareTo("0") == 0) {
					venta.setFechaRecibo(" ");
					Cons.error("No deberia tener recibo 0:" + venta.toString());

				} else {

					por = getPorcentaje(venta, vex.getMetaAlcanzada(), EXTERNO,
							idVendedor, vex);
					// se hace solo el calculo de la comision
				}
				venta.setPorcentaje(por);

				str += venta.printFormato(true) + "\n";

				// para el resumen proveedor
				vex.addVentaProveedorCobranza(venta);

			}

			str += ventasMarcasAsigandas(vex, true);

			if (SI_VENTAS == true) {
				str += "\n Ventas del Mes \n";
				Iterator<Venta> iteV = vex.getVentasIterator();
				while (iteV.hasNext()) {
					Venta venta = iteV.next();
					str += venta.printFormato(true) + "\n";
				}
			}
			vex.calculaTotales();

			print("-----------------------------------------------------");
			print(vex.printCabeceraArchivo());
			print(Venta.printFormatoCabecera());
			print(str);

			String datosSalida = "";
			// datosSalida += vex.printCabeceraArchivo() + "\n";

			datosSalida += VendedorExterno.printResumenCabecera() + "\n";
			datosSalida += vex.printResumen() + "\n\n";
			datosSalida += Venta.printFormatoCabecera() + "\n";
			datosSalida += str + "\n -- FIN --\n";
			datosSalida += vex.getTextoResumenProveedors();
			m.grabarStringToArchivo(direOut + vex.getNombreArchivo(),
					FECHA_EJECUCION + datosSalida);

			resumen += vex.printResumen() + "\n";

			vex.calculaTotales();

		}

		resumen += "\n\n\n";

		print("-----------------------------------");
		print("------- Vendedores Mostrador  ------");
		print("-----------------------------------");

		resumen += VendedorMostrador.printResumenCabecera() + "\n";

		// vendedores de mostrador
		Enumeration<VendedorMostrador> enuVem = vendedoresMostrador.elements();
		while (enuVem.hasMoreElements() && vueltas < 6) {
			vueltas++;

			VendedorMostrador vem = enuVem.nextElement();
			vem.initResumenProveedor();
			vem.calculaTotales();

			String idVendedor = vem.getId();

			System.out.println("Calculando comisiones .... "
					+ vem.getNombreArchivo());

			String str = "";

			// recorrer las ventas
			Iterator<Venta> ite = vem.getCobranzasIterator();
			while (ite.hasNext()) {
				Venta venta = ite.next();

				double por = 0;
				if (venta.getNroRecibo().compareTo("0") == 0) {
					venta.setFechaRecibo(" ");
					Cons.error("MOS: no deberia tener recibo 0:"
							+ venta.toString());

				} else {
					por = getPorcentaje(venta, 0, MOSTRADOR, idVendedor, vem);
					/*
					 * if (venta.isEsCompartida() == true) { // el calculo de la
					 * comision ya fue hecho en // el vendedor externo } else {
					 * por = getPorcentaje(venta.isEsCompartida(),
					 * venta.isContado(), venta.getIdProveedor(), 0, MOSTRADOR);
					 * }
					 */
				}

				venta.setPorcentaje(por);

				str += venta.printFormato(false) + "\n";

				// para el resumen Proveedor
				vem.addVentaProveedorCobranza(venta);

			}

			str += ventasMarcasAsigandas(vem, false);

			if (SI_VENTAS == true) {
				str += "\n Ventas del Mes \n";
				Iterator<Venta> iteV = vem.getVentasIterator();
				while (iteV.hasNext()) {
					Venta venta = iteV.next();
					str += venta.printFormato(false) + "\n";
				}
			}

			print("-----------------------------------------------------");
			print(vem.printCabeceraArchivo());
			print(Venta.printFormatoCabecera());
			print(str);

			String datosSalida = "";
			datosSalida += VendedorMostrador.printResumenCabecera() + "\n";
			datosSalida += vem.printResumen() + "\n\n";
			datosSalida += Venta.printFormatoCabecera() + "\n";
			datosSalida += str + "\n -- FIN --\n";
			datosSalida += vem.getTextoResumenProveedors();

			m.grabarStringToArchivo(direOut + vem.getNombreArchivo(),
					FECHA_EJECUCION + datosSalida);

			resumen += vem.printResumen() + "\n";

		}

		resumen += "\n\n\n";

		print("-----------------------------------");
		print("------- Vendedores Los Crespi  ----");
		print("-----------------------------------");

		resumen += VendedorCrespi.printResumenCabecera() + "\n";

		// vendedores de mostrador
		Enumeration<VendedorCrespi> enuVemCrespi = vendedoresCrespi.elements();
		while (enuVemCrespi.hasMoreElements() && vueltas < 6) {
			vueltas++;

			VendedorCrespi vec = enuVemCrespi.nextElement();
			vec.initResumenProveedor();
			vec.calculaTotales();

			String idVendedor = vec.getId();

			System.out.println("Calculando comisiones .... "
					+ vec.getNombreArchivo());

			String str = "";

			// recorrer las ventas
			Iterator<Venta> ite = vec.getCobranzasIterator();
			while (ite.hasNext()) {
				Venta venta = ite.next();

				double por = 0;
				if (venta.getNroRecibo().compareTo("0") == 0) {
					venta.setFechaRecibo(" ");
					Cons.error("MOS: no deberia tener recibo 0:"
							+ venta.toString());

				} else {
					por = getPorcentaje(venta, 0, LOS_CRESPI, idVendedor, vec);
					/*
					 * if (venta.isEsCompartida() == true) { // el calculo de la
					 * comision ya fue hecho en // el vendedor externo } else {
					 * por = getPorcentaje(venta.isEsCompartida(),
					 * venta.isContado(), venta.getIdProveedor(), 0, MOSTRADOR);
					 * }
					 */
				}

				venta.setPorcentaje(por);

				str += venta.printFormato(false) + "\n";

				// para el resumen Proveedor
				vec.addVentaProveedorCobranza(venta);

			}

			str += ventasMarcasAsigandas(vec, false);

			if (SI_VENTAS == true) {
				str += "\n Ventas del Mes \n";
				Iterator<Venta> iteV = vec.getVentasIterator();
				while (iteV.hasNext()) {
					Venta venta = iteV.next();
					str += venta.printFormato(false) + "\n";
				}
			}

			print("-----------------------------------------------------");
			print(vec.printCabeceraArchivo());
			print(Venta.printFormatoCabecera());
			print(str);

			String datosSalida = "";
			datosSalida += vec.printMetasPorMarca()+"\n\n";
			
			
			
			datosSalida += VendedorCrespi.printResumenCabecera() + "\n";
			
			datosSalida += vec.printResumen() + "\n\n";
			datosSalida += Venta.printFormatoCabecera() + "\n";
			datosSalida += str + "\n -- FIN --\n";
			datosSalida += vec.getTextoResumenProveedors();

			m.grabarStringToArchivo(direOut + vec.getNombreArchivo(),
					FECHA_EJECUCION + datosSalida);

			resumen += vec.printResumen() + "\n";

		}

		resumen += "\n\n\n\n\n";

		// ================== TOTAL ==================
		String totalVentas = "";
		totalVentas += "\n -- RESUMEN DE PROVEEDORES --\n";
		totalVentas += VENDEDOR_TOTAL.getTextoResumenProveedors();
		resumen += totalVentas;

		m.grabarStringToArchivo(direFileResumen, FECHA_EJECUCION + resumen);

	}

	public static String ventasMarcasAsigandas(Vendedor ven, boolean b) {
		String out = "";
		out += "---- Ventas de Marcas Asignadas -------------------\n";
		for (int i = 0; i < ven.getVentasMarcaAsignada().size(); i++) {
			Venta v = ven.getVentasMarcaAsignada().get(i);
			out += v.printFormato(b, (0.5)) + "\n";
		}
		out += "---- Ventas de Marcas NO Asignadas -------------------\n";
		for (int i = 0; i < ven.getVentasMarcaNOAsignada().size(); i++) {
			Venta v = ven.getVentasMarcaNOAsignada().get(i);
			out += v.printFormato(b, (-.5)) + "\n";
		}
		out += "------------------------------------------------------\n";
		return out;
	}

	/* inicializa segun los datos que se conocen */
	public static void init() throws Exception {
		csvtoTable(fileVentas, ventas);
		csvtoTable(fileCobranzas, cobranzas);

		cargaVenMostradorPorcentaje();
		cargaPorcentajesPorProveedor();
		defineMetas();
		cargaVendedores();
		cargaComisionEspecialVendedorProveedor();
		// cargarVendedoresAsignados a Marcas

		recorreVentasCobranzas(ventas, false);
		recorreVentasCobranzas(cobranzas, true);

		calculaComisiones();
		calculaComisiones();

		// System.out.println(LOGs);
	}

	public static void comentarios() {
		error("----------------------------------------------------");
		error("OjO, vendedor Gabriel con IdVendedor = 12 Iduser = 0");
		error("----------------------------------------------------");

		error(" Temporal Sixto Sanchex (92) y usuario SSANCHEZ no es compartida");
		error("----------------------------------------------------");
		error(" Ojo ver caso de LMUÑOZ que sale como 0");
		error("----------------------------------------------------");
	}

}

class ArrayComparator implements Comparator<Object[]> {
	@Override
	public int compare(Object[] o1, Object[] o2) {
		String s1 = (String) o1[3];
		String s2 = (String) o2[3];

		String s11 = (String) o1[1];
		String s22 = (String) o2[1];

		String A = s1 + s11;
		String B = s2 + s22;
		return A.compareTo(B);
	}
}
