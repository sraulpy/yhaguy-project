package com.yhaguy.util.migracion.central;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.coreweb.domain.Tipo;
import com.coreweb.domain.Usuario;
import com.coreweb.extras.csv.CSV;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.ArticuloAplicacion;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.ArticuloGrupo;
import com.yhaguy.domain.ArticuloHistorialMigracion;
import com.yhaguy.domain.ArticuloMarca;
import com.yhaguy.domain.ArticuloModelo;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.DepartamentoApp;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaRubro;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.PlanDeCuenta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.util.Utiles;

public class MigracionCentral {

	static final String DIR_MIGRACION = "./WEB-INF/docs/migracion/central/";
	
	static final String SUC_CENTRAL = "CENTRAL";
	static final String SUC_MRA = "M.R.A";
	static final String SUC_MCAL = "MCAL";
	static final String SUC_GAM = "GRAN ALMACEN";
	
	/**
	 * SISTEMA
	 */
	
	/**
	 * sucursales departamentos depositos
	 */
	public static void migrarSucursales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		String[] sucs = new String[] { SUC_MRA, SUC_CENTRAL, SUC_MCAL, SUC_GAM };
		String[] dirs = new String[] { "", "", "", "" };
		String[] estbc = new String[] { "2", "1", "3", "4" };
		String[] tels = new String[] { "", "", "", "" };
		
		String[][] departamentos = new String[][] { { "ADMINISTRACION", "VENTAS" },
				{ "ADMINISTRACION", "RR.HH", "INFORMATICA", "COBRANZAS", "ABASTECIMIENTO", "VENTAS", "MARKETING",
						"AUDITORIA", "LOGISTICA", "DIRECTORIO" },
				{ "ADMINISTRACION", "VENTAS" }, { "ADMINISTRACION", "VENTAS" } };

		for (int i = 0; i < sucs.length; i++) {
			SucursalApp suc = new SucursalApp();
			suc.setDescripcion(sucs[i]);
			suc.setDireccion(dirs[i]);
			suc.setEstablecimiento(estbc[i]);
			suc.setNombre(sucs[i]);
			suc.setTelefono(tels[i]);
			rr.saveObject(suc, "sys");
			
			String[] depmts = departamentos[i];
			for (int j = 0; j < depmts.length; j++) {
				DepartamentoApp dep = new DepartamentoApp();
				dep.setDescripcion(depmts[j]);
				dep.setNombre(depmts[j]);
				dep.setSucursal(suc);
				rr.saveObject(dep, "sys");
			}
			
			System.out.println(suc.getDescripcion());
		}	
	}
	
	
	/**
	 * funcionarios..
	 */
	public static void migrarFuncionarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "FUNCIONARIOS.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "RAZONSOCIAL", CSV.STRING }, { "RUC", CSV.STRING }, { "DIRECCION", CSV.STRING }, { "TELEFONO", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String rs = csv.getDetalleString("RAZONSOCIAL");
			String ruc = csv.getDetalleString("RUC");
			String dir = csv.getDetalleString("DIRECCION");
			String tel = csv.getDetalleString("TELEFONO");

			Empresa emp = new Empresa();
			emp.setRazonSocial(rs);
			emp.setRuc(ruc);
			emp.setCi(ruc);
			emp.setDireccion_(dir);
			emp.setTelefono_(tel);
			rr.saveObject(emp, "sys");
			
			Funcionario fun = new Funcionario();
			fun.setEmpresa(emp);
			rr.saveObject(fun, "sys");

			System.out.println(emp.getRazonSocial());

		}
	}
	
	/**
	 * usuarios..
	 */
	public static void migrarUsuariosCentral() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "USUARIOS_CENTRAL.csv";
		SucursalApp central = rr.getSucursalAppByNombre(SUC_CENTRAL);
		Set<SucursalApp> sucs = new HashSet<SucursalApp>();
		sucs.add(central);
		DepartamentoApp dep = (DepartamentoApp) rr.getObject(DepartamentoApp.class.getName(), 1);

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "NOMBRE", CSV.STRING }, { "LOGIN", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String nombre = csv.getDetalleString("NOMBRE");
			String login = csv.getDetalleString("LOGIN");

			Usuario user = new Usuario();
			user.setNombre(nombre);
			user.setLogin(login);
			user.setClave(Utiles.encriptar("123", true));
			rr.saveObject(user, "sys");
			
			AccesoApp acceso = new AccesoApp();
			acceso.setDescripcion("");
			acceso.setSucursales(sucs);
			acceso.setUsuario(user);
			acceso.setDepartamento(dep);
			rr.saveObject(acceso, "sys");
			
			Set<AccesoApp> accs = new HashSet<AccesoApp>();
			accs.add(acceso);
			
			Funcionario fun = rr.getFuncionario(nombre);
			fun.setAccesos(accs);
			rr.saveObject(fun, "sys");
			
			System.out.println(user.getLogin());

		}
	}
	
	/**
	 * usuarios mcal..
	 */
	public static void migrarUsuariosMCAL() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "USUARIOS_MCAL.csv";
		SucursalApp mcal = rr.getSucursalAppByNombre(SUC_MCAL);
		Set<SucursalApp> sucs = new HashSet<SucursalApp>();
		sucs.add(mcal);

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "NOMBRE", CSV.STRING }, { "LOGIN", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String nombre = csv.getDetalleString("NOMBRE");
			String login = csv.getDetalleString("LOGIN");

			Usuario user = new Usuario();
			user.setNombre(nombre);
			user.setLogin(login);
			user.setClave(Utiles.encriptar("123", true));
			rr.saveObject(user, "sys");
			
			AccesoApp acceso = new AccesoApp();
			acceso.setDescripcion("");
			acceso.setSucursales(sucs);
			acceso.setUsuario(user);
			rr.saveObject(acceso, "sys");
			
			Set<AccesoApp> accs = new HashSet<AccesoApp>();
			accs.add(acceso);
			
			Funcionario fun = rr.getFuncionario(nombre);
			fun.setAccesos(accs);
			rr.saveObject(fun, "sys");
			
			System.out.println(user.getLogin());

		}
	}
	
	/**
	 * usuarios gran almacen..
	 */
	public static void migrarUsuariosGAM() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "USUARIOS_GAM.csv";
		SucursalApp gam = rr.getSucursalAppByNombre(SUC_GAM);
		Set<SucursalApp> sucs = new HashSet<SucursalApp>();
		sucs.add(gam);

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "NOMBRE", CSV.STRING }, { "LOGIN", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String nombre = csv.getDetalleString("NOMBRE");
			String login = csv.getDetalleString("LOGIN");

			Usuario user = new Usuario();
			user.setNombre(nombre);
			user.setLogin(login);
			user.setClave(Utiles.encriptar("123", true));
			rr.saveObject(user, "sys");
			
			AccesoApp acceso = new AccesoApp();
			acceso.setDescripcion("");
			acceso.setSucursales(sucs);
			acceso.setUsuario(user);
			rr.saveObject(acceso, "sys");
			
			Set<AccesoApp> accs = new HashSet<AccesoApp>();
			accs.add(acceso);
			
			Funcionario fun = rr.getFuncionario(nombre);
			fun.setAccesos(accs);
			rr.saveObject(fun, "sys");
			
			System.out.println(user.getLogin());

		}
	}
	
	/**
	 * plan de cuentas..
	 */
	public static void migrarPlanDeCuenta() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		Hashtable<String, Tipo> tipos = new Hashtable<String, Tipo>();
		Tipo activo = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_ACTIVO);
		Tipo pasivo = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_PASIVO);
		Tipo ingreso = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_INGRESO);
		Tipo egreso = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_EGRESO);

		tipos.put("1", activo);
		tipos.put("2", pasivo);
		tipos.put("3", ingreso);
		tipos.put("4", egreso);
		
		String src = DIR_MIGRACION + "PLAN_CUENTA.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "IDPLANCUENTA", CSV.STRING },
				{ "NOMBRE", CSV.STRING }, { "IDTIPOCUENTA", CSV.STRING },
				{ "IMPUTABLE", CSV.STRING }, { "IMPOSITIVO", CSV.STRING },
				{ "CCOSTO", CSV.STRING }, { "NIVEL", CSV.NUMERICO }, };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("IDPLANCUENTA");
			String descripcion = csv.getDetalleString("NOMBRE");
			String tipoCta = csv.getDetalleString("IDTIPOCUENTA");

			String imputable = csv.getDetalleString("IMPUTABLE");
			String impositivo = csv.getDetalleString("IMPOSITIVO");
			String ccosto = csv.getDetalleString("CCOSTO");
			int nivel = ((Float)csv.getDetalle("NIVEL")).intValue();
						
			PlanDeCuenta pct = new PlanDeCuenta();
			pct.setCodigo(codigo);
			pct.setDescripcion(descripcion);
			pct.setTipoCuenta(tipos.get(tipoCta));
			pct.setImputable(imputable);
			pct.setImpositivo(impositivo);
			pct.setCcosto(ccosto);
			pct.setNivel(nivel);
			rr.saveObject(pct, "sys");
			
			System.out.println(pct.getDescripcion());
		}
	}
	
	/**
	 * cuentas contables..
	 */
	public static void migrarCuentasContables() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "CUENTAS_CONTABLES.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING }, { "ALIAS", CSV.STRING } };

		PlanDeCuenta pc = (PlanDeCuenta) rr.getObject(PlanDeCuenta.class.getName(), 1);
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String cod = csv.getDetalleString("CODIGO");
			String desc = csv.getDetalleString("DESCRIPCION");
			String alias = csv.getDetalleString("ALIAS");
			
			CuentaContable cta = new CuentaContable();
			cta.setAlias(alias);
			cta.setCodigo(cod);
			cta.setDescripcion(desc);
			cta.setPlanCuenta(pc);			
			rr.saveObject(cta, "sys");
			
			ArticuloGasto ag = new ArticuloGasto();
			ag.setCuentaContable(cta);
			ag.setDescripcion(desc);
			rr.saveObject(cta, "sys");

			System.out.println(desc);
		}
	}
	
	
	/**
	 * ARTICULOS - DEPOSITOS
	 */
	
	/**
	 * familias de articulos..
	 */
	public static void migrarArticuloFamilias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		String[] familias = new String[] { "FILTROS", "LUBRICANTES", "CUBIERTAS", "REPUESTOS", "BATERIAS", "MARKETING", "RETAIL SHOP", "SERVICIOS" };

		for (int i = 0; i < familias.length; i++) {
			ArticuloFamilia flia = new ArticuloFamilia();
			flia.setDescripcion(familias[i]);
			rr.saveObject(flia, "sys");
			System.out.println(flia.getDescripcion());
		}
	}
	
	/**
	 * grupos de articulos..
	 */
	public static void migrarArticuloGrupo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "ARTICULO_GRUPO.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String desc = csv.getDetalleString("DESCRIPCION");

			ArticuloGrupo grupo = new ArticuloGrupo();
			grupo.setDescripcion(desc.toUpperCase());
			rr.saveObject(grupo, "sys");

			System.out.println(desc);

		}
	}
	
	/**
	 * marcas de articulos..
	 */
	public static void migrarArticuloMarca() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "ARTICULO_MARCA.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String desc = csv.getDetalleString("DESCRIPCION");

			ArticuloMarca marca = new ArticuloMarca();
			marca.setDescripcion(desc.toUpperCase());
			rr.saveObject(marca, "sys");

			System.out.println(desc);

		}
	}
	
	/**
	 * aplicaciones de articulos..
	 */
	public static void migrarArticuloAplicacion() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "ARTICULO_APLICACION.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String desc = csv.getDetalleString("DESCRIPCION");

			ArticuloAplicacion aplicacion = new ArticuloAplicacion();
			aplicacion.setDescripcion(desc.toUpperCase());
			rr.saveObject(aplicacion, "sys");

			System.out.println(desc);

		}
	}
	
	/**
	 * modelos de articulos..
	 */
	public static void migrarArticuloModelo() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "ARTICULO_MODELO.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING } };

		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String desc = csv.getDetalleString("DESCRIPCION");

			ArticuloModelo modelo = new ArticuloModelo();
			modelo.setDescripcion(desc.toUpperCase());
			rr.saveObject(modelo, "sys");

			System.out.println(desc);
		}
	}
	
	/**
	 * machear articulos / stock / costos / precios..
	 */
	public static void machearArticulosSaldos(String archivo, long idDeposito) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + archivo + ".csv";
		
		String[][] cab = { { "Empresa", CSV.STRING } };			
		String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING }, { "FAMILIA", CSV.STRING },
				{ "MARCA", CSV.STRING }, { "ESTADO", CSV.STRING }, { "SALDO", CSV.STRING },
				{ "COSTO", CSV.STRING }, { "MAYORISTA", CSV.STRING }, { "DOLARES", CSV.STRING }, { "MINORISTA", CSV.STRING },
				{ "AUTOCENTRO", CSV.STRING } };
		
		Deposito dep = (Deposito) rr.getObject(Deposito.class.getName(), idDeposito);
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");
			String descripcion = csv.getDetalleString("DESCRIPCION");
			String stock = csv.getDetalleString("SALDO");
			String costo = csv.getDetalleString("COSTO");
			
			long stock_ = 0;
			if (stock.contains(",")) {
				Double stck = Double.parseDouble(stock.replace(",", "."));
				stock_ = stck.longValue();
			} else {
				stock_ = Long.parseLong(stock);
			}
			
			//Articulo art = rr.getArticulo(codigo);
			
			if (stock_ > 0) {
				ArticuloHistorialMigracion mig = new ArticuloHistorialMigracion();
				mig.setCodigoBarra("");
				mig.setCodigoInterno(codigo);
				mig.setCodigoOriginal("");
				mig.setCodigoProveedor("");
				mig.setCosto(Double.parseDouble(costo.replace(",", ".")));
				mig.setDescripcion(descripcion);
				mig.setFechaAlta(Utiles.getFecha("05-10-2018 00:00:00"));
				mig.setStock(stock_);
				mig.setSucursal(rr.getSucursalAppById(2));
				mig.setAuxi("ID_DEPOSITO:" + idDeposito);
				mig.setDeposito(dep);
				rr.saveObject(mig, "sys");
				
				System.out.println(codigo);
			}
			
			/*if (art != null) {
				art.setCostoGs(Double.parseDouble(costo.replace(",", ".")));
				art.setPrecioGs(Double.parseDouble(mayorista.replace(",", ".")));
				art.setPrecioDs(Double.parseDouble(dolares.replace(",", ".")));
				art.setPrecioListaGs(Double.parseDouble(autocentro.replace(",", ".")));
				art.setPrecioMinoristaGs(Double.parseDouble(minorista.replace(",", ".")));
				
				ArticuloDeposito ad = new ArticuloDeposito();
				ad.setArticulo(art);
				ad.setDeposito(dep);
				ad.setStock(Long.parseLong(stock));
				
				rr.saveObject(art, "sys");
				rr.saveObject(ad, "sys");
				System.out.println(codigo + " - " + dep.getDescripcion());
			} else {
				
				ArticuloFamilia artFlia = rr.getArticuloFamilia(flia);
				ArticuloGrupo artGrupo = (ArticuloGrupo) rr.getObject(ArticuloGrupo.class.getName(), 1);
				ArticuloMarca artMarca = rr.getArticuloMarca(marca);
				ArticuloAplicacion artAplicacion = (ArticuloAplicacion) rr.getObject(ArticuloAplicacion.class.getName(), 1);
				ArticuloModelo artModelo = (ArticuloModelo) rr.getObject(ArticuloModelo.class.getName(), 1);
				ArticuloLinea artLinea = (ArticuloLinea) rr.getObject(ArticuloLinea.class.getName(), 1);
				ArticuloSubLinea artSubLinea = rr.getArticuloSubLinea("SIN SUBLINEA");
				ArticuloSubGrupo artSubGrupo = rr.getArticuloSubGrupo("SIN SUBGRUPO");
				ArticuloProcedencia artProcedencia = rr.getArticuloProcedencia("SIN PROCEDENCIA");
				ArticuloAPI artAPI = rr.getArticuloAPI("SIN API");
				
				art = new Articulo();
				art.setCodigoInterno(codigo);
				art.setDescripcion(descripcion);
				art.setPeso_("");
				art.setVolumen_("");
				
				art.setFamilia(artFlia);
				art.setMarca(artMarca);
				art.setGrupo(artGrupo);
				art.setAplicacion(artAplicacion);
				art.setModelo(artModelo);
				art.setLinea(artLinea);
				art.setSublinea(artSubLinea);
				art.setSubgrupo(artSubGrupo);
				art.setProcedencia(artProcedencia);
				art.setAPI(artAPI);
				art.setArticuloEstado(rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO));
				
				art.setCostoGs(Double.parseDouble(costo.replace(",", ".")));
				art.setPrecioGs(Double.parseDouble(mayorista.replace(",", ".")));
				art.setPrecioDs(Double.parseDouble(dolares.replace(",", ".")));
				art.setPrecioListaGs(Double.parseDouble(autocentro.replace(",", ".")));
				art.setPrecioMinoristaGs(Double.parseDouble(minorista.replace(",", ".")));
				
				ArticuloDeposito ad = new ArticuloDeposito();
				ad.setArticulo(art);
				ad.setDeposito(dep);
				ad.setStock(Long.parseLong(stock));
				
				rr.saveObject(art, "sys");
				rr.saveObject(ad, "sys");
				System.err.println(codigo + " -- AGREGADO..");
			}*/
		}
	}

	
	
	/**
	 * EMPRESAS - CLIENTES - PROVEEDORES
	 */
	
	
	/**
	 * rubros de empresas..
	 */
	public static void migrarRubrosClientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		String[] rubros = new String[] { "VENTA DE CUBIERTAS", "TRANSPORTE DE PASAJEROS", "TRANSPORTE DE CARGAS",
				"TALLER MECANICO", "PUERTOS", "NAVIERA", "GOMERIA", "ESTACION DE SERVICIO", "DISTRIBUIDOR DE YHAGUY",
				"COOPERATIVAS", "CONSTRUCTORA", "CONSECIONARIAS", "CENTRO DE LUBRICACION", "CASA DE REPUESTOS",
				"CASA DE FILTROS", "CASA DE BATERIAS", "CONSUMIDOR FINAL" };
		
		String[] subrubros = new String[] { "CUBIERTERO", "TRANSPORTE", "TRANSPORTE", "TALLERES", "PUERTOS", "NAVIERA",
				"GOMERIA", "SURTIDORES", "DISTRIBUIDORES", "COOPERATIVAS", "CONSTRUCTORA", "CONSECIONARIAS",
				"LUBRICENTRISTAS", "REPUESTERO", "FILTRERO", "BATERISTAS", "FINALES" };
		
		Double[] descuentos = new Double[] { 10.0, 25.0, 20.0, 20.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
				10.0, 25.0, 10.0, 10.0, 10.0 };

		for (int i = 0; i < rubros.length; i++) {
			EmpresaRubro rubro = new EmpresaRubro();
			rubro.setDescripcion(rubros[i]);
			rubro.setSubrubro(subrubros[i]);
			rubro.setDescuentoRepuestos(descuentos[i]);
			rr.saveObject(rubro, "sys");
			System.out.println(rubro.getDescripcion());
		}
	}	
	
	public static void main(String[] args) {
		try {
			
		/**	MigracionCentral.migrarArticuloFamilias();
			MigracionCentral.migrarArticuloGrupo();
			MigracionCentral.migrarArticuloMarca();
			MigracionCentral.migrarArticuloAplicacion();
			MigracionCentral.migrarArticuloModelo();
			MigracionCentral.migrarArticuloLinea();
			MigracionCentral.migrarArticuloSubLinea();
			MigracionCentral.migrarArticuloSubGrupo();
			MigracionCentral.migrarArticuloAPI();
			MigracionCentral.migrarArticuloProcedencia();
			MigracionCentral.migrarArticulos("ARTICULO_REPUESTOS");
			MigracionCentral.migrarArticulos("ARTICULO_FILTROS");
			MigracionCentral.migrarArticulos("ARTICULO_LUBRICANTES");
			MigracionCentral.migrarArticulos("ARTICULO_NEUMATICOS");
			MigracionCentral.migrarArticulos("ARTICULO_BATERIAS");
			MigracionCentral.migrarArticulos("ARTICULO_SERVICIOS");
			MigracionCentral.migrarArticulos("ARTICULO_RETAIL");
			MigracionCentral.migrarArticulos("ARTICULO_MARKETING"); 
			MigracionCentral.machearArticulosSaldos("1_STOCK_MINORISTA", 1);
			MigracionCentral.machearArticulosSaldos("2_STOCK_TEMPORAL_CENTRAL", 2);
			MigracionCentral.machearArticulosSaldos("3_STOCK_RECLAMOS_CENTRAL", 3);
			MigracionCentral.machearArticulosSaldos("4_STOCK_REPOSICION_CENTRAL", 4);
			MigracionCentral.machearArticulosSaldos("5_STOCK_MCAL", 5); 
			MigracionCentral.machearArticulosSaldos("5_STOCK_MCAL_", 5);
			MigracionCentral.machearArticulosSaldos("6_STOCK_MCAL_TEMPORAL", 6);
			MigracionCentral.machearArticulosSaldos("7_STOCK_MAYORISTA", 7); **/
			MigracionCentral.machearArticulosSaldos("8_STOCK_MAYORISTA_TEMPORAL", 8); 
			
		//	MigracionCentral.migrarVendedores();
		//	MigracionCentral.migrarRubrosClientes();
		//	MigracionCentral.migrarClientes();
		//	MigracionCentral.machearClientesVendedores();
		//	MigracionCentral.machearClientesSaldos();
		//	MigracionCentral.migrarProveedores();
		//	MigracionCentral.migrarFuncionarios();
		//	MigracionCentral.migrarChequesRechazados();
		//	MigracionCentral.machearSaldosVendedor();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
